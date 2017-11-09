package codechicken.wirelessredstone.entity;

import codechicken.lib.math.MathHelper;
import codechicken.lib.util.CommonUtils;
import codechicken.lib.vec.Vector3;
import codechicken.wirelessredstone.api.ITileWireless;
import codechicken.wirelessredstone.manager.RedstoneEther;
import codechicken.wirelessredstone.manager.RedstoneEtherAddons;
import codechicken.wirelessredstone.network.WRServerPH;
import net.minecraft.block.Block;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

import java.util.Iterator;
import java.util.List;
import java.util.TreeSet;

public class EntityREP extends Entity {

    public EntityREP(World world) {
        super(world);
        xTileREP = -1;
        yTileREP = -1;
        zTileREP = -1;
        setSize(0.25F, 0.25F);
    }

    protected void entityInit() {
    }

    public boolean isInRangeToRenderDist(double d) {
        return true;
    }

    public EntityREP(World world, EntityLivingBase entityliving) {
        super(world);
        xTileREP = -1;
        yTileREP = -1;
        zTileREP = -1;
        shootingEntity = entityliving;
        setSize(0.25F, 0.25F);
        setLocationAndAngles(entityliving.posX, entityliving.posY + entityliving.getEyeHeight(), entityliving.posZ, entityliving.rotationYaw, entityliving.rotationPitch);
        posX -= MathHelper.cos((rotationYaw / 180F) * 3.141593F) * 0.16F;
        posY -= 0.10000000149011612D;
        posZ -= MathHelper.sin((rotationYaw / 180F) * 3.141593F) * 0.16F;
        setPosition(posX, posY, posZ);
        float f = 0.4F;
        motionX = -MathHelper.sin((rotationYaw / 180F) * 3.141593F) * MathHelper.cos((rotationPitch / 180F) * 3.141593F) * f;
        motionZ = MathHelper.cos((rotationYaw / 180F) * 3.141593F) * MathHelper.cos((rotationPitch / 180F) * 3.141593F) * f;
        motionY = -MathHelper.sin((rotationPitch / 180F) * 3.141593F) * f;
        setREPHeading(motionX, motionY, motionZ, 1.5F, 1.0F);
    }

    public EntityREP(World world, double d, double d1, double d2) {
        super(world);
        xTileREP = -1;
        yTileREP = -1;
        zTileREP = -1;
        setSize(0.25F, 0.25F);
        setPosition(d, d1, d2);
    }

    public void setREPHeading(double d, double d1, double d2, float f, float f1) {
        float f2 = MathHelper.sqrt(d * d + d1 * d1 + d2 * d2);
        d /= f2;
        d1 /= f2;
        d2 /= f2;
        d += rand.nextGaussian() * 0.0074999998323619366D * f1;
        d1 += rand.nextGaussian() * 0.0074999998323619366D * f1;
        d2 += rand.nextGaussian() * 0.0074999998323619366D * f1;
        d *= f;
        d1 *= f;
        d2 *= f;
        motionX = d;
        motionY = d1;
        motionZ = d2;
        float f3 = MathHelper.sqrt(d * d + d2 * d2);
        prevRotationYaw = rotationYaw = (float) ((Math.atan2(d, d2) * 180D) / 3.1415927410125732D);
        prevRotationPitch = rotationPitch = (float) ((Math.atan2(d1, f3) * 180D) / 3.1415927410125732D);
        ticksInGroundREP = 0;
    }

    public void setVelocity(double d, double d1, double d2) {
        motionX = d;
        motionY = d1;
        motionZ = d2;
        if (prevRotationPitch == 0.0F && prevRotationYaw == 0.0F) {
            float f = MathHelper.sqrt(d * d + d2 * d2);
            prevRotationYaw = rotationYaw = (float) ((Math.atan2(d, d2) * 180D) / 3.1415927410125732D);
            prevRotationPitch = rotationPitch = (float) ((Math.atan2(d1, f) * 180D) / 3.1415927410125732D);
        }
    }

    public void onUpdate() {
        lastTickPosX = posX;
        lastTickPosY = posY;
        lastTickPosZ = posZ;
        super.onUpdate();
        if (shakeREP > 0) {
            shakeREP--;
        }
        if (inGroundREP) {
            Block block = world.getBlockState(new BlockPos(xTileREP, yTileREP, zTileREP)).getBlock();
            if (block != inTileREP) {
                inGroundREP = false;
                motionX *= rand.nextFloat() * 0.2F;
                motionY *= rand.nextFloat() * 0.2F;
                motionZ *= rand.nextFloat() * 0.2F;
                ticksInGroundREP = 0;
                ticksInAirREP = 0;
            } else {
                ticksInGroundREP++;
                if (ticksInGroundREP == 1200) {
                    setDead();
                }
                return;
            }
        } else {
            ticksInAirREP++;
        }
        Vec3d vec3d = new Vec3d(posX, posY, posZ);
        Vec3d vec3d1 = new Vec3d(posX + motionX, posY + motionY, posZ + motionZ);
        RayTraceResult hit = world.rayTraceBlocks(vec3d, vec3d1);
        vec3d = new Vec3d(posX, posY, posZ);
        vec3d1 = new Vec3d(posX + motionX, posY + motionY, posZ + motionZ);
        if (hit != null) {
            vec3d1 = new Vec3d(hit.hitVec.x, hit.hitVec.y, hit.hitVec.z);
        }

        if (!world.isRemote) {
            Entity entity = null;
            List<Entity> list = world.getEntitiesWithinAABBExcludingEntity(this, getEntityBoundingBox().expand(motionX, motionY, motionZ).expand(1.0D, 1.0D, 1.0D));
            double d = 0.0D;
            for (Entity entity1 : list) {
                if (!entity1.canBeCollidedWith() || entity1 == shootingEntity && ticksInAirREP < 5) {
                    continue;
                }

                float f4 = 0.3F;
                AxisAlignedBB axisalignedbb = entity1.getEntityBoundingBox().expand(f4, f4, f4);
                RayTraceResult hit1 = axisalignedbb.calculateIntercept(vec3d, vec3d1);
                if (hit1 == null) {
                    continue;
                }

                double d1 = vec3d.distanceTo(hit1.hitVec);
                if (d1 < d || d == 0.0D) {
                    entity = entity1;
                    d = d1;
                }
            }

            if (entity != null) {
                hit = new RayTraceResult(entity);
            }
        }
        if (hit != null) {
            detonate();
            setDead();
        }
        posX += motionX;
        posY += motionY;
        posZ += motionZ;
        float f = MathHelper.sqrt(motionX * motionX + motionZ * motionZ);
        rotationYaw = (float) ((Math.atan2(motionX, motionZ) * 180D) / 3.1415927410125732D);
        for (rotationPitch = (float) ((Math.atan2(motionY, f) * 180D) / 3.1415927410125732D); rotationPitch - prevRotationPitch < -180F; prevRotationPitch -= 360F) {
        }
        for (; rotationPitch - prevRotationPitch >= 180F; prevRotationPitch += 360F) {
        }
        for (; rotationYaw - prevRotationYaw < -180F; prevRotationYaw -= 360F) {
        }
        for (; rotationYaw - prevRotationYaw >= 180F; prevRotationYaw += 360F) {
        }
        rotationPitch = prevRotationPitch + (rotationPitch - prevRotationPitch) * 0.2F;
        rotationYaw = prevRotationYaw + (rotationYaw - prevRotationYaw) * 0.2F;
        float f1 = 0.99F;
        float f2 = 0.03F;
        if (isInWater()) {
            for (int k = 0; k < 4; k++) {
                float f3 = 0.25F;
                world.spawnParticle(EnumParticleTypes.WATER_BUBBLE, posX - motionX * f3, posY - motionY * f3, posZ - motionZ * f3, motionX, motionY, motionZ);
            }

            f1 = 0.8F;
        }
        motionX *= f1;
        motionY *= f1;
        motionZ *= f1;
        motionY -= f2;
        setPosition(posX, posY, posZ);
    }

    public void detonate() {
        if (world.isRemote) {
            return;
        }

        int boltsgen = 0;
        List<Entity> entities = world.getEntitiesWithinAABBExcludingEntity(this, new AxisAlignedBB(posX - 10, posY - 10, posZ - 10, posX + 10, posY + 10, posZ + 10));
        for (Iterator<Entity> iterator = entities.iterator(); iterator.hasNext(); ) {
            if (boltsgen > maxbolts) {
                break;
            }
            Entity target = iterator.next();

            if (!(target instanceof EntityLivingBase) || Vector3.fromEntity(this).subtract(Vector3.fromEntity(target)).magSquared() > 100) {
                continue;
            }

            WirelessBolt bolt = new WirelessBolt(world, Vector3.fromEntity(this), Vector3.fromEntity(target), world.rand.nextLong());
            bolt.defaultFractal();
            bolt.finalizeBolt();
            bolt = new WirelessBolt(world, Vector3.fromEntity(this), Vector3.fromEntity(target), world.rand.nextLong());
            bolt.defaultFractal();
            bolt.finalizeBolt();
            boltsgen += 2;
        }

        TreeSet<BlockPos> nodes = RedstoneEther.server().getNodesInRangeofPoint(CommonUtils.getDimension(world), Vector3.fromEntity(this), RedstoneEther.jammerrange, true);
        for (Iterator<BlockPos> iterator = nodes.iterator(); iterator.hasNext(); ) {
            if (boltsgen > maxbolts) {
                break;
            }
            BlockPos node = iterator.next();
            ITileWireless tile = (ITileWireless) RedstoneEther.getTile(world, node);

            WirelessBolt bolt = new WirelessBolt(world, Vector3.fromEntity(this), tile, world.rand.nextLong());
            bolt.defaultFractal();
            bolt.finalizeBolt();
            boltsgen++;
        }

        for (int i = 0; i < 16; i++) {
            if (boltsgen > maxbolts) {
                break;
            }
            WirelessBolt bolt = new WirelessBolt(world, Vector3.fromEntity(this), new Vector3(posX + 20 * world.rand.nextFloat() - 10, posY + 20 * world.rand.nextFloat() - 10, posZ + 20 * world.rand.nextFloat() - 10), world.rand.nextLong());
            bolt.defaultFractal();
            bolt.finalizeBolt();
            boltsgen++;
        }
    }

    @Override
    public void setDead() {
        super.setDead();
        RedstoneEtherAddons.get(world.isRemote).invalidateREP((EntityPlayer) shootingEntity);
        if (!world.isRemote) {
            WRServerPH.sendKillREP(this);
        }
    }

    public void writeEntityToNBT(NBTTagCompound tag) {
        tag.setShort("xTile", (short) xTileREP);
        tag.setShort("yTile", (short) yTileREP);
        tag.setShort("zTile", (short) zTileREP);
        tag.setShort("inTile", (short) Block.getIdFromBlock(inTileREP));
        tag.setByte("shake", (byte) shakeREP);
        tag.setByte("inGround", (byte) (inGroundREP ? 1 : 0));
    }

    public void readEntityFromNBT(NBTTagCompound tag) {
        xTileREP = tag.getShort("xTile");
        yTileREP = tag.getShort("yTile");
        zTileREP = tag.getShort("zTile");
        inTileREP = Block.getBlockById(tag.getShort("inTile") & 0xFFFF);
        shakeREP = tag.getByte("shake") & 0xff;
        inGroundREP = tag.getByte("inGround") == 1;
    }

    public float getShadowSize() {
        return 0.0F;
    }

    private int xTileREP;
    private int yTileREP;
    private int zTileREP;
    private Block inTileREP;
    private boolean inGroundREP;
    public int shakeREP;
    public EntityLivingBase shootingEntity;
    private int ticksInGroundREP;
    private int ticksInAirREP;

    public final int maxbolts = 50;
}
