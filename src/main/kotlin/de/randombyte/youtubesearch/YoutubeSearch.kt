package de.randombyte.youtubesearch

import com.google.inject.Inject
import de.randombyte.kosp.extensions.getPlayer
import de.randombyte.kosp.extensions.sendTo
import de.randombyte.kosp.extensions.toText
import de.randombyte.youtubesearch.YoutubeSearch.Companion.AUTHOR
import de.randombyte.youtubesearch.YoutubeSearch.Companion.ID
import de.randombyte.youtubesearch.YoutubeSearch.Companion.NAME
import de.randombyte.youtubesearch.YoutubeSearch.Companion.VERSION
import de.randombyte.youtubesearch.commands.SearchCommand
import de.randombyte.youtubesearch.config.ConfigAccessor
import org.apache.commons.lang3.RandomUtils
import org.bstats.sponge.Metrics2
import org.slf4j.Logger
import org.spongepowered.api.Sponge
import org.spongepowered.api.command.args.GenericArguments.*
import org.spongepowered.api.command.spec.CommandSpec
import org.spongepowered.api.config.ConfigDir
import org.spongepowered.api.entity.living.player.Player
import org.spongepowered.api.event.Listener
import org.spongepowered.api.event.game.GameReloadEvent
import org.spongepowered.api.event.game.state.GameInitializationEvent
import org.spongepowered.api.event.network.ClientConnectionEvent
import org.spongepowered.api.plugin.Plugin
import org.spongepowered.api.scheduler.Task
import java.nio.file.Path
import java.util.*
import java.util.concurrent.TimeUnit

@Plugin(id = ID,
        name = NAME,
        version = VERSION,
        authors = [AUTHOR])
class YoutubeSearch @Inject constructor(
        val logger: Logger,
        @ConfigDir(sharedRoot = false) configPath: Path,
        private val metrics: Metrics2
) {
    companion object {
        const val ID = "youtube-search"
        const val NAME = "YoutubeSearch"
        const val VERSION = "1.0.0"
        const val AUTHOR = "RandomByte"

        const val ROOT_PERMISSION = ID

        const val QUERY_ARG = "query"
        const val PLAYER_ARG = "player"

        private val _INSTANCE = lazy { Sponge.getPluginManager().getPlugin(ID).get().instance.get() as YoutubeSearch }
        val INSTANCE: YoutubeSearch get() = _INSTANCE.value
    }

    val configAccessor = ConfigAccessor(configPath)

    @Listener
    fun onInit(event: GameInitializationEvent) {
        configAccessor.reloadAll()
        registerCommands()

        if (needsMotivationalSpeech()) {
            Task.builder()
                    .delay(RandomUtils.nextLong(80, 130), TimeUnit.SECONDS)
                    .execute { -> Messages.motivationalSpeech.forEach { it.sendTo(Sponge.getServer().console) } }
                    .submit(this)
        }

        logger.info("Loaded $NAME: $VERSION")
    }

    @Listener
    fun onReload(event: GameReloadEvent) {
        configAccessor.reloadAll()

        logger.info("Reloaded!")
    }

    private fun registerCommands() {
        Sponge.getCommandManager().register(this, CommandSpec.builder()
                .permission("$ROOT_PERMISSION.search.self")
                .arguments(
                        optionalWeak(requiringPermission(player(PLAYER_ARG.toText()), "$ROOT_PERMISSION.search.others")),
                        remainingRawJoinedStrings(QUERY_ARG.toText())
                )
                .executor(SearchCommand())
                .build(), "yotubesearch", "search", "yts")
    }

    private val metricsNoteSent = mutableSetOf<UUID>()

    @Listener
    fun onPlayerJoin(event: ClientConnectionEvent.Join) {
        val uuid = event.targetEntity.uniqueId
        if (needsMotivationalSpeech(event.targetEntity)) {
            Task.builder()
                    .delay(RandomUtils.nextLong(10, 50), TimeUnit.SECONDS)
                    .execute { ->
                        val player = uuid.getPlayer() ?: return@execute
                        metricsNoteSent += uuid
                        Messages.motivationalSpeech.forEach { it.sendTo(player) }
                    }
                    .submit(this)
        }
    }

    private fun needsMotivationalSpeech(player: Player? = null) = configAccessor.general.get().enableMetricsMessages &&
            !Sponge.getMetricsConfigManager().areMetricsEnabled(this) &&
            (player == null || player.uniqueId !in metricsNoteSent && player.hasPermission("nucleus.mute.base")) // also passes OPs without Nucleus
}