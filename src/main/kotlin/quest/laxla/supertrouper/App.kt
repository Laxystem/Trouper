package quest.laxla.supertrouper

import com.kotlindiscord.kord.extensions.ExtensibleBot
import com.kotlindiscord.kord.extensions.utils.env
import com.kotlindiscord.kord.extensions.utils.envOrNull
import dev.kord.gateway.PrivilegedIntent
import kotlinx.coroutines.runBlocking
import quest.laxla.supertrouper.messaging.PrivateMassagingExtension

private val token = env("TOKEN")
private val testingServer = envOrNull("TESTING_SERVER")

fun main() = runBlocking {
	ExtensibleBot(token) {
		applicationCommands {
			testingServer?.let { defaultGuild(it) }
		}

		extensions {
			add(::MaintenanceExtension)
			@OptIn(PrivilegedIntent::class)
			add(::PrivateMassagingExtension)
		}
	}.start()
}
