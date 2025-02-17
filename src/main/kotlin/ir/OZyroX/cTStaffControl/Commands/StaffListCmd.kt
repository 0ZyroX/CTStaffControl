package ir.OZyroX.cTStaffControl.Commands

import com.google.inject.Inject
import com.mojang.brigadier.Command
import com.mojang.brigadier.tree.LiteralCommandNode
import com.velocitypowered.api.command.BrigadierCommand
import com.velocitypowered.api.command.CommandSource
import com.velocitypowered.api.proxy.ProxyServer
import ir.OZyroX.cTStaffControl.Events.CheckStatus
import ir.OZyroX.cTStaffControl.Events.ConfigHandler
import ir.OZyroX.cTStaffControl.Events.DBManager
import ir.OZyroX.cTStaffControl.Events.Staff
import net.kyori.adventure.text.minimessage.MiniMessage
import org.slf4j.Logger
import java.util.*

class StaffListCmd @Inject constructor(
    val proxy: ProxyServer,
    private val configHandler: ConfigHandler
) {
    val db = DBManager()
    val staffs = db.readData()
    val status = CheckStatus(proxy)
    fun createStaffListCommand(): BrigadierCommand {
        val staffListCmd: LiteralCommandNode<CommandSource> = BrigadierCommand.literalArgumentBuilder("stafflist")
            .requires { s -> s.hasPermission("ctstaffcontrol.stafflist") }
            .executes { context ->
                val source = context.source
                val staffList = DBManager().getStaffList()
                val checkStatus = CheckStatus(proxy)

                val sortedStaffList = staffList.sortedWith(compareByDescending<Staff> {
                    checkStatus.getPlayerStatus(it.uuid) == "<green>ONLINE"
                }.thenByDescending { it.weight })

                val messageList = mutableListOf<String>()
                sortedStaffList.forEach { staff ->
                    val status = checkStatus.getPlayerStatus(staff.name)

                    val serverName = if (status == "<green>ONLINE") {
                        proxy.getPlayer(UUID.fromString(staff.uuid)).flatMap { it.currentServer }.map { it.serverInfo.name }.orElse("")
                    } else {
                        ""
                    }

                    val lastOnline = if (status != "<green>ONLINE") {
                        val lastOnlineTime = db.getLastOnlineTime(staff.uuid)
                        lastOnlineTime ?: "Never"
                    } else {
                        ""
                    }

                    val stafflistMessageFormat = configHandler.stafflistMessageFormat
                    val message = stafflistMessageFormat
                        .replace("{prefix}", staff.prefix)
                        .replace("{playername}", staff.name)
                        .replace("{server}", serverName)
                        .replace("{status}", status)
                        .replace("{lastOnline}", lastOnline)

                    messageList.add(message)
                }

                val stafflistFormat= configHandler.stafflistFormat
                val finalMessage = stafflistFormat.joinToString("\n") { line ->
                    line.replace("{list}", messageList.joinToString("\n"))
                }

                source.sendMessage(MiniMessage.miniMessage().deserialize(finalMessage))
                Command.SINGLE_SUCCESS
            }
            .build()

        return BrigadierCommand(staffListCmd)
    }
}