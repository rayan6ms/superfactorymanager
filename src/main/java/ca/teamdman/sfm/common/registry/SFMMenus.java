package ca.teamdman.sfm.common.registry;


import ca.teamdman.sfm.SFM;
import ca.teamdman.sfm.client.ClientRaycastHelpers;
import ca.teamdman.sfm.common.blockentity.ManagerBlockEntity;
import ca.teamdman.sfm.common.blockentity.TestBarrelTankBlockEntity;
import ca.teamdman.sfm.common.containermenu.ManagerContainerMenu;
import ca.teamdman.sfm.common.containermenu.TestBarrelTankContainerMenu;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.loading.FMLEnvironment;
import net.neoforged.neoforge.common.extensions.IMenuTypeExtension;
import net.neoforged.neoforge.network.IContainerFactory;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.function.Supplier;

public class SFMMenus {
    private static final DeferredRegister<MenuType<?>> MENU_TYPES = DeferredRegister.create(
            BuiltInRegistries.MENU,
            SFM.MOD_ID
    );

    public static void register(IEventBus bus) {
        MENU_TYPES.register(bus);
    }

    public static final Supplier<MenuType<ManagerContainerMenu>> MANAGER_MENU = MENU_TYPES.register(
            "manager",
            () -> IMenuTypeExtension.create(
                    new IContainerFactory<>() {
                        @Override
                        public ManagerContainerMenu create(
                                int windowId,
                                Inventory inv,
                                RegistryFriendlyByteBuf data
                        ) {
                            return new ManagerContainerMenu(
                                    windowId,
                                    inv,
                                    data
                            );
                        }

                        @Override
                        public ManagerContainerMenu create(
                                int windowId,
                                Inventory inv
                        ) {
                            if (FMLEnvironment.dist.isClient()) {
                                BlockEntity be = ClientRaycastHelpers.getLookBlockEntity();
                                if (!(be instanceof ManagerBlockEntity mbe)) {
                                    return IContainerFactory.super.create(windowId, inv);
                                }
                                return new ManagerContainerMenu(windowId, inv, mbe);
                            } else {
                                return IContainerFactory.super.create(
                                        windowId,
                                        inv
                                );
                            }
                        }
                    })
    );

    public static final Supplier<MenuType<TestBarrelTankContainerMenu>> TEST_BARREL_TANK_MENU = MENU_TYPES.register(
            "test_barrel_tank",
            () -> IMenuTypeExtension.create(
                    new IContainerFactory<>() {
                        @Override
                        public TestBarrelTankContainerMenu create(
                                int windowId,
                                Inventory inv,
                                RegistryFriendlyByteBuf data
                        ) {
                            return new TestBarrelTankContainerMenu(
                                    windowId,
                                    inv,
                                    data
                            );
                        }

                        @Override
                        public TestBarrelTankContainerMenu create(
                                int windowId,
                                Inventory inv
                        ) {
                            if (FMLEnvironment.dist.isClient()) {
                                BlockEntity be = ClientRaycastHelpers.getLookBlockEntity();
                                if (!(be instanceof TestBarrelTankBlockEntity blockEntity)) {
                                    return IContainerFactory.super.create(windowId, inv);
                                }
                                return new TestBarrelTankContainerMenu(windowId, inv, blockEntity);
                            } else {
                                return IContainerFactory.super.create(
                                        windowId,
                                        inv
                                );
                            }
                        }
                    })
    );


}
