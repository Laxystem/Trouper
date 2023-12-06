package quest.laxla.supertrouper.messaging

import com.kotlindiscord.kord.extensions.utils.any
import com.kotlindiscord.kord.extensions.utils.envOrNull
import dev.kord.common.entity.Permission
import dev.kord.core.behavior.GuildBehavior
import dev.kord.core.behavior.UserBehavior
import dev.kord.core.entity.User
import dev.kord.core.entity.channel.Category
import dev.kord.core.entity.channel.TextChannel
import kotlinx.coroutines.flow.filterIsInstance
import kotlinx.coroutines.flow.firstOrNull

const val PrivateMessagesCategoryName = "Private Messages"
val memberLimit = envOrNull("AUTOMATIC_CHANNEL_CREATION_MEMBER_LIMIT")?.toInt() ?: 30
val privateMessageOwnerPermissions = Permission.ViewChannel + Permission.ReadMessageHistory
val privateMessageBotPermissions =
	privateMessageOwnerPermissions + Permission.ManageChannels + Permission.SendMessages + Permission.ManageMessages

suspend infix fun TextChannel.isOf(user: UserBehavior) = topic?.contains(user.mention) == true || pinnedMessages.any {
	it.author?.id == kord.selfId && it.mentionedUserIds.singleOrNull() == user.id
}

suspend fun getChannel(category: Category, user: User) = category.channels.filterIsInstance<TextChannel>().firstOrNull {
	it.categoryId == category.id && it isOf user
}

suspend fun getCategory(guild: GuildBehavior) =
	guild.channels.filterIsInstance<Category>().firstOrNull { it.isUsableForPrivateMessaging }

val User.isEligible get() = !isBot
val Category.isUsableForPrivateMessaging get() = name.equals(PrivateMessagesCategoryName, ignoreCase = true)
