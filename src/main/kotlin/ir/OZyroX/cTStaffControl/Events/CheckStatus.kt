package ir.OZyroX.cTStaffControl.Events

import com.google.inject.Inject
import com.velocitypowered.api.proxy.ProxyServer

class CheckStatus @Inject constructor(private val proxy: ProxyServer){
    fun getPlayerStatus(uuid: String): String {
        val player = proxy.getPlayer(uuid)

        if (player.isPresent) {
            return if (player.get().isActive) "<green>ONLINE" else "<red>OFFLINE"
        } else {
            return "<red>OFFLINE"
        }
    }

}