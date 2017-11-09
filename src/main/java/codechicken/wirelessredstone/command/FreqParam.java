package codechicken.wirelessredstone.command;

import net.minecraft.command.ICommandSender;

import java.util.Random;

public abstract class FreqParam {

    public static Random rand = new Random();

    public abstract void printHelp(ICommandSender listener);

    public abstract String getName();

    public abstract void handleCommand(String playername, String[] subArray, ICommandSender listener);
}
