package dev.codeerror.overflow;

import dev.codeerror.overflow.command.PingCommand;
import dev.codeerror.overflow.command.WhereCommand;
import dev.codeerror.overflow.util.OverflowUtils;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.minestom.server.MinecraftServer;
import net.minestom.server.command.CommandManager;
import net.minestom.server.coordinate.Pos;
import net.minestom.server.entity.GameMode;
import net.minestom.server.entity.Player;
import net.minestom.server.event.GlobalEventHandler;
import net.minestom.server.event.player.*;
import net.minestom.server.event.server.ServerListPingEvent;
import net.minestom.server.extras.MojangAuth;
import net.minestom.server.extras.bungee.BungeeCordProxy;
import net.minestom.server.extras.velocity.VelocityProxy;
import net.minestom.server.instance.InstanceContainer;
import net.minestom.server.instance.block.Block;
import net.minestom.server.network.packet.server.play.PlayerInfoUpdatePacket;
import net.minestom.server.ping.ResponseData;
import net.minestom.server.utils.NamespaceID;
import net.minestom.server.world.DimensionType;
import net.minestom.server.world.DimensionTypeManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.management.ManagementFactory;
import java.util.Set;

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
                .effects("minecraft:the_end")
                .ambientLight(1.0f)
                .height(384)
                .minY(-64)
                .logicalHeight(384)
                .build();
        dimensionManager.addDimension(THE_VOID);

        // World Container
        logger.info("Initializing world...");
        InstanceContainer world = MinecraftServer.getInstanceManager().createInstanceContainer(THE_VOID);
        GlobalEventHandler events = MinecraftServer.getGlobalEventHandler();
        Pos spawnPos = new Pos(0.5, 128, 0.5);

        world.setTime(0);
        world.setTimeRate(0);
        world.setTimeUpdate(null);
        world.setGenerator(unit -> unit.modifier().fillHeight(-64, 320, Block.AIR));
        world.enableAutoChunkLoad(true);

        // Commands
        CommandManager commandManager = MinecraftServer.getCommandManager();
        commandManager.register(new WhereCommand());
        commandManager.register(new PingCommand());

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
            if (world.getPlayers().size() >= config.getPlayerLimit() && config.getPlayerLimit() > -1) {
                event.getPlayer().getPlayerConnection().disconnect();
            } else {
                logger.info(player.getUsername() + " [" + player.getPlayerConnection().getRemoteAddress() + "] connected.");
            }
        });
        events.addListener(AsyncPlayerConfigurationEvent.class, event -> {
            Player player = event.getPlayer();
            player.setAutoViewable(false);
            player.setRespawnPoint(spawnPos);
            event.setSpawningInstance(world);
        });
        events.addListener(PlayerSpawnEvent.class, event -> {
            Player player = event.getPlayer();
            player.setGameMode(GameMode.SPECTATOR);
            player.setNoGravity(true);
            player.setInvulnerable(true);
            player.setInvisible(true);
        });
        if (!config.isTablistEnabled()) {
            events.addListener(PlayerPacketOutEvent.class, event -> {
                if (event.getPacket() instanceof PlayerInfoUpdatePacket) event.setCancelled(true);
            });
        }
        events.addListener(PlayerMoveEvent.class, event -> event.setCancelled(true));
        events.addListener(PlayerChatEvent.class, event -> event.setCancelled(true));
        events.addListener(PlayerDisconnectEvent.class, event -> {
            Player player = event.getPlayer();
            logger.info(player.getUsername() + " [" + player.getPlayerConnection().getRemoteAddress() + "] disconnected.");
        });

        // Proxy Forwarding Support
        String proxyType = config.getProxyType();
        switch (proxyType) {
            case "velocity" -> {
                String velocitySecret = config.getVelocitySecret();
                if (velocitySecret.isBlank()) break;
                VelocityProxy.enable(velocitySecret);
                logger.info("Velocity modern forwarding support enabled.");
            }
            case "bungeecord" -> {
                String bungeeGuardToken = config.getBungeeGuardToken();
                if (bungeeGuardToken.isBlank()) {
                    BungeeCordProxy.enable();
                    logger.warn("BungeeCord legacy forwarding support enabled.");
                    logger.warn("Please use BungeeGuard for secure BungeeCord forwarding support.");
                } else {
                    BungeeCordProxy.setBungeeGuardTokens(Set.of(bungeeGuardToken));
                    BungeeCordProxy.enable();
                    logger.info("BungeeGuard secure forwarding support for BungeeCord enabled.");
                }
            }
            default -> MojangAuth.init();
        }

        // Start Server
        server.start(config.getServerAddress(), config.getServerPort());
        long uptime = ManagementFactory.getRuntimeMXBean().getUptime();
        logger.info("Done in " + uptime + "ms!");

    }

}
