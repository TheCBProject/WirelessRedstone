package codechicken.wirelessredstone.command;

import codechicken.lib.command.CoreCommand;
import codechicken.lib.util.ServerUtils;
import codechicken.wirelessredstone.manager.RedstoneEther;
import codechicken.wirelessredstone.manager.RedstoneEtherServer;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.text.Style;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.util.text.TextFormatting;

import java.util.Arrays;

public class ParamJam extends FreqParam {

    @Override
    public void printHelp(ICommandSender listener) {
        CoreCommand.chatT(listener, "wrcbe.param.jam.usage");
        CoreCommand.chatT(listener, "wrcbe.param.jam.usage1");
        CoreCommand.chatT(listener, "wrcbe.param.jam.usage" + (rand.nextInt(5) + 2), "jam");
    }

    @Override
    public String getName() {
        return "jam";
    }

    @Override
    public void handleCommand(String playername, String[] args, ICommandSender listener) {
        jamOpenCommand(playername, Arrays.copyOfRange(args, 1, args.length), listener, true);
    }

    public static void jamOpenCommand(String playername, String[] args, ICommandSender listener, boolean jam) {
        RedstoneEtherServer ether = RedstoneEther.server();

        if (args.length == 0) {
            CoreCommand.chatT(listener, "wrcbe.param.invalidno");
            return;
        }

        if ((args.length == 1 && ServerUtils.getPlayer(playername) == null)) {
            CoreCommand.chatT(listener, "wrcbe.param.jam.noplayer");
            return;
        }

        String range = args[args.length - 1];
        String jamPlayer = args.length == 1 ? playername : args[0];

        int startfreq;
        int endfreq;

        if (range.equals("all")) {
            startfreq = 1;
            endfreq = RedstoneEther.numfreqs;
        } else if (range.equals("default")) {
            startfreq = ether.getLastSharedFrequency() + 1;
            endfreq = RedstoneEther.numfreqs;
        } else {
            int[] freqrange = RedstoneEther.parseFrequencyRange(range);
            startfreq = freqrange[0];
            endfreq = freqrange[1];
        }

        if (startfreq < 1 || endfreq > RedstoneEther.numfreqs || endfreq < startfreq) {
            CoreCommand.chatT(listener, "wrcbe.param.invalidfreqrange");
            return;
        }

        ether.setFrequencyRangeCommand(jamPlayer, startfreq, endfreq, jam);

        int publicend = ether.getLastPublicFrequency();
        EntityPlayer player = ServerUtils.getPlayer(jamPlayer);
        String paramName = jam ? "jam" : "open";
        Style playerStyle = new Style().setColor(TextFormatting.YELLOW);
        if (startfreq == endfreq) {
            if (startfreq <= publicend) {
                CoreCommand.chatT(listener, "wrcbe.param.jam.errpublic");
                return;
            }
            CoreCommand.chatOpsT("wrcbe.param." + paramName + ".opjammed", playername, jamPlayer, startfreq);
            if (player != null) {
                player.sendMessage(new TextComponentTranslation("wrcbe.param." + paramName + ".jammed", startfreq).setStyle(playerStyle));
            }
        } else {
            if (startfreq <= publicend && endfreq <= publicend) {
                CoreCommand.chatT(listener, "wrcbe.param.jam.errpublic");
                return;
            }
            if (startfreq <= publicend) {
                startfreq = publicend + 1;
            }

            CoreCommand.chatOpsT("wrcbe.param." + paramName + ".opjammed2", playername, jamPlayer, startfreq + "-" + endfreq);
            if (player != null) {
                player.sendMessage(new TextComponentTranslation("wrcbe.param." + paramName + ".jammed2", startfreq + "-" + endfreq).setStyle(playerStyle));
            }
        }
    }
}
