package codechicken.wirelessredstone.command;

import codechicken.lib.command.CoreCommand;
import codechicken.wirelessredstone.manager.RedstoneEther;
import net.minecraft.command.ICommandSender;

public class ParamPrivate extends FreqParam
{
    @Override
    public void printHelp(ICommandSender listener) {
        CoreCommand.chatT(listener,"wrcbe.param.private.usage");
        CoreCommand.chatT(listener,"wrcbe.param.private.usage1");
        CoreCommand.chatT(listener,"wrcbe.param.private.usage2");
        CoreCommand.chatT(listener,"wrcbe.param.private.usage3");
        CoreCommand.chatT(listener,"wrcbe.param.private.usage4");
    }

    @Override
    public String getName() {
        return "private";
    }

    @Override
    public void handleCommand(String playername, String[] subArray, ICommandSender listener) {
        RedstoneEther ether = RedstoneEther.get(false);

        if (subArray.length == 1) {
            CoreCommand.chatT(listener,"wrcbe.param.invalidno");
            return;
        }

        if (subArray[1].equals("all")) {
            StringBuilder returnString = new StringBuilder();
            for (int freq = 1; freq <= RedstoneEther.numfreqs; freq++) {
                if (ether.isFreqPrivate(freq)) {
                    if (returnString.length() > 0)
                        returnString.append(", ");

                    returnString.append(freq);
                }
            }

            if (returnString.length() == 0)
                CoreCommand.chatT(listener,"wrcbe.param.private.none");
            else
                CoreCommand.chatT(listener,"wrcbe.param.private.", returnString);
            return;
        }

        if (subArray[1].equals("clear")) {
            if (subArray.length == 2) {
                CoreCommand.chatT(listener,"wrcbe.param.invalidno");
                return;
            }

            int freq = -1;
            try {
                freq = Integer.parseInt(subArray[2]);
            } catch (NumberFormatException ne) {}

            if (freq != -1) {
                if (freq < 1 || freq > RedstoneEther.numfreqs) {
                    CoreCommand.chatT(listener,"wrcbe.param.invalidfreq");
                    return;
                }

                if (freq <= ether.getLastPublicFrequency()) {
                    CoreCommand.chatT(listener,"wrcbe.param.private.publicanyway", freq);
                    return;
                }

                if (!ether.isFreqPrivate(freq)) {
                    CoreCommand.chatT(listener,"wrcbe.param.private.notprivate", freq);
                    return;
                }

                ether.removeFreqOwner(freq);
                CoreCommand.chatT(listener,"wrcbe.param.private.nowhared", freq);
                return;
            }

            String scanPlayer = subArray[2];

            StringBuilder returnString = new StringBuilder();
            for (freq = 1; freq <= RedstoneEther.numfreqs; freq++) {
                if (ether.isFreqPrivate(freq)) {
                    if (scanPlayer.equals("all") || ether.getFreqOwner(freq).equalsIgnoreCase(scanPlayer)) {
                        if (returnString.length() > 0)
                            returnString.append(", ");

                        returnString.append(freq);
                        ether.removeFreqOwner(freq);
                    }
                }
            }

            if (returnString.length() == 0)
                if (scanPlayer.equals("all"))
                    CoreCommand.chatT(listener,"wrcbe.param.private.none");
                else
                    CoreCommand.chatT(listener,"wrcbe.param.private.noneowned", scanPlayer);
            else
                CoreCommand.chatT(listener,"wrcbe.param.private.nowshared2", returnString);
            return;
        }

        int freq = -1;
        try {
            freq = Integer.parseInt(subArray[1]);
        } catch (NumberFormatException ne) {}

        if (freq != -1) {
            if (freq < 1 || freq > RedstoneEther.numfreqs) {
                CoreCommand.chatT(listener,"wrcbe.param.invalidfreq");
                return;
            }

            if (freq <= ether.getLastPublicFrequency()) {
                CoreCommand.chatT(listener,"wrcbe.param.private.ispublic", freq);
                return;
            }

            if (!ether.isFreqPrivate(freq)) {
                CoreCommand.chatT(listener,"wrcbe.param.private.notprivate", freq);
                return;
            }

            CoreCommand.chatT(listener,"wrcbe.param.private.ownedby", freq, ether.getFreqOwner(freq));
            return;
        }

        String scanPlayer = subArray[1];
        if (subArray.length == 2) {
            StringBuilder returnString = new StringBuilder();
            for (freq = 1; freq <= RedstoneEther.numfreqs; freq++) {
                if (ether.isFreqPrivate(freq) && ether.getFreqOwner(freq).equalsIgnoreCase(scanPlayer)) {
                    if (returnString.length() > 0)
                        returnString.append(", ");

                    returnString.append(freq);
                }
            }

            if (returnString.length() == 0)
                CoreCommand.chatT(listener,"wrcbe.param.private.noneowned", scanPlayer);
            else
                CoreCommand.chatT(listener,"wrcbe.param.private.owns", scanPlayer, returnString);
            return;
        }

        try {
            freq = Integer.parseInt(subArray[2]);
        } catch (NumberFormatException ne) {
            CoreCommand.chatT(listener,"wrcbe.param.invalidfreq");
            return;
        }

        if (freq < 1 || freq > RedstoneEther.numfreqs) {
            CoreCommand.chatT(listener,"wrcbe.param.invalidfreq");
            return;
        }

        if (freq <= ether.getLastPublicFrequency()) {
            CoreCommand.chatT(listener,"wrcbe.param.private.ispublic", freq);
            return;
        }

        if (freq > ether.getLastSharedFrequency()) {
            CoreCommand.chatT(listener,"wrcbe.param.private.notshared", freq);
            return;
        }

        ether.setFreqOwner(freq, scanPlayer);
        if (ether.isFreqPrivate(freq) && ether.getFreqOwner(freq).equalsIgnoreCase(scanPlayer))
            CoreCommand.chatT(listener,"wrcbe.param.private.nowownedby", scanPlayer);
        else
            CoreCommand.chatT(listener,"wrcbe.param.private.limit", scanPlayer, ether.getNumPrivateFreqs());
    }

}
