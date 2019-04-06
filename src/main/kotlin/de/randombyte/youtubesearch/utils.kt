package de.randombyte.youtubesearch

import de.randombyte.youtubesearch.config.GeneralConfig
import de.randombyte.youtubesearch.config.TextsConfig
import org.slf4j.Logger

val generalConfig: GeneralConfig get() = YoutubeSearch.INSTANCE.configAccessor.general.get()
val texts: TextsConfig get() = YoutubeSearch.INSTANCE.configAccessor.texts.get()
val logger: Logger get() = YoutubeSearch.INSTANCE.logger