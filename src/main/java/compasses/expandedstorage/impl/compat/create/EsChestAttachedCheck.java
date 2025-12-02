package compasses.expandedstorage.impl.compat.create;

import com.simibubi.create.api.contraption.BlockMovementChecks;
import com.simibubi.create.api.contraption.BlockMovementChecks.CheckResult;
import com.simibubi.create.api.contraption.BlockMovementChecks.AttachedCheck;
import compasses.expandedstorage.api.EsChestType;
import compasses.expandedstorage.impl.block.AbstractChestBlock;
import compasses.expandedstorage.impl.ForgeMain;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;

public class EsChestAttachedCheck implements AttachedCheck {

    @Override
    public CheckResult isBlockAttachedTowards(BlockState state, Level world, BlockPos pos, Direction direction) {
        if (!(state.getBlock() instanceof AbstractChestBlock))
            return CheckResult.FAIL;

        EsChestType type = state.getValue(AbstractChestBlock.CURSED_CHEST_TYPE);
        if (type == EsChestType.SINGLE)
            return CheckResult.FAIL;

        Direction attachedDir = AbstractChestBlock.getDirectionToAttached(state);
        BlockPos otherPos = pos.relative(attachedDir);

        if (direction == attachedDir && world.getBlockState(otherPos).getBlock() instanceof AbstractChestBlock) {
            return CheckResult.SUCCESS;
        }

        return CheckResult.FAIL;
    }

    public static void register() {
        BlockMovementChecks.registerAttachedCheck(new EsChestAttachedCheck());
        ForgeMain.LOGGER.info("[ExpandedStorage] Loading Create Compat: ES Chest Attached Check");
    }
}
