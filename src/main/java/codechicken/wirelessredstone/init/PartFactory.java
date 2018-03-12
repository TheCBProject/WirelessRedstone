package codechicken.wirelessredstone.init;

import codechicken.multipart.MultiPartRegistry;
import codechicken.multipart.MultipartGenerator;
import codechicken.multipart.TMultiPart;
import codechicken.multipart.api.IPartFactory;
import codechicken.wirelessredstone.part.JammerPart;
import codechicken.wirelessredstone.part.ReceiverPart;
import codechicken.wirelessredstone.part.TransmitterPart;
import net.minecraft.util.ResourceLocation;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;

/**
 * Created by covers1624 on 24/01/2017.
 */
public class PartFactory implements IPartFactory {

    public static PartFactory instance = new PartFactory();
    private static final Map<ResourceLocation, Supplier<TMultiPart>> parts = new HashMap<>();

    static {
        parts.put(new ResourceLocation("wrcbe:transmitter"), TransmitterPart::new);
        parts.put(new ResourceLocation("wrcbe:receiver"), ReceiverPart::new);
        parts.put(new ResourceLocation("wrcbe:jammer"), JammerPart::new);
    }

    public static void init() {
        MultiPartRegistry.registerParts(instance, parts.keySet());

        MultipartGenerator.registerPassThroughInterface("codechicken.wirelessredstone.api.ITileWireless");
        MultipartGenerator.registerPassThroughInterface("codechicken.wirelessredstone.api.ITileReceiver");
        MultipartGenerator.registerPassThroughInterface("codechicken.wirelessredstone.api.ITileJammer");
    }

    @Override
    public TMultiPart createPart(ResourceLocation name, boolean client) {
        if (parts.containsKey(name)) {
            return parts.get(name).get();
        }
        return null;
    }
}
