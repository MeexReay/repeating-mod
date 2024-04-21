package themixray.repeating.mod.events;

import net.minecraft.entity.MovementType;
import net.minecraft.util.math.Vec3d;
import themixray.repeating.mod.Main;

public class RecordMoveEvent extends RecordEvent {
    public Vec3d vec;
    public float yaw;
    public float pitch;

    public static RecordMoveEvent fromArgs(String[] a) {
        return new RecordMoveEvent(new Vec3d(
                Double.parseDouble(a[0]),
                Double.parseDouble(a[1]),
                Double.parseDouble(a[2])),
                Float.parseFloat(a[3]),
                Float.parseFloat(a[4]));
    }

    public RecordMoveEvent(Vec3d vec, float yaw, float pitch) {
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

    public String serialize() {
        return "m=" + vec.getX() + "&" + vec.getY() + "&" + vec.getZ() + "&" + yaw + "&" + pitch;
    }

    public String getType() {
        return "move";
    }
}
