package com.example.examplemod;

import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.client.registry.RenderingRegistry;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.registry.EntityEntry;
import net.minecraftforge.fml.common.registry.EntityEntryBuilder;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.lwjgl.Sys;

@Mod.EventBusSubscriber(modid = ExampleMod.MODID)
public class EntityRegistry {

    private static int ID = 0;

    public static EntityEntry zombi = EntityEntryBuilder
            .create()
            .entity(MyEntityZombie.class)
            .name("Small Herobrine")
            .id("small_herobrine", ID++)
            .egg(0xff4040, 0xd891ef)
            .tracker(160, 2, false)
            .build();

    @SubscribeEvent
    public static void registryEntity(RegistryEvent.Register<EntityEntry> event) {

        event.getRegistry().registerAll(zombi);
    }
}
