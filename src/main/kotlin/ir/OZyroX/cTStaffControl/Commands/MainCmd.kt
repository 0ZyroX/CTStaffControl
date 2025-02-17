package ir.OZyroX.cTStaffControl.Commands

import com.google.inject.Inject
import com.mojang.brigadier.Command
import com.mojang.brigadier.arguments.StringArgumentType
import com.mojang.brigadier.context.CommandContext
import com.mojang.brigadier.tree.LiteralCommandNode
import com.velocitypowered.api.command.BrigadierCommand
import com.velocitypowered.api.command.CommandSource
import com.velocitypowered.api.proxy.ProxyServer
import ir.OZyroX.cTStaffControl.CTStaffControl
import ir.OZyroX.cTStaffControl.Discord.Bot.Bot
import ir.OZyroX.cTStaffControl.Discord.CheckEnable
import ir.OZyroX.cTStaffControl.Events.ConfigHandler
import net.kyori.adventure.text.minimessage.MiniMessage
import org.slf4j.Logger

class MainCmd @Inject constructor(
    val proxy: ProxyServer,
    private val configHandler: ConfigHandler
) {

    fun createMainCommand(): BrigadierCommand {
        val mainCmd: LiteralCommandNode<CommandSource> = BrigadierCommand.literalArgumentBuilder("ctstaffcontrol")
            .executes { context ->
                val source = context.source
                val info = CTStaffControl(proxy).pluginInfo()
                val message = MiniMessage.miniMessage().deserialize("${info}")
                source.sendMessage(message)
                Command.SINGLE_SUCCESS
            }
            .then(
                BrigadierCommand.literalArgumentBuilder("reload")
                    .requires { s -> s.hasPermission("ctstaffcontrol.reload") }
                    .executes { context: CommandContext<CommandSource> ->

                        val bot = Bot(configHandler, proxy)
                        val check = CheckEnable(configHandler).check()
                        val formattedMessage = configHandler.reload
                        val source = context.source
                        configHandler.ReloadConfig()
                        source.sendMessage(MiniMessage.miniMessage().deserialize(formattedMessage))
                        Command.SINGLE_SUCCESS
                    }
            )
            .then(
                BrigadierCommand.literalArgumentBuilder("reloaddiscord")
                    .requires { s -> s.hasPermission("ctstaffcontrol.reloaddiscord") }
                    .executes { context: CommandContext<CommandSource> ->

                        val bot = Bot(configHandler, proxy)
                        val check = CheckEnable(configHandler).check()
                        val formattedMessage = configHandler.reload
                        val source = context.source
                        configHandler.ReloadConfig()
                        source.sendMessage(MiniMessage.miniMessage().deserialize(formattedMessage))
                        if (check){
                            if (configHandler.discordmode == "BOT"){
                                bot.stopBot()
                                bot.startBot()
                                configHandler.loadDiscord()
                            }
                        }
                        Command.SINGLE_SUCCESS
                    }
            )
            .then(
                BrigadierCommand.literalArgumentBuilder("toggle")
                    .requires { s -> s.hasPermission("ctstaffcontrol.toggle") }
                    .executes { ctx ->
                        val source = ctx.source
                        source.sendMessage(MiniMessage.miniMessage().deserialize("<red>Usage: /ctstaffcontrol toggle <staffchat/adminchat/devchat>"))
                        Command.SINGLE_SUCCESS
                    }
                    .then(
                        BrigadierCommand.requiredArgumentBuilder("module", StringArgumentType.word())
                            .suggests { ctx, builder ->
                                builder.suggest("staffchat")
                                builder.suggest("adminchat")
                                builder.suggest("devchat")
                                builder.buildFuture()
                            }
                            .executes { ctx ->
                                val enteredToggle = ctx.getArgument("module", String::class.java)
                                val source = ctx.source
                                when (enteredToggle.toLowerCase()) {
                                    "staffchat" -> {
                                        if (configHandler.staffchat) {
                                            configHandler.staffchat = false
                                            source.sendMessage(
                                                MiniMessage.miniMessage().deserialize(configHandler.staffchatdiable)
                                            )
                                            return@executes Command.SINGLE_SUCCESS
                                        } else
                                            configHandler.staffchat = true
                                        source.sendMessage(
                                            MiniMessage.miniMessage().deserialize(configHandler.staffchatenable)
                                        )
                                        return@executes Command.SINGLE_SUCCESS
                                    }
                                    "devchat" -> {
                                        if (configHandler.devchat) {
                                            configHandler.devchat = false
                                            source.sendMessage(
                                                MiniMessage.miniMessage().deserialize(configHandler.devchatdiable)
                                            )
                                            return@executes Command.SINGLE_SUCCESS
                                        } else
                                            configHandler.devchat = true
                                        source.sendMessage(
                                            MiniMessage.miniMessage().deserialize(configHandler.devchatenable)
                                        )
                                        return@executes Command.SINGLE_SUCCESS
                                    }
                                    "adminchat" -> {
                                        if (configHandler.adminchat) {
                                            configHandler.adminchat = false
                                            source.sendMessage(
                                                MiniMessage.miniMessage().deserialize(configHandler.adminchatdiable)
                                            )
                                            return@executes Command.SINGLE_SUCCESS
                                        } else
                                            configHandler.adminchat = true
                                        source.sendMessage(
                                            MiniMessage.miniMessage().deserialize(configHandler.adminchatenable)
                                        )
                                        return@executes Command.SINGLE_SUCCESS
                                    }

                                    else -> {
                                        source.sendMessage(MiniMessage.miniMessage().deserialize("<red>Usage: /ctstaffcontrol toggle <staffchat/adminchat/devchat>"))
                                        Command.SINGLE_SUCCESS
                                    }
                                }
                            }
                    )
            )
            .build()

        return BrigadierCommand(mainCmd)
    }
}