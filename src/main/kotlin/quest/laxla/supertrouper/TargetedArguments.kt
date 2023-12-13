package quest.laxla.supertrouper

import com.kotlindiscord.kord.extensions.commands.Arguments
import com.kotlindiscord.kord.extensions.commands.application.slash.SlashCommandContext
import com.kotlindiscord.kord.extensions.commands.converters.impl.optionalMember
import com.kotlindiscord.kord.extensions.components.forms.ModalForm

private const val TargetArgumentName = "target"
private const val TargetArgumentDescription = "Target of this command. Defaults to you."

open class TargetedArguments : Arguments() {
	val targetOrNull by optionalMember {
		name = TargetArgumentName
		description = TargetArgumentDescription
	}
}

val <C, A, M> C.target where C : SlashCommandContext<*, A, M>, A : TargetedArguments, M : ModalForm
	get() = arguments.targetOrNull ?: member!!
