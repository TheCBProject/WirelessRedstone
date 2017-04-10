package codechicken.wirelessredstone.entity;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Random;

import codechicken.lib.util.CommonUtils;
import codechicken.wirelessredstone.WirelessRedstone;
import codechicken.wirelessredstone.api.ITileJammer;
import codechicken.wirelessredstone.api.ITileWireless;
import codechicken.wirelessredstone.network.WRServerPH;
import codechicken.wirelessredstone.manager.RedstoneEther;
import codechicken.wirelessredstone.manager.RedstoneEtherServer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.RayTraceResult.Type;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

import codechicken.lib.config.ConfigTag;
import codechicken.lib.vec.Vector3;

public class WirelessBolt
{
    public class BoltPoint
    {
        public BoltPoint(Vector3 basepoint, Vector3 offsetvec) {
            this.point = basepoint.copy().add(offsetvec);
            this.basepoint = basepoint;
            this.offsetvec = offsetvec;
        }

        public Vector3 point;
        Vector3 basepoint;
        Vector3 offsetvec;
    }

    public class SegmentSorter implements Comparator<Segment>
    {
        public int compare(Segment o1, Segment o2) {
            if (o1.splitno != o2.splitno) return o1.splitno < o2.splitno ? -1 : 1;
            if (o1.segmentno != o2.segmentno) return o1.segmentno < o2.segmentno ? -1 : 1;
            return 0;
        }
    }

    public class SegmentLightSorter implements Comparator<Segment>
    {
        public int compare(Segment o1, Segment o2) {
            return o1.light != o2.light ? o1.light < o2.light ? -1 : 1 : 0;
        }
    }

    public class Segment
    {
        public Segment(BoltPoint start, BoltPoint end, float light, int segmentnumber, int splitnumber) {
            this.startpoint = start;
            this.endpoint = end;
            this.light = light;
            this.segmentno = segmentnumber;
            this.splitno = splitnumber;

            calcDiff();
        }

        public Segment(Vector3 start, Vector3 end) {
            this(new BoltPoint(start, new Vector3(0, 0, 0)), new BoltPoint(end, new Vector3(0, 0, 0)), 1, 0, 0);
        }

        public void calcDiff() {
            diff = endpoint.point.copy().subtract(startpoint.point);
        }

        public void calcEndDiffs() {
            if (prev != null) {
                Vector3 prevdiffnorm = prev.diff.copy().normalize();
                Vector3 thisdiffnorm = diff.copy().normalize();

                prevdiff = thisdiffnorm.copy().add(prevdiffnorm).normalize();
                sinprev = (float) Math.sin(thisdiffnorm.angle(prevdiffnorm.negate()) / 2);
            } else {
                prevdiff = diff.copy().normalize();
                sinprev = 1;
            }

            if (next != null) {
                Vector3 nextdiffnorm = next.diff.copy().normalize();
                Vector3 thisdiffnorm = diff.copy().normalize();

                nextdiff = thisdiffnorm.add(nextdiffnorm).normalize();
                sinnext = (float) Math.sin(thisdiffnorm.angle(nextdiffnorm.negate()) / 2);
            } else {
                nextdiff = diff.copy().normalize();
                sinnext = 1;
            }
        }

        public String toString() {
            return startpoint.point.toString() + " " + endpoint.point.toString();
        }

        public BoltPoint startpoint;
        public BoltPoint endpoint;

        public Vector3 diff;

        public Segment prev;
        public Segment next;

        public Vector3 nextdiff;
        public Vector3 prevdiff;

        public float sinprev;
        public float sinnext;
        public float light;

        public int segmentno;
        public int splitno;
    }

    public ArrayList<Segment> segments = new ArrayList<>();
    public Vector3 start;
    public Vector3 end;
    BlockPos target;
    HashMap<Integer, Integer> splitparents = new HashMap<>();

    public double length;
    public int numsegments0;
    private int numsplits;
    private boolean finalized;
    private boolean canhittarget = true;
    private Random rand;
    public long seed;

    public int particleAge;
    public int particleMaxAge;
    public boolean isDead;
    private AxisAlignedBB boundingBox;

    public World world;
    private Entity wrapper;
    private RedstoneEther ether;

    public static ArrayList<WirelessBolt> serverboltlist = new ArrayList<>();
    public static ArrayList<WirelessBolt> clientboltlist = new ArrayList<>();

    public static final float speed = 3;//ticks per metre
    public static final int fadetime = 20;

    public static int playerdamage;
    public static int entitydamage;

    public WirelessBolt(World world, Vector3 jammervec, Vector3 targetvec, long seed) {
        this.world = world;
        this.seed = seed;
        this.rand = new Random(seed);
        ether = RedstoneEther.get(world.isRemote);

        start = jammervec;
        end = targetvec;

        numsegments0 = 1;

        length = end.copy().subtract(start).mag();
        particleMaxAge = fadetime + rand.nextInt(fadetime) - (fadetime / 2);
        particleAge = -(int) (length * speed);

        boundingBox = new AxisAlignedBB(Math.min(start.x, end.x), Math.min(start.y, end.y), Math.min(start.z, end.z),
                Math.max(start.x, end.x), Math.max(start.y, end.y), Math.max(start.z, end.z))
                .expand(length / 2, length / 2, length / 2);

        segments.add(new Segment(start, end));
    }

    public static Vector3 getFocalPoint(ITileWireless tile) {
        return Vector3.fromTileCenter((TileEntity) tile).add(tile.getFocalPoint());
    }

    public static Vector3 getFocalPoint(ITileJammer tile) {
        return Vector3.fromTileCenter((TileEntity) tile).add(tile.getFocalPoint());
    }

    public WirelessBolt(World world, Vector3 jammer, ITileWireless target, long seed) {
        this(world, jammer, getFocalPoint(target), seed);
        this.target = new BlockPos(((TileEntity) target).getPos());
    }

    public void setWrapper(Entity entity) {
        wrapper = entity;
    }

    public void fractal(int splits, double amount, double splitchance, double splitlength, double splitangle) {
        if (finalized) {
            return;
        }

        ArrayList<Segment> oldsegments = segments;
        segments = new ArrayList<>();

        Segment prev = null;

        for (Iterator<Segment> iterator = oldsegments.iterator(); iterator.hasNext(); ) {
            Segment segment = iterator.next();
            prev = segment.prev;

            Vector3 subsegment = segment.diff.copy().multiply(1F / splits);

            BoltPoint[] newpoints = new BoltPoint[splits + 1];

            Vector3 startpoint = segment.startpoint.point;
            newpoints[0] = segment.startpoint;
            newpoints[splits] = segment.endpoint;

            for (int i = 1; i < splits; i++) {
                Vector3 randoff = segment.diff.copy().perpendicular().normalize().rotate(rand.nextFloat() * 360, segment.diff);
                randoff.multiply((rand.nextFloat() - 0.5F) * amount * 2);

                Vector3 basepoint = startpoint.copy().add(subsegment.copy().multiply(i));

                newpoints[i] = new BoltPoint(basepoint, randoff);
            }
            for (int i = 0; i < splits; i++) {
                Segment next = new Segment(newpoints[i], newpoints[i + 1], segment.light, segment.segmentno * splits + i, segment.splitno);
                next.prev = prev;
                if (prev != null) {
                    prev.next = next;
                }

                if (i != 0 && rand.nextFloat() < splitchance) {
                    Vector3 splitrot = next.diff.copy().xCrossProduct().rotate(rand.nextFloat() * 360, next.diff);
                    Vector3 diff = next.diff.copy().rotate((rand.nextFloat() * 0.66F + 0.33F) * splitangle, splitrot).multiply(splitlength);

                    numsplits++;
                    splitparents.put(numsplits, next.splitno);

                    Segment split = new Segment(newpoints[i], new BoltPoint(newpoints[i + 1].basepoint, newpoints[i + 1].offsetvec.copy().add(diff)), segment.light / 2F, next.segmentno, numsplits);
                    split.prev = prev;

                    segments.add(split);
                }

                prev = next;
                segments.add(next);
            }
            if (segment.next != null) {
                segment.next.prev = prev;
            }
        }

        numsegments0 *= splits;
    }

    public void defaultFractal() {
        fractal(2, length / 1.5, 0.7F, 0.7F, 45);
        fractal(2, length / 4, 0.5F, 0.8F, 50);
        fractal(2, length / 15, 0.5F, 0.9F, 55);
        fractal(2, length / 30, 0.5F, 1.0F, 60);
        fractal(2, length / 60, 0, 0, 0);
        fractal(2, length / 100, 0, 0, 0);
        fractal(2, length / 400, 0, 0, 0);
    }

    private float rayTraceResistance(Vector3 start, Vector3 end, float prevresistance) {
        RayTraceResult hit = world.rayTraceBlocks(start.vec3(), end.vec3());

        if (hit == null)
            return prevresistance;

        if (hit.typeOfHit == Type.BLOCK) {
            IBlockState state = world.getBlockState(hit.getBlockPos());
            if (state.getBlock().isAir(state, world, hit.getBlockPos()))
                return prevresistance;
            
            /*if(Block.blocksList[blockID] instanceof ISpecialResistance) 
            {
                ISpecialResistance isr = (ISpecialResistance) Block.blocksList[blockID];
                 return prevresistance + (isr.getSpecialExplosionResistance(world, hit.blockX, hit.blockY, hit.blockZ,
                         start.x, start.y, start.z, wrapper) + 0.3F);
            } 
            else 
            {*/
            return prevresistance + state.getBlock().getExplosionResistance(wrapper) + 0.3F;
            //}
        }
        return prevresistance;
    }

    private void vecBBDamageSegment(Vector3 start, Vector3 end, ArrayList<Entity> entitylist) {
        Vec3d start3D = start.vec3();
        Vec3d end3D = end.vec3();

        for (Iterator<Entity> iterator = entitylist.iterator(); iterator.hasNext(); ) {
            Entity entity = iterator.next();
            if (entity instanceof EntityLivingBase &&
                    (entity.getEntityBoundingBox().isVecInside(start3D) || entity.getEntityBoundingBox().isVecInside(end3D))) {
                if (entity instanceof EntityPlayer)
                    entity.attackEntityFrom(WirelessRedstone.damageBolt, playerdamage);
                else
                    entity.attackEntityFrom(WirelessRedstone.damageBolt, entitydamage);

                ether.jamEntity((EntityLivingBase) entity, true);
            }
        }
    }

    private void bbTestEntityDamage() {
        if (world.isRemote)
            return;

        int newestsegment = (int) ((particleAge + (int) (length * speed)) / (float) (int) (length * speed) * numsegments0);

        List<Entity> nearentities = world.getEntitiesWithinAABBExcludingEntity(wrapper, boundingBox);
        if (nearentities.size() == 0)
            return;

        for (Iterator<Segment> iterator = segments.iterator(); iterator.hasNext(); ) {
            Segment segment = iterator.next();

            if (segment.segmentno > newestsegment) {
                continue;
            }

            vecBBDamageSegment(segment.startpoint.point, segment.endpoint.point, (ArrayList<Entity>) nearentities);
        }
    }

    private void calculateCollisionAndDiffs() {
        HashMap<Integer, Integer> lastactivesegment = new HashMap<>();

        Collections.sort(segments, new SegmentSorter());

        int lastsplitcalc = 0;
        int lastactiveseg = 0;//unterminated
        float splitresistance = 0;

        for (Iterator<Segment> iterator = segments.iterator(); iterator.hasNext(); )//iterate each branch and do tests for the last active split
        {
            Segment segment = iterator.next();
            if (segment.splitno > lastsplitcalc)//next split trace
            {
                lastactivesegment.put(lastsplitcalc, lastactiveseg);//put last active segment for split in map
                //reset                
                lastsplitcalc = segment.splitno;
                lastactiveseg = lastactivesegment.get(splitparents.get(segment.splitno));//last active is parent
                splitresistance = lastactiveseg < segment.segmentno ? 50 : 0;//already teminated if the last parent segment was before the start of this one
            }
            if (splitresistance >= 40 * segment.light) {
                continue;
            }
            splitresistance = rayTraceResistance(segment.startpoint.point, segment.endpoint.point, splitresistance);
            lastactiveseg = segment.segmentno;
        }
        lastactivesegment.put(lastsplitcalc, lastactiveseg);//put last active segment for split in map

        lastsplitcalc = 0;
        lastactiveseg = lastactivesegment.get(0);
        for (Iterator<Segment> iterator = segments.iterator(); iterator.hasNext(); )//iterate each segment and kill off largeones
        {
            Segment segment = iterator.next();
            if (lastsplitcalc != segment.splitno) {
                lastsplitcalc = segment.splitno;
                lastactiveseg = lastactivesegment.get(segment.splitno);
            }
            if (segment.segmentno > lastactiveseg) {
                iterator.remove();
            }
            segment.calcEndDiffs();
        }

        if (lastactivesegment.get(0) + 1 < numsegments0) {
            canhittarget = false;
        }
    }

    public void finalizeBolt() {
        if (finalized) {
            return;
        }
        finalized = true;

        calculateCollisionAndDiffs();

        Collections.sort(segments, new SegmentLightSorter());

        if (world.isRemote)
            clientboltlist.add(this);
        else {
            serverboltlist.add(this);
            WRServerPH.sendWirelessBolt(this);
        }
    }

    private void jamTile() {
        if (world.isRemote || target == null)
            return;

        RedstoneEtherServer ether = (RedstoneEtherServer) this.ether;
        if (canhittarget) {
            TileEntity tile = RedstoneEther.getTile(world, target);
            if (tile == null || !(tile instanceof ITileWireless)) {
                ether.unjamTile(world, target.getX(), target.getY(), target.getZ());
                return;
            }
            ITileWireless wirelesstile = (ITileWireless) tile;
            int freq = wirelesstile.getFreq();
            if (freq == 0) {
                ether.unjamTile(world, target.getX(), target.getY(), target.getZ());
                return;
            }
            ether.jamNode(world, target, CommonUtils.getDimension(world), freq);
            wirelesstile.jamTile();
        } else {
            ether.unjamTile(world, target.getX(), target.getY(), target.getZ());
        }
    }

    public void onUpdate() {
        particleAge++;

        bbTestEntityDamage();

        if (particleAge == 0) {
            jamTile();
        }

        if (particleAge >= particleMaxAge) {
            isDead = true;
        }
    }

    public static void init(ConfigTag rpconfig) {
        ConfigTag boltconfig = rpconfig.getTag("boltEffect").useBraces();
        ConfigTag damageconfig = boltconfig.getTag("damage").setComment("Damages are in half hearts:If an entity gets knocked into another bolt it may suffer multiple hits");
        entitydamage = damageconfig.getTag("entity").setComment("").getIntValue(5);
        playerdamage = damageconfig.getTag("player").setComment("").getIntValue(3);
    }

    public static void update(List<WirelessBolt> boltlist) {
        for (Iterator<WirelessBolt> iterator = boltlist.iterator(); iterator.hasNext(); ) {
            WirelessBolt bolt = iterator.next();
            bolt.onUpdate();
            if (bolt.isDead) {
                iterator.remove();
            }
        }
    }
}
