package codechicken.wirelessredstone.core;

import java.util.Random;

import net.minecraft.command.ICommandSender;

public abstract class FreqParam
{
    public static Random rand = new Random();
    
    public abstract void printHelp(ICommandSender listener);
    public abstract String getName();
    public abstract void handleCommand(String playername, String[] subArray, ICommandSender listener);
}
