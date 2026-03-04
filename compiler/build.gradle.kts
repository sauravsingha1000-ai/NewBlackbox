plugins {
    `java-library`
}

java {
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(17))
    }
}

dependencies {
    // Standard dependencies for Blackbox compiler
    implementation("com.google.auto.service:auto-service:1.1.1")
    annotationProcessor("com.google.auto.service:auto-service:1.1.1")
    implementation("com.squareup:javapoet:1.13.0")
    
    // Correct Kotlin DSL syntax to pull the specific release variant
    compileOnly(project(mapOf("path" to ":black-reflection", "configuration" to "releaseRuntimeElements")))
}
