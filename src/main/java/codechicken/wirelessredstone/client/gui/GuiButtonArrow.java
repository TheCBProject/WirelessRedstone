package codechicken.wirelessredstone.client.gui;

import codechicken.lib.colour.ColourARGB;
import codechicken.core.gui.GuiCCButton;

import codechicken.lib.texture.TextureUtils;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.VertexBuffer;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;

public class GuiButtonArrow extends GuiCCButton
{
    public GuiButtonArrow(int x, int y, int w, int h, int arrow)
    {
        super(x, y, w, h, "");
        setArrowDirection(arrow);
    }
    
    public void setArrowDirection(int dir)
    {
        arrowdirection = dir;
    }
    
    @Override
    public void draw(int mousex, int mousey, float frame)
    {
        if(!visible)
            return;
        
        drawArrow(x + width / 2 - 2, y + (height - 8) / 2, getTextColour(mousex, mousey));
    }
    
    private void drawArrow(int x, int y, int colour)
    {
        TextureUtils.changeTexture("wrcbe:textures/gui/arrow.png");
        
        new ColourARGB(colour).glColour();
        Tessellator t = Tessellator.getInstance();
        VertexBuffer buffer = t.getBuffer();
        buffer.begin(7, DefaultVertexFormats.POSITION_TEX);
        buffer.pos(x + 0, y + 8, zLevel).tex( arrowdirection * 0.25, 1).endVertex();
        buffer.pos(x + 8, y + 8, zLevel).tex( (arrowdirection + 1) * 0.25, 1).endVertex();
        buffer.pos(x + 8, y + 0, zLevel).tex( (arrowdirection + 1) * 0.25, 0).endVertex();
        buffer.pos(x + 0, y + 0, zLevel).tex( arrowdirection * 0.25, 0).endVertex();
        t.draw();
    }
    
    int arrowdirection;
}
