package themixray.repeating.mod.event.events;

import net.minecraft.entity.MovementType;
import net.minecraft.util.math.Vec3d;
import themixray.repeating.mod.Main;
import themixray.repeating.mod.event.RecordEvent;

public class MoveEvent extends RecordEvent {
    public Vec3d vec;
    public float yaw;
    public float pitch;

    public static MoveEvent deserialize(String[] a) {
        return new MoveEvent(new Vec3d(
                Double.parseDouble(a[0]),
                Double.parseDouble(a[1]),
                Double.parseDouble(a[2])),
                Float.parseFloat(a[3]),
                Float.parseFloat(a[4]));
    }

    public MoveEvent(Vec3d vec, float yaw, float pitch) {
        this.vec = vec;
        this.yaw = yaw;
        this.pitch = pitch;
    }

    public void replay() {
        Vec3d p = Main.client.player.getPos();
        Vec3d v = new Vec3d(vec.getX() - p.getX(), vec.getY() - p.getY(), vec.getZ() - p.getZ());
        Main.client.player.move(MovementType.SELF, v);
        Main.client.player.setYaw(yaw);
        Main.client.player.setPitch(pitch);
    }

    protected String[] serializeArgs() {
        return new String[]{
                String.valueOf(vec.getX()),
                String.valueOf(vec.getZ()),
                String.valueOf(yaw),
                String.valueOf(pitch)
        };
    }
}
