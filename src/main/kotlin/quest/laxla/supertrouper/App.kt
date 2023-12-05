package quest.laxla.supertrouper

import com.kotlindiscord.kord.extensions.ExtensibleBot
import com.kotlindiscord.kord.extensions.utils.env
import kotlinx.coroutines.runBlocking

private val token = env("token")
val officialServer = env("official_server")

fun main() = runBlocking {
	ExtensibleBot(token) {
		extensions {
			add(::AboutExtension)
		}

		applicationCommands {
			defaultGuild(officialServer)
		}
	}.start()
}
