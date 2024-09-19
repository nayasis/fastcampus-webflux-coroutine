plugins {
	kotlin("jvm") version "1.9.24"
}

java {
	sourceCompatibility = JavaVersion.VERSION_17
}

repositories {
	mavenCentral()
}

dependencies {

	implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.7.1")

	implementation("ch.qos.logback:logback-classic:1.4.14")
	implementation("io.github.microutils:kotlin-logging:3.0.5")

	implementation("org.jetbrains.kotlin:kotlin-reflect")
	implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8")
}