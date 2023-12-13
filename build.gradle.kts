import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
	application

	kotlin("jvm")
	kotlin("plugin.serialization")

	id("com.github.johnrengelman.shadow")
}

group = "quest.laxla"
version = "0.0.1"

repositories {
	google()
	mavenCentral()

	maven("https://oss.sonatype.org/content/repositories/snapshots") {
		name = "Sonatype Snapshots (Legacy)"
	}

	maven("https://s01.oss.sonatype.org/content/repositories/snapshots") {
		name = "Sonatype Snapshots"
	}
}

val kordex: String by project
val serialization: String by project
val logback: String by project
val slf4j: String by project
val klogging: String by project

dependencies {
	implementation(kotlin("stdlib"))
	implementation(kotlin("reflect"))
	implementation("org.jetbrains.kotlinx:kotlinx-serialization-protobuf:$serialization")

	implementation("com.kotlindiscord.kord.extensions:kord-extensions:$kordex")

	implementation("io.github.oshai:kotlin-logging:$klogging")
	runtimeOnly("org.slf4j:slf4j-api:$slf4j")
	runtimeOnly("ch.qos.logback:logback-classic:$logback")
}

application {
	mainClass = "quest.laxla.supertrouper.AppKt"
}

val jvm: String by project

tasks.withType<KotlinCompile> { kotlinOptions.jvmTarget = jvm }

tasks.jar {
	manifest {
		attributes(
			"Main-Class" to application.mainClass.get()
		)
	}
}

java {
	val java = JavaVersion.toVersion(jvm)
	sourceCompatibility = java
	targetCompatibility = java
}
