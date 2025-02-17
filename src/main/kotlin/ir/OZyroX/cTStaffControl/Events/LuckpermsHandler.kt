package ir.OZyroX.cTStaffControl.Events

import com.velocitypowered.api.proxy.Player
import net.luckperms.api.LuckPermsProvider
import net.luckperms.api.query.QueryOptions

class LuckpermsHandler {


    fun getLuckPermsPrefixAndRank(player: Player, callback: (prefix: String, rank: String) -> Unit) {
        val luckPerms = LuckPermsProvider.get()
        val userManager = luckPerms.userManager

        userManager.loadUser(player.uniqueId).thenAccept { user ->
            if (user != null) {
                val queryOptions = QueryOptions.defaultContextualOptions()
                val metaData = user.cachedData.getMetaData(queryOptions)

                val rawPrefix = metaData.prefix ?: ""
                val rank = user.primaryGroup

                val formattedPrefix = if (rawPrefix.startsWith("&#")) {
                    convertRGBPrefixToMiniMessage(rawPrefix)
                } else {
                    convertLegacyPrefixToMiniMessage(rawPrefix)
                }

                callback(formattedPrefix, rank)
            } else {
                callback("", "none")
            }
        }.exceptionally { throwable ->
            throwable.printStackTrace()
            callback("", "error in getting player rank")
            null
        }
    }

    fun convertRGBPrefixToMiniMessage(prefix: String): String {
        val regex = "&#([A-Fa-f0-9]{6})".toRegex()
        return regex.replace(prefix) { matchResult ->
            val hexColor = matchResult.groupValues[1]
            "<#$hexColor>"
        }
    }

    fun convertLegacyPrefixToMiniMessage(prefix: String): String {
        val legacyToMini = mapOf(
            "&0" to "<black>",
            "&1" to "<dark_blue>",
            "&2" to "<dark_green>",
            "&3" to "<dark_aqua>",
            "&4" to "<dark_red>",
            "&5" to "<dark_purple>",
            "&6" to "<gold>",
            "&7" to "<gray>",
            "&8" to "<dark_gray>",
            "&9" to "<blue>",
            "&a" to "<green>",
            "&b" to "<aqua>",
            "&c" to "<red>",
            "&d" to "<light_purple>",
            "&e" to "<yellow>",
            "&f" to "<white>",
            "&l" to "<b>",
            "&o" to "<i>",
            "&n" to "<u>",
            "&m" to "<st>",
            "&r" to "<reset>"
        )

        var formatted = prefix
        legacyToMini.forEach { (legacy, mini) ->
            formatted = formatted.replace(legacy, mini, ignoreCase = true)
        }

        return formatted
    }




}