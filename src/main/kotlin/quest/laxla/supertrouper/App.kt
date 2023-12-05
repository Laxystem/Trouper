package quest.laxla.supertrouper

import com.kotlindiscord.kord.extensions.ExtensibleBot
import com.kotlindiscord.kord.extensions.utils.env
import dev.kord.common.entity.Snowflake
import kotlinx.coroutines.runBlocking

private val token = env("token")
val officialServer = env("official_server")

fun main() = runBlocking {
	ExtensibleBot(token) {
		extensions {
			add(::MaintenanceExtension)
		}

		applicationCommands {
			defaultGuild(Snowflake(officialServer))
		}
	}.start()
}
