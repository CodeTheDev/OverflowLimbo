package dev.codeerror.overflow;

import dev.codeerror.overflow.util.OverflowUtils;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.minestom.server.MinecraftServer;
import net.minestom.server.coordinate.Pos;
import net.minestom.server.entity.GameMode;
import net.minestom.server.entity.Player;
import net.minestom.server.entity.PlayerSkin;
import net.minestom.server.event.GlobalEventHandler;
import net.minestom.server.event.player.*;
import net.minestom.server.event.server.ServerListPingEvent;
import net.minestom.server.extras.velocity.VelocityProxy;
import net.minestom.server.instance.InstanceContainer;
import net.minestom.server.instance.block.Block;
import net.minestom.server.ping.ResponseData;
import net.minestom.server.timer.Scheduler;
import net.minestom.server.timer.TaskSchedule;
import net.minestom.server.utils.NamespaceID;
import net.minestom.server.world.DimensionType;
import net.minestom.server.world.DimensionTypeManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.management.ManagementFactory;

public class OverflowLimbo {

    public static void main(String[] args) {

        Logger logger = LoggerFactory.getLogger(OverflowLimbo.class);
        logger.info("Starting OverflowLimbo server...");

        // Load Configuration
        OverflowConfig config = new OverflowConfig();
        config.saveDefaultConfig();
        config.loadConfig();

        System.setProperty("minestom.chunk-view-distance", String.valueOf(config.getViewDistance()));

        // Initialize Server
        MinecraftServer server = MinecraftServer.init();
        MinecraftServer.setBrandName(config.getServerBrand());
        MinecraftServer.setCompressionThreshold(config.getCompressionThreshold() == -1 ? 0 : config.getCompressionThreshold());
        String encodedServerIcon = OverflowUtils.encodeServerIcon();
        String serverVersion = config.getServerBrand() + " " + MinecraftServer.VERSION_NAME;
        Component serverMotd = MiniMessage.miniMessage().deserialize(config.getMotd());
        Component serverTabHeader = MiniMessage.miniMessage().deserialize(config.getTabHeader());
        Component serverTabFooter = MiniMessage.miniMessage().deserialize(config.getTabFooter());

        // Dimension Registration
        logger.info("Initializing dimension manager...");
        DimensionTypeManager dimensionManager = MinecraftServer.getDimensionTypeManager();
        DimensionType THE_VOID = DimensionType.builder(NamespaceID.from("overflow:the_void"))
                .ultrawarm(false)
                .natural(false)
                .piglinSafe(false)
                .respawnAnchorSafe(false)
                .bedSafe(false)
                .raidCapable(false)
                .skylightEnabled(true)
                .ceilingEnabled(false)
                .fixedTime(null)
                .effects("minecraft:the_end")
                .ambientLight(0.0f)
                .height(384)
                .minY(-64)
                .logicalHeight(384)
                .build();
        dimensionManager.addDimension(THE_VOID);

        // World Container
        logger.info("Initializing world...");
        InstanceContainer world = MinecraftServer.getInstanceManager().createInstanceContainer(THE_VOID);
        GlobalEventHandler events = MinecraftServer.getGlobalEventHandler();
        Scheduler scheduler = MinecraftServer.getSchedulerManager();
        Pos spawnPos = new Pos(0.5, 128, 0.5);

        world.setTime(0);
        world.setTimeRate(0);
        world.setTimeUpdate(null);
        world.setGenerator(unit -> unit.modifier().fillHeight(-64, 320, Block.AIR));
        world.enableAutoChunkLoad(true);

        // Event Listeners
        logger.info("Registering event listeners...");
        events.addListener(ServerListPingEvent.class, event -> {
            ResponseData response = new ResponseData();

            response.setVersion(serverVersion);
            response.setMaxPlayer(config.getPlayerLimit() == -1 ? event.getResponseData().getOnline() + 1 : config.getPlayerLimit());
            response.setDescription(serverMotd);
            response.setFavicon(encodedServerIcon);
            event.setResponseData(response);
        });
        events.addListener(AsyncPlayerPreLoginEvent.class, event -> {
            Player player = event.getPlayer();
            logger.info(player.getUsername() + " [" + player.getPlayerConnection().getRemoteAddress() + "] connected.");
        });
        events.addListener(PlayerLoginEvent.class, event -> {
            Player player = event.getPlayer();

            player.setAutoViewable(false);
            player.setRespawnPoint(spawnPos);
            event.setSpawningInstance(world);
        });
        events.addListener(PlayerSkinInitEvent.class, event -> event.setSkin(PlayerSkin.fromUsername(event.getPlayer().getUsername())));
        events.addListener(PlayerSpawnEvent.class, event -> {
            Player player = event.getPlayer();

            player.setGameMode(GameMode.SPECTATOR);
            player.setNoGravity(true);
            player.setInvulnerable(true);
            player.setInvisible(true);

            player.sendPlayerListHeaderAndFooter(serverTabHeader, serverTabFooter);

            if (config.isVelocityEnabled()) {
                player.sendMessage(Component
                        .text("Servers are currently full! You have been temporarily placed into an overflow limbo server.")
                        .color(NamedTextColor.YELLOW)
                );
            }
        });
        events.addListener(PlayerMoveEvent.class, event -> event.setCancelled(true));
        events.addListener(PlayerChatEvent.class, event -> event.setCancelled(true));
        events.addListener(PlayerDisconnectEvent.class, event -> {
            Player player = event.getPlayer();

            // TODO: Fix tab list persistence when connecting to other servers.

            logger.info(player.getUsername() + " [" + player.getPlayerConnection().getRemoteAddress() + "] disconnected.");
        });

        // Scheduler Tasks
        logger.info("Registering scheduler tasks...");
        if (config.isVelocityEnabled()) {
            scheduler.submitTask(() -> {
                world.sendMessage(Component
                        .text("Servers are currently full! You have been temporarily placed into an overflow limbo server.")
                        .color(NamedTextColor.YELLOW)
                );
                return TaskSchedule.minutes(1);
            });
        }

        // Velocity Forwarding Support
        boolean velocityEnabled = config.isVelocityEnabled();
        String velocitySecret = config.getVelocitySecret();
        if (velocityEnabled && !velocitySecret.equalsIgnoreCase("NONE") && !velocitySecret.isBlank()) {
            VelocityProxy.enable(velocitySecret);
            logger.info("Velocity modern forwarding support enabled.");
        }

        // Start Server
        server.start(config.getServerAddress(), config.getServerPort());
        long uptime = ManagementFactory.getRuntimeMXBean().getUptime();
        logger.info("Done in " + uptime + "ms!");

    }

}
