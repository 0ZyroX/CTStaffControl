package ir.OZyroX.cTStaffControl.Events

import com.google.inject.Inject
import com.velocitypowered.api.proxy.Player
import org.slf4j.Logger
import org.yaml.snakeyaml.Yaml
import java.nio.file.Files
import java.nio.file.Paths
import java.io.File
import java.io.FileInputStream
import java.io.IOException
import java.io.InputStream
import java.io.OutputStream

class ConfigHandler @Inject constructor(){

    var staffchatformat : String = ""
    var adminchatformat : String = ""
    var devchatformat : String = ""
    var reload : String = ""
    var stafflistFormat : List<String> = listOf()
    var stafflistMessageFormat : String = ""
    var disabled : String = ""
    var staffchat : Boolean = true
    var devchat : Boolean = true
    var adminchat : Boolean = true
    var lognotify : Boolean = true
    var playeronly : String = ""

    var staffchatenable : String = ""
    var staffchatdiable : String = ""

    var adminchatenable : String = ""
    var adminchatdiable : String = ""

    var devchatenable : String = ""
    var devchatdiable : String = ""

    var switchalert : String = ""

    var discordenable : Boolean = true
    var discordmode : String = ""
    var discordbot : Map<String, Any> = mapOf()
    var discordbottoken : String = ""
    var discordbotguild : String = ""
    var discordbotchatlogChannel : String = ""
    var discordbotswitchlogChannel : String = ""
    var discordbotstatus : String = ""
    var discordbotactivity : Map<String, Any> = mapOf()
    var discordbotactivityEnable : Boolean = true
    var discordbotactivityType : String = ""
    var discordbotactivityMessage : String = ""

    var discordwebhook : Map<String, Any> = mapOf()
    var discordembed : Map<String, Any> = mapOf()
    var discordwebhookAvatar : String = ""
    var discordwebhookThumbnail : String = ""
    var discordwebhookImage : String = ""
    var discordwebhookUrl : String = ""
    var discordwebhookUsername : String = ""
    var discordwebhookFooter : String = ""
    var discordwebhookTag : String = ""

    var discorddisplay : Map<String, Any> = mapOf()
    var discorddisplayPlayername : String = ""
    var discorddisplayServer : String = ""

    var discordmodule : Map<String, Any> = mapOf()
    var discordmoduleChat : Boolean = true
    var discordmoduleSwitch : Boolean = true


    var switchTitle : String = ""
    var switchMessage : String = ""
    var chatlogTitle : String = ""
    var chatlogMessage : String = ""
    var stafflistTitle : String = ""
    var stafflistMessage : String = ""

    var discordStaffChatTitle : String = ""
    var discordStaffChatDMessage : String = ""
    var discordStaffChatMMessage : String = ""

    var discordDevChatTitle : String = ""
    var discordDevChatDMessage : String = ""
    var discordDevChatMMessage : String = ""

    var discordAdminChatTitle : String = ""
    var discordAdminChatDMessage : String = ""
    var discordAdminChatMMessage : String = ""

    var discordPermStaffList : String = ""
    var discordPermStaffChat : String = ""
    var discordPermDevChat : String = ""
    var discordPermAdminChat : String = ""


    fun createConfig(){
        copyDefaultConfig()
        copyLang()
        copyDiscord()
        loadLang()
        loadConfig()
        loadDiscord()
    }
    fun ReloadConfig(){
        loadConfig()
        loadLang()
    }

    fun copyDefaultConfig() {
        val configFile = File("plugins/CTStaffControl/config.yml")
        if (!configFile.exists()) {
            try {
                val resourceStream: InputStream = javaClass.classLoader.getResourceAsStream("config.yml")
                    ?: throw IOException("Resource config.yml not found")
                Files.createDirectories(Paths.get("plugins/CTStaffControl"))
                val outputStream: OutputStream = Files.newOutputStream(configFile.toPath())
                resourceStream.use { input ->
                    outputStream.use { output ->
                        input.copyTo(output)
                    }
                }
                println("Generating Config File")
            } catch (e: IOException) {
                e.printStackTrace()
                println("Failed to generate the config file!")
            }
        }
    }
    fun copyLang() {
        val configFile = File("plugins/CTStaffControl/lang.yml")
        if (!configFile.exists()) {
            try {
                val resourceStream: InputStream = javaClass.classLoader.getResourceAsStream("lang.yml")
                    ?: throw IOException("Resource lang.yml not found")
                Files.createDirectories(Paths.get("plugins/CTStaffControl"))
                val outputStream: OutputStream = Files.newOutputStream(configFile.toPath())
                resourceStream.use { input ->
                    outputStream.use { output ->
                        input.copyTo(output)
                    }
                }
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }
    }
    fun copyDiscord() {
        val configFile = File("plugins/CTStaffControl/discord.yml")
        if (!configFile.exists()) {
            try {
                val resourceStream: InputStream = javaClass.classLoader.getResourceAsStream("discord.yml")
                    ?: throw IOException("Resource discord.yml not found")
                Files.createDirectories(Paths.get("plugins/CTStaffControl"))
                val outputStream: OutputStream = Files.newOutputStream(configFile.toPath())
                resourceStream.use { input ->
                    outputStream.use { output ->
                        input.copyTo(output)
                    }
                }
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }
    }
    fun loadConfig() {
        val config = loadYamlFile("plugins/CTStaffControl/config.yml")

        if (config == null) {
            println("Failed to load config.yml!")
            return
        }

        staffchat = config["staffchat"] as? Boolean ?: true
        devchat = config["devchat"] as? Boolean ?: true
        adminchat = config["adminchat"] as? Boolean ?: true
        lognotify = config["log-notify"] as? Boolean ?: true
    }
    fun loadDiscord() {
        loadYamlFile("plugins/CTStaffControl/discord.yml") ?.let { discordData ->

            if (discordData == null) {
                println("Failed to load discord.yml!")
                return
            }

            val discord = discordData["discord"] as? Map<String, Any> ?: emptyMap()
            val permission = discordData["role-permission"] as? Map<String, String> ?: emptyMap()

            discordenable = discord["enable"] as? Boolean ?: true
            discordmode = discord["mode"] as? String ?: "BOT"
            discordbot = discord["bot"] as? Map<String, Any> ?: emptyMap()
            discordbottoken = discordbot["token"] as? String ?: ""
            discordbotguild = discordbot["guild"] as? String ?: ""
            discordbotchatlogChannel = discordbot["chatlog-channel"] as? String ?: ""
            discordbotswitchlogChannel = discordbot["switchlog-channel"] as? String ?: ""
            discordbotstatus = discordbot["status"] as? String ?: "ONLINE"

            discordbotactivity = discordbot["activity"] as? Map<String, Any> ?: emptyMap()
            discordbotactivityEnable = discordbotactivity["enable"] as? Boolean ?: false
            discordbotactivityType = discordbotactivity["type"] as? String ?: "WATCHING"
            discordbotactivityMessage = discordbotactivity["message"] as? String ?: "Watching {online} Player"

            discordwebhook = discord["webhook"] as? Map<String, Any> ?: emptyMap()
            discordembed = discord["embed"] as? Map<String, Any> ?: emptyMap()
            discordwebhookAvatar = discordwebhook["avatar"] as? String ?: "https://mc-heads.net/head/{uuid}"
            discordwebhookThumbnail = discordembed["thumbnail"] as? String ?: ""
            discordwebhookImage = discordembed["image"] as? String ?: ""
            discordwebhookUsername = discordwebhook["username"] as? String ?: "{player}"
            discordwebhookUrl = discordwebhook["url"] as? String ?: ""
            discordwebhookFooter = discordembed["footer"] as? String ?: "Powered By CTStaffControl"
            discordwebhookTag = discordembed["tag"] as? String ?: ""

            discorddisplay = discord["display"] as? Map<String, Any> ?: emptyMap()
            discorddisplayServer = discorddisplay["server"] as? String ?: "Discord"
            discorddisplayPlayername = discorddisplay["playername"] as? String ?: "NAME"

            discordmodule = discord["modules"] as? Map<String, Any> ?: emptyMap()
            discordmoduleChat = discordmodule["chatlog"] as? Boolean ?: true
            discordmoduleSwitch = discordmodule["switchlog"] as? Boolean ?: true

            discordmodule = discord["modules"] as? Map<String, Any> ?: emptyMap()
            discordmoduleChat = discordmodule["chatlog"] as? Boolean ?: true
            discordmoduleSwitch = discordmodule["switchlog"] as? Boolean ?: true

            discordPermStaffList = permission["stafflist"] as? String ?: ""
            discordPermStaffChat = permission["staffchat"] as? String ?: ""
            discordPermAdminChat = permission["adminchat"] as? String ?: ""
            discordPermDevChat = permission["devchat"] as? String ?: ""
        } ?: println("lang.yml not found!")

    }

    fun getPlayerGroupInfo(player: Player): Triple<String, String, Int> {
        val dbHandler = DBManager()
        val playerData = dbHandler.getPlayerDataByUUID(player.uniqueId.toString())

        return if (playerData != null) {
            Triple(playerData.rank, playerData.prefix, playerData.weight)
        } else {
            Triple("default", "<gray>Player", 0)
        }
    }




    fun loadLang() {
        loadYamlFile("plugins/CTStaffControl/lang.yml")?.let { langData ->

            val format = langData["Format"] as? Map<String, String> ?: emptyMap()
            val stafflistSection = langData["stafflist"] as? Map<String, Any> ?: emptyMap()
            val togglesection = langData["toggle"] as? Map<String, String> ?: emptyMap()

            val discord = langData["discord"] as? Map<String, Any> ?: emptyMap()
            val switchlog = discord["switchlog"] as? Map<String, Any> ?: emptyMap()
            val chatlog = discord["chatlog"] as? Map<String, Any> ?: emptyMap()
            val stafflist = discord["stafflist"] as? Map<String, Any> ?: emptyMap()
            val staffchat = discord["staffchat"] as? Map<String, Any> ?: emptyMap()
            val devchat = discord["devchat"] as? Map<String, Any> ?: emptyMap()
            val adminchat = discord["adminchat"] as? Map<String, Any> ?: emptyMap()

            switchTitle = switchlog["title"] as? String ?: "Staff Switch Alert"
            switchMessage = switchlog["message"] as? String ?: "{prefix} ``{playername}`` {oldServer} ➜ {newServer}"

            chatlogTitle = chatlog["title"] as? String ?: "Staff Chats Logger"
            chatlogMessage = chatlog["message"] as? String ?: "{prefix} ``{playername}`` [{server}] ➜ {message}"

            stafflistTitle = stafflist["title"] as? String ?: "Staff List"
            stafflistMessage = stafflist["message"] as? String ?: "{prefix} ``{playername}`` [{server}] {status} {lastOnline}"

            discordStaffChatTitle = staffchat["title"] as? String ?: "Staff Chat"
            discordStaffChatDMessage = staffchat["discord-message"] as? String ?: "**STAFFCHAT** ``{playername}`` [{server}] ➜ {message}"
            discordStaffChatMMessage = staffchat["mc-message"] as? String ?: "<dark_gray>[<b><gradient:#A32CC4:#CD47FF>ꜱᴛᴀꜰꜰᴄʜᴀᴛ</gradient></b><dark_gray>] <aqua>{playername} <gray>[<gold>{server}<gray>] <gray>➜ <white>{message}"

            discordDevChatTitle = devchat["title"] as? String ?: "Dev Chat"
            discordDevChatDMessage = devchat["discord-message"] as? String ?: "**DEVCHAT** ``{playername}`` [{server}] ➜ {message}"
            discordDevChatMMessage = devchat["mc-message"] as? String ?: "<dark_gray>[<b><gradient:#A32CC4:#CD47FF>ᴅᴇᴠᴄʜᴀᴛ</gradient></b><dark_gray>] <dark_gray>[<b><gradient:#A32CC4:#CD47FF>ᴅᴇᴠᴄʜᴀᴛ</gradient></b><dark_gray>] <aqua>{playername} <gray>[<gold>{server}<gray>] <gray>➜ <white>{message}"

            discordAdminChatTitle = adminchat["title"] as? String ?: "Admin Chat"
            discordAdminChatDMessage = adminchat["discord-message"] as? String ?: "**ADMINCHAT** ``{playername}`` [{server}] ➜ {message}"
            discordAdminChatMMessage = adminchat["mc-message"] as? String ?: "<aqua>{playername} <gray>[<gold>{server}<gray>] <gray>➜ <white>{message}"


            staffchatformat = format?.get("staffchat") ?: "<dark_gray>[<b><gradient:#A32CC4:#CD47FF>ꜱᴛᴀꜰꜰᴄʜᴀᴛ</gradient></b><dark_gray>] {prefix} <yellow>{playername} <dark_gray>[<gold>{server}<dark_gray>] <gray>➜ <white>{message}"
            adminchatformat = format?.get("adminchat") ?: "<dark_gray>[<b><gradient:#A32CC4:#CD47FF>ᴀᴅᴍɪɴᴄʜᴀᴛ</gradient></b><dark_gray>] {prefix} <yellow>{playername} <dark_gray>[<gold>{server}<dark_gray>] <gray>➜ <white>{message}"
            devchatformat = format?.get("devchat") ?: "<dark_gray>[<b><gradient:#A32CC4:#CD47FF>ᴅᴇᴠᴄʜᴀᴛ</gradient></b><dark_gray>] {prefix} <yellow>{playername} <dark_gray>[<gold>{server}<dark_gray>] <gray>➜ <white>{message}"
            reload = langData["reload"] as? String ?: "<gradient:#A32CC4:#CD47FF><b>ꜱᴛᴀꜰꜰᴄᴏɴᴛʀᴏʟ</b></gradient> <gradient:#00FF3B:#00C305>ᴄᴏɴꜰɪɢᴜʀᴀᴛɪᴏɴ ʀᴇʟᴏᴀᴅᴇᴅ ꜱᴜᴄᴄᴇꜱꜱꜰᴜʟʟʏ!</gradient>"
            switchalert = langData["switch-alert"] as? String ?: "<gray>[{prefix} <aqua>{playername} <red>{oldServer} <light_purple>➜ <green>{newServer}<gray>]"
            disabled = langData["disabled"] as? String ?: "<gradient:#FF1D1D:#FF3737>ᴛʜɪꜱ ᴄᴏᴍᴍᴀɴᴅ ʜᴀꜱ ʙᴇᴇɴ ᴅɪꜱᴀʙʟᴇᴅ ʙʏ ᴛʜᴇ ᴀᴅᴍɪɴɪꜱᴛʀᴀᴛᴏʀ</gradient>"
            playeronly = langData["player-only"] as? String ?: "<gradient:#FF1D1D:#FF3737>ᴏɴʟʏ ᴘʟᴀʏᴇʀꜱ ᴄᴀɴ ᴇxᴇᴄᴜᴛᴇ ᴛʜɪꜱ ᴄᴏᴍᴍᴀɴᴅ</gradient>"
            stafflistMessageFormat = stafflistSection["list_format"] as? String ?: "<#A32CC4>| {prefix} <white>{playername} <dark_gray>[<gold>{server}<dark_gray>] <b>{status}</b> <aqua>{lastOnline}"
            stafflistFormat = stafflistSection["format"] as? List<String> ?: listOf(
                "<dark_purple><st>              </st></dark_purple> <gradient:#A32CC4:#CD47FF>StaffList</gradient> <dark_purple><st>              </st></dark_purple>",
                "{list}",
                "<dark_purple><st>                                 </st>"
            )
            staffchatdiable = togglesection?.get("staffchat-disable") ?: "<gradient:#FF1D1D:#FF3737>ꜱᴛᴀꜰꜰᴄʜᴀᴛ ᴅɪꜱᴀʙʟᴇᴅ</gradient>"
            staffchatenable = togglesection?.get("staffchat-enable") ?: "<gradient:#00FF3B:#00C305>ꜱᴛᴀꜰꜰᴄʜᴀᴛ ᴇɴᴀʙʟᴇᴅ</gradient>"

            adminchatdiable = togglesection?.get("adminchat-disable") ?: "<gradient:#FF1D1D:#FF3737>ᴀᴅᴍɪɴᴄʜᴀᴛ ᴅɪꜱᴀʙʟᴇᴅ</gradient>"
            adminchatenable = togglesection?.get("adminchat-enable") ?: "<gradient:#00FF3B:#00C305>ᴀᴅᴍɪɴᴄʜᴀᴛ ᴇɴᴀʙʟᴇᴅ</gradient>"

            devchatdiable = togglesection?.get("devchat-disable") ?: "<gradient:#FF1D1D:#FF3737>ᴅᴇᴠᴄʜᴀᴛ ᴅɪꜱᴀʙʟᴇᴅ</gradient>"
            devchatenable = togglesection?.get("devchat-enable") ?: "<gradient:#00FF3B:#00C305>ᴅᴇᴠᴄʜᴀᴛ ᴇɴᴀʙʟᴇᴅ</gradient>"
        } ?: println("lang.yml not found!")
    }

    fun loadYamlFile(filePath: String): Map<String, Any>? {
        val yaml = Yaml()
        val file = File(filePath)
        return if (file.exists()) {
            try {
                FileInputStream(file).use { inputStream ->
                    yaml.load(inputStream) as? Map<String, Any>
                }
            } catch (e: IOException) {
                e.printStackTrace()
                null
            }
        } else {
            null
        }
    }
}