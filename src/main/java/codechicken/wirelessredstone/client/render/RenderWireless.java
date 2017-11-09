package codechicken.wirelessredstone.client.render;

import codechicken.lib.colour.Colour;
import codechicken.lib.lighting.LightModel;
import codechicken.lib.lighting.LightModel.Light;
import codechicken.lib.lighting.PlanarLightModel;
import codechicken.lib.math.MathHelper;
import codechicken.lib.render.*;
import codechicken.lib.texture.TextureUtils;
import codechicken.lib.texture.TextureUtils.IIconRegister;
import codechicken.lib.vec.*;
import codechicken.lib.vec.uv.MultiIconTransformation;
import codechicken.wirelessredstone.part.TransceiverPart;
import codechicken.wirelessredstone.part.WirelessPart;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.util.ResourceLocation;

import java.util.Arrays;
import java.util.Map;

import static codechicken.lib.vec.Rotation.sideOrientation;
import static codechicken.lib.vec.Vector3.center;
import static codechicken.lib.vec.Vector3.zero;

public class RenderWireless implements IIconRegister {

    private static MultiIconTransformation model_icont;
    private static MultiIconTransformation base_icont[] = new MultiIconTransformation[2];
    private static CCModel[][] models = new CCModel[3][24];

    private static TextureAtlasSprite base;
    private static TextureAtlasSprite on;
    private static TextureAtlasSprite off;
    private static TextureAtlasSprite blaze;

    //@formatter:off
    private static LightModel lm = new LightModel()
            .setAmbient(new Vector3(0.7, 0.7, 0.7))
            .addLight(new Light(new Vector3(0.2, 1, -0.7))
                    .setDiffuse(new Vector3(0.3, 0.3, 0.3)))
            .addLight(new Light(new Vector3(-0.2, 1, 0.7))
                    .setDiffuse(new Vector3(0.3, 0.3, 0.3)))
            .addLight(new Light(new Vector3(0.7, -1, -0.2))
                    .setDiffuse(new Vector3(0.2, 0.2, 0.2)))
            .addLight(new Light(new Vector3(-0.7, -1, 0.2))
                    .setDiffuse(new Vector3(0.2, 0.2, 0.2)));
    private static PlanarLightModel rlm = lm.reducePlanar();
    //@formatter:on

    static {
        Map<String, CCModel> modelMap = OBJParser.parseModels(new ResourceLocation("wrcbe", "models/logic.obj"), 7, null);
        CCModel tstand = setTex(modelMap.get("TStand"), 2);
        CCModel jstand = setTex(tstand.copy(), 1);
        CCModel rstand = setTex(modelMap.get("RStand"), 2);
        CCModel rdish = modelMap.get("RDish").shrinkUVs(0.0005);

        models[0][0] = tstand;
        models[1][0] = CCModel.combine(Arrays.asList(rstand, rdish));
        models[2][0] = jstand;

        for (int i = 0; i < 3; i++) {
            models[i][0].computeNormals();
        }

        for (int j = 1; j < 24; j++) {
            Transformation t = sideOrientation(j >> 2, j & 3).at(center);
            for (int i = 0; i < models.length; i++) {
                models[i][j] = models[i][0].copy().apply(t);
            }
        }

        for (int j = 0; j < 24; j++) {
            for (int i = 0; i < 3; i++) {
                models[i][j].computeLighting(lm);
            }
        }
    }

    private static CCModel setTex(CCModel model, int index) {
        for (Vertex5 v : model.verts) {
            v.uv.tex = index;
        }

        return model;
    }

    @Override
    public void registerIcons(TextureMap textureMap) {
        base = textureMap.registerSprite(new ResourceLocation("wrcbe:blocks/base"));
        on = textureMap.registerSprite(new ResourceLocation("wrcbe:blocks/on"));
        off = textureMap.registerSprite(new ResourceLocation("wrcbe:blocks/off"));
        blaze = textureMap.registerSprite(new ResourceLocation("wrcbe:blocks/blaze"));

    }

    public static void postRegisterIcons() {
        TextureAtlasSprite obsidian = TextureUtils.getBlockTexture("obsidian");

        model_icont = new MultiIconTransformation(base, blaze, obsidian);
        base_icont[0] = new MultiIconTransformation(base, off, base, base, base, base);
        base_icont[1] = new MultiIconTransformation(base, on, base, base, base, base);
    }

    public static void renderInv(CCRenderState ccrs, WirelessPart p) {
        ccrs.reset();
        ccrs.pullLightmap();
        ccrs.startDrawing(7, DefaultVertexFormats.ITEM);
        ccrs.setPipeline(p.rotationT().at(center), base_icont[0]);
        BlockRenderer.renderCuboid(ccrs, WirelessPart.baseBounds(0), 0);
        models[p.modelId()][p.side() << 2 | p.rotation()].render(ccrs, model_icont);
        ccrs.draw();

        renderPearl(ccrs, zero, p);
    }

    public static void renderWorld(CCRenderState ccrs, WirelessPart p) {
        ccrs.reset();
        ccrs.setBrightness(p.world(), p.pos());

        Transformation t = new Translation(p.pos());
        ccrs.setPipeline(p.rotationT().at(center).with(t), base_icont[p.textureSet()], rlm);
        BlockRenderer.renderCuboid(ccrs, p.baseRenderBounds, p.baseRenderMask);
        models[p.modelId()][p.side() << 2 | p.rotation()].render(ccrs, t, model_icont);
    }

    public static void renderFreq(Vector3 pos, TransceiverPart p) {
        GlStateManager.pushMatrix();

        pos.copy().add(center).translation().glApply();
        p.rotationT().glApply();

        renderFreq(p.getFreq());
        GlStateManager.rotate(180, 0, 1, 0);
        renderFreq(p.getFreq());

        GlStateManager.popMatrix();
    }

    private static void renderFreq(int freq) {
        float scale = 1 / 64F;

        GlStateManager.pushMatrix();
        GlStateManager.rotate(90, 0, 1, 0);
        GlStateManager.rotate(90, 1, 0, 0);
        GlStateManager.translate(0, -5 / 16D, 0.374);
        GlStateManager.scale(scale, scale, scale);

        FontRenderer font = Minecraft.getMinecraft().fontRenderer;
        String s = Integer.toString(freq);
        GlStateManager.depthMask(false);
        font.drawString(s, -font.getStringWidth(s) / 2, 0, 0);
        GlStateManager.depthMask(true);

        GlStateManager.popMatrix();
    }

    public static void renderPearl(CCRenderState ccrs, Vector3 pos, WirelessPart p) {
        GlStateManager.pushMatrix();

        pos.translation().glApply();
        p.rotationT().at(center).glApply();
        p.getPearlPos().translation().glApply();
        p.getPearlRotation().glApply();
        new Scale(p.getPearlScale()).glApply();
        float light = 1;
        if (p.tile() != null) {
            GlStateManager.rotate((float) (p.getPearlSpin() * MathHelper.todeg), 0, 1, 0);
            light = p.getPearlLight();
        }

        GlStateManager.disableLighting();
        ccrs.reset();
        TextureUtils.changeTexture("wrcbe:textures/hedronmap.png");
        ccrs.pullLightmap();
        ccrs.colour = Colour.packRGBA(light, light, light, 1);
        ccrs.startDrawing(4, DefaultVertexFormats.POSITION_TEX_NORMAL);
        CCModelLibrary.icosahedron4.render(ccrs);
        ccrs.draw();
        GlStateManager.enableLighting();

        GlStateManager.popMatrix();
    }

    public static TextureAtlasSprite getBreakingIcon(int tex) {
        return base_icont[tex].icons[1];
    }
}
