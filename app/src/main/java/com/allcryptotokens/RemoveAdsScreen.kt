package com.allcryptotokens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

private val ActYellow = Color(0xFFFFD600)
private val ActCard = Color(0xFF242424)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RemoveAdsScreen(
    state: BillingUiState,
    onBack: () -> Unit,
    onRestore: () -> Unit,
    onPurchase: (PlanType) -> Unit
) {
    var selected by remember { mutableStateOf(PlanType.ANNUAL) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Remove Ads") },
                navigationIcon = { TextButton(onClick = onBack) { Text("‹", fontSize = 32.sp) } }
            )
        }
    ) { pad ->
        Column(
            modifier = Modifier.padding(pad).fillMaxSize().verticalScroll(rememberScrollState()).padding(20.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            if (state.isAdFree) {
                Text("Ads removed", fontSize = 28.sp, fontWeight = FontWeight.Bold, color = ActYellow)
                Spacer(Modifier.height(12.dp))
                Text(
                    when (state.plan) {
                        PlanType.MONTHLY -> "Monthly plan is active."
                        PlanType.ANNUAL -> "Yearly plan is active."
                        PlanType.ONE_TIME -> "One-time purchase is active."
                        PlanType.FREE -> ""
                    },
                    textAlign = TextAlign.Center
                )
                if (state.plan == PlanType.MONTHLY || state.plan == PlanType.ANNUAL) {
                    Spacer(Modifier.height(12.dp))
                    Text(
                        "You can manage your subscription through Google Play.",
                        color = Color.LightGray,
                        fontSize = 14.sp,
                        textAlign = TextAlign.Center
                    )
                }
            } else {
                Text("Enjoy AllCryptoTokens without banner ads.", fontSize = 20.sp, fontWeight = FontWeight.SemiBold, textAlign = TextAlign.Center)
                Spacer(Modifier.height(24.dp))
                PlanCard("Monthly", state.monthlyPrice, "Renews monthly until canceled.", selected == PlanType.MONTHLY) { selected = PlanType.MONTHLY }
                Spacer(Modifier.height(12.dp))
                PlanCard("Yearly", state.annualPrice, "Renews yearly until canceled.", selected == PlanType.ANNUAL) { selected = PlanType.ANNUAL }
                Spacer(Modifier.height(12.dp))
                PlanCard("One-time Purchase", state.oneTimePrice, "No renewal required.", selected == PlanType.ONE_TIME) { selected = PlanType.ONE_TIME }
                Spacer(Modifier.height(22.dp))
                Button(
                    onClick = { onPurchase(selected) },
                    enabled = !state.isLoading,
                    colors = ButtonDefaults.buttonColors(containerColor = ActYellow, contentColor = Color.Black),
                    modifier = Modifier.fillMaxWidth().height(56.dp),
                    shape = RoundedCornerShape(18.dp)
                ) { Text(if (state.isLoading) "Please wait…" else "Continue", fontWeight = FontWeight.Bold, fontSize = 18.sp) }
                Spacer(Modifier.height(10.dp))
                TextButton(onClick = onRestore, enabled = !state.isLoading) { Text("Restore purchases") }
            }
            if (!state.isAdFree) {
                state.message?.let {
                    Spacer(Modifier.height(12.dp))
                    Text(it, textAlign = TextAlign.Center)
                }
            }
        }
    }
}

@Composable
private fun PlanCard(title: String, price: String, subtitle: String, selected: Boolean, onClick: () -> Unit) {
    Surface(
        modifier = Modifier.fillMaxWidth().clickable(onClick = onClick),
        shape = RoundedCornerShape(18.dp),
        color = ActCard,
        border = BorderStroke(if (selected) 2.dp else 1.dp, if (selected) ActYellow else Color(0xFF555555))
    ) {
        Row(Modifier.padding(18.dp), verticalAlignment = Alignment.CenterVertically) {
            RadioButton(selected = selected, onClick = onClick, colors = RadioButtonDefaults.colors(selectedColor = ActYellow))
            Spacer(Modifier.width(10.dp))
            Column(Modifier.weight(1f)) {
                Text(title, fontSize = 19.sp, fontWeight = FontWeight.Bold)
                Text(subtitle, color = Color.LightGray, fontSize = 13.sp)
            }
            Text(price, fontSize = 20.sp, fontWeight = FontWeight.Bold, color = if (selected) ActYellow else Color.White)
        }
    }
}
