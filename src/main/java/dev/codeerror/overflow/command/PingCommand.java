package dev.codeerror.overflow.command;

import net.kyori.adventure.text.minimessage.MiniMessage;
import net.minestom.server.command.builder.Command;
import net.minestom.server.command.builder.condition.Conditions;
import net.minestom.server.entity.Player;

public class PingCommand extends Command {

    public PingCommand() {
        super("ping");
        setDefaultExecutor((sender, context) -> {
            Player player = ((Player) sender);
            sender.sendMessage(MiniMessage.miniMessage().deserialize(
                    "<green>[\u2714] Your ping is <gold>" + player.getLatency() + "ms</gold>.</green>"
            ));
        });
        setCondition(Conditions::playerOnly);
        addSyntax((sender, context) -> {
            Player player = ((Player) sender);
            sender.sendMessage(MiniMessage.miniMessage().deserialize(
                    "<green>[\u2714] Your ping is <gold>" + player.getLatency() + "ms</gold>.</green>"
            ));
        });
    }

}
