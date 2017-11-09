package codechicken.wirelessredstone.command;

import codechicken.lib.command.CoreCommand;
import codechicken.lib.util.ServerUtils;
import codechicken.wirelessredstone.manager.RedstoneEther;
import net.minecraft.command.ICommandSender;

public class ParamScan extends FreqParam {

    @Override
    public void printHelp(ICommandSender listener) {
        CoreCommand.chatT(listener, "wrcbe.param.scan.usage");
        CoreCommand.chatT(listener, "wrcbe.param.scan.usage1");
        CoreCommand.chatT(listener, "wrcbe.param.scan.usage" + (rand.nextInt(2) + 2));
    }

    @Override
    public String getName() {
        return "scan";
    }

    @Override
    public void handleCommand(String playername, String[] subArray, ICommandSender listener) {
        RedstoneEther ether = RedstoneEther.get(false);

        if (subArray.length == 1 && ServerUtils.getPlayer(playername) == null) {
            CoreCommand.chatT(listener, "wrcbe.param.invalidno");
            return;
        }

        String scanPlayer = subArray.length == 1 ? playername : subArray[1];

        StringBuilder freqs = new StringBuilder();
        int ranges = 0;
        int startfreq;
        int endfreq = ether.getLastPublicFrequency();
        while (true) {
            int[] freqrange = ether.getNextFrequencyRange(scanPlayer, endfreq + 1, false);
            startfreq = freqrange[0];
            endfreq = freqrange[1];
            if (startfreq == -1) {
                break;
            }

            if (ranges != 0) {
                freqs.append(", ");
            }

            if (startfreq == endfreq) {
                freqs.append(startfreq);
            } else {
                freqs.append(startfreq).append("-").append(endfreq);
            }

            ranges++;

            if (endfreq == RedstoneEther.numfreqs) {
                break;
            }
        }

        if (ranges == 0) {
            CoreCommand.chatT(listener, "wrcbe.param.scan.onlypublic", scanPlayer);
        } else {
            CoreCommand.chatT(listener, "wrcbe.param.scan.list", scanPlayer, freqs);
        }
    }

}
