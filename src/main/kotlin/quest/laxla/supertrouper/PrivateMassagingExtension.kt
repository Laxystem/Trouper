package quest.laxla.supertrouper

import com.kotlindiscord.kord.extensions.checks.anyGuild
import com.kotlindiscord.kord.extensions.checks.isNotBot
import com.kotlindiscord.kord.extensions.extensions.*
import com.kotlindiscord.kord.extensions.utils.any
import com.kotlindiscord.kord.extensions.utils.envOrNull
import dev.kord.common.entity.OverwriteType
import dev.kord.common.entity.Permission
import dev.kord.core.behavior.GuildBehavior
import dev.kord.core.behavior.channel.createTextChannel
import dev.kord.core.behavior.channel.edit
import dev.kord.core.behavior.createCategory
import dev.kord.core.entity.User
import dev.kord.core.entity.channel.Category
import dev.kord.core.entity.channel.TextChannel
import dev.kord.core.event.guild.MemberJoinEvent
import dev.kord.gateway.Intent
import dev.kord.gateway.PrivilegedIntent
import dev.kord.rest.builder.channel.addMemberOverwrite
import dev.kord.rest.builder.channel.addRoleOverwrite
import kotlinx.coroutines.flow.*

private const val PrivateMessagesCategoryName = "Private Messages"
private val memberLimit = envOrNull("AUTOMATIC_CHANNEL_CREATION_MEMBER_LIMIT")?.toInt() ?: 30
private val privateMessageOwnerPermissions = Permission.ViewChannel + Permission.ReadMessageHistory
private val privateMessageBotPermissions =
	privateMessageOwnerPermissions + Permission.ManageChannels + Permission.SendMessages + Permission.ManageMessages

class PrivateMassagingExtension : TrouperExtension() {
	@OptIn(PrivilegedIntent::class)
	override suspend fun setup() {
		intents += Intent.GuildMembers

		slashCommandCheck {
			anyGuild()
			isNotBot()
		}

		userCommandCheck {
			anyGuild()
			isNotBot()
		}

		event<MemberJoinEvent> {
			action {
				if (event.guild.members.count() < memberLimit) getOrCreateChannel(event.guild, event.member)
			}
		}

		ephemeralSlashCommand(::TargetedArguments) {
			name = "pm"
			description = "Get a link to a user's private messages channel"

			action {
				respond {
					content = getOrCreateChannelMention(guild!!, target.asUser())
				}
			}
		}

		ephemeralUserCommand {
			name = "Private Message"

			action {
				respond {
					content = getOrCreateChannelMention(guild!!, targetUsers.single())
				}
			}
		}

		ephemeralSlashCommand(::TargetedArguments) slash@{
			name = "sync"
			description = "Syncs a private message channel's permissions with the category."

			requirePermission(Permission.ManageRoles)

			action {
				val category = getOrCreateCategory(guild!!)
				val targetUser = target.asUser()
				val targetChannel = getChannel(category, targetUser)
				if (targetChannel == null) {
					respond { content = "${target.mention} does not have a private message channel in this server." }
					return@action
				}

				targetChannel.edit {
					sync(
						overwrite(this@slash.kord.selfId, OverwriteType.Member, allowed = privateMessageBotPermissions),
						overwrite(targetUser.id, OverwriteType.Member, allowed = privateMessageOwnerPermissions),
						defaults = category.permissionOverwrites
					)
				}

				respond {
					content = "Synced ${targetChannel.mention} for ${target.mention} successfully."
				}
			}
		}
	}

	private suspend fun getOrCreateChannelMention(guild: GuildBehavior, user: User): String =
		user.mention + ": " + (getOrCreateChannel(guild, user)?.mention ?: "Ineligible")

	private suspend fun getOrCreateChannel(guild: GuildBehavior, user: User) =
		getOrCreateChannel(getOrCreateCategory(guild), user)

	private suspend fun getOrCreateCategory(guild: GuildBehavior) = getCategory(guild) ?: createCategory(guild)

	private suspend fun getCategory(guild: GuildBehavior) = guild.channels.filterIsInstance<Category>().filter {
		it.name.equals(PrivateMessagesCategoryName, ignoreCase = true)
	}.singleOrNull()

	private suspend fun createCategory(guild: GuildBehavior) = guild.createCategory(PrivateMessagesCategoryName) {
		reason = "Private messaging category was missing."
		nsfw = false

		addMemberOverwrite(kord.selfId) {
			allowed += privateMessageBotPermissions
		}

		addRoleOverwrite(guild.id) {
			denied += Permission.ViewChannel
		}
	}

	private suspend fun getOrCreateChannel(category: Category, user: User) =
		if (user.isBot) null else getChannel(category, user) ?: createChannel(category, user)

	private suspend fun getChannel(category: Category, user: User) =
		category.channels.filterIsInstance<TextChannel>().firstOrNull { channel ->
			channel.categoryId == category.id && (channel.topic?.contains(user.mention) == true || channel.pinnedMessages.any {
				it.author?.id == kord.selfId && it.mentionedUserIds.singleOrNull() == user.id
			})
		}

	private suspend fun createChannel(category: Category, user: User): TextChannel {
		val mention = user.mention

		val channel = category.createTextChannel(user.username) {
			reason = "Created a PM with $mention."
			nsfw = category.data.nsfw.discordBoolean
			topic = mention

			sync(
				overwrite(kord.selfId, OverwriteType.Member, allowed = privateMessageBotPermissions),
				overwrite(user.id, OverwriteType.Member, allowed = privateMessageOwnerPermissions),
				defaults = category.permissionOverwrites
			)
		}

		return channel
	}
}
