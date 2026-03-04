# NewBlackbox

A virtual engine for Android that allows cloning and running virtual applications without installing APKs.

## Features

- **Virtual App Cloning** – Clone and run multiple instances of any app
- **Sandboxed Environment** – No root required; runs apps in isolation
- **Multi-Architecture Support** – ARM64, ARMv7, x86 (32-bit & 64-bit)
- **Device Spoofing** – Fake device properties per virtual app
- **Fake Location** – Override GPS coordinates for virtual apps
- **Android 5.0–15.0+ Support** – Wide compatibility range
- **GMS Support** – Google Mobile Services integration

## Modules

| Module | Language | Description |
|--------|----------|-------------|
| `Bcore` | Java + C++ | Core virtual engine library |
| `app` | Kotlin | Demo/host application |
| `black-reflection` | Java | Reflection utility library |
| `compiler` | Java | Annotation processor |

## Build Requirements

- Android Studio Hedgehog or newer
- Android NDK r25c or newer
- JDK 17
- Gradle 8.x

## Quick Start

```bash
git clone https://github.com/ALEX5402/NewBlackbox.git
cd NewBlackbox
./gradlew assembleDebug
```

## Architecture

NewBlackbox intercepts Android system calls at multiple layers:
1. **Java layer** – Hooks Binder calls, ActivityManager, PackageManager
2. **C++ layer** – Hooks libc/linker syscalls via Dobby
3. **IO layer** – Redirects file system access to virtual paths

## License

Apache 2.0 – see [LICENSE](LICENSE)
