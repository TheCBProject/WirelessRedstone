package codechicken.wirelessredstone.command;

import codechicken.lib.command.CoreCommand;
import net.minecraft.command.ICommandSender;

import java.util.Arrays;

public class ParamOpen extends FreqParam
{
    @Override
    public void printHelp(ICommandSender listener) {
        CoreCommand.chatT(listener, "wrcbe.param.open.usage");
        CoreCommand.chatT(listener, "wrcbe.param.open.usage1");
        CoreCommand.chatT(listener, "wrcbe.param.jam.usage" + (rand.nextInt(5) + 2), "open");
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
