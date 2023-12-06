package quest.laxla.supertrouper

import com.kotlindiscord.kord.extensions.checks.isBotAdmin
import com.kotlindiscord.kord.extensions.commands.application.slash.publicSubCommand
import com.kotlindiscord.kord.extensions.extensions.publicSlashCommand
import com.kotlindiscord.kord.extensions.extensions.slashCommandCheck

class MaintenanceExtension : TrouperExtension() {
	override suspend fun setup() {
		slashCommandCheck {
			isBotAdmin()
		}

		publicSlashCommand {
			name = "maintenance"
			description = "Maintenance commands for maintainers of the bot"

			publicSubCommand {
				name = "stop"
				description = "Stops the bot completely"

				action {
					//language=Markdown
					respond { content = "# Invoking Protocol: Emergency Stop" }
					bot.stop()
				}
			}
		}
	}
}
