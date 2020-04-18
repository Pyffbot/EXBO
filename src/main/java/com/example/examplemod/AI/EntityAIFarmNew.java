package com.example.examplemod.AI;


import com.example.examplemod.MyEntityZombie;
import net.minecraft.block.Block;
import net.minecraft.block.BlockCrops;
import net.minecraft.block.BlockDoor;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.ai.EntityAIMoveToBlock;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.inventory.InventoryBasic;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.lwjgl.Sys;


public class EntityAIFarmNew extends EntityAIMoveToBlock {

    /** Villager that is harvesting */
    private final MyEntityZombie zombie;
    private boolean hasFarmItem;
    private boolean wantsToReapStuff;
    /** 0 => harvest, 1 => replant, -1 => none */
    private int currentTask;

    //сколько клеток обходим поиском
    private final int searchLength = 32;

    public EntityAIFarmNew(MyEntityZombie villagerIn, double speedIn)
    {
        super(villagerIn, speedIn, 32);
        this.zombie = villagerIn;
    }

    /**
     * должна ли EntityAIBase начать выполнение.
     */
    public boolean shouldExecute()
    {
            if (!net.minecraftforge.event.ForgeEventFactory.getMobGriefingEvent(this.zombie.world, this.zombie))
            {
                return false;
            }

            this.currentTask = -1;
            this.hasFarmItem = this.zombie.isFarmItemInInventory();
            this.wantsToReapStuff = this.zombie.wantsMoreFood();

        return searchForDestination();
    }

    /**
     * Продолжаем ли выполнение
     */
    public boolean shouldContinueExecuting()
    {
        return this.currentTask >= 0 && super.shouldContinueExecuting();
    }

    /**
     * Продолжаем выполнять задачу которая была начата
     */
    public void updateTask()
    {
        super.updateTask();
        this.zombie.getLookHelper().setLookPosition((double)this.destinationBlock.getX() + 0.5D, (double)(this.destinationBlock.getY() + 1), (double)this.destinationBlock.getZ() + 0.5D, 10.0F, (float)this.zombie.getVerticalFaceSpeed());

        if (this.getIsAboveDestination())
        {
            World world = this.zombie.world;
            BlockPos blockpos = this.destinationBlock.up();
            IBlockState iblockstate = world.getBlockState(blockpos);
            Block block = iblockstate.getBlock();

            if (this.currentTask == 0 && block instanceof BlockCrops && ((BlockCrops)block).isMaxAge(iblockstate))
            {
                world.destroyBlock(blockpos, true);
            }
            else if (this.currentTask == 1 && iblockstate.getMaterial() == Material.WOOD)
            {
                //((BlockDoor)block).toggleDoor(this.zombie.world, blockpos, false);
                zombie.dropItem(Items.APPLE,1);
            }

            this.currentTask = -1;
            this.runDelay = 10;
        }
    }

    /**
     * Верните true, чтобы установить данную позицию в качестве пункта назначения
     */
    protected boolean shouldMoveTo(World worldIn, BlockPos pos)
    {
        Block block = worldIn.getBlockState(pos).getBlock();

        if (block == Blocks.FARMLAND)
        {
            pos = pos.up();
            IBlockState iblockstate = worldIn.getBlockState(pos);
            block = iblockstate.getBlock();

            if (block instanceof BlockCrops && ((BlockCrops)block).isMaxAge(iblockstate) && this.wantsToReapStuff && (this.currentTask == 0 || this.currentTask < 0))
            {
                this.currentTask = 0;
                return true;
            }
        }

        if(block == Blocks.JUNGLE_DOOR)
        {
            System.out.println("door1");
            IBlockState iblockstate = worldIn.getBlockState(pos);
            block = iblockstate.getBlock(); //получаем тип блока над землей

            //Если блок собираемый и у него максимальный возраст достигнут
            if (block instanceof BlockDoor  && (this.currentTask == 1 || this.currentTask < 0)) {

                this.currentTask = 1;
                zombie.setCustomNameTag("DOOOR " + zombie.inventory.getSizeInventory());
                return true;
            }

        }

        return false;
    }

    private boolean searchForDestination()
    {
        int i = this.searchLength;
        int j = 1;
        BlockPos blockpos = new BlockPos(this.zombie);

        for (int k = 0; k <= 1; k = k > 0 ? -k : 1 - k)
        {
            for (int l = 0; l < i; ++l)
            {
                for (int i1 = 0; i1 <= l; i1 = i1 > 0 ? -i1 : 1 - i1)
                {
                    for (int j1 = i1 < l && i1 > -l ? l : 0; j1 <= l; j1 = j1 > 0 ? -j1 : 1 - j1)
                    {
                        BlockPos blockpos1 = blockpos.add(i1, k - 1, j1);

                        if (this.zombie.isWithinHomeDistanceFromPosition(blockpos1) && this.shouldMoveTo(this.zombie.world, blockpos1))
                        {
                            this.destinationBlock = blockpos1;
                            return true;
                        }
                    }
                }
            }
        }

        return false;
    }

    /**
     *   Определите, может ли эта задача AI прерываться задачей с более высоким приоритетом (= более низким значением). Дефолтно true
      */
    @Override
    public boolean isInterruptible() {
        return super.isInterruptible();
    }

    /**
     *  Выполните одну задачу или начните выполнять непрерывную задачу
     */
    @Override
    public void startExecuting() {
        super.startExecuting();
    }

    /**
     * Сбрасывает внутреннее состояние задачи. Вызывается, когда эта задача прерывается другой
     */
    public void resetTask()
    {
        super.resetTask();
    }
}