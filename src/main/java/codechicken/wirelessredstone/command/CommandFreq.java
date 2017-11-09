package codechicken.wirelessredstone.command;

import codechicken.lib.command.CoreCommand;
import net.minecraft.command.ICommandSender;

import java.util.LinkedList;

public class CommandFreq extends CoreCommand {

    public static LinkedList<FreqParam> paramHandlers = new LinkedList<>();

    static {
        paramHandlers.add(new ParamScan());
        paramHandlers.add(new ParamJam());
        paramHandlers.add(new ParamOpen());
        paramHandlers.add(new ParamGet());
        paramHandlers.add(new ParamSet());
        paramHandlers.add(new ParamPrivate());
    }

    @Override
    public String getName() {
        return "freq";
    }

    @Override
    public void handleCommand(String command, String playername, String[] args, ICommandSender listener) {
        if (args[0].equals("help")) {
            for (FreqParam param : paramHandlers) {
                if (args[1].equals(param.getName())) {
                    param.printHelp(listener);
                    return;
                }
            }
            chatT(listener, "wrcbe.param.missing");
            return;
        }

        for (FreqParam param : paramHandlers) {
            if (args[0].equals(param.getName())) {
                param.handleCommand(playername, args, listener);
                return;
            }
        }
        chatT(listener, "wrcbe.param.missing");
    }

    @Override
    public void printHelp(ICommandSender listener) {
        chatT(listener, "wrcbe.command.usage");
        StringBuilder paramNames = new StringBuilder();
        for (FreqParam param : paramHandlers) {
            if (paramNames.length() > 0) {
                paramNames.append(", ");
            }
            paramNames.append(param.getName());
        }
        chatT(listener, "wrcbe.command.usage1", paramNames.toString());
        chatT(listener, "wrcbe.command.usage2");
    }

    @Override
    public boolean isOpOnly() {
        return true;
    }

    @Override
    public int minimumParameters() {
        return 1;
    }
}
