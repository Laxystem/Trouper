package quest.laxla.supertrouper

import com.kotlindiscord.kord.extensions.extensions.publicSlashCommand
import dev.kord.common.entity.Permission
import dev.kord.common.entity.Snowflake

class MaintenanceExtension : TrouperExtension() {
	override suspend fun setup() {
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
