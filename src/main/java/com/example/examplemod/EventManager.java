package com.example.examplemod;

import net.minecraft.entity.Entity;
import net.minecraft.entity.monster.*;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

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
        }


    }



}
