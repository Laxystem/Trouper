package quest.laxla.trouper

import com.kotlindiscord.kord.extensions.extensions.Extension

private const val NameRegexGroup = "name"

abstract class TrouperExtension : Extension() {
	final override val name: String = this::class.simpleName!!.substringBeforeLast("Extension")
		.replace("(?<$NameRegexGroup>[A-Z])".toRegex()) { '-' + it.groups[NameRegexGroup]!!.value.lowercase() }.removePrefix("-")
}
