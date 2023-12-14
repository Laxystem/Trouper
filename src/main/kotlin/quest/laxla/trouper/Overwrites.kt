package quest.laxla.trouper

import dev.kord.common.entity.Overwrite
import dev.kord.common.entity.OverwriteType
import dev.kord.common.entity.Permissions
import dev.kord.common.entity.Snowflake
import dev.kord.core.entity.PermissionOverwriteEntity
import dev.kord.rest.builder.channel.PermissionOverwritesBuilder

fun overwrite(
	target: Snowflake,
	type: OverwriteType,
	allowed: Permissions = Permissions(),
	denied: Permissions = Permissions()
) = Overwrite(target, type, allowed, denied)

fun PermissionOverwritesBuilder.addOverwrite(
	target: Snowflake,
	type: OverwriteType,
	allowed: Permissions = Permissions(),
	denied: Permissions = Permissions()
) = addOverwrite(overwrite(target, type, allowed, denied))

fun PermissionOverwritesBuilder.sync(
	vararg overrides: Overwrite,
	defaults: Iterable<PermissionOverwriteEntity>,
	neverAllow: Permissions = Permissions()
) = sync(overrides.asIterable(), defaults, neverAllow)

fun PermissionOverwritesBuilder.sync(
	overrides: Iterable<Overwrite>,
	defaults: Iterable<PermissionOverwriteEntity>,
	neverAllow: Permissions = Permissions()
) {
	val permissions = mutableMapOf<Overwrite, PermissionOverwriteEntity>()

	defaults.forEach { default ->
		val override = overrides.find { it.id == default.target && it.type == default.type }

		if (override == null) addOverwrite(default.target, default.type, default.allowed - neverAllow, default.denied)
		else permissions[override] = default
	}

	overrides.forEach { override ->
		val default = permissions[override]

		if (default == null) addOverwrite(override.copy(allow = override.allow - neverAllow))
		else addOverwrite(
			default.target,
			default.type,
			default.allowed - default.denied + override.allow - override.deny - neverAllow,
			default.denied - default.allowed - override.allow + override.deny
		)
	}
}
