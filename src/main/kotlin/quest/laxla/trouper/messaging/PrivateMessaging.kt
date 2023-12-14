package quest.laxla.trouper.messaging

import com.kotlindiscord.kord.extensions.utils.envOrNull
import dev.kord.common.entity.ALL
import dev.kord.common.entity.Permission
import dev.kord.common.entity.Permissions
import dev.kord.common.entity.Snowflake
import dev.kord.core.behavior.GuildBehavior
import dev.kord.core.behavior.UserBehavior
import dev.kord.core.behavior.channel.MessageChannelBehavior
import dev.kord.core.behavior.channel.createMessage
import dev.kord.core.entity.Member
import dev.kord.core.entity.User
import dev.kord.core.entity.channel.Category
import dev.kord.core.entity.channel.MessageChannel
import dev.kord.core.entity.channel.TextChannel
import dev.kord.rest.builder.message.allowedMentions
import dev.kord.rest.builder.message.create.AbstractMessageCreateBuilder
import kotlinx.coroutines.flow.filterIsInstance
import kotlinx.coroutines.flow.firstOrNull

const val PrivateMessagesCategoryName = "Private Messages"
const val PingButton = "PM.ping"
const val UserIdCapturingGroup = "userID"
val memberLimit = envOrNull("AUTOMATIC_CHANNEL_CREATION_MEMBER_LIMIT")?.toInt() ?: 30
val pmMemberPermissions = Permission.ViewChannel + Permission.ReadMessageHistory
val pmBotPermissions =
	pmMemberPermissions + Permission.ManageChannels + Permission.SendMessages + Permission.ManageMessages
val userMentionRegex = "<@(?<$UserIdCapturingGroup>[1-9][0-9]+)>".toRegex()

infix fun TextChannel.isOf(user: UserBehavior) = topic?.contains(user.mention) == true

suspend fun getChannel(category: Category, user: User) = category.channels.filterIsInstance<TextChannel>().firstOrNull {
	it.categoryId == category.id && it isOf user
}

suspend fun getCategory(guild: GuildBehavior) =
	guild.channels.filterIsInstance<Category>().firstOrNull { it.isUsableForPrivateMessaging }

val User.isEligible get() = !isBot
val Category.isUsableForPrivateMessaging get() = name.equals(PrivateMessagesCategoryName, ignoreCase = true)

fun AbstractMessageCreateBuilder.executePingCommand(
	channel: MessageChannel,
	pinger: UserBehavior
) {
	val owners = channel.owners?.toList()

	if (owners == null) {
		allowedMentions()

		content = "The owner of this channel is unknown. They need to be mentioned in the channel's topic, " +
			"Like this: `<@userID>`."
	} else {
		allowedMentions {
			users.addAll(owners.asSequence().map {
				Snowflake(it.groups[UserIdCapturingGroup]!!.value)
			})
		}

		content = "Hey, " + owners.joinToString(separator = " ") {
			it.value
		} + ", y'all were pinged by " + pinger.mention + '!'
	}
}

suspend fun MessageChannelBehavior.ping(user: UserBehavior) = createMessage {
	allowedMentions { users.add(user.id) }
	content = user.mention
}.delete(reason = "Ghost pinged " + user.mention)

val MessageChannel.owners get() = data.topic.value?.let { userMentionRegex.findAll(it) }

suspend fun Member.getDeniedPermissions() = Permissions.ALL - getPermissions()
