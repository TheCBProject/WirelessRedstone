package codechicken.wirelessredstone.command;

import java.util.LinkedList;

import codechicken.core.commands.CoreCommand;
import net.minecraft.command.ICommandSender;

public class CommandFreq extends CoreCommand
{    
    public static LinkedList<FreqParam> paramHandlers = new LinkedList<FreqParam>();
    
    static
    {
        paramHandlers.add(new ParamScan());
        paramHandlers.add(new ParamJam());
        paramHandlers.add(new ParamOpen());
        paramHandlers.add(new ParamGet());
        paramHandlers.add(new ParamSet());
        paramHandlers.add(new ParamPrivate());
    }
    
    @Override
    public String getCommandName()
    {
        return "freq";
    }

    @Override
    public void handleCommand(String command, String playername, String[] args, ICommandSender listener)
    {        
        if(args[0].equals("help"))
        {
            for(FreqParam param : paramHandlers)
            {
                if(args[1].equals(param.getName()))
                {
                    param.printHelp(listener);
                    return;
                }
            }
            chatT(listener, "wrcbe_core.param.missing");
            return;
        }
        
        for(FreqParam param : paramHandlers)
        {
            if(args[0].equals(param.getName()))
            {
                param.handleCommand(playername, args, listener);
                return;
            }
        }
        chatT(listener, "wrcbe_core.param.missing");
    }
    
    @Override
    public void printHelp(ICommandSender listener)
    {
        chatT(listener, "wrcbe_core.command.usage");
        StringBuilder paramNames = new StringBuilder();
        for(FreqParam param : paramHandlers)
        {
            if(paramNames.length() > 0)
                paramNames.append(", ");
            paramNames.append(param.getName());
        }
        chatT(listener, "wrcbe_core.command.usage1", paramNames.toString());
        chatT(listener, "wrcbe_core.command.usage2");
    }

    @Override
    public boolean isOpOnly()
    {
        return true;
    }

    @Override
    public int minimumParameters()
    {
        return 1;
    }
}
