import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
	id("org.springframework.boot") version "2.7.1"
	id("io.spring.dependency-management") version "1.0.11.RELEASE"
	kotlin("jvm") version "1.6.21"
	kotlin("plugin.spring") version "1.6.21"
	kotlin("plugin.jpa") version "1.6.20"
	kotlin("plugin.allopen") version "1.6.21"
}

group = "com.yaincoding"
version = "0.0.1-SNAPSHOT"
java.sourceCompatibility = JavaVersion.VERSION_17

repositories {
	mavenCentral()
}

dependencies {
	implementation("org.springframework.boot:spring-boot-starter")
	implementation("org.springframework.boot:spring-boot-starter-web")
	implementation("org.springframework.boot:spring-boot-starter-data-jpa")
	implementation("org.springframework.boot:spring-boot-starter-webflux")
	testImplementation("org.springframework.boot:spring-boot-starter-test")

	//security
	implementation ("org.springframework.boot:spring-boot-starter-oauth2-client")
	implementation ("org.springframework.boot:spring-boot-starter-security")

	//jwt
	implementation("com.google.api-client:google-api-client:2.0.0")
	compileOnly("io.jsonwebtoken:jjwt-api:0.11.2")
	runtimeOnly("io.jsonwebtoken:jjwt-impl:0.11.2")
	runtimeOnly("io.jsonwebtoken:jjwt-jackson:0.11.2")

	implementation("com.fasterxml.jackson.module:jackson-module-kotlin:2.13.4")
	implementation("org.jetbrains.kotlin:kotlin-reflect")
	implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8")
	implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.4.0-RC")

	//db
	implementation("mysql:mysql-connector-java:8.0.29")
	implementation("org.mariadb.jdbc:mariadb-java-client:3.0.7")
	runtimeOnly("com.h2database:h2")

	//aws
	implementation("com.amazonaws:aws-java-sdk-s3:1.12.301")
}

allOpen {
	annotation("javax.persistence.Entity")
	annotation("javax.persistence.MappedSuperclass")
	annotation("javax.persistence.Embeddable")
}

sourceSets {
	main {
		resources {
			listOf("$projectDir/src/main/frontend/build", "$projectDir/build/resources/main/static")
		}
	}
}

tasks.register<Exec>("npmInstall") {
	workingDir("$projectDir/src/main/frontend")
	commandLine("npm", "install")
}

tasks.register<Exec>("reactBuild") {
	dependsOn("npmInstall")
	workingDir("$projectDir/src/main/frontend")
	commandLine("npm", "run", "build")
}

tasks.register<Copy>("copyBuildFiles") {
	dependsOn("reactBuild")
	from("$projectDir/src/main/frontend/build")
	into("$projectDir/build/resources/main/static")
}

tasks.getByName("build").dependsOn("copyBuildFiles")

tasks.withType<KotlinCompile> {
	dependsOn("copyBuildFiles")
	kotlinOptions {
		freeCompilerArgs = listOf("-Xjsr305=strict")
		jvmTarget = "17"
	}
}

tasks.withType<Test> {
	useJUnitPlatform()
}
