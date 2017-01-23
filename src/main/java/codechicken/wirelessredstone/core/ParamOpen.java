package codechicken.wirelessredstone.core;

import codechicken.core.commands.CoreCommand;
import net.minecraft.command.ICommandSender;

import java.util.Arrays;

public class ParamOpen extends FreqParam
{
    @Override
    public void printHelp(ICommandSender listener) {
        CoreCommand.chatT(listener, "wrcbe_core.param.open.usage");
        CoreCommand.chatT(listener, "wrcbe_core.param.open.usage1");
        CoreCommand.chatT(listener, "wrcbe_core.param.jam.usage" + (rand.nextInt(5) + 2), "open");
    }

    @Override
    public String getName() {
        return "open";
    }

    @Override
    public void handleCommand(String playername, String[] args, ICommandSender listener) {
        ParamJam.jamOpenCommand(playername, Arrays.copyOfRange(args, 1, args.length), listener, false);
    }
}
