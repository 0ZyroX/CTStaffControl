package ir.OZyroX.cTStaffControl.Discord.Webhook

import com.google.inject.Inject
import ir.OZyroX.cTStaffControl.Discord.Webhook.ChatLogWebHook.Companion
import ir.OZyroX.cTStaffControl.Events.ConfigHandler
import okhttp3.*
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.IOException

class SwitchLogWebHook @Inject constructor(val config : ConfigHandler){
    companion object {
        private val client = OkHttpClient()
    }


    val url = config.discordswitchlogwebhookUrl
    val tag = config.discordwebhookTag

    fun sendSwitchLogWebHook(message: String, username : String, avatar : String, title: String, footer : String, thumbnail: String,Image: String){
        if (url.isNullOrBlank()) {
            println("❌ Webhook URL is not set!")
            return
        }

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
        var json = """
            {
                "username": "$username",
                "avatar_url": "$avatar",
                "content": "$finalTag",
                  "embeds": [
                    {
                        "title": "$title",
                        "description": "$message",
                        "footer": {
                          "text": "$footer"
                        },
                        "thumbnail": {
                          "url": "$thumbnail"
                        },
                        "image": {
                          "url": "$Image"
                        }
                    }
                ]
            }      
             """.trimIndent()

        val body = json.toRequestBody("application/json".toMediaTypeOrNull())

        val request = Request.Builder()
            .url(url)
            .post(body)
            .build()

        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                println("Failed to send webhook message: ${e.message}")
            }

            override fun onResponse(call: Call, response: Response) {
                response.close()
            }

        })

    }
}