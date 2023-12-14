pluginManagement {
	val kotlin: String by settings
	val shadow: String by settings

	plugins {
		kotlin("jvm") version kotlin
		kotlin("plugin.serialization") version kotlin

		id("com.github.johnrengelman.shadow") version shadow
	}
}

rootProject.name = "Trouper"
