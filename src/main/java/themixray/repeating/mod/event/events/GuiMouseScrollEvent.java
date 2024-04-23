package themixray.repeating.mod.event.events;

import themixray.repeating.mod.Main;
import themixray.repeating.mod.event.RecordEvent;

public class GuiMouseScrollEvent extends RecordEvent {
    private double mouseX;
    private double mouseY;
    private double amount;

    public GuiMouseScrollEvent(double mouseX, double mouseY, double amount) {
        this.mouseX = mouseX;
        this.mouseY = mouseY;
        this.amount = amount;
    }

    public void replay() {
        if (Main.client.currentScreen != null) {
            Main.client.currentScreen.mouseScrolled(mouseX, mouseY, amount);
        }
    }

    protected String[] serializeArgs() {
        return new String[] {
            String.valueOf(mouseX),
            String.valueOf(mouseY),
            String.valueOf(amount)
        };
    }

    public static GuiMouseScrollEvent deserialize(String[] args) {
        return new GuiMouseScrollEvent(
            Double.parseDouble(args[0]),
            Double.parseDouble(args[1]),
            Double.parseDouble(args[2])
        );
    }
}
