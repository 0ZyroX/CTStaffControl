package ir.OZyroX.cTStaffControl.Commands

import com.google.inject.Inject
import com.mojang.brigadier.Command
import com.mojang.brigadier.arguments.StringArgumentType
import com.mojang.brigadier.tree.LiteralCommandNode
import com.velocitypowered.api.command.BrigadierCommand
import com.velocitypowered.api.command.CommandSource
import com.velocitypowered.api.proxy.Player
import com.velocitypowered.api.proxy.ProxyServer
import ir.OZyroX.cTStaffControl.Discord.CheckEnable
import ir.OZyroX.cTStaffControl.Events.ConfigHandler
import ir.OZyroX.cTStaffControl.Events.LogHandler
import ir.OZyroX.cTStaffControl.Events.LuckpermsHandler
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.NamedTextColor
import net.kyori.adventure.text.minimessage.MiniMessage

class AdminChatCmd @Inject constructor(
    val proxy: ProxyServer,
    private val configHandler: ConfigHandler
) {
    fun createAdminChatCommand(): BrigadierCommand {
        val adminChatCmd: LiteralCommandNode<CommandSource> = BrigadierCommand.literalArgumentBuilder("adminchat")
            .requires { s -> s.hasPermission("ctstaffcontrol.adminchat.use") }
            .then(
                BrigadierCommand.requiredArgumentBuilder("message", StringArgumentType.greedyString())
                    .suggests { ctx, builder ->
                        builder.suggest("<message>")
                        builder.buildFuture()
                    }
                    .executes { context ->
                        val sender = context.source
                        if (!configHandler.adminchat) {
                            sender.sendMessage(MiniMessage.miniMessage().deserialize(configHandler.disabled))
                            return@executes Command.SINGLE_SUCCESS
                        }
                        if (sender !is Player) {
                            sender.sendMessage(MiniMessage.miniMessage().deserialize(configHandler.playeronly))
                            return@executes Command.SINGLE_SUCCESS
                        }
                        val serverName = sender.currentServer.map { it.serverInfo.name }.orElse("Unknown")
                        val msg = context.getArgument("message", String::class.java)
                        val luckperm = LuckpermsHandler().getLuckPermsPrefixAndRank(sender) { prefix, group ->

                            val formattedMessage = MiniMessage.miniMessage().deserialize(
                                configHandler.adminchatformat
                                    .replace("{server}", serverName)
                                    .replace("{playername}", sender.username)
                                    .replace("{message}", msg)
                                    .replace("{prefix}", prefix)
                                    .replace("{group}", group)
                            )

                            val check = CheckEnable(configHandler).check()
                            if (check) {
                                if (configHandler.discordmoduleChat && configHandler.discordmode == "WEBHOOK")
                                    LogHandler(configHandler, proxy).finalWebhook(
                                        serverName,
                                        sender.username,
                                        msg,
                                        prefix,
                                        group,
                                        sender.uniqueId.toString(),
                                        "ADMINCHAT"
                                    )
                                else if (configHandler.discordmoduleChat && configHandler.discordmode == "BOT")
                                    LogHandler(configHandler, proxy).finalWebhookBot(
                                        serverName,
                                        sender.username,
                                        msg,
                                        prefix,
                                        group,
                                        sender.uniqueId.toString(),
                                        "STAFFCHAT"
                                    )
                            }

                            proxy.allPlayers.forEach { player ->
                                if (player.hasPermission("ctstaffcontrol.adminchat.view")) {
                                    player.sendMessage(formattedMessage)
                                }
                            }
                        }
                        Command.SINGLE_SUCCESS
                    }
            )
            .build()

        return BrigadierCommand(adminChatCmd)
    }
}