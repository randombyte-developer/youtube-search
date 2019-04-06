package de.randombyte.youtubesearch.config

import ninja.leaping.configurate.objectmapping.Setting
import ninja.leaping.configurate.objectmapping.serialize.ConfigSerializable

@ConfigSerializable
class TextsConfig(
        @Setting("searching") val searching: String = "&eSearching...",
        @Setting("results-header") val resultsHeader: String = "&a========= Results for &6\"${Placeholders.QUERY}\" &a=========",
        @Setting("result-representation") val resultRepresentation: String = "- &e\"${Placeholders.TITLE}\" ",
        @Setting("watch-button") val watchButton: String = "&b[WATCH]",
        @Setting("title-max-characters") val titleMaxCharacters: Int = 60
) {
    object Placeholders {
        val QUERY = "query".asPlaceholder
        val TITLE = "title".asPlaceholder

        private val String.asPlaceholder get() = "%$this%"
    }
}