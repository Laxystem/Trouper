package quest.laxla.supertrouper

import com.kotlindiscord.kord.extensions.ExtensibleBot
import com.kotlindiscord.kord.extensions.utils.env
import com.kotlindiscord.kord.extensions.utils.envOrNull
import dev.kord.gateway.PrivilegedIntent
import kotlinx.coroutines.runBlocking
import quest.laxla.supertrouper.messaging.PrivateMassagingExtension

private val token = env("TOKEN")
val officialServer = env("OFFICIAL_SERVER")
val isDevelopmentEnvironment = envOrNull("IS_DEV_ENV").toBoolean()

fun main() = runBlocking {
	ExtensibleBot(token) {
		applicationCommands {
			if (isDevelopmentEnvironment) defaultGuild(officialServer)
		}

		extensions {
			add(::MaintenanceExtension)
			@OptIn(PrivilegedIntent::class)
			add(::PrivateMassagingExtension)
		}
	}.start()
}
