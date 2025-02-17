package ir.OZyroX.cTStaffControl.Events

import com.google.inject.Inject;
import com.velocitypowered.api.command.BrigadierCommand;
import com.velocitypowered.api.proxy.ProxyServer;
import ir.OZyroX.cTStaffControl.CTStaffControl
import ir.OZyroX.cTStaffControl.Commands.*
import org.slf4j.Logger

class CommandHandler @Inject constructor(
    val proxy: ProxyServer,
    private val plugin: CTStaffControl,
    private val configHandler: ConfigHandler
) {
    init {
        val commandManager = proxy.commandManager

        registerCommand("staffchat", StaffChatCmd(proxy, configHandler).createStaffChatCommand(),"sc", "schat")
        registerCommand("adminchat", AdminChatCmd(proxy, configHandler).createAdminChatCommand(),"ac", "achat")
        registerCommand("devchat", DevChatCmd(proxy, configHandler).createDevChatCommand(),"dc", "dchat")
        registerCommand("ctstaffcontrol", MainCmd(proxy, configHandler).createMainCommand(),"staffcontrol", "scontrol")
        registerCommand("stafflist", StaffListCmd(proxy, configHandler).createStaffListCommand(),"slist", "sl")
    }

    private fun registerCommand(command: String, cmd: BrigadierCommand, alias1: String, alias2: String) {
        val commandMeta = proxy.commandManager.metaBuilder(command)
            .aliases(alias1, alias2)
            .plugin(plugin)
            .build()
        proxy.commandManager.register(commandMeta, cmd)
    }
}
