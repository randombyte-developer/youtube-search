package de.randombyte.youtubesearch

import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport
import com.google.api.client.json.jackson2.JacksonFactory
import com.google.api.services.youtube.YouTube
import com.google.api.services.youtube.model.SearchListResponse

object YoutubeApi {

    val service = lazy {
        YouTube.Builder(
                GoogleNetHttpTransport.newTrustedTransport(),
                JacksonFactory.getDefaultInstance(),
                null
        ).setApplicationName("YoutubeSearch Minecraft Sponge plugin").build()
    }

    fun searchBlocking(query: String, maxItems: Int): SearchListResponse {
        val request = service.value.search().list("snippet")
        return request
                .setKey(generalConfig.apiKey)
                .setType("video")
                .setQ(query)
                .setMaxResults(maxItems.toLong())
                .execute()
    }
}