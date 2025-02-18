package ir.OZyroX.cTStaffControl.Listeners

import com.google.inject.Inject
import com.velocitypowered.api.event.Subscribe
import com.velocitypowered.api.event.connection.LoginEvent
import ir.OZyroX.cTStaffControl.Events.ConfigHandler
import ir.OZyroX.cTStaffControl.Events.DBManager
import ir.OZyroX.cTStaffControl.Events.LuckpermsHandler
import net.luckperms.api.LuckPermsProvider
import org.slf4j.Logger
import java.time.LocalDateTime

class LoginListener {

    val configHandler = ConfigHandler()

    @Subscribe
    fun onLogin(event: LoginEvent) {
        val dbHandler = DBManager()
        val p = event.player
        val uuid = event.player.uniqueId
        val name = event.player.username
        val luckpermsHandler = LuckpermsHandler()

        var playerGroup = "default"
        var prefix = ""
        var weight = 0

        luckpermsHandler.getLuckPermsPrefixAndRank(p) { formattedPrefix, group ->
            prefix = formattedPrefix
            playerGroup = group

            val luckPerms = LuckPermsProvider.get()
            val groupManager = luckPerms.groupManager
            val lpGroup = groupManager.getGroup(group)
            weight = lpGroup?.weight?.orElse(0) ?: 0

            if (!p.hasPermission("ctstaffcontrol.staff"))
                dbHandler.deleteStaff(uuid.toString())


            if (p.hasPermission("ctstaffcontrol.staff") && playerGroup != "default") {
                dbHandler.insertData(name, uuid.toString(), playerGroup, prefix, weight)
                dbHandler.updateLastOnline(uuid.toString())
            }
        }


    }

}