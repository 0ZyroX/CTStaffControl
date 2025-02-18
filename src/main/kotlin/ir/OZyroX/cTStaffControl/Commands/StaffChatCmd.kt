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
import ir.OZyroX.cTStaffControl.Discord.Webhook.ChatLogWebHook
import ir.OZyroX.cTStaffControl.Events.ConfigHandler
import ir.OZyroX.cTStaffControl.Events.LogHandler
import ir.OZyroX.cTStaffControl.Events.LuckpermsHandler
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.NamedTextColor
import net.kyori.adventure.text.minimessage.MiniMessage
import org.slf4j.Logger

class StaffChatCmd @Inject constructor(
    val proxy: ProxyServer,
    private val configHandler: ConfigHandler
) {
    fun createStaffChatCommand(): BrigadierCommand {
        val staffChatCmd: LiteralCommandNode<CommandSource> = BrigadierCommand.literalArgumentBuilder("staffchat")
            .requires { s -> s.hasPermission("ctstaffcontrol.staffchat.use") }
            .executes { context ->
                val source = context.source
                val message = Component.text("Usage: /staffchat <message>", NamedTextColor.RED)
                source.sendMessage(message)
                Command.SINGLE_SUCCESS
            }
            .then(
                BrigadierCommand.requiredArgumentBuilder("message", StringArgumentType.greedyString())
                    .suggests { ctx, builder ->
                        builder.suggest("<message>")
                        builder.buildFuture()
                    }
                    .executes { context ->
                        val sender = context.source
                        if (!configHandler.staffchat) {
                            sender.sendMessage(MiniMessage.miniMessage().deserialize(configHandler.disabled))
                            return@executes Command.SINGLE_SUCCESS
                        }
                        if (sender !is Player) {
                            sender.sendMessage(MiniMessage.miniMessage().deserialize(configHandler.playeronly))
                            return@executes Command.SINGLE_SUCCESS
                        }
                        val serverName = sender.currentServer.map { it.serverInfo.name }.orElse("Unknown")
                        val msg = context.getArgument("message", String::class.java)
                        val check = CheckEnable(configHandler).check()
                        val luckperm = LuckpermsHandler().getLuckPermsPrefixAndRank(sender) { prefix,group  ->

                            val formattedMessage = MiniMessage.miniMessage().deserialize(
                                configHandler.staffchatformat
                                    .replace("{server}", serverName)
                                    .replace("{playername}", sender.username)
                                    .replace("{message}", msg)
                                    .replace("{prefix}", prefix)
                                    .replace("{group}", group)
                            )


                            if (check) {
                                if (configHandler.discordmoduleChat && configHandler.discordmode == "WEBHOOK")
                                    LogHandler(configHandler, proxy).finalWebhook(
                                        serverName,
                                        sender.username,
                                        msg,
                                        prefix,
                                        group,
                                        sender.uniqueId.toString(),
                                        "STAFFCHAT"
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
                                if (player.hasPermission("ctstaffcontrol.staffchat.view")) {
                                    player.sendMessage(formattedMessage)
                                }
                            }
                        }
                        Command.SINGLE_SUCCESS
                    }
            )
            .build()

        return BrigadierCommand(staffChatCmd)
    }
}