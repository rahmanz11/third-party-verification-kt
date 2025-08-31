import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    kotlin("jvm") version "2.1.21"
kotlin("plugin.serialization") version "2.1.21"
id("io.ktor.plugin") version "3.2.3"
id("org.jetbrains.kotlin.plugin.allopen") version "2.1.21"
}

group = "com.external.verification"
version = "1.0.0"

repositories {
    mavenCentral()
    // Add Digital Persona repository if needed
    maven { url = uri("https://repo.digitalpersona.com/repository/maven-public/") }
}

dependencies {
    implementation("io.ktor:ktor-server-core")
    implementation("io.ktor:ktor-server-netty")
    implementation("io.ktor:ktor-server-content-negotiation")
    implementation("io.ktor:ktor-serialization-kotlinx-json")
    implementation("io.ktor:ktor-server-auth")
    implementation("io.ktor:ktor-server-auth-jwt")
    implementation("io.ktor:ktor-server-cors")
    implementation("io.ktor:ktor-server-call-logging")
    implementation("io.ktor:ktor-server-status-pages")
    implementation("io.ktor:ktor-server-swagger")

    // Add WebSocket support for real-time fingerprint device communication
    implementation("io.ktor:ktor-server-websockets")

    implementation("io.ktor:ktor-server-freemarker")
    
    implementation("io.ktor:ktor-client-core")
    implementation("io.ktor:ktor-client-cio")
    implementation("io.ktor:ktor-client-content-negotiation")
    implementation("io.ktor:ktor-client-logging")
    
    // Add WebSocket client support
    implementation("io.ktor:ktor-client-websockets")
    
    implementation("org.valiktor:valiktor-core:0.12.0")
    
    implementation("ch.qos.logback:logback-classic:1.4.11")
    implementation("io.github.microutils:kotlin-logging-jvm:3.0.5")

    // Digital Persona SDK - Real JAR files
    implementation(files("libs/dpfpenrollment.jar"))
    implementation(files("libs/dpfpverification.jar"))
    implementation(files("libs/dpotapi.jar"))
    implementation(files("libs/dpotjni.jar"))
    
    // Image processing for fingerprint conversion
    implementation("org.apache.commons:commons-imaging:1.0-alpha3")
    
    // Base64 encoding/decoding utilities
    implementation("commons-codec:commons-codec:1.15")
}

tasks.withType<KotlinCompile> {
    kotlinOptions {
        jvmTarget = "21"
        freeCompilerArgs += listOf("-opt-in=kotlin.RequiresOptIn")
    }
}

java {
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(21))
    }
}



ktor {
    fatJar {
        archiveFileName.set("verification-fullstack.jar")
    }
}

application {
    mainClass.set("com.external.verification.ApplicationKt")
}
