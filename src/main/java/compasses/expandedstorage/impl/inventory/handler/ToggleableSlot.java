package compasses.expandedstorage.impl.inventory.handler;

import net.minecraft.world.Container;
import net.minecraft.world.inventory.Slot;

public class ToggleableSlot extends Slot {
    private boolean active;
    private int mutableX;
    private int mutableY;

    public ToggleableSlot(Container container, int slot, int x, int y, boolean active) {
        super(container, slot, x, y);
        this.active = active;
    }

    @Override
    public boolean isActive() {
        return active;
    }

    public void toggleActive() {
        active = !active;
    }

    public int getX() {
        return mutableX;
    }
    public int getY() {
        return mutableY;
    }
    public void setX(int x) {
        this.mutableX = x;
    }
    public void setY(int y) {    
        this.mutableY = y;
    }
}
