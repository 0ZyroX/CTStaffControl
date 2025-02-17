package ir.OZyroX.cTStaffControl.Discord.Bot

import com.google.inject.Inject
import com.velocitypowered.api.proxy.ProxyServer
import ir.OZyroX.cTStaffControl.Events.CheckStatus
import ir.OZyroX.cTStaffControl.Events.ConfigHandler
import ir.OZyroX.cTStaffControl.Events.DBManager
import ir.OZyroX.cTStaffControl.Events.Staff
import net.dv8tion.jda.api.EmbedBuilder
import net.dv8tion.jda.api.JDA
import net.dv8tion.jda.api.JDABuilder
import net.dv8tion.jda.api.entities.Activity
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent
import net.dv8tion.jda.api.events.message.MessageReceivedEvent
import net.dv8tion.jda.api.hooks.ListenerAdapter
import net.dv8tion.jda.api.interactions.commands.OptionType
import net.dv8tion.jda.api.interactions.commands.build.Commands
import net.dv8tion.jda.api.interactions.commands.build.SlashCommandData
import net.dv8tion.jda.api.requests.GatewayIntent
import net.kyori.adventure.text.minimessage.MiniMessage
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer
import java.time.Instant
import java.util.*
import javax.security.auth.login.LoginException

class Bot @Inject constructor(val config: ConfigHandler, val proxy: ProxyServer) : ListenerAdapter() {

    companion object {
        var jda: JDA? = null
    }


    var players = proxy.allPlayers.count().toString()

    override fun onSlashCommandInteraction(event: SlashCommandInteractionEvent) {
        val slashcommand = SlashCommands(proxy, config)
        slashcommand.onSlashCommandStaffList(event)
        slashcommand.onSlashCommandStaffChat(event)
        slashcommand.onSlashCommandAdminChat(event)
        slashcommand.onSlashCommandDevChat(event)
    }

    fun startBot() {
        val token = config.discordbottoken
        if (token.isNullOrBlank()) {
            println("❌ Bot token is missing or invalid in config!")
            return
        }



        try {
            jda = JDABuilder.createDefault(token)
                .enableIntents(GatewayIntent.GUILD_MESSAGES, GatewayIntent.MESSAGE_CONTENT)
                .setActivity(getUpdatedActivity())
                .addEventListeners(this)
                .build()
                .awaitReady()

            val guild = jda!!.getGuildById(config.discordbotguild)

            guild?.updateCommands()?.addCommands(
                Commands.slash("stafflist", "Get Your StaffList On Minecraft Server"),
                Commands.slash("staffchat", "Send Message In Staff Chat")
                   .addOption(OptionType.STRING, "message", "Message For Send", true),
                Commands.slash("adminchat", "Send Message In Admin Chat")
                    .addOption(OptionType.STRING, "message", "Message For Send", true),
                Commands.slash("devchat", "Send Message In Dev Chat")
                    .addOption(OptionType.STRING, "message", "Message For Send", true),
            )?.queue {
                println("✅ Slash commands have been registered successfully!")
            }


            println("✅ Discord bot is ready!")

        } catch (e: Exception) {
            println("❌ Failed to start the bot: ${e.message}")
            e.printStackTrace()
        }
    }

    fun updateActivity() {
        jda?.presence?.activity = getUpdatedActivity()
    }

    private fun getUpdatedActivity(): Activity {
        val players = proxy.allPlayers.count().toString()
        val activityMessage = config.discordbotactivityMessage.replace("{online}", players)

        return when (config.discordbotactivityType.lowercase()) {
            "playing" -> Activity.playing(activityMessage)
            "watching" -> Activity.watching(activityMessage)
            "listening" -> Activity.listening(activityMessage)
            else -> Activity.playing(activityMessage)
        }
    }


    fun stopBot() {
        jda?.shutdownNow()
    }

    fun sendEmbedChatLog(
        title: String,
        description: String,
        footer: String,
        thumbnail: String,
        image: String,
        systemname: String
    ) {
        if (jda == null) {
            println("❌ JDA instance is null! Bot might not be initialized.")
            return
        }

        val channelId = config.discordbotchatlogChannel
        if (channelId.isNullOrBlank()) {
            println("❌ chatlog-channel ID is missing or invalid in config!")
            return
        }

        val channel: TextChannel? = jda?.getTextChannelById(channelId)
        if (channel == null) {
            println("❌ Channel Not Found On Discord (check discord.yml chatlog-channel: $channelId)")
            return
        }
        var tag = config.discordwebhookTag
        var finalTag = ""

        when (tag) {
            "HERE" -> { finalTag = "@here" }
            "EVERYONE" -> { finalTag = "@everyone" }
            else -> {
                val isValidRoleId = tag.matches(Regex("\\d+")) && tag.length >= 16
                if (isValidRoleId) {
                    finalTag = "<@&$tag>"
                } else {
                    finalTag = "."
                }
            }
        }

        if (config.discordwebhookImage.isNotBlank() && config.discordwebhookImage.isNotBlank()) {
            val embed = EmbedBuilder()
                .setTitle(title)
                .setDescription("$systemname $description")
                .setFooter(footer, null)
                .setTimestamp(java.time.Instant.now())
                .setThumbnail(thumbnail)
                .setImage(image)
                .build()

            channel.sendMessage("$finalTag").addEmbeds(embed).queue()
        } else if (config.discordwebhookImage.isNotBlank()) {
            val embed = EmbedBuilder()
                .setTitle(title)
                .setDescription("$systemname $description")
                .setFooter(footer, null)
                .setTimestamp(java.time.Instant.now())
                .setImage(image)
                .build()

            channel.sendMessage("$finalTag").addEmbeds(embed).queue()
        } else if (config.discordwebhookThumbnail.isNotBlank()) {
            val embed = EmbedBuilder()
                .setTitle(title)
                .setDescription("$systemname $description")
                .setFooter(footer, null)
                .setTimestamp(java.time.Instant.now())
                .setThumbnail(thumbnail)
                .build()

            channel.sendMessage("$finalTag").addEmbeds(embed).queue()
        } else {
            val embed = EmbedBuilder()
                .setTitle(title)
                .setDescription("$systemname $description")
                .setFooter(footer, null)
                .setTimestamp(java.time.Instant.now())
                .build()

            channel.sendMessage("$finalTag").addEmbeds(embed).queue()
        }


    }
    fun sendEmbedSwitchLog(
        title: String,
        description: String,
        footer: String,
        thumbnail: String,
        image: String
    ) {
        if (jda == null) {
            println("❌ JDA instance is null! Bot might not be initialized.")
            return
        }

        val channelId = config.discordbotchatlogChannel
        if (channelId.isNullOrBlank()) {
            println("❌ chatlog-channel ID is missing or invalid in config!")
            return
        }



        val channel: TextChannel? = jda?.getTextChannelById(channelId)
        if (channel == null) {
            println("❌ Channel Not Found On Discord (check discord.yml chatlog-channel: $channelId)")
            return
        }
        var tag = config.discordwebhookTag
        var finalTag = ""

        when (tag) {
            "HERE" -> { finalTag = "@here" }
            "EVERYONE" -> { finalTag = "@everyone" }
            else -> {
                val isValidRoleId = tag.matches(Regex("\\d+")) && tag.length >= 16
                if (isValidRoleId) {
                    finalTag = "<@&$tag>"
                } else {
                    finalTag = ""
                }
            }
        }

        if (config.discordwebhookImage.isNotBlank() && config.discordwebhookImage.isNotBlank()) {
            val embed = EmbedBuilder()
                .setTitle(title)
                .setDescription(description)
                .setFooter(footer, null)
                .setTimestamp(java.time.Instant.now())
                .setThumbnail(thumbnail)
                .setImage(image)
                .build()

            channel.sendMessage("$finalTag").addEmbeds(embed).queue()

        } else if (config.discordwebhookImage.isNotBlank()) {
            val embed = EmbedBuilder()
                .setTitle(title)
                .setDescription(description)
                .setFooter(footer, null)
                .setTimestamp(java.time.Instant.now())
                .setImage(image)
                .build()

            channel.sendMessage("$finalTag").addEmbeds(embed).queue()
        } else if (config.discordwebhookThumbnail.isNotBlank()) {
            val embed = EmbedBuilder()
                .setTitle(title)
                .setDescription(description)
                .setFooter(footer, null)
                .setTimestamp(java.time.Instant.now())
                .setThumbnail(thumbnail)
                .build()
            channel.sendMessage("$finalTag").addEmbeds(embed).queue()
        } else {
            val embed = EmbedBuilder()
                .setTitle(title)
                .setDescription(description)
                .setFooter(footer, null)
                .setTimestamp(java.time.Instant.now())
                .build()

            channel.sendMessage("$finalTag").addEmbeds(embed).queue()
        }


    }
}
