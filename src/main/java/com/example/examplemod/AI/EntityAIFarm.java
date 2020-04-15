package com.example.examplemod.AI;

import net.minecraft.block.Block;
import net.minecraft.block.BlockCrops;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityCreature;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.ai.EntityAIBase;
import net.minecraft.entity.ai.EntityAIMoveToBlock;
import net.minecraft.entity.ai.EntityAISwimming;
import net.minecraft.entity.monster.EntityZombie;
import net.minecraft.entity.passive.EntityVillager;
import net.minecraft.init.Blocks;
import net.minecraft.pathfinding.PathNavigateFlying;
import net.minecraft.pathfinding.PathNavigateGround;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.lwjgl.Sys;

public class EntityAIFarm extends EntityAIMoveToBlock {

    private final EntityZombie zombie;

    /** 0 => собираем уражай , 1 => сажаем , -1 => неделаем ничего */
    private int currentTask;
    // проверка есть у жителя семена , кортофель или морковь
    private boolean hasFarmItem = true;
    // проверка нужно или еще еды
    private boolean wantsToReapStuff = true;

    //сколько будем ждать перед проверкой
    private int timeoutCounter ;
    private int maxStayTicks;

    public EntityAIFarm(EntityZombie creature, double speedIn, int length) {
        super((EntityCreature)creature, speedIn, length);
        this.zombie = creature;
    }

    public void startExecuting()
    {
        this.zombie.getNavigator().tryMoveToXYZ((double)((float)this.destinationBlock.getX()) + 0.5D, (double)(this.destinationBlock.getY() + 1), (double)((float)this.destinationBlock.getZ()) + 0.5D, 1);
        this.timeoutCounter = 0;
        this.maxStayTicks = 100;
    }

    @Override
    protected boolean shouldMoveTo(World worldIn, BlockPos pos) {

        System.out.println("shouldMoveTo");

        Block block = worldIn.getBlockState(pos).getBlock();

        if (block == Blocks.FARMLAND) //проверяет блок к которому идем это земля на которой можно сажать
        {
            pos = pos.up(); //проверяет блок выше на 1
            IBlockState iblockstate = worldIn.getBlockState(pos);
            block = iblockstate.getBlock(); //получаем тип блока над землей

            //Если блок собираемый и у него максимальный возраст достигнут
            if (block instanceof BlockCrops && ((BlockCrops)block).isMaxAge(iblockstate) && this.wantsToReapStuff && (this.currentTask == 0 || this.currentTask < 0))
            {
                this.currentTask = 0;
                return true;
            }


            if (iblockstate.getMaterial() == Material.AIR && this.hasFarmItem && (this.currentTask == 1 || this.currentTask < 0))
            {
                this.currentTask = 1;
                return true;
            }
        }
        return false;
    }

    @Override
    public void updateTask() {
        System.out.println("updateTask()");
        super.updateTask();
        zombie.setCustomNameTag(String.valueOf(currentTask));
        this.zombie.getLookHelper().setLookPosition((double)this.destinationBlock.getX() + 0.5D, (double)(this.destinationBlock.getY() + 1), (double)this.destinationBlock.getZ() + 0.5D, 10.0F, (float)this.zombie.getVerticalFaceSpeed());

        if (this.getIsAboveDestination())//проверяем выше ли мы уровня пшенички
        {
            World world = this.zombie.world;
            BlockPos blockpos = this.destinationBlock.up();
            IBlockState iblockstate = world.getBlockState(blockpos);
            Block block = iblockstate.getBlock();

            //срываем пшеничку если она подросла и вообще пшеничка
            if (this.currentTask == 0 && block instanceof BlockCrops && ((BlockCrops)block).isMaxAge(iblockstate))
            {
                world.destroyBlock(blockpos, true);
            }

            // если мы в состоянии сажать тогда ищем блоки в которые мона чет посадить
            if (this.currentTask == 1 && iblockstate.getMaterial() == Material.AIR)
            {
                world.setBlockState(blockpos, Blocks.WHEAT.getDefaultState(), 3);
            }

            this.currentTask = -1;
            this.runDelay = 0;
        }
    }

    /**
     * Returns whether an in-progress EntityAIBase should continue executing
     */
    public boolean shouldContinueExecuting()
    {
        return this.currentTask >= 0;
    }
}
