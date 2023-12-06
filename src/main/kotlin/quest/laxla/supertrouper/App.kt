package quest.laxla.supertrouper

import com.kotlindiscord.kord.extensions.ExtensibleBot
import com.kotlindiscord.kord.extensions.utils.env
import com.kotlindiscord.kord.extensions.utils.envOrNull
import kotlinx.coroutines.runBlocking

private val token = env("TOKEN")
private val testingServer = envOrNull("TESTING_SERVER")

fun main() = runBlocking {
	ExtensibleBot(token) {
		applicationCommands {
			testingServer?.let { defaultGuild(it) }
		}

		extensions {
			add(::MaintenanceExtension)
			add(::PrivateMassagingExtension)
		}
	}.start()
}
