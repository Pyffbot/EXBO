package com.example.examplemod;

import com.example.examplemod.AI.EntityAIFarm;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityCreature;
import net.minecraft.entity.monster.*;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import java.lang.reflect.Field;

public class EventManager {


    @SubscribeEvent
    public void removeSpawningMobs(EntityJoinWorldEvent event) {

        if ((event.getEntity() instanceof EntityZombie) && !(event.getEntity() instanceof MyEntityZombie)) {
            EntityZombie e = (EntityZombie)event.getEntity();
            e.setRevengeTarget(null);
            e.tasks.taskEntries.clear();
            reflection(e);
            e.setAlwaysRenderNameTag(true);
            e.tasks.addTask(0, new EntityAIFarm(e, 1,32));
        }


    }


    public void reflection(Entity entity){
        try {
            Entity temp = entity;
            Field field = Entity.class.getDeclaredField("isImmuneToFire");
            field.setAccessible(true);
            field.set(temp,true);
        } catch (Exception ee) {
            ee.printStackTrace();
        }
    }


}
