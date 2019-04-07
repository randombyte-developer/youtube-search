package de.randombyte.youtubesearch.config

import ninja.leaping.configurate.objectmapping.Setting
import ninja.leaping.configurate.objectmapping.serialize.ConfigSerializable

@ConfigSerializable
class GeneralConfig (
        @Setting("enable-metrics-messages", comment =
                "Since you are already editing configs, how about enabling metrics for at least this plugin? ;)\n" +
                "Go to the 'config/sponge/global.conf', scroll to the 'metrics' section and enable metrics.\n" +
                "Anonymous metrics data collection enables the developer to see how many people and servers are using this plugin.\n" +
                "Seeing that my plugin is being used is a big factor in motivating me to provide future support and updates.\n" +
                "If you really don't want to enable metrics and don't want to receive any messages anymore, you can disable this config option :("
        ) val enableMetricsMessages: Boolean = true,

        @Setting("api-key") val apiKey: String = "",
        @Setting("results-count") val resultsCount: Int = 3,
        @Setting("query-prefix", comment = "This will be put in front of every query done by users") val queryPrefix: String = "",
        @Setting("intercepted-keywords") val interceptedKeywords: List<KeywordBundle> = listOf(
                KeywordBundle(
                        keywords = listOf("torch"),
                        messages = listOf("Torches can be used to light up a certain area.")
                )
        )
) {
    @ConfigSerializable
    class KeywordBundle(
            @Setting("keywords") val keywords: List<String> = emptyList(),
            @Setting("messages") val messages: List<String> = emptyList()
    )
}