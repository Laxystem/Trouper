package quest.laxla.trouper

import com.kotlindiscord.kord.extensions.ExtensibleBot
import com.kotlindiscord.kord.extensions.utils.env
import com.kotlindiscord.kord.extensions.utils.envOrNull
import dev.kord.gateway.PrivilegedIntent
import kotlinx.coroutines.runBlocking
import quest.laxla.trouper.messaging.PrivateMassagingExtension

private val token = env("TOKEN")
val officialServer = env("OFFICIAL_SERVER")
val isDevelopmentEnvironment = envOrNull("IS_DEV_ENV").toBoolean()
val officialServerUrl = envOrNull("OFFICIAL_SERVER_URL")
val license = envOrNull("LICENSE")
val licenseUrl = envOrNull("LICENSE_URL")
val donateUrl = envOrNull("DONATE_URL")
val repoUrl = envOrNull("REPO_URL")

fun main() = runBlocking {
	ExtensibleBot(token) {
		applicationCommands {
			if (isDevelopmentEnvironment) defaultGuild(officialServer)
		}

		extensions {
			add(::MaintenanceExtension)
			@OptIn(PrivilegedIntent::class)
			add(::PrivateMassagingExtension)
			add(::AboutExtension)
		}
	}.start()
}
