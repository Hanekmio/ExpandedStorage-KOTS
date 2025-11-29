package compasses.expandedstorage.impl.block.entity;

import compasses.expandedstorage.api.EsChestType;
import compasses.expandedstorage.impl.block.AbstractChestBlock;
import compasses.expandedstorage.impl.block.OpenableBlock;
import compasses.expandedstorage.impl.block.entity.extendable.InventoryBlockEntity;
import compasses.expandedstorage.impl.block.entity.extendable.OpenableBlockEntity;
import compasses.expandedstorage.impl.block.misc.DoubleItemAccess;
import compasses.expandedstorage.impl.block.strategies.ItemAccess;
import compasses.expandedstorage.impl.block.strategies.Lockable;
import compasses.expandedstorage.impl.inventory.VariableSidedInventory;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.WorldlyContainer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.DoubleBlockCombiner;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.wrapper.InvWrapper;
import org.jetbrains.annotations.Nullable;

import java.util.function.Function;
import java.util.function.Supplier;

public class OldChestBlockEntity extends InventoryBlockEntity {
    WorldlyContainer cachedDoubleInventory = null;
    private final LazyOptional<IItemHandler> singleHandler = LazyOptional.of(() -> new InvWrapper(this));

    public OldChestBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state, ResourceLocation blockId,
                               Function<OpenableBlockEntity, ItemAccess> access, Supplier<Lockable> lockable) {
        super(type, pos, state, blockId, ((OpenableBlock) state.getBlock()).getInventoryTitle(), ((OpenableBlock) state.getBlock()).getSlotCount());
        this.setItemAccess(access.apply(this));
        this.setLockable(lockable.get());
    }

    public void invalidateDoubleBlockCache() {
        cachedDoubleInventory = null;
        this.getItemAccess().setOther(null);
    }

    public WorldlyContainer getCachedDoubleInventory() {
        return cachedDoubleInventory;
    }

    public void setCachedDoubleInventory(OldChestBlockEntity other) {
        BlockState state = this.getBlockState();
        if (!(state.getBlock() instanceof AbstractChestBlock)) {
            this.cachedDoubleInventory = VariableSidedInventory.of(this.getInventory(), other.getInventory());
            return;
        }

        EsChestType type = state.getValue(AbstractChestBlock.CURSED_CHEST_TYPE);

        if (AbstractChestBlock.getBlockType(type) == DoubleBlockCombiner.BlockType.FIRST) {
            this.cachedDoubleInventory = VariableSidedInventory.of(this.getInventory(), other.getInventory());
        } else {
            this.cachedDoubleInventory = VariableSidedInventory.of(other.getInventory(), this.getInventory());
        }
    }

    @Override
    public void setChanged() {
        super.setChanged();

        BlockState state = this.getBlockState();
        if (state.getBlock() instanceof AbstractChestBlock) {
            if (state.getValue(AbstractChestBlock.CURSED_CHEST_TYPE) != EsChestType.SINGLE) {
                Direction dir = AbstractChestBlock.getDirectionToAttached(state);
                BlockPos otherPos = worldPosition.relative(dir);
                BlockEntity be = level.getBlockEntity(otherPos);
                if (be instanceof OldChestBlockEntity other) {
                    this.setCachedDoubleInventory(other);
                    other.setCachedDoubleInventory(this);
                } else {
                    this.invalidateDoubleBlockCache();
                }
            } else {
                this.invalidateDoubleBlockCache();
            }
        }
    }

    @Override
    public DoubleItemAccess getItemAccess() {
        return (DoubleItemAccess) super.getItemAccess();
    }

    @Override
    public <T> LazyOptional<T> getCapability(Capability<T> cap, @Nullable Direction side) {
        setChanged();
        if (cap == ForgeCapabilities.ITEM_HANDLER) {
            if (cachedDoubleInventory != null) {
                return LazyOptional.of(() -> new InvWrapper(cachedDoubleInventory)).cast();
            }
            return singleHandler.cast();
        }
        return super.getCapability(cap, side);
    }

    @Override
    public int getContainerSize() {
        if (cachedDoubleInventory != null) {
            return cachedDoubleInventory.getContainerSize();
        }
        return super.getContainerSize();
    }

    @Override
    public boolean isEmpty() {
        if (cachedDoubleInventory != null) {
            return cachedDoubleInventory.isEmpty();
        }
        return super.isEmpty();
    }

    @Override
    public ItemStack getItem(int slot) {
        if (cachedDoubleInventory != null) {
            return cachedDoubleInventory.getItem(slot);
        }
        return super.getItem(slot);
    }

    @Override
    public void setItem(int slot, ItemStack stack) {
        if (cachedDoubleInventory != null) {
            cachedDoubleInventory.setItem(slot, stack);
        } else {
            super.setItem(slot, stack);
        }
    }

    @Override
    public ItemStack removeItem(int slot, int count) {
        if (cachedDoubleInventory != null) {
            return cachedDoubleInventory.removeItem(slot, count);
        }
        return super.removeItem(slot, count);
    }

    @Override
    public ItemStack removeItemNoUpdate(int slot) {
        if (cachedDoubleInventory != null) {
            return cachedDoubleInventory.removeItemNoUpdate(slot);
        }
        return super.removeItemNoUpdate(slot);
    }

    @Override
    public void clearContent() {
        if (cachedDoubleInventory != null) {
            cachedDoubleInventory.clearContent();
        } else {
            super.clearContent();
        }
    }
}
