package themixray.repeating.mod.event.events;

import themixray.repeating.mod.Main;
import themixray.repeating.mod.event.RecordEvent;

public class InputEvent extends RecordEvent {
    public Boolean sneaking;
    public Boolean jumping;
    public Boolean pressingForward;
    public Boolean pressingBack;
    public Boolean pressingLeft;
    public Boolean pressingRight;
    public Boolean sprinting;

    public Float movementSideways;
    public Float movementForward;

    public float yaw;
    public float head_yaw;
    public float body_yaw;
    public float pitch;
    public float speed;

    public InputEvent(Boolean sneaking,
                      Boolean jumping,
                      Float movementSideways,
                      Float movementForward,
                      Boolean pressingForward,
                      Boolean pressingBack,
                      Boolean pressingLeft,
                      Boolean pressingRight,
                      float head_yaw,
                      float body_yaw,
                      float head_pitch,
                      Boolean sprinting,
                      float yaw,
                      float speed) {
        this.sneaking = sneaking;
        this.jumping = jumping;
        this.movementSideways = movementSideways;
        this.movementForward = movementForward;
        this.pressingForward = pressingForward;
        this.pressingBack = pressingBack;
        this.pressingLeft = pressingLeft;
        this.pressingRight = pressingRight;
        this.head_yaw = head_yaw;
        this.body_yaw = body_yaw;
        this.pitch = head_pitch;
        this.sprinting = sprinting;
        this.yaw = yaw;
        this.speed = speed;
    }

    public static InputEvent deserialize(String[] a) {
        return new InputEvent(
                (a[0].equals("n") ? null : a[0].equals("1")),
                (a[1].equals("n") ? null : a[1].equals("1")),
                (a[2].equals("n") ? null : Float.parseFloat(a[2])),
                (a[3].equals("n") ? null : Float.parseFloat(a[3])),
                (a[4].equals("n") ? null : a[4].equals("1")),
                (a[5].equals("n") ? null : a[5].equals("1")),
                (a[6].equals("n") ? null : a[6].equals("1")),
                (a[7].equals("n") ? null : a[7].equals("1")),
                Float.parseFloat(a[8]), Float.parseFloat(a[9]),
                Float.parseFloat(a[10]),
                (a[11].equals("n") ? null : a[11].equals("1")),
                Float.parseFloat(a[12]),
                Float.parseFloat(a[13]));
    }

    protected String[] serializeArgs() {
        return new String[] {
                ((sneaking == null) ? "n" : (sneaking ? "1" : "0")),                   // sneaking
                ((jumping == null) ? "n" : (jumping ? "1" : "0")),                     // jumping
                ((movementSideways == null) ? "n" : String.valueOf(movementSideways)), // movement sideways
                ((movementForward == null) ? "n" : String.valueOf(movementForward)),   // movement forward
                ((pressingForward == null) ? "n" : (pressingForward ? "1" : "0")),     // pressing forward
                ((pressingBack == null) ? "n" : (pressingBack ? "1" : "0")),           // pressing back
                ((pressingLeft == null) ? "n" : (pressingLeft ? "1" : "0")),           // pressing left
                ((pressingRight == null) ? "n" : (pressingRight ? "1" : "0")),         // pressing right
                String.valueOf(head_yaw),                                              // head yaw
                String.valueOf(body_yaw),                                              // body yaw
                String.valueOf(pitch),                                                 // pitch
                ((sprinting == null) ? "n" : (sprinting ? "1" : "0")),                 // sprinting
                String.valueOf(yaw),                                                   // yaw
                String.valueOf(speed)                                                  // speed
        };
    }

    public void fillEmpty(InputEvent e) {
        if (sneaking == null) sneaking = e.sneaking;
        if (jumping == null) jumping = e.jumping;
        if (movementSideways == null) movementSideways = e.movementSideways;
        if (movementForward == null) movementForward = e.movementForward;
        if (pressingForward == null) pressingForward = e.pressingForward;
        if (pressingBack == null) pressingBack = e.pressingBack;
        if (pressingLeft == null) pressingLeft = e.pressingLeft;
        if (pressingRight == null) pressingRight = e.pressingRight;
        if (sprinting == null) sprinting = e.sprinting;
    }

    public boolean isEmpty() {
        return sneaking == null &&
                jumping == null &&
                movementSideways == null &&
                movementForward == null &&
                pressingForward == null &&
                pressingBack == null &&
                pressingLeft == null &&
                pressingRight == null &&
                sprinting == null;
    }

    public void replay() {
        Main.input_replay = this;
    }

    public void inputCallback() {
        if (Main.client.player != null) {
            if (sprinting != null && Main.client.player.isSprinting() != sprinting)
                Main.client.player.setSprinting(sprinting);
            if (Main.client.player.getYaw() != yaw)
                Main.client.player.setYaw(yaw);
            if (Main.client.player.getHeadYaw() != head_yaw)
                Main.client.player.setHeadYaw(head_yaw);
            if (Main.client.player.getBodyYaw() != body_yaw)
                Main.client.player.setBodyYaw(body_yaw);
            if (Main.client.player.getPitch() != pitch)
                Main.client.player.setPitch(pitch);
            if (Main.client.player.getMovementSpeed() != speed)
                Main.client.player.setMovementSpeed(speed);
            if (sneaking != null && Main.client.player.input.sneaking != sneaking)
                Main.client.player.input.sneaking = sneaking;
            if (jumping != null && Main.client.player.input.jumping != jumping)
                Main.client.player.input.jumping = jumping;
            if (movementSideways != null && Main.client.player.input.movementSideways != movementSideways)
                Main.client.player.input.movementSideways = movementSideways;
            if (movementForward != null && Main.client.player.input.movementForward != movementForward)
                Main.client.player.input.movementForward = movementForward;
            if (pressingForward != null && Main.client.player.input.pressingForward != pressingForward)
                Main.client.player.input.pressingForward = pressingForward;
            if (pressingBack != null && Main.client.player.input.pressingBack != pressingBack)
                Main.client.player.input.pressingBack = pressingBack;
            if (pressingLeft != null && Main.client.player.input.pressingLeft != pressingLeft)
                Main.client.player.input.pressingLeft = pressingLeft;
            if (pressingRight != null && Main.client.player.input.pressingRight != pressingRight)
                Main.client.player.input.pressingRight = pressingRight;
        }
    }
}
