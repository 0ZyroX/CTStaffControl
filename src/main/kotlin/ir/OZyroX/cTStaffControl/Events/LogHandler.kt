package ir.OZyroX.cTStaffControl.Events

import com.google.common.eventbus.EventBus
import com.google.inject.Inject
import com.velocitypowered.api.proxy.ProxyServer
import ir.OZyroX.cTStaffControl.Discord.Bot.Bot
import ir.OZyroX.cTStaffControl.Discord.Webhook.ChatLogWebHook
import ir.OZyroX.cTStaffControl.Discord.Webhook.SwitchLogWebHook
import net.kyori.adventure.text.minimessage.MiniMessage
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer

class LogHandler @Inject constructor(val config : ConfigHandler, val proxy : ProxyServer) {

    val chatlogmsg = config.chatlogMessage
    val username = config.discordwebhookUsername
    val avatar = config.discordwebhookAvatar
    val title = config.chatlogTitle
    val titleS = config.switchTitle
    val footer = config.discordwebhookFooter
    val thumbnail = config.discordwebhookThumbnail
    val image = config.discordwebhookImage
    val switchlogmsg = config.switchMessage

    fun finalWebhook(serverName: String, username: String, msg: String, prefix: String, group: String, uuid: String, systemname: String){


        val discordmsg = chatLog(serverName, username, msg, prefix, group)
        val usern = username(serverName, username, msg, prefix, group, uuid)
        val avatar = avatar(serverName, username, msg, prefix, group, uuid)
        val title = title(serverName, username, msg, prefix, group)
        val footer = footer(serverName, username, msg, prefix, group)
        val thumbnail = thumbnail(serverName, username, msg, prefix, group, uuid)
        val image = image(serverName, username, msg, prefix, group, uuid)

        ChatLogWebHook(config).sendChatLogWebHook(systemname, discordmsg, usern, avatar, title, footer, thumbnail, image)
    }

    fun finalWebhookBot(serverName: String, username: String, msg: String, prefix: String, group: String, uuid: String, systemname: String){


        val discordmsg = chatLog(serverName, username, msg, prefix, group)
        val usern = username(serverName, username, msg, prefix, group, uuid)
        val avatar = avatar(serverName, username, msg, prefix, group, uuid)
        val title = title(serverName, username, msg, prefix, group)
        val footer = footer(serverName, username, msg, prefix, group)
        val thumbnail = thumbnail(serverName, username, msg, prefix, group, uuid)
        val image = image(serverName, username, msg, prefix, group, uuid)

        Bot(config, proxy).sendEmbedChatLog(title, discordmsg, footer, thumbnail, image, systemname)
    }

    fun finalBotSwitch(NewServerName: String, OlderverName: String, username: String, msg: String, prefix: String, group: String, uuid: String){


        val discordmsg = switchLog(NewServerName,OlderverName, username, msg, prefix, group, uuid)
        val title = titleS(NewServerName, username, msg, prefix, group)
        val footer = footer(NewServerName, username, msg, prefix, group)
        val thumbnail = thumbnail(NewServerName, username, msg, prefix, group, uuid)
        val image = image(NewServerName, username, msg, prefix, group, uuid)

        Bot(config, proxy).sendEmbedSwitchLog(title, discordmsg, footer, thumbnail, image)
    }

    fun finalWebhookSwitch(NewServerName: String, OlderverName: String, username: String, msg: String, prefix: String, group: String, uuid: String){


        val discordmsg = switchLog(NewServerName,OlderverName, username, msg, prefix, group, uuid)
        val usern = username(NewServerName, username, msg, prefix, group, uuid)
        val avatar = avatar(NewServerName, username, msg, prefix, group, uuid)
        val title = titleS(NewServerName, username, msg, prefix, group)
        val footer = footer(NewServerName, username, msg, prefix, group)
        val thumbnail = thumbnail(NewServerName, username, msg, prefix, group, uuid)
        val image = image(NewServerName, username, msg, prefix, group, uuid)

        SwitchLogWebHook(config).sendSwitchLogWebHook(discordmsg, usern, avatar, title, footer, thumbnail, image)
    }

    fun finalWebhookDc(NewServerName: String, username: String, msg: String, prefix: String, group: String, uuid: String){


        val discordmsg = dcLog(NewServerName,username, msg, prefix, group, uuid)
        val usern = username(NewServerName, username, msg, prefix, group, uuid)
        val avatar = avatar(NewServerName, username, msg, prefix, group, uuid)
        val title = titleS(NewServerName, username, msg, prefix, group)
        val footer = footer(NewServerName, username, msg, prefix, group)
        val thumbnail = thumbnail(NewServerName, username, msg, prefix, group, uuid)
        val image = image(NewServerName, username, msg, prefix, group, uuid)

        SwitchLogWebHook(config).sendSwitchLogWebHook(discordmsg, usern, avatar, title, footer, thumbnail, image)
    }


    fun finalprefix (prefix: String): String {
        val parsedComponent = try {
            MiniMessage.miniMessage().deserialize(prefix)
        } catch (e: Exception) {
            println("❌ Error in prefix format: ${e.message}")
            return prefix
        }
        return PlainTextComponentSerializer.plainText().serialize(parsedComponent)
    }

    fun chatLog (serverName: String, sender: String, message: String, prefix: String, group: String): String {



        var finalmsg = chatlogmsg
            .replace("{server}", serverName)
            .replace("{playername}", sender)
            .replace("{prefix}", finalprefix(prefix))
            .replace("{message}", message)
            .replace("{group}", group)
        return finalmsg
    }

    fun switchLog (NewServerName: String, OlderverName: String, username: String, msg: String, prefix: String, group: String, uuid: String): String {

        var finalmsg = switchlogmsg
            .replace("{oldServer}", OlderverName)
            .replace("{newServer}", NewServerName)
            .replace("{playername}", username)
            .replace("{prefix}", finalprefix(prefix))
            .replace("{message}", msg)
            .replace("{group}", group)
            .replace("{uuid}", uuid)
        return finalmsg
    }

    fun dcLog (OlderverName: String, username: String, msg: String, prefix: String, group: String, uuid: String): String {

        var finalmsg = switchlogmsg
            .replace("{oldServer}", OlderverName)
            .replace("{newServer}", "❌")
            .replace("{playername}", username)
            .replace("{prefix}", finalprefix(prefix))
            .replace("{message}", msg)
            .replace("{group}", group)
            .replace("{uuid}", uuid)
        return finalmsg
    }

    fun username (serverName: String, sender: String, message: String, prefix: String, group: String, uuid: String): String {

        var finalmsg = username
            .replace("{server}", serverName)
            .replace("{playername}", sender)
            .replace("{prefix}", finalprefix(prefix))
            .replace("{message}", message)
            .replace("{group}", group)
            .replace("{uuid}", uuid)
        return finalmsg
    }
    fun avatar (serverName: String, sender: String, message: String, prefix: String, group: String, uuid: String): String {

        var finalmsg = avatar
            .replace("{server}", serverName)
            .replace("{playername}", sender)
            .replace("{prefix}", finalprefix(prefix))
            .replace("{message}", message)
            .replace("{group}", group)
            .replace("{uuid}", uuid)
        return finalmsg
    }
    fun title (serverName: String, sender: String, message: String, prefix: String, group: String): String {

        var finalmsg = title
            .replace("{server}", serverName)
            .replace("{playername}", sender)
            .replace("{prefix}", finalprefix(prefix))
            .replace("{message}", message)
            .replace("{group}", group)
        return finalmsg
    }

    fun titleS (serverName: String, sender: String, message: String, prefix: String, group: String): String {

        var finalmsg = titleS
            .replace("{server}", serverName)
            .replace("{playername}", sender)
            .replace("{prefix}", finalprefix(prefix))
            .replace("{message}", message)
            .replace("{group}", group)
        return finalmsg
    }
    fun footer (serverName: String, sender: String, message: String, prefix: String, group: String): String {

        var finalmsg = footer
            .replace("{server}", serverName)
            .replace("{playername}", sender)
            .replace("{prefix}", finalprefix(prefix))
            .replace("{message}", message)
            .replace("{group}", group)
        return finalmsg
    }
    fun thumbnail (serverName: String, sender: String, message: String, prefix: String, group: String, uuid : String): String {

        var finalmsg = thumbnail
            .replace("{server}", serverName)
            .replace("{playername}", sender)
            .replace("{prefix}", finalprefix(prefix))
            .replace("{message}", message)
            .replace("{group}", group)
            .replace("{uuid}", uuid)
        return finalmsg
    }
    fun image (serverName: String, sender: String, message: String, prefix: String, group: String, uuid : String): String {

        var finalmsg = image
            .replace("{server}", serverName)
            .replace("{playername}", sender)
            .replace("{prefix}", finalprefix(prefix))
            .replace("{message}", message)
            .replace("{group}", group)
            .replace("{uuid}", uuid)
        return finalmsg
    }

}