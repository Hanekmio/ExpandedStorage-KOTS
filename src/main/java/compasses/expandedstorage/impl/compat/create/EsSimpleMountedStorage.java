package compasses.expandedstorage.impl.compat.create;

import com.mojang.serialization.Codec;
import com.simibubi.create.api.contraption.storage.item.MountedItemStorage;
import com.simibubi.create.api.contraption.storage.item.MountedItemStorageType;
import com.simibubi.create.api.contraption.storage.item.simple.SimpleMountedStorage;
import com.simibubi.create.api.contraption.storage.item.menu.StorageInteractionWrapper;
import com.simibubi.create.content.contraptions.Contraption;
import com.simibubi.create.foundation.item.ItemHelper;
import compasses.expandedstorage.api.EsChestType;
import compasses.expandedstorage.impl.CommonMain;
import compasses.expandedstorage.impl.block.AbstractChestBlock;
import compasses.expandedstorage.impl.block.OpenableBlock;
import compasses.expandedstorage.impl.block.entity.extendable.InventoryBlockEntity;
import compasses.expandedstorage.impl.compat.create.EsMountedStorageTypes;
import compasses.expandedstorage.impl.inventory.handler.AbstractHandler;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.Container;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.DoubleBlockCombiner;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate.StructureBlockInfo;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.IItemHandlerModifiable;
import net.minecraftforge.items.wrapper.CombinedInvWrapper;
import net.minecraftforge.items.wrapper.InvWrapper;

import java.util.function.Consumer;
import java.util.function.Predicate;

import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.NotNull;

public class EsSimpleMountedStorage extends SimpleMountedStorage {
    public static final Codec<EsSimpleMountedStorage> CODEC =
            SimpleMountedStorage.codec(EsSimpleMountedStorage::new);

    protected EsSimpleMountedStorage(MountedItemStorageType<?> type, IItemHandler handler) {
        super(type, handler);
    }

    public EsSimpleMountedStorage(IItemHandler handler) {
		this(EsMountedStorageTypes.ES_SIMPLE.get(), handler);
	}

    @Override
    public boolean handleInteraction(ServerPlayer player, Contraption contraption, StructureBlockInfo info) {
        IItemHandlerModifiable handler = getHandlerForMenu(info, contraption);
        if (handler == null) return false;

        Predicate<Player> stillValid = p -> {
            Vec3 local = Vec3.atCenterOf(info.pos());
            Vec3 world = contraption.entity.toGlobalVector(local, 0);
            return isMenuValid(player, contraption, world);
        };
        Container container = new StorageInteractionWrapper(handler, stillValid, p -> {
            Vec3 local = Vec3.atCenterOf(info.pos());
            Vec3 world = contraption.entity.toGlobalVector(local, 0);
            playClosingSound(player.serverLevel(), world);
        });

        Component title = getMenuName(info, contraption);
        ResourceLocation forcedScreenType = info.state().getBlock() instanceof OpenableBlock ? 
                ((OpenableBlock)info.state().getBlock()).getForcedScreenType() : null;


        CommonMain.platformHelper().openScreenHandler(player, container, title, forcedScreenType);
        Vec3 worldPos = contraption.entity.toGlobalVector(Vec3.atCenterOf(info.pos()), 0);
        playOpeningSound(player.serverLevel(), worldPos);
        return true;
    }

	protected void playOpeningSound(ServerLevel level, Vec3 pos) {
		level.playSound(
			null, BlockPos.containing(pos),
			SoundEvents.BARREL_OPEN, SoundSource.BLOCKS,
			0.75f, 1f
		);
	}

	protected void playClosingSound(ServerLevel level, Vec3 pos) {
		level.playSound(
			null, BlockPos.containing(pos),
			SoundEvents.BARREL_CLOSE, SoundSource.BLOCKS,
			0.75f, 1f
		);
	}
}