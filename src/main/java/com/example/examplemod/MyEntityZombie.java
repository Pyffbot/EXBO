package com.example.examplemod;

import net.minecraft.entity.monster.EntityCreeper;
import net.minecraft.entity.monster.EntityZombie;
import net.minecraft.world.World;

public class MyEntityZombie extends EntityCreeper {

    public MyEntityZombie(World worldIn) {
        super(worldIn);
        setRevengeTarget(null);
        tasks.taskEntries.clear();
        this.isImmuneToFire = true;
    }



}
