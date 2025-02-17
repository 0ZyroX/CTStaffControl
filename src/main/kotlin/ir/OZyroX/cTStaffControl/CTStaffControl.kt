package ir.OZyroX.cTStaffControl;

import com.google.inject.Inject
import com.velocitypowered.api.event.Subscribe
import com.velocitypowered.api.event.proxy.ProxyInitializeEvent
import com.velocitypowered.api.plugin.Plugin
import com.velocitypowered.api.plugin.PluginContainer
import com.velocitypowered.api.proxy.ProxyServer
import com.velocitypowered.api.scheduler.ScheduledTask
import ir.OZyroX.cTStaffControl.Discord.Bot.Bot
import ir.OZyroX.cTStaffControl.Discord.CheckEnable
import ir.OZyroX.cTStaffControl.Events.CommandHandler
import ir.OZyroX.cTStaffControl.Events.ConfigHandler
import ir.OZyroX.cTStaffControl.Events.DBManager
import ir.OZyroX.cTStaffControl.Listeners.DisconnectListener
import ir.OZyroX.cTStaffControl.Listeners.LoginListener
import ir.OZyroX.cTStaffControl.Listeners.SwitchListener
import java.time.Duration
import java.util.concurrent.TimeUnit
import java.util.concurrent.atomic.AtomicInteger


@Plugin(
    id = "ctstaffcontrol",
    name = "CTStaffControl",
    version = BuildConstants.VERSION,
    url = "www.craft-tech.xyz",
    authors = ["OZyroX"]
)

class CTStaffControl @Inject constructor(private val proxy: ProxyServer) {

    private lateinit var commandHandler: CommandHandler
    private lateinit var configHandler: ConfigHandler
    private lateinit var dbManager: DBManager
    lateinit var pluginContainer: PluginContainer


    @Subscribe
    fun onProxyInitialization(event: ProxyInitializeEvent) {

        val configHandler = ConfigHandler()
        DBManager().runDB()
        configHandler.createConfig()
        val bot = Bot(configHandler, proxy)
        val check = CheckEnable(configHandler).check()
        if (check) {
            if (configHandler.discordmode == "BOT") {
                bot.startBot()
            }
        }

        proxy.scheduler.buildTask(this, Runnable {
            if (configHandler.discordmode == "BOT" && configHandler.discordenable) {
                bot.updateActivity()
            }
        }).repeat(1, TimeUnit.MINUTES).schedule()

        commandHandler = CommandHandler(proxy, this, configHandler)
        commandHandler = CommandHandler(proxy, this, configHandler)
        proxy.eventManager.register(this, LoginListener())
        proxy.eventManager.register(this, SwitchListener(proxy, configHandler))
        proxy.eventManager.register(this, DisconnectListener(proxy, configHandler))
    }

    fun pluginInfo(): String {
        var name = "CTStaffControl"
        var version = BuildConstants.VERSION
        var authors = "OZyroX"
        return "<aqua>$name <yellow>$version <white>By <aqua>$authors"
    }

}