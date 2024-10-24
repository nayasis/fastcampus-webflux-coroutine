plugins {
	id("org.springframework.boot") version "3.3.4"
	id("io.spring.dependency-management") version "1.1.5"
	kotlin("jvm") version "1.9.24"
	kotlin("plugin.spring") version "1.9.24"
}

group = "dev.fastcampus"
version = "0.0.1-SNAPSHOT"

java {
	toolchain {
		languageVersion = JavaLanguageVersion.of(17)
	}
}

repositories {
	mavenCentral()
}

dependencies {
	implementation("org.springframework.boot:spring-boot-starter-data-r2dbc")
	implementation("org.springframework.boot:spring-boot-starter-webflux")
	implementation("com.fasterxml.jackson.module:jackson-module-kotlin")
	implementation("io.projectreactor.kotlin:reactor-kotlin-extensions")
	implementation("org.jetbrains.kotlin:kotlin-reflect")
	implementation("org.jetbrains.kotlinx:kotlinx-coroutines-reactor")

	runtimeOnly("com.h2database:h2")
	runtimeOnly("io.r2dbc:r2dbc-h2")

	testImplementation("org.springframework.boot:spring-boot-starter-test")
	testImplementation("io.projectreactor:reactor-test")
	testImplementation("org.jetbrains.kotlin:kotlin-test-junit5")
	testRuntimeOnly("org.junit.platform:junit-platform-launcher")

	implementation("io.github.oshai:kotlin-logging-jvm:5.1.0")

	testImplementation("io.kotest:kotest-runner-junit5:5.6.2")
//	testImplementation("io.kotest:kotest-assertions-core:5.6.2")
	testImplementation("io.kotest.extensions:kotest-extensions-spring:1.1.3")
	testImplementation("io.mockk:mockk:1.13.11")

//	runtimeOnly("org.mariadb:r2dbc-mariadb:1.1.3")
//	runtimeOnly("org.mariadb.jdbc:mariadb-java-client")

	// 1. txid
	implementation("org.jetbrains.kotlinx:kotlinx-coroutines-slf4j:1.8.1") // maven coroutine slf4j
	implementation("org.springframework.boot:spring-boot-starter-aop")
	implementation("io.micrometer:context-propagation:1.1.1")

	// 2. error handler
	implementation("org.springframework.boot:spring-boot-starter-validation") // maven spring validation starter

	// 4. rate limiter
	implementation("io.github.resilience4j:resilience4j-spring-boot3:2.2.0")
	implementation("io.github.resilience4j:resilience4j-reactor:2.2.0")

	implementation("org.springframework.boot:spring-boot-starter-actuator")

}

kotlin {
	compilerOptions {
		freeCompilerArgs.addAll("-Xjsr305=strict")
	}
}

tasks.withType<Test> {
	useJUnitPlatform()
}
