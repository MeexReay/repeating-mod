package themixray.repeating.mod.event.events;

import net.minecraft.util.math.BlockPos;
import themixray.repeating.mod.Main;
import themixray.repeating.mod.event.RecordEvent;

public class BlockBreakEvent extends RecordEvent {
    public BlockPos pos;

    public BlockBreakEvent(
            BlockPos pos) {
        this.pos = pos;
    }

    public static BlockBreakEvent deserialize(String[] a) {
        return new BlockBreakEvent(new BlockPos(
                Integer.parseInt(a[0]),
                Integer.parseInt(a[1]),
                Integer.parseInt(a[2])));
    }

    protected String[] serializeArgs() {
        return new String[]{
                String.valueOf(pos.getX()),
                String.valueOf(pos.getY()),
                String.valueOf(pos.getZ())
        };
    }

    public void replay() {
        Main.client.interactionManager.breakBlock(pos);
    }
}
