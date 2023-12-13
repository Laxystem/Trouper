package quest.laxla.supertrouper

import com.kotlindiscord.kord.extensions.checks.isBotAdmin
import com.kotlindiscord.kord.extensions.extensions.publicSlashCommand
import com.kotlindiscord.kord.extensions.extensions.slashCommandCheck
import dev.kord.common.entity.Snowflake

class MaintenanceExtension : TrouperExtension() {
	override suspend fun setup() {
		slashCommandCheck {
			isBotAdmin()
		}

		publicSlashCommand {
			name = "stop"
			description = "Stops the bot completely"
			guildId = Snowflake(officialServer)

			action {
				//language=Markdown
				respond { content = "# Invoking Protocol: Emergency Stop" }
				bot.stop()
			}
		}
	}
}
