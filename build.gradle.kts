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
	testImplementation("org.springframework.boot:spring-boot-starter-test")
	implementation("org.jetbrains.kotlin:kotlin-reflect")
	implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8")

	implementation("mysql:mysql-connector-java:8.0.29")
	implementation("org.mariadb.jdbc:mariadb-java-client:3.0.7")

	runtimeOnly("com.h2database:h2")

	implementation("com.google.code.gson:gson:2.9.0")
	implementation("org.apache.httpcomponents:httpclient:4.5.13")
	implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.4.0-RC")

	//aws
	implementation("com.amazonaws:aws-java-sdk-s3:1.12.301")
	implementation("com.amazonaws:aws-java-sdk-opensearch:1.12.301")

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
