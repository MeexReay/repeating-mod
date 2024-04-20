package themixray.repeating.mod.events;

import net.minecraft.util.math.BlockPos;
import themixray.repeating.mod.RepeatingMod;

public class RecordBlockBreakEvent extends RecordEvent {
    public BlockPos pos;

    public static RecordBlockBreakEvent fromArgs(String[] a) {
        return new RecordBlockBreakEvent(new BlockPos(
                Integer.parseInt(a[0]),
                Integer.parseInt(a[1]),
                Integer.parseInt(a[2])));
    }

    public RecordBlockBreakEvent(
            BlockPos pos) {
        this.pos = pos;
    }

    public void replay() {
        RepeatingMod.client.interactionManager.breakBlock(pos);
    }

    public String serialize() {
        return "b=" + pos.getX() + "&" + pos.getY() + "&" + pos.getZ();
    }

    public String getType() {
        return "block_break";
    }
}
