package compasses.expandedstorage.impl.compat.create;

import com.simibubi.create.foundation.data.CreateRegistrate;
import com.simibubi.create.api.contraption.storage.item.MountedItemStorageType;
import com.tterrag.registrate.util.entry.RegistryEntry;
import compasses.expandedstorage.impl.ForgeMain;
import compasses.expandedstorage.impl.block.AbstractChestBlock;
import compasses.expandedstorage.impl.compat.create.CreateCompat;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;


public class EsMountedStorageTypes {
    public static final CreateRegistrate REGISTRATE = CreateCompat.REGISTRATE;

    public static final RegistryEntry<EsChestMountedStorageType> ES_CHEST =
        REGISTRATE.mountedItemStorage("es_chest", EsChestMountedStorageType::new)
            .associateBlockTag(TagKey.create(Registries.BLOCK, ResourceLocation.fromNamespaceAndPath("create", "es_chest_mounted_storage")))
            .register();

    public static final RegistryEntry<EsSimpleMountedStorageType> ES_SIMPLE =
        REGISTRATE.mountedItemStorage("es_simple", EsSimpleMountedStorageType::new)
            .associateBlockTag(TagKey.create(Registries.BLOCK, ResourceLocation.fromNamespaceAndPath("create", "es_simple_mounted_storage")))
            .register();

    public static void register() {
        ForgeMain.LOGGER.info("[ExpandedStorage] Loading Create Compat: Mounted Storage Types");
    }
}