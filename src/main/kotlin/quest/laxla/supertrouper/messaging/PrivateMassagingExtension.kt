package quest.laxla.supertrouper.messaging

import com.kotlindiscord.kord.extensions.checks.anyGuild
import com.kotlindiscord.kord.extensions.checks.isNotBot
import com.kotlindiscord.kord.extensions.extensions.*
import com.kotlindiscord.kord.extensions.types.EphemeralInteractionContext
import dev.kord.common.entity.ButtonStyle
import dev.kord.common.entity.DiscordPartialEmoji
import dev.kord.common.entity.OverwriteType
import dev.kord.common.entity.Permission
import dev.kord.core.behavior.GuildBehavior
import dev.kord.core.behavior.UserBehavior
import dev.kord.core.behavior.channel.createMessage
import dev.kord.core.behavior.channel.createTextChannel
import dev.kord.core.behavior.channel.edit
import dev.kord.core.behavior.createCategory
import dev.kord.core.behavior.interaction.respondPublic
import dev.kord.core.entity.User
import dev.kord.core.entity.channel.Category
import dev.kord.core.entity.channel.TextChannel
import dev.kord.core.event.guild.MemberJoinEvent
import dev.kord.core.event.interaction.GuildButtonInteractionCreateEvent
import dev.kord.gateway.Intent
import dev.kord.gateway.PrivilegedIntent
import dev.kord.rest.builder.channel.addMemberOverwrite
import dev.kord.rest.builder.channel.addRoleOverwrite
import dev.kord.rest.builder.message.actionRow
import dev.kord.rest.builder.message.embed
import kotlinx.coroutines.flow.count
import quest.laxla.supertrouper.*

@PrivilegedIntent
class PrivateMassagingExtension : TrouperExtension() {
	override suspend fun setup() {
		intents += Intent.GuildMembers

		slashCommandCheck { anyGuild(); isNotBot() }
		userCommandCheck { anyGuild(); isNotBot() }

		event<MemberJoinEvent> {
			action {
				if (event.member.isEligible && event.guild.members.count() < memberLimit)
					getOrCreateChannel(getOrCreateCategory(event.guild), event.member)
			}
		}

		ephemeralSlashCommand(::TargetedArguments) {
			name = "pm"
			description = "Get a link to a user's private messages channel"

			action {
				executeFindCommand(getOrCreateCategory(guild!!), target.asUser(), user)
			}
		}

		ephemeralUserCommand {
			name = "Private Message"

			action {
				executeFindCommand(getOrCreateCategory(guild!!), targetUsers.single(), user)
			}
		}

		ephemeralSlashCommand(::TargetedArguments) {
			name = "sync"
			description = "Syncs a private message channel's permissions with the category"

			requirePermission(Permission.ManageRoles)

			action {
				executeSyncCommand(getOrCreateCategory(guild!!), target.asUser())
			}
		}

		ephemeralUserCommand {
			name = "Sync PM Channel"

			requirePermission(Permission.ManageRoles)

			action {
				executeSyncCommand(getOrCreateCategory(guild!!), user.asUser())
			}
		}

		event<GuildButtonInteractionCreateEvent> {
			action {
				when (event.interaction.componentId) {
					PingButton -> event.interaction.respondPublic {
						executePingCommand(event.interaction.channel.asChannel(), event.interaction.user)
					}
				}
			}
		}
	}

	private suspend fun EphemeralInteractionContext.executeSyncCommand(category: Category, user: User) {
		val channel = getChannel(category, user)
		if (channel == null) {
			respond { content = "${user.mention} does not have a private messaging channel in this server." }
			return
		}

		val userMention = user.mention
		val channelMention = channel.mention

		channel.edit {
			reason = "Sync $channelMention with category for $userMention"

			sync(
				overwrite(kord.selfId, OverwriteType.Member, allowed = pmBotPermissions),
				overwrite(user.id, OverwriteType.Member, allowed = pmMemberPermissions),
				defaults = category.permissionOverwrites,
				neverAllow = kord.getSelf().asMember(category.guildId).getDeniedPermissions()
			)
		}

		respond {
			content = "Synced $channelMention for $userMention successfully."
		}
	}

	private suspend fun EphemeralInteractionContext.executeFindCommand(
		category: Category, user: User, searcher: UserBehavior = user
	) {
		if (user.isEligible) {
			val channel = getOrCreateChannel(category, user)
			respond { content = channel.mention }

			channel.ping(searcher)
		} else respond {
			content = user.mention + " is not eligible for private messaging."
		}
	}

	private suspend fun getOrCreateCategory(guild: GuildBehavior) = getCategory(guild) ?: createCategory(guild)

	private suspend fun createCategory(guild: GuildBehavior) = guild.createCategory(PrivateMessagesCategoryName) {
		reason = "Private messaging category was missing."
		nsfw = false

		addMemberOverwrite(kord.selfId) {
			allowed += pmBotPermissions
		}

		addRoleOverwrite(guild.id) {
			denied += Permission.ViewChannel
		}
	}

	private suspend fun getOrCreateChannel(category: Category, user: User) =
		getChannel(category, user) ?: createChannel(category, user)

	private suspend fun createChannel(category: Category, user: User): TextChannel {
		val mention = user.mention

		val channel = category.createTextChannel(user.username) {
			reason = "Created a PM with $mention."
			nsfw = category.data.nsfw.discordBoolean
			topic = "$mention's private messaging channel."

			sync(
				overwrite(kord.selfId, OverwriteType.Member, allowed = pmBotPermissions),
				overwrite(user.id, OverwriteType.Member, allowed = pmMemberPermissions),
				defaults = category.permissionOverwrites,
				neverAllow = kord.getSelf().asMember(category.guildId).getDeniedPermissions()
			)
		}

		val avatar = (user.avatar ?: user.defaultAvatar).cdnUrl.toUrl()

		channel.createMessage {
			embed {
				description = "# $mention"
				thumbnail { url = avatar }
			}

			actionRow {
				interactionButton(ButtonStyle.Primary, customId = PingButton) {
					label = "Ping"
					emoji = DiscordPartialEmoji(name = "\uD83D\uDD14")
				}
			}
		}

		channel.ping(user)

		return channel
	}
}
