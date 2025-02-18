package ir.OZyroX.cTStaffControl.Listeners

import com.google.inject.Inject
import com.velocitypowered.api.event.Subscribe
import com.velocitypowered.api.event.connection.DisconnectEvent
import com.velocitypowered.api.proxy.ProxyServer
import ir.OZyroX.cTStaffControl.Discord.CheckEnable
import ir.OZyroX.cTStaffControl.Events.ConfigHandler
import ir.OZyroX.cTStaffControl.Events.DBManager
import ir.OZyroX.cTStaffControl.Events.LogHandler
import net.kyori.adventure.text.minimessage.MiniMessage
import org.slf4j.Logger
import java.time.LocalDateTime
import kotlin.math.log

class DisconnectListener @Inject constructor(val proxy: ProxyServer, private val configHandler: ConfigHandler) {

    private var db = DBManager()

    @Subscribe
    fun onPlayerDisconnect(event: DisconnectEvent) {
        val player = event.player
        val (group, prefix, weight) = configHandler.getPlayerGroupInfo(player)


        if (player.hasPermission("ctstaffcontrol.staff")) {
            val oldServerName = db.getOldServer(player.uniqueId.toString())
            proxy.allPlayers.forEach { p ->
                if (p.hasPermission("ctstaffcontrol.notify")) {

                    val message = MiniMessage.miniMessage().deserialize(
                        configHandler.switchalert
                            .replace("{playername}", player.username)
                            .replace("{prefix}", prefix)
                            .replace("{oldServer}", oldServerName ?: "")
                            .replace("{newServer}", "<red>❌")
                    )
                    p.sendMessage(message)
                }
            }

            db.updateLastOnline(player.uniqueId.toString())
            val check = CheckEnable(configHandler).check()
            if (check) {
                if (configHandler.discordmoduleSwitch && configHandler.discordmode == "WEBHOOK") {
                    LogHandler(configHandler, proxy).finalWebhookDc(
                        oldServerName ?: "",
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


}