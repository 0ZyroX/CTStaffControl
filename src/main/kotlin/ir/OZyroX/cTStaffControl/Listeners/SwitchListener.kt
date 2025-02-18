package ir.OZyroX.cTStaffControl.Listeners

import com.google.inject.Inject
import com.velocitypowered.api.event.Subscribe
import com.velocitypowered.api.event.player.ServerConnectedEvent
import com.velocitypowered.api.proxy.Player
import com.velocitypowered.api.proxy.ProxyServer
import ir.OZyroX.cTStaffControl.Discord.CheckEnable
import ir.OZyroX.cTStaffControl.Events.ConfigHandler
import ir.OZyroX.cTStaffControl.Events.DBManager
import ir.OZyroX.cTStaffControl.Events.LogHandler
import net.kyori.adventure.text.minimessage.MiniMessage
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer
import net.luckperms.api.LuckPermsProvider
import net.luckperms.api.model.user.User
import net.luckperms.api.query.QueryOptions
import org.slf4j.Logger
import java.util.concurrent.CompletableFuture

class SwitchListener @Inject constructor(
    val proxy: ProxyServer,
    private val configHandler: ConfigHandler
) {

    val playerServer = mutableMapOf<Player, String>()
    val db = DBManager()


    @Subscribe
    fun onPlayerServerSwitch(event: ServerConnectedEvent) {
        val player = event.player
        val newServerName = event.server.serverInfo.name
        val (group, prefix, weight) = configHandler.getPlayerGroupInfo(player)


        val previousServerName = event.previousServer.map { it.serverInfo.name }.orElse("")
        try {
            db.updateOldServer(player.uniqueId.toString(), newServerName)
        } catch (e: Exception) {
            println(e.message)
        }


        if (configHandler.lognotify) {
            if (player.hasPermission("ctstaffcontrol.staff")) {
                proxy.allPlayers.forEach { p ->
                    if (p.hasPermission("ctstaffcontrol.notify")) {


                        val fix = fix(configHandler.switchalert)
                        val message = MiniMessage.miniMessage().deserialize(
                            fix
                                .replace("{playername}", player.username)
                                .replace("{prefix}", prefix)
                                .replace("{group}", group)
                                .replace("{oldServer}", previousServerName)
                                .replace("{newServer}", newServerName)
                        )

                        p.sendMessage(message)
                        playerServer[player] = newServerName

                    }
                }
            }
        }

        val check = CheckEnable(configHandler).check()
        if (check) {
            if (player.hasPermission("ctstaffcontrol.staff")) {
                if (configHandler.discordmoduleSwitch && configHandler.discordmode == "WEBHOOK") {
                    LogHandler(configHandler, proxy).finalWebhookSwitch(
                        newServerName,
                        previousServerName,
                        player.username,
                        configHandler.switchMessage,
                        prefix,
                        group,
                        player.uniqueId.toString()
                    )
                } else if (configHandler.discordmoduleSwitch && configHandler.discordmode == "BOT") {
                    LogHandler(configHandler, proxy).finalBotSwitch(
                        newServerName,
                        previousServerName,
                        player.username,
                        configHandler.switchMessage,
                        prefix,
                        group,
                        player.uniqueId.toString()
                    )
                }
            }
        }
    }

    fun fix(input: String): String {
        return input.trim().replace(Regex("\\s+"), " ")
    }

}