package dev.codeerror.overflow.command;

import net.kyori.adventure.text.minimessage.MiniMessage;
import net.minestom.server.command.builder.Command;
import net.minestom.server.command.builder.condition.Conditions;

public class WhereCommand extends Command {

    public WhereCommand() {
        super("whereami", "where");
        setDefaultExecutor((sender, context) -> sender.sendMessage(MiniMessage.miniMessage().deserialize(
                "<green>[\u2714] You are currently connected to a limbo server.</green>"
        )));
        setCondition(Conditions::playerOnly);
        addSyntax((sender, context) -> sender.sendMessage(MiniMessage.miniMessage().deserialize(
                "<green>[\u2714] You are currently connected to a limbo server.</green>"
        )));
    }

}