package com.example.examplemod.AI;

import net.minecraft.entity.ai.EntityAIBase;

public class EntityAITest extends EntityAIBase {

    private int mutexBits;

    public EntityAITest() {
    }


    @Override
    public boolean shouldContinueExecuting() {
        System.out.println("shouldContinueExecuting()");
        return super.shouldContinueExecuting();
    }

    @Override
    public boolean isInterruptible() {
        System.out.println("isInterruptible()");
        return super.isInterruptible();
    }

    @Override
    public void startExecuting() {
        System.out.println("startExecuting()");
        super.startExecuting();
    }

    @Override
    public void resetTask() {
        System.out.println("resetTask() ");
        super.resetTask();
    }

    @Override
    public void updateTask() {
        System.out.println("updateTask() ");
        super.updateTask();
    }

    @Override
    public void setMutexBits(int mutexBitsIn) {
        System.out.println("setMutexBits(int mutexBitsIn) ");
        super.setMutexBits(mutexBitsIn);
    }

    @Override
    public int getMutexBits() {
        System.out.println("getMutexBits() ");
        return super.getMutexBits();
    }

    @Override
    public boolean shouldExecute() {
        System.out.println("getMutexBits() ");
        return false;
    }
}
