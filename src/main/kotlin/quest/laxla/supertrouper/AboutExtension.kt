package quest.laxla.supertrouper

import com.kotlindiscord.kord.extensions.extensions.Extension
import com.kotlindiscord.kord.extensions.extensions.ephemeralSlashCommand
import com.kotlindiscord.kord.extensions.extensions.publicSlashCommand
import dev.kord.common.entity.Permission
import dev.kord.common.entity.Snowflake

class AboutExtension : Extension() {
	override val name: String
		get() = "about"

	override suspend fun setup() {
		ephemeralSlashCommand {
			name = "about"

			action {
				respond {
					//language=Markdown
					content = "Hey! This is a test command. It's powered by *magic*:sparkles:"
				}
			}
		}

		publicSlashCommand {
			name = "stop"
			description = "WARNING: Stops the bot completely."
			guildId = Snowflake(officialServer)
			requirePermission(Permission.Administrator)

			action {
				//language=Markdown
				respond { content = "# Invoking Protocol: Emergency Stop" }
				bot.stop()
			}
		}
	}
}
