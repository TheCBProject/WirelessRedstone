package codechicken.wirelessredstone.init;

import codechicken.multipart.IPartFactory;
import codechicken.multipart.MultiPartRegistry;
import codechicken.multipart.MultipartGenerator;
import codechicken.multipart.TMultiPart;
import codechicken.wirelessredstone.part.JammerPart;
import codechicken.wirelessredstone.part.ReceiverPart;
import codechicken.wirelessredstone.part.TransmitterPart;

/**
 * Created by covers1624 on 24/01/2017.
 */
public class PartFactory implements IPartFactory {

    public static PartFactory instance = new PartFactory();


    public static void init() {
        MultiPartRegistry.registerParts(instance, new String[]{
                "wrcbe-tran",
                "wrcbe-recv",
                "wrcbe-jamm"
        });

        MultipartGenerator.registerPassThroughInterface("codechicken.wirelessredstone.api.ITileWireless");
        MultipartGenerator.registerPassThroughInterface("codechicken.wirelessredstone.api.ITileReceiver");
        MultipartGenerator.registerPassThroughInterface("codechicken.wirelessredstone.api.ITileJammer");
    }


    @Override
    public TMultiPart createPart(String name, boolean client) {
        if(name.equals("wrcbe-tran"))
            return new TransmitterPart();
        if(name.equals("wrcbe-recv"))
            return new ReceiverPart();
        if(name.equals("wrcbe-jamm"))
            return new JammerPart();
        return null;
    }
}
