package com.finalprojectteam11.noteworthy.data

import android.content.Intent
import kotlinx.serialization.Transient
import com.google.gson.JsonDeserializationContext
import com.google.gson.JsonDeserializer
import com.google.gson.JsonElement
import java.lang.reflect.Type

data class AssistAction(
    val title: String = "",
    val description: String = "",
    val response: String = "",
    var category: String = "None",
    val url: String = "",
    val term : String = "",
    val location: String = "",
    val hour: Int = 0,
    val minute: Int = 0,
    @Transient
    var intent: Intent? = null
)

class AssistActionDeserializer : JsonDeserializer<AssistAction> {
    override fun deserialize(
        json: JsonElement?,
        typeOfT: Type?,
        context: JsonDeserializationContext?
    ): AssistAction {
        val jsonObject = json?.asJsonObject

        val title = jsonObject?.get("title")?.asString ?: ""
        val description = jsonObject?.get("description")?.asString ?: ""
        val response = jsonObject?.get("response")?.asString ?: ""
        val category = jsonObject?.get("category")?.asString ?: "None"
        val url = jsonObject?.get("url")?.asString ?: ""
        val term = jsonObject?.get("term")?.asString ?: ""
        val location = jsonObject?.get("location")?.asString ?: ""
        val hour = jsonObject?.get("hour")?.asIntOrNull() ?: 0
        val minute = jsonObject?.get("minute")?.asIntOrNull() ?: 0

        return AssistAction(title, description, response, category, url, term, location, hour, minute)
    }
}

private fun JsonElement.asIntOrNull(): Int? = when {
    isJsonPrimitive && asJsonPrimitive.isNumber -> asInt
    isJsonPrimitive && asJsonPrimitive.isString -> asString.toIntOrNull()
    else -> null
}