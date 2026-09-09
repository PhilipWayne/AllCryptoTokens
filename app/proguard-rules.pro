# Preserve generic signatures and runtime annotations used by Retrofit/Gson/Room.
-keepattributes Signature
-keepattributes *Annotation*

# Keep Retrofit service interfaces. R8 can still optimize the rest of the app.
-keep,allowoptimization,allowshrinking interface com.allcryptotokens.MarketApiService

# Retrofit's Gson converter deserializes these Worker response models via reflection.
# Keep their fields/constructors intact in minified release builds.
-keep class com.allcryptotokens.MarketCurrentResponse { *; }
-keep class com.allcryptotokens.MarketCurrentData { *; }
-keep class com.allcryptotokens.PriceHistoryResponse { *; }
-keep class com.allcryptotokens.PriceHistoryData { *; }
-keep class com.allcryptotokens.PriceHistoryPoint { *; }
