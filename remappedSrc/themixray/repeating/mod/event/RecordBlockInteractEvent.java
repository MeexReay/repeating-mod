package themixray.repeating.mod.event;

import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;
import themixray.repeating.mod.Main;

public class RecordBlockInteractEvent extends RecordEvent {
    public Hand hand;
    public BlockHitResult hitResult;

    public static RecordBlockInteractEvent fromArgs(String[] a) {
        return new RecordBlockInteractEvent(
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

    public RecordBlockInteractEvent(Hand hand, BlockHitResult hitResult) {
        this.hand = hand;
        this.hitResult = hitResult;
    }

    public void replay() {
        Main.client.interactionManager.interactBlock(Main.client.player, hand, hitResult);
    }

    public String serialize() {
        return "i=" + hitResult.getBlockPos().getX() + "&" + hitResult.getBlockPos().getY() + "&" + hitResult.getBlockPos().getZ() +
                "&" + (hitResult.isInsideBlock() ? "1" : "0") + "&" + hitResult.getSide().getId() + "&" + hand.name();
    }

    public String getType() {
        return "block_interact";
    }
}
