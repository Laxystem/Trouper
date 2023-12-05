package quest.laxla.supertrouper

import com.kotlindiscord.kord.extensions.extensions.Extension

abstract class TrouperExtension : Extension() {
	final override val name: String = this::class.simpleName!!.substringBeforeLast("Extension")
		.replace("([A-Z])".toRegex()) { '-' + it.groups.single()!!.value.lowercase() }.removePrefix("-")
}
