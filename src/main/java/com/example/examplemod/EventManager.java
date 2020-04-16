package com.example.examplemod;

import com.example.examplemod.AI.EntityAIFarm;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityCreature;
import net.minecraft.entity.monster.*;
import net.minecraft.entity.passive.EntityVillager;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.registry.EntityEntry;
import net.minecraftforge.fml.common.registry.EntityEntryBuilder;

import java.lang.reflect.Field;

public class EventManager {


    @SubscribeEvent
    public void removeSpawningMobs(EntityJoinWorldEvent event) {

        if (event.getEntity() instanceof EntityZombie && !(event.getEntity() instanceof MyEntityZombie)) {

            Entity entity = new MyEntityZombie(event.getWorld());
            entity.setPosition(event.getEntity().posX, event.getEntity().posY, event.getEntity().posZ);
            if (!event.getWorld().isRemote){
                event.getWorld().spawnEntity(entity);
            }
            event.setCanceled(true);


            /*
            EntityZombie e = (EntityZombie)event.getEntity();
            e.setRevengeTarget(null);
            e.tasks.taskEntries.clear();
            reflection(e);
            e.setAlwaysRenderNameTag(true);
            e.tasks.addTask(0, new EntityAIFarm(e, 1,32));*/
        }


    }



}
