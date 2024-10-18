plugins {
	kotlin("jvm") version "1.9.25"
	kotlin("plugin.spring") version "1.9.25"
	id("org.springframework.boot") version "3.3.4"
	id("io.spring.dependency-management") version "1.1.6"
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
//	runtimeOnly("org.mariadb:r2dbc-mariadb:1.1.3")
	runtimeOnly("org.mariadb:r2dbc-mariadb:1.2.2")
	runtimeOnly("org.mariadb.jdbc:mariadb-java-client")
	testImplementation("org.springframework.boot:spring-boot-starter-test")
	testImplementation("io.projectreactor:reactor-test")
	testImplementation("org.jetbrains.kotlin:kotlin-test-junit5")
	testRuntimeOnly("org.junit.platform:junit-platform-launcher")

	implementation("io.github.oshai:kotlin-logging-jvm:5.1.0")

	testImplementation("io.kotest:kotest-runner-junit5:5.6.2")
	testImplementation("io.kotest.extensions:kotest-extensions-spring:1.1.3")

	testImplementation("io.mockk:mockk:1.13.11")
	testImplementation("com.ninja-squad:springmockk:4.0.2")

}

kotlin {
	compilerOptions {
		freeCompilerArgs.addAll("-Xjsr305=strict")
	}
}

tasks.withType<Test> {
	useJUnitPlatform()
}
