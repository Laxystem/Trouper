package quest.laxla.trouper

import com.kotlindiscord.kord.extensions.components.components
import com.kotlindiscord.kord.extensions.components.disabledButton
import com.kotlindiscord.kord.extensions.components.linkButton
import com.kotlindiscord.kord.extensions.extensions.ephemeralSlashCommand
import dev.kord.common.entity.ButtonStyle
import dev.kord.core.entity.effectiveName
import dev.kord.rest.builder.message.allowedMentions
import dev.kord.rest.builder.message.create.AbstractMessageCreateBuilder
import dev.kord.rest.builder.message.embed
import quest.laxla.trouper.messaging.PrivateMessagesCategoryName

class AboutExtension : TrouperExtension() {
	override suspend fun setup() {
		ephemeralSlashCommand {
			name = "about"
			description = "About Super Trouper"

			action {
				respond {
					about()
				}
			}
		}
	}

	suspend fun AbstractMessageCreateBuilder.about() {
		val self = kord.getSelf()
		val avatar = (self.avatar ?: self.defaultAvatar).cdnUrl.toUrl()
		val mention = self.mention
		allowedMentions()

		embed {
			title = "About ${self.effectiveName}" + if (version == null) "" else " `$version`"
			thumbnail { url = avatar }

			if (isDevelopmentEnvironment) field {
				name = "Development Environment"
				//language=Markdown
				value = "> This instance is hosted on someone's personal computer, " +
					"and *may* contain **malicious code** and/or **steal your data**. " +
					"This is not considered to be an official version of Trouper. " +
					"Do *not* rely on the lack of this message to determine if an instance is official."
			}

			//language=Markdown
			description = "$mention is an *open-source* bot made by the plural community, for the plural community.\n\n" +
				"$mention creates a private channel for members, " +
				"allowing them to talk to the server's owners or moderators. " +
				"Use `/pm` to get a link to your PM channel. " +
				"PM channels inherit their permissions from the `$PrivateMessagesCategoryName` category, " +
				"and can be synced by moderators (`Manage Permissions` is required by default).\n\n" +
				"$mention is free, open source software. You can host the bot on your own server, " +
				"without paying us a penny. We know not everyone can afford that, so we host it for you, " +
				"using our own money. Please, help us making $mention available for everyone, everywhere, for free."
		}

		components {
			val app = kord.getApplicationInfo()

			disabledButton {
				style = ButtonStyle.Primary
				label = "About"
			}

			app.inviteUrl?.let {
				linkButton {
					url = it
					label = "Invite"
				}
			}

			officialServerUrl?.let {
				linkButton {
					url = it
					label = "Join"
				}
			}

			donateUrl?.let {
				linkButton {
					url = it
					label = "Donate"
				}
			}

			repoUrl?.let {
				linkButton {
					url = it
					label = "Contribute"
				}
			}

			disabledButton(row = 1) {
				style = ButtonStyle.Primary
				label = "Legal"
			}

			licenseUrl?.let {
				linkButton(row = 1) {
					url = it
					label = license ?: "License"
				}
			}

			app.privacyPolicyUrl?.let {
				linkButton(row = 1) {
					url = it
					label = "Privacy Policy"
				}
			}

			app.termsOfServiceUrl?.let {
				linkButton(row = 1) {
					url = it
					label = "Terms of Service"
				}
			}
		}
	}
}
