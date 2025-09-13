# Project Snapshot

## Commit
$(cat .commit)

## Gradle & Android
- Gradle wrapper: $(./gradlew --version | sed -n '1,6p' | sed 's/^/  /')
- Android Gradle Plugin: (from build.gradle / gradle.properties)
- Kotlin: (from build.gradle)

## Modules
$(find . -maxdepth 2 -type d -name "app" -print | sed 's/^/  /')

## Source Layout (top level only)
$(find app/src/main/java -maxdepth 2 -type d | sed 's/^/  /')

## Key Files
- AndroidManifest.xml
- app/build.gradle
- settings.gradle
- Network/Retrofit services
- Token* screens, repository, Room entities/dao

## Current Features (checked = exists)
- [x] Token list from Crypto.com instruments
- [x] CoinGecko resolver for names
- [x] Detail screen
- [ ] (confirm) Offline caching (Room) for descriptions
- [ ] (confirm) Background sync / rate-limit handling

## Known Issues / Questions
- Fill in here…

## Next Goals (proposed)
1. Restore icons reliably (markets endpoint or cached mapping).
2. Offline-first descriptions (Room + TTL).
3. Gentle retry/backoff on HTTP 429.
4. One repo API facade (less duplication).
