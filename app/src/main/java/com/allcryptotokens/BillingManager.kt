package com.allcryptotokens

import android.app.Activity
import android.content.Context
import android.os.Handler
import android.os.Looper
import android.os.SystemClock
import android.util.Log
import com.android.billingclient.api.AcknowledgePurchaseParams
import com.android.billingclient.api.BillingClient
import com.android.billingclient.api.BillingClientStateListener
import com.android.billingclient.api.BillingFlowParams
import com.android.billingclient.api.BillingResult
import com.android.billingclient.api.PendingPurchasesParams
import com.android.billingclient.api.ProductDetails
import com.android.billingclient.api.Purchase
import com.android.billingclient.api.PurchasesUpdatedListener
import com.android.billingclient.api.QueryProductDetailsParams
import com.android.billingclient.api.QueryPurchasesParams
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

/** Single owner of the Google Play Billing connection and ad-free entitlement. */
class BillingManager(context: Context) : PurchasesUpdatedListener {

    companion object {
        private const val TAG = "BillingManager"
        private const val PURCHASE_REFRESH_TIMEOUT_MS = 12_000L
    }

    private val _state = MutableStateFlow(BillingUiState())
    val state: StateFlow<BillingUiState> = _state.asStateFlow()

    private val productDetails = mutableMapOf<PlanType, ProductDetails>()
    private val offerTokens = mutableMapOf<PlanType, String>()
    private val mainHandler = Handler(Looper.getMainLooper())

    private var connectionInProgress = false
    private var pendingRefreshAfterConnection = false
    private var pendingRefreshShowsMessage = false
    private var nextRefreshId = 0L
    private var activeRefresh: PurchaseRefresh? = null
    private var automaticEmptyRefreshRetryScheduled = false

    private val billingClient = BillingClient.newBuilder(context.applicationContext)
        .setListener(this)
        .enablePendingPurchases(
            PendingPurchasesParams.newBuilder()
                .enableOneTimeProducts()
                .build()
        )
        .enableAutoServiceReconnection()
        .build()

    fun start() {
        queryProductsAndRefresh(showUserMessage = false)
    }

    /** Silent entitlement sync used when the app returns to the foreground. */
    fun refreshPurchasesSilently() {
        requestPurchaseRefresh(showUserMessage = false)
    }

    fun launchPurchase(activity: Activity, plan: PlanType) {
        if (!billingClient.isReady) {
            _state.value = _state.value.copy(message = "Connecting to Google Play...")
            ensureConnected(requestRefresh = true, showUserMessage = false)
            return
        }

        val details = productDetails[plan]
        val offerToken = offerTokens[plan]

        if (details == null || offerToken.isNullOrBlank()) {
            _state.value = _state.value.copy(
                message = "This plan is not available yet. Check the product setup in Google Play Console."
            )
            queryProducts()
            return
        }

        val productParams = BillingFlowParams.ProductDetailsParams.newBuilder()
            .setProductDetails(details)
            .setOfferToken(offerToken)
            .build()

        val params = BillingFlowParams.newBuilder()
            .setProductDetailsParamsList(listOf(productParams))
            .build()

        _state.value = _state.value.copy(isLoading = true, message = null)

        val result = billingClient.launchBillingFlow(activity, params)
        if (result.responseCode != BillingClient.BillingResponseCode.OK) {
            publishError(result, "The Google Play purchase screen could not be opened.")
        }
    }

    fun restorePurchases() {
        _state.value = _state.value.copy(
            isLoading = true,
            message = "Checking Google Play purchases..."
        )
        requestPurchaseRefresh(showUserMessage = true)
    }

    private fun queryProductsAndRefresh(showUserMessage: Boolean) {
        if (billingClient.isReady) {
            _state.value = _state.value.copy(isConnected = true)
            queryProducts()
            beginPurchaseRefresh(showUserMessage)
        } else {
            ensureConnected(requestRefresh = true, showUserMessage = showUserMessage)
        }
    }

    private fun ensureConnected(requestRefresh: Boolean, showUserMessage: Boolean) {
        if (requestRefresh) {
            pendingRefreshAfterConnection = true
            pendingRefreshShowsMessage = pendingRefreshShowsMessage || showUserMessage
        }

        if (billingClient.isReady) {
            val shouldRefresh = pendingRefreshAfterConnection
            val shouldShowMessage = pendingRefreshShowsMessage
            pendingRefreshAfterConnection = false
            pendingRefreshShowsMessage = false
            _state.value = _state.value.copy(isConnected = true)
            queryProducts()
            if (shouldRefresh) beginPurchaseRefresh(shouldShowMessage)
            return
        }

        if (connectionInProgress) {
            Log.d(TAG, "Billing connection already in progress; refresh request queued")
            return
        }

        connectionInProgress = true
        Log.d(TAG, "Starting Google Play Billing connection")

        billingClient.startConnection(
            object : BillingClientStateListener {
                override fun onBillingSetupFinished(result: BillingResult) {
                    connectionInProgress = false
                    Log.d(
                        TAG,
                        "Billing setup finished: responseCode=${result.responseCode}, " +
                            "debugMessage=${result.debugMessage}"
                    )

                    if (result.responseCode == BillingClient.BillingResponseCode.OK) {
                        _state.value = _state.value.copy(isConnected = true)
                        queryProducts()

                        val shouldRefresh = pendingRefreshAfterConnection
                        val shouldShowMessage = pendingRefreshShowsMessage
                        pendingRefreshAfterConnection = false
                        pendingRefreshShowsMessage = false
                        if (shouldRefresh) beginPurchaseRefresh(shouldShowMessage)
                    } else {
                        val showMessage = pendingRefreshShowsMessage
                        pendingRefreshAfterConnection = false
                        pendingRefreshShowsMessage = false
                        handleAutomaticOrManualError(
                            result = result,
                            fallback = "Google Play Billing is unavailable.",
                            showUserMessage = showMessage
                        )
                    }
                }

                override fun onBillingServiceDisconnected() {
                    connectionInProgress = false
                    Log.w(TAG, "Google Play Billing service disconnected")
                    _state.value = _state.value.copy(isConnected = false)
                }
            }
        )
    }

    private fun queryProducts() {
        if (!billingClient.isReady) return

        val subscriptionProducts = listOf(ProductIds.monthly, ProductIds.annual).map { productId ->
            QueryProductDetailsParams.Product.newBuilder()
                .setProductId(productId)
                .setProductType(BillingClient.ProductType.SUBS)
                .build()
        }

        val oneTimeProducts = listOf(
            QueryProductDetailsParams.Product.newBuilder()
                .setProductId(ProductIds.oneTime)
                .setProductType(BillingClient.ProductType.INAPP)
                .build()
        )

        queryProductGroup(subscriptionProducts)
        queryProductGroup(oneTimeProducts)
    }

    private fun queryProductGroup(products: List<QueryProductDetailsParams.Product>) {
        val params = QueryProductDetailsParams.newBuilder().setProductList(products).build()

        billingClient.queryProductDetailsAsync(params) { result, queryResult ->
            Log.d(
                TAG,
                "Product query finished: responseCode=${result.responseCode}, " +
                    "debugMessage=${result.debugMessage}"
            )

            queryResult.productDetailsList.forEach { details ->
                Log.d(
                    TAG,
                    "Fetched product: id=${details.productId}, type=${details.productType}, " +
                        "subscriptionOffers=${details.subscriptionOfferDetails?.size ?: 0}, " +
                        "oneTimeOffers=${details.oneTimePurchaseOfferDetailsList?.size ?: 0}"
                )
                cacheProduct(details)
            }

            queryResult.unfetchedProductList.forEach { product ->
                Log.e(
                    TAG,
                    "Unfetched product: id=${product.productId}, type=${product.productType}, " +
                        "statusCode=${product.statusCode}"
                )
            }

            if (result.responseCode != BillingClient.BillingResponseCode.OK) {
                publishError(result, "Google Play prices could not be loaded.")
                return@queryProductDetailsAsync
            }

            publishPrices()
        }
    }

    private fun cacheProduct(details: ProductDetails) {
        val plan = ProductIds.planFor(details.productId) ?: return
        val token = when (plan) {
            PlanType.MONTHLY, PlanType.ANNUAL ->
                details.subscriptionOfferDetails?.firstOrNull()?.offerToken
            PlanType.ONE_TIME ->
                details.oneTimePurchaseOfferDetailsList?.firstOrNull()?.offerToken
            PlanType.FREE -> null
        } ?: return

        productDetails[plan] = details
        offerTokens[plan] = token
    }

    private fun publishPrices() {
        fun price(plan: PlanType): String? {
            val details = productDetails[plan] ?: return null
            return when (plan) {
                PlanType.MONTHLY, PlanType.ANNUAL ->
                    details.subscriptionOfferDetails
                        ?.firstOrNull()
                        ?.pricingPhases
                        ?.pricingPhaseList
                        ?.lastOrNull()
                        ?.formattedPrice
                PlanType.ONE_TIME ->
                    details.oneTimePurchaseOfferDetailsList?.firstOrNull()?.formattedPrice
                PlanType.FREE -> null
            }
        }

        _state.value = _state.value.copy(
            isConnected = true,
            monthlyPrice = price(PlanType.MONTHLY) ?: _state.value.monthlyPrice,
            annualPrice = price(PlanType.ANNUAL) ?: _state.value.annualPrice,
            oneTimePrice = price(PlanType.ONE_TIME) ?: _state.value.oneTimePrice,
            availablePlans = productDetails.keys.toSet()
        )
    }

    private fun requestPurchaseRefresh(showUserMessage: Boolean) {
        if (!billingClient.isReady) {
            ensureConnected(requestRefresh = true, showUserMessage = showUserMessage)
            return
        }
        beginPurchaseRefresh(showUserMessage)
    }

    private fun beginPurchaseRefresh(showUserMessage: Boolean) {
        val running = activeRefresh
        if (running != null) {
            running.showUserMessage = running.showUserMessage || showUserMessage
            if (showUserMessage) {
                _state.value = _state.value.copy(
                    isLoading = true,
                    message = "Checking Google Play purchases..."
                )
            }
            Log.d(TAG, "Purchase refresh #${running.id} already running; duplicate request merged")
            return
        }

        val refresh = PurchaseRefresh(
            id = ++nextRefreshId,
            startedAtMs = SystemClock.elapsedRealtime(),
            showUserMessage = showUserMessage
        )
        activeRefresh = refresh

        if (showUserMessage) {
            _state.value = _state.value.copy(
                isLoading = true,
                message = "Checking Google Play purchases..."
            )
        }

        Log.d(TAG, "Purchase refresh #${refresh.id} started")

        val timeout = Runnable {
            val current = activeRefresh
            if (current?.id != refresh.id) return@Runnable
            Log.e(
                TAG,
                "Purchase refresh #${refresh.id} timed out after " +
                    "${SystemClock.elapsedRealtime() - refresh.startedAtMs} ms; " +
                    "waitingFor=${current.pendingTypes.joinToString()}"
            )
            finishPurchaseRefreshWithError(
                refresh = current,
                result = null,
                fallback = "Google Play purchases could not be checked. Please try again."
            )
        }
        refresh.timeoutRunnable = timeout
        mainHandler.postDelayed(timeout, PURCHASE_REFRESH_TIMEOUT_MS)

        queryPurchaseType(refresh, BillingClient.ProductType.INAPP)
        queryPurchaseType(refresh, BillingClient.ProductType.SUBS)
    }

    private fun queryPurchaseType(refresh: PurchaseRefresh, productType: String) {
        val startedAt = SystemClock.elapsedRealtime()
        Log.d(TAG, "Purchase refresh #${refresh.id}: $productType query started")

        val params = QueryPurchasesParams.newBuilder()
            .setProductType(productType)
            .build()

        billingClient.queryPurchasesAsync(params) { result, purchases ->
            val current = activeRefresh
            if (current?.id != refresh.id) {
                Log.w(TAG, "Purchase refresh #${refresh.id}: late $productType callback ignored")
                return@queryPurchasesAsync
            }

            val duration = SystemClock.elapsedRealtime() - startedAt
            Log.d(
                TAG,
                "Purchase refresh #${refresh.id}: $productType query finished in ${duration} ms; " +
                    "responseCode=${result.responseCode}, purchases=${purchases.size}, " +
                    "debugMessage=${result.debugMessage}"
            )

            if (result.responseCode != BillingClient.BillingResponseCode.OK) {
                finishPurchaseRefreshWithError(
                    refresh = current,
                    result = result,
                    fallback = "Google Play purchases could not be checked. Please try again."
                )
                return@queryPurchasesAsync
            }

            current.purchases += purchases
            current.pendingTypes.remove(productType)

            if (current.pendingTypes.isEmpty()) {
                finishPurchaseRefreshSuccessfully(current)
            }
        }
    }

    private fun finishPurchaseRefreshSuccessfully(refresh: PurchaseRefresh) {
        if (activeRefresh?.id != refresh.id) return
        cancelRefreshTimeout(refresh)
        activeRefresh = null
        Log.d(
            TAG,
            "Purchase refresh #${refresh.id} completed successfully in " +
                "${SystemClock.elapsedRealtime() - refresh.startedAtMs} ms; " +
                "purchases=${refresh.purchases.size}"
        )
        processPurchases(
            purchases = refresh.purchases,
            showRestoreResult = refresh.showUserMessage,
            queryFailed = false
        )

        // Google Play can occasionally return an empty entitlement snapshot during
        // the first connection on a second device. Verify one more time shortly
        // afterwards before relying on the initial Basic state. Restore Purchases
        // remains available as the explicit manual fallback.
        val hasConfiguredPurchase = refresh.purchases.any { purchase ->
            purchase.purchaseState == Purchase.PurchaseState.PURCHASED &&
                purchase.products.any(ProductIds::isConfiguredProduct)
        }
        if (!refresh.showUserMessage && !hasConfiguredPurchase && !automaticEmptyRefreshRetryScheduled) {
            automaticEmptyRefreshRetryScheduled = true
            mainHandler.postDelayed(
                { requestPurchaseRefresh(showUserMessage = false) },
                1_500L
            )
        }
    }

    private fun finishPurchaseRefreshWithError(
        refresh: PurchaseRefresh,
        result: BillingResult?,
        fallback: String
    ) {
        if (activeRefresh?.id != refresh.id) return
        cancelRefreshTimeout(refresh)
        activeRefresh = null

        if (result != null) {
            Log.e(
                TAG,
                "Purchase refresh #${refresh.id} failed: responseCode=${result.responseCode}, " +
                    "debugMessage=${result.debugMessage}"
            )
        }

        // Keep the last known entitlement on Billing errors. Only unlock the UI.
        _state.value = _state.value.copy(
            isLoading = false,
            isConnected = billingClient.isReady,
            message = if (refresh.showUserMessage) {
                result?.debugMessage?.ifBlank { fallback } ?: fallback
            } else {
                null
            }
        )
    }

    private fun cancelRefreshTimeout(refresh: PurchaseRefresh) {
        refresh.timeoutRunnable?.let(mainHandler::removeCallbacks)
        refresh.timeoutRunnable = null
    }

    override fun onPurchasesUpdated(result: BillingResult, purchases: MutableList<Purchase>?) {
        when (result.responseCode) {
            BillingClient.BillingResponseCode.OK -> {
                processPurchases(
                    purchases = purchases.orEmpty(),
                    showRestoreResult = false,
                    queryFailed = false
                )
            }
            BillingClient.BillingResponseCode.USER_CANCELED -> {
                _state.value = _state.value.copy(isLoading = false, message = null)
            }
            BillingClient.BillingResponseCode.ITEM_ALREADY_OWNED -> {
                requestPurchaseRefresh(showUserMessage = false)
            }
            else -> publishError(result, "The purchase could not be completed.")
        }
    }

    private fun processPurchases(
        purchases: List<Purchase>,
        showRestoreResult: Boolean,
        queryFailed: Boolean
    ) {
        if (queryFailed) {
            _state.value = _state.value.copy(
                isLoading = false,
                message = if (showRestoreResult) {
                    "Google Play purchases could not be checked. Please try again."
                } else null
            )
            return
        }

        val purchased = purchases.filter { purchase ->
            purchase.purchaseState == Purchase.PurchaseState.PURCHASED &&
                purchase.products.any(ProductIds::isConfiguredProduct)
        }

        purchased.filterNot(Purchase::isAcknowledged).forEach { purchase ->
            val params = AcknowledgePurchaseParams.newBuilder()
                .setPurchaseToken(purchase.purchaseToken)
                .build()

            billingClient.acknowledgePurchase(params) { result ->
                if (result.responseCode != BillingClient.BillingResponseCode.OK) {
                    // The entitlement is already confirmed by queryPurchases/onPurchasesUpdated.
                    // A transient acknowledgement failure must not replace the active-plan UI
                    // with a raw Google Play "Server error" message. Unacknowledged purchases
                    // are retried on the next entitlement refresh/app resume.
                    Log.e(
                        TAG,
                        "Purchase acknowledgement failed: responseCode=${result.responseCode}, " +
                            "debugMessage=${result.debugMessage}"
                    )
                }
            }
        }

        val activeProducts = purchased.flatMap(Purchase::getProducts).toSet()
        val plan = when {
            ProductIds.oneTime in activeProducts -> PlanType.ONE_TIME
            ProductIds.annual in activeProducts -> PlanType.ANNUAL
            ProductIds.monthly in activeProducts -> PlanType.MONTHLY
            else -> PlanType.FREE
        }

        _state.value = _state.value.copy(
            isLoading = false,
            isConnected = true,
            plan = plan,
            message = when {
                plan != PlanType.FREE -> null
                showRestoreResult -> "No active purchase was found for this Google Play account."
                else -> null
            }
        )
    }

    private fun handleAutomaticOrManualError(
        result: BillingResult,
        fallback: String,
        showUserMessage: Boolean
    ) {
        Log.e(
            TAG,
            "Billing error: responseCode=${result.responseCode}, debugMessage=${result.debugMessage}"
        )
        _state.value = _state.value.copy(
            isLoading = false,
            isConnected = false,
            message = if (showUserMessage) result.debugMessage.ifBlank { fallback } else null
        )
    }

    private fun publishError(result: BillingResult, fallback: String) {
        Log.e(
            TAG,
            "Billing error: responseCode=${result.responseCode}, debugMessage=${result.debugMessage}"
        )
        _state.value = _state.value.copy(
            isLoading = false,
            message = result.debugMessage.ifBlank { fallback }
        )
    }

    fun close() {
        activeRefresh?.let(::cancelRefreshTimeout)
        activeRefresh = null
        mainHandler.removeCallbacksAndMessages(null)
        billingClient.endConnection()
    }

    private data class PurchaseRefresh(
        val id: Long,
        val startedAtMs: Long,
        var showUserMessage: Boolean,
        val purchases: MutableList<Purchase> = mutableListOf(),
        val pendingTypes: MutableSet<String> = mutableSetOf(
            BillingClient.ProductType.INAPP,
            BillingClient.ProductType.SUBS
        ),
        var timeoutRunnable: Runnable? = null
    )
}

data class BillingUiState(
    val isLoading: Boolean = true,
    val isConnected: Boolean = false,
    val plan: PlanType = PlanType.FREE,
    val monthlyPrice: String = "C$2.99",
    val annualPrice: String = "C$11.99",
    val oneTimePrice: String = "C$19.99",
    val availablePlans: Set<PlanType> = emptySet(),
    val message: String? = null
) {
    val isAdFree: Boolean
        get() = plan != PlanType.FREE
}

enum class PlanType {
    FREE,
    MONTHLY,
    ANNUAL,
    ONE_TIME
}

object ProductIds {
    const val monthly = "remove_ads_monthly"
    const val annual = "remove_ads_yearly"
    const val oneTime = "remove_ads_one_time"

    private val all = setOf(monthly, annual, oneTime)

    fun isConfiguredProduct(productId: String): Boolean = productId in all

    fun planFor(productId: String): PlanType? = when (productId) {
        monthly -> PlanType.MONTHLY
        annual -> PlanType.ANNUAL
        oneTime -> PlanType.ONE_TIME
        else -> null
    }
}
