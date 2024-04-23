package themixray.repeating.mod.event.events;

import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;
import themixray.repeating.mod.Main;
import themixray.repeating.mod.event.RecordEvent;

public class BlockInteractEvent extends RecordEvent {
    public Hand hand;
    public BlockHitResult hitResult;

    public static BlockInteractEvent deserialize(String[] a) {
        return new BlockInteractEvent(
                Hand.valueOf(a[5]),
                new BlockHitResult(new Vec3d(
                        Double.parseDouble(a[0]),
                        Double.parseDouble(a[1]),
                        Double.parseDouble(a[2])),
                        Direction.byId(Integer.parseInt(a[4])),
                        new BlockPos(
                                Integer.parseInt(a[0]),
                                Integer.parseInt(a[1]),
                                Integer.parseInt(a[2])),
                        a[3].equals("1")));
    }

    public BlockInteractEvent(Hand hand, BlockHitResult hitResult) {
        this.hand = hand;
        this.hitResult = hitResult;
    }

    public void replay() {
        if (Main.client.interactionManager != null) {
            Main.client.interactionManager.interactBlock(Main.client.player, hand, hitResult);
        }
    }

    protected String[] serializeArgs() {
        return new String[]{
                String.valueOf(hitResult.getBlockPos().getX()),
                String.valueOf(hitResult.getBlockPos().getY()),
                String.valueOf(hitResult.getBlockPos().getZ()),
                (hitResult.isInsideBlock() ? "1" : "0"),
                String.valueOf(hitResult.getSide().getId()),
                hand.name()
        };
    }
}
