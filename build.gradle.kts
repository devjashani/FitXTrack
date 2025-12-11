plugins {
    id("com.android.application") version "8.2.0" apply false  // CHANGED FROM 8.5.2
    id("org.jetbrains.kotlin.android") version "1.9.23" apply false
    id("com.google.gms.google-services") version "4.4.0" apply false
    id("com.google.devtools.ksp") version "1.9.23-1.0.20" apply false  // For Room KSP
//    id("com.google.dagger.hilt.android") version "2.57.2" apply false
}

tasks.register("clean", Delete::class) {
    delete(rootProject.buildDir)
}
