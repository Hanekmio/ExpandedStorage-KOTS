package compasses.expandedstorage.impl.compat.create;

import com.simibubi.create.foundation.data.CreateRegistrate;
import compasses.expandedstorage.impl.ForgeMain;
import compasses.expandedstorage.impl.compat.create.EsChestAttachedCheck;
import compasses.expandedstorage.impl.compat.create.EsMountedStorageTypes;

public class CreateCompat {
    public static final CreateRegistrate REGISTRATE = CreateRegistrate.create("expandedstorage");

    public static void register() {
        ForgeMain.LOGGER.info("[ExpandedStorage] Loading Create Compat");
        EsChestAttachedCheck.register();
        EsMountedStorageTypes.register();
    }
}