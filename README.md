# Alone (Fabric 1.21.4)

## What it does
- **Empty world:** every mob is removed (zombies, cows, sheep, villagers...), natural spawning is off.
- **The stranger (???):** tall Steve-like entity (1.5x), appears 40-52 blocks away, always stares at you and slowly
  walks toward you. If you look at him for 1 second he vanishes in smoke. He comes back 15-60 s later.
- **Sounds:** random footsteps walking to you, someone mining, a door opening/closing, heartbeat, distant moans.
- **Tab list:** a fake player named "???" is shown; this also makes TAB work in single-player.
- **Fog:** permanent thick grey fog.

## Build / run
1. JDK 21 + IntelliJ IDEA Community.
2. Add the Gradle wrapper: generate any project at fabricmc.net/develop/template and copy `gradlew`,
   `gradlew.bat` and the `gradle/` folder here.
3. `./gradlew runClient` to test (single-player works).
4. `./gradlew build` -> jar in build/libs (the one without -sources). Put jar + Fabric API in `mods/`.

## Easy tweaks
- Stalker: StalkerEntity.java (LOOK_DOT, VANISH_TICKS, FOLLOW_FROM, SCALE in createStalkerAttributes)
- Spawn distance / frequency: StalkerSpawner.java
- Sound frequency (EVENT_CHANCE) and sound types: CreepyAmbience.java
- Fog thickness and colour: mixin/BackgroundRendererMixin.java (start, end, colour)

## Creative lock
GameLock.java puts anyone who enters Creative back to Survival instantly (even with /gamemode creative).
To allow it for yourself, delete the GameLock.tick(server) line in QozhaMod.java.

## Get the JAR without installing anything (GitHub)
1. Make a free account at github.com, create a new repository.
2. Upload ALL files of this folder (including the .github folder) to it.
3. Open the repo > Actions tab > "Build mod" > wait ~3 minutes for the green check.
4. Open the finished run, download the "alone-jar" artifact (zip), unzip it.
5. Use the jar WITHOUT "-sources" in the name. Put it + Fabric API into your mods folder (Fabric Loader 1.21.4).
