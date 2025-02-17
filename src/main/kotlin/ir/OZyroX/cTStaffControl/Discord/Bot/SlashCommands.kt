package ir.OZyroX.cTStaffControl.Discord.Bot

import com.velocitypowered.api.proxy.ProxyServer
import ir.OZyroX.cTStaffControl.Events.CheckStatus
import ir.OZyroX.cTStaffControl.Events.ConfigHandler
import ir.OZyroX.cTStaffControl.Events.DBManager
import ir.OZyroX.cTStaffControl.Events.Staff
import net.dv8tion.jda.api.EmbedBuilder
import net.dv8tion.jda.api.Permission
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent
import net.kyori.adventure.text.minimessage.MiniMessage
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer
import java.time.Instant
import java.util.*
import javax.inject.Inject

class SlashCommands @Inject constructor(var proxy: ProxyServer, var config: ConfigHandler) {
    fun onSlashCommandStaffChat(event: SlashCommandInteractionEvent) {
        if (event.name.lowercase() != "staffchat") return

        val message = event.getOption("message")?.asString ?: "No message provided"

        val member = event.member ?: run {
            event.deferReply(true).queue { hook ->
                hook.editOriginal("❌ Member data not found!").queue()
            }
            return
        }

        val perm = config.discordPermStaffChat
        val hasRole = member.roles.any { it.id == perm }
        val isadmin = member.hasPermission(Permission.ADMINISTRATOR)

        if (hasRole || isadmin) {
            event.deferReply().queue { hook ->

                if (config.staffchat) {
                    val username = when (config.discorddisplayPlayername) {
                        "NAME" -> member.effectiveName.ifEmpty { event.user.name }
                        "USERNAME" -> event.user.name
                        else -> "UNKNOWN"
                    }

                    val formattedMessage = config.discordStaffChatDMessage
                        .replace("{playername}", username)
                        .replace("{server}", config.discorddisplayServer)
                        .replace("{message}", message)

                    val embed = EmbedBuilder()
                        .setTitle(config.discordStaffChatTitle)
                        .setFooter(config.discordwebhookFooter)
                        .setDescription(formattedMessage)
                        .setTimestamp(Instant.now())

                    val formattedMessageMC = MiniMessage.miniMessage().deserialize(
                        config.discordStaffChatMMessage
                            .replace("{playername}", username)
                            .replace("{server}", config.discorddisplayServer)
                            .replace("{message}", message)
                    )

                    hook.editOriginalEmbeds(embed.build()).queue()

                    proxy.allPlayers.forEach { player ->
                        if (player.hasPermission("ctstaffcontrol.staffchat.view")) {
                            player.sendMessage(formattedMessageMC)
                        }
                    }
                } else {
                    val embed = EmbedBuilder()
                        .setTitle(config.discordAdminChatTitle)
                        .setFooter(config.discordwebhookFooter)
                        .setDescription("STAFFCHAT not enabled")
                        .setTimestamp(Instant.now())

                    hook.editOriginalEmbeds(embed.build()).queue()
                }
            }
        } else {
            event.deferReply(true).queue { hook ->
                hook.editOriginal("❌ You don't have permission to use this command!").queue()
            }
        }
    }


    fun onSlashCommandAdminChat(event: SlashCommandInteractionEvent) {
        if (event.name.lowercase() != "adminchat") return

        val message = event.getOption("message")?.asString ?: "No message provided"

        val member = event.member ?: run {
            event.deferReply(true).queue { hook ->
                hook.editOriginal("❌ Member data not found!").queue()
            }
            return
        }

        val perm = config.discordPermAdminChat
        val hasRole = member.roles.any { it.id == perm }
        val isadmin = member.hasPermission(Permission.ADMINISTRATOR)

        if (hasRole || isadmin) {
            event.deferReply().queue { hook ->

                if (config.adminchat) {
                    val username = when (config.discorddisplayPlayername) {
                        "NAME" -> member.effectiveName.ifEmpty { event.user.name }
                        "USERNAME" -> event.user.name
                        else -> "UNKNOWN"
                    }

                    val formattedMessage = config.discordAdminChatDMessage
                        .replace("{playername}", username)
                        .replace("{server}", config.discorddisplayServer)
                        .replace("{message}", message)

                    val embed = EmbedBuilder()
                        .setTitle(config.discordAdminChatTitle)
                        .setFooter(config.discordwebhookFooter)
                        .setDescription(formattedMessage)
                        .setTimestamp(Instant.now())

                    val formattedMessageMC = MiniMessage.miniMessage().deserialize(
                        config.discordAdminChatMMessage
                            .replace("{playername}", username)
                            .replace("{server}", config.discorddisplayServer)
                            .replace("{message}", message)
                    )

                    hook.editOriginalEmbeds(embed.build()).queue()

                    proxy.allPlayers.forEach { player ->
                        if (player.hasPermission("ctstaffcontrol.adminchat.view")) {
                            player.sendMessage(formattedMessageMC)
                        }
                    }
                } else {
                    val embed = EmbedBuilder()
                        .setTitle(config.discordAdminChatTitle)
                        .setFooter(config.discordwebhookFooter)
                        .setDescription("ADMINCHAT not enabled")
                        .setTimestamp(Instant.now())

                    hook.editOriginalEmbeds(embed.build()).queue()
                }
            }
        } else {
            event.deferReply(true).queue { hook ->
                hook.editOriginal("❌ You don't have permission to use this command!").queue()
            }
        }
    }

    fun onSlashCommandDevChat(event: SlashCommandInteractionEvent) {
        if (event.name.lowercase() != "devchat") return

        val message = event.getOption("message")?.asString ?: "No message provided"

        val member = event.member ?: run {
            event.deferReply(true).queue { hook ->
                hook.editOriginal("❌ Member data not found!").queue()
            }
            return
        }

        val perm = config.discordPermDevChat
        val hasRole = member.roles.any { it.id == perm }
        val isadmin = member.hasPermission(Permission.ADMINISTRATOR)

        if (hasRole || isadmin) {
            event.deferReply().queue { hook ->

                if (config.devchat) {
                    val username = when (config.discorddisplayPlayername) {
                        "NAME" -> member.effectiveName.ifEmpty { event.user.name }
                        "USERNAME" -> event.user.name
                        else -> "UNKNOWN"
                    }

                    val formattedMessage = config.discordDevChatDMessage
                        .replace("{playername}", username)
                        .replace("{server}", config.discorddisplayServer)
                        .replace("{message}", message)

                    val embed = EmbedBuilder()
                        .setTitle(config.discordDevChatTitle)
                        .setFooter(config.discordwebhookFooter)
                        .setDescription(formattedMessage)
                        .setTimestamp(Instant.now())

                    val formattedMessageMC = MiniMessage.miniMessage().deserialize(
                        config.discordDevChatMMessage
                            .replace("{playername}", username)
                            .replace("{server}", config.discorddisplayServer)
                            .replace("{message}", message)
                    )

                    hook.editOriginalEmbeds(embed.build()).queue()

                    proxy.allPlayers.forEach { player ->
                        if (player.hasPermission("ctstaffcontrol.devchat.view")) {
                            player.sendMessage(formattedMessageMC)
                        }
                    }
                } else {
                    val embed = EmbedBuilder()
                        .setTitle(config.discordDevChatTitle)
                        .setFooter(config.discordwebhookFooter)
                        .setDescription("DEVCHAT not enabled")
                        .setTimestamp(Instant.now())

                    hook.editOriginalEmbeds(embed.build()).queue()
                }
            }
        } else {
            event.deferReply(true).queue { hook ->
                hook.editOriginal("❌ You don't have permission to use this command!").queue()
            }
        }
    }


    fun onSlashCommandStaffList(event: SlashCommandInteractionEvent) {
        if (event.name.lowercase() != "stafflist") return

        val staffList = DBManager().getStaffList()
        val checkStatus = CheckStatus(proxy)


        val perm = config.discordPermStaffList
        val hasRole = event.member?.roles?.any { it.id == perm } ?: false
        val isadmin = event.member?.hasPermission(Permission.ADMINISTRATOR) ?: false
        if (hasRole || isadmin) {

            if (staffList.isEmpty()) {
                event.reply("🚫 No staff members found!").setEphemeral(true).queue()
                return
            }

            val sortedStaffList = staffList.sortedWith(
                compareByDescending<Staff> { checkStatus.getPlayerStatus(it.uuid) == "ONLINE" }
                    .thenByDescending { it.weight }
            )

            event.deferReply().queue { hook ->
            val embed = EmbedBuilder()
                .setTitle(config.stafflistTitle)
                .setFooter(config.discordwebhookFooter)
                .setTimestamp(Instant.now())

            val descriptionList = mutableListOf<String>()

            sortedStaffList.forEach { staff ->
                val status = checkStatus.getPlayerStatus(staff.name)
                val serverName = if (status == "<green>ONLINE") {
                    proxy.getPlayer(UUID.fromString(staff.uuid))
                        .flatMap { it.currentServer }
                        .map { it.serverInfo.name }
                        .orElse("Unknown")
                } else {
                    "none"
                }

                val lastOnline = if (status != "<green>ONLINE") {
                    val lastOnline = DBManager().getLastOnlineTime(staff.uuid)
                    lastOnline ?: "Never"
                } else {
                    ""
                }

                val formattedMessage = MiniMessage.miniMessage().deserialize(
                    config.stafflistMessage
                        .replace("{prefix}", staff.prefix)
                        .replace("{playername}", staff.name)
                        .replace("{server}", serverName)
                        .replace("{status}", if (status == "<green>ONLINE") "🟢 Online" else "🔴 Offline")
                        .replace("{lastOnline}", lastOnline)
                )


                descriptionList.add(PlainTextComponentSerializer.plainText().serialize(formattedMessage))
            }

            embed.setDescription(descriptionList.joinToString("\n"))

            hook.editOriginalEmbeds(embed.build()).queue()
        }}
        else {
            event.reply("You Don't Have Permission For Use This Command").setEphemeral(true).queue()
        }
    }
}