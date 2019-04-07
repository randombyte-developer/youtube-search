package de.randombyte.youtubesearch.commands

import de.randombyte.kosp.extensions.*
import de.randombyte.youtubesearch.YoutubeApi
import de.randombyte.youtubesearch.YoutubeSearch
import de.randombyte.youtubesearch.config.TextsConfig.Placeholders
import de.randombyte.youtubesearch.generalConfig
import de.randombyte.youtubesearch.texts
import org.apache.commons.lang3.StringEscapeUtils
import org.spongepowered.api.command.CommandResult
import org.spongepowered.api.command.CommandSource
import org.spongepowered.api.command.args.CommandContext
import org.spongepowered.api.command.spec.CommandExecutor
import org.spongepowered.api.scheduler.Task
import org.spongepowered.api.text.action.TextActions
import java.net.URL

class SearchCommand : CommandExecutor {
    override fun execute(src: CommandSource, args: CommandContext): CommandResult {
        val target = args.getOne<CommandSource>(YoutubeSearch.PLAYER_ARG).orElse(src)
        val query = args.getOne<String>(YoutubeSearch.QUERY_ARG).get()

        val isOtherTarget = args.hasAny(YoutubeSearch.PLAYER_ARG)
        if (isOtherTarget) src.sendMessage("Sending query results to ${target.name}!".green())

        var foundKeyword = false
        generalConfig.interceptedKeywords.forEach { keywordBundle ->
            if (keywordBundle.keywords.any { query.contains(it, ignoreCase = true) }) {
                foundKeyword = true
                keywordBundle.messages.forEach { target.sendMessage(it.deserialize()) }
            }
        }
        if (foundKeyword) return CommandResult.success()

        target.sendMessage(texts.searching.deserialize())
        Task.builder()
                .async()
                .execute { task ->
                    val maxItems = generalConfig.resultsCount.coerceIn(1..25)
                    val finalQuery = "${generalConfig.queryPrefix} $query".trim()
                    val response = YoutubeApi.searchBlocking(finalQuery, maxItems)

                    target.sendMessage(texts.resultsHeader.replace(Placeholders.QUERY to finalQuery).deserialize())

                    response.items.take(maxItems).forEach { item ->
                        val title = StringEscapeUtils.unescapeHtml4(item.snippet.title).let { title ->
                            if (title.length > texts.titleMaxCharacters) {
                                title.limit(texts.titleMaxCharacters) + "…"
                            } else title
                        }
                        val url = URL("https://www.youtube.com/watch?v=" + item.id.videoId)

                        val titleText = texts.resultRepresentation.replace(Placeholders.TITLE to title).deserialize()
                        val watchButton = texts.watchButton.deserialize().action(TextActions.openUrl(url))

                        target.sendMessage(titleText + watchButton)
                    }
                }
                .submit(YoutubeSearch.INSTANCE)

        return CommandResult.success()
    }
}