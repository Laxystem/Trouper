import org.jetbrains.kotlin.gradle.tasks.KotlinCompile
import org.jetbrains.kotlin.util.removeSuffixIfPresent

plugins {
	application

	kotlin("jvm")
	kotlin("plugin.serialization")

	id("com.github.johnrengelman.shadow")
}

group = "quest.laxla"
version = file(".version").readText().removeSuffixIfPresent("\n")

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

val generatedResources = layout.buildDirectory.dir("generated/resources")

tasks.processResources {
	from(generatedResources)

	doFirst {
		generatedResources.orNull?.run {
			asFile.mkdirs()
			file(".version").asFile.writeText(version.toString())
		}
	}
}

application {
	mainClass = "quest.laxla.trouper.AppKt"
}

tasks.jar {
	manifest {
		attributes(
			"Main-Class" to application.mainClass.get()
		)
	}
}

val jvm: String by project

tasks.withType<KotlinCompile> { kotlinOptions.jvmTarget = jvm }

java {
	val java = JavaVersion.toVersion(jvm)
	sourceCompatibility = java
	targetCompatibility = java
}
