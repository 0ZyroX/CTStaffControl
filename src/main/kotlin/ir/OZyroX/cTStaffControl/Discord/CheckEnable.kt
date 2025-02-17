package ir.OZyroX.cTStaffControl.Discord

import com.google.inject.Inject
import ir.OZyroX.cTStaffControl.Events.ConfigHandler

class CheckEnable @Inject constructor(val configHandler: ConfigHandler)  {


    fun check(): Boolean {

        if (configHandler.discordenable) {
            var mode = configHandler.discordmode
            if (mode == "BOT" || mode == "WEBHOOK") {
                return true
            } else {
                return false
            }
        } else {
            return false
        }
    }
}
