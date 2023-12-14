package quest.laxla.trouper

import dev.kord.core.entity.Application
import kotlin.contracts.ExperimentalContracts
import kotlin.contracts.InvocationKind
import kotlin.contracts.contract
import kotlin.text.buildString as buildStringKt

@OptIn(ExperimentalContracts::class)
inline fun <T> T.buildString(capacity: Int? = null, builderAction: StringBuilder.(T) -> Unit): String {
	contract {
		callsInPlace(builderAction, InvocationKind.EXACTLY_ONCE)
	}

	return if (capacity == null) buildStringKt { builderAction(this@buildString) }
	else buildStringKt(capacity) { builderAction(this@buildString) }
}


val Application.inviteUrl get() = customInstallUrl ?: installParams?.buildString {
	append("https://discord.com/api/oauth2/authorize?client_id=")
	append(id)
	append("&permissions=")
	append(it.permissions.code.value)
	append("&scope=")
	append(it.scopes.joinToString(separator = "+"))
}
