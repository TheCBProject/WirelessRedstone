package codechicken.wirelessredstone.command;

import codechicken.lib.command.CoreCommand;
import codechicken.wirelessredstone.manager.RedstoneEther;
import net.minecraft.command.ICommandSender;

public class ParamSet extends FreqParam {

    @Override
    public void printHelp(ICommandSender listener) {
        CoreCommand.chatT(listener, "wrcbe.param.set.usage");
        CoreCommand.chatT(listener, "wrcbe.param.set.usage1");
        CoreCommand.chatT(listener, "wrcbe.param.set.usage2");
        CoreCommand.chatT(listener, "wrcbe.param.set.usage3");
    }

    @Override
    public String getName() {
        return "set";
    }

    @Override
    public void handleCommand(String playername, String[] subArray, ICommandSender listener) {
        RedstoneEther ether = RedstoneEther.get(false);

        if (subArray.length != 3) {
            CoreCommand.chatT(listener, "wrcbe.param.invalidno");
            return;
        }

        int freq;
        try {
            freq = Integer.parseInt(subArray[2]);
        } catch (NumberFormatException ne) {
            CoreCommand.chatT(listener, "");
            return;
        }

        if (subArray[1].equals("public")) {
            if (freq < 1 || freq > RedstoneEther.numfreqs) {
                CoreCommand.chatT(listener, "wrcbe.param.invalidfreq");
                return;
            }

            ether.setLastPublicFrequency(freq);
            CoreCommand.chatOpsT("wrcbe.param.set.nowpublic", playername, ether.getLastPublicFrequency());

            if (freq >= ether.getLastSharedFrequency()) {
                CoreCommand.chatOpsT("wrcbe.param.set.sharedpublic", playername);
            } else {
                CoreCommand.chatOpsT("wrcbe.param.set.nowshared", playername, (freq + 1), ether.getLastSharedFrequency());
            }
        } else if (subArray[1].equals("shared")) {
            if (freq < 1 || freq > RedstoneEther.numfreqs) {
                CoreCommand.chatT(listener, "wrcbe.param.invalidfreq");
                return;
            }

            boolean wasPublic = ether.getLastPublicFrequency() >= ether.getLastSharedFrequency();

            ether.setLastSharedFrequency(freq);

            if (ether.getLastSharedFrequency() >= freq) {
                if (!wasPublic) {
                    CoreCommand.chatOpsT("wrcbe.param.set.sharedremoved", playername);
                } else {
                    CoreCommand.chatOpsT("wrcbe.param.set.nowshared", playername, (ether.getLastPublicFrequency() + 1), freq);
                }
            }
        } else if (subArray[1].equals("private")) {
            if (freq < 0 || freq > RedstoneEther.numfreqs) {
                CoreCommand.chatT(listener, "Invalid Quantity.");
                return;
            }

            ether.setNumPrivateFreqs(freq);
            CoreCommand.chatOpsT("wrcbe.param.set.privateno", playername, freq);
        } else {
            CoreCommand.chatT(listener, "wrcbe.param.set.invalidqty");
        }
    }
}
