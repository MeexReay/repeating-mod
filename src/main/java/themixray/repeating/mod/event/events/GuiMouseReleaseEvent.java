package themixray.repeating.mod.event.events;

import themixray.repeating.mod.Main;
import themixray.repeating.mod.event.RecordEvent;

public class GuiMouseReleaseEvent extends RecordEvent {
    private double mouseX;
    private double mouseY;
    private int button;

    public GuiMouseReleaseEvent(double mouseX, double mouseY, int button) {
        this.mouseX = mouseX;
        this.mouseY = mouseY;
        this.button = button;
    }

    public void replay() {
        if (Main.client.currentScreen != null) {
            Main.client.currentScreen.mouseReleased(mouseX, mouseY, button);
        }
    }

    protected String[] serializeArgs() {
        return new String[] {
            String.valueOf(mouseX),
            String.valueOf(mouseY),
            String.valueOf(button)
        };
    }

    public static GuiMouseReleaseEvent deserialize(String[] args) {
        return new GuiMouseReleaseEvent(
            Double.parseDouble(args[0]),
            Double.parseDouble(args[1]),
            Integer.parseInt(args[2])
        );
    }
}
