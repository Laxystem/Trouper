pluginManagement {
	val kotlin: String by settings
	val detekt: String by settings
	val shadow: String by settings
	val hooks: String by settings

	plugins {
		// Update this in libs.version.toml when you change it here.
		kotlin("jvm") version kotlin
		kotlin("plugin.serialization") version kotlin

		// Update this in libs.version.toml when you change it here.
		id("io.gitlab.arturbosch.detekt") version detekt

		id("com.github.jakemarsden.git-hooks") version hooks
		id("com.github.johnrengelman.shadow") version shadow
	}
}

rootProject.name = "supertrouper"
