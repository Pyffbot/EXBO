package com.example.examplemod.AI;

import com.example.examplemod.MyEntityZombie;
import net.minecraft.block.Block;
import net.minecraft.block.BlockCrops;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityCreature;
import net.minecraft.entity.ai.EntityAIMoveToBlock;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.lwjgl.Sys;

public class EntityAlFarmFirst extends EntityAIMoveToBlock {

    private final MyEntityZombie zombie;

    public EntityAlFarmFirst(MyEntityZombie zombie) {
        super(zombie, 1, 32);
        this.zombie = zombie;
    }

    @Override
    protected boolean shouldMoveTo(World worldIn, BlockPos pos) {

        Block block = worldIn.getBlockState(pos).getBlock();

        if (block == Blocks.FARMLAND) //проверяет блок к которому идем это земля на которой можно сажать
        {
            pos = pos.up(); //проверяет блок выше на 1
            IBlockState iblockstate = worldIn.getBlockState(pos);
            block = iblockstate.getBlock(); //получаем тип блока над землей

            //Если блок собираемый и у него максимальный возраст достигнут
            if (block.getClass() == BlockCrops.class && ((BlockCrops) block).isMaxAge(iblockstate)) {
                return true;
            }
        }
        return false;
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

            if (block instanceof BlockCrops && ((BlockCrops)block).isMaxAge(iblockstate))
            {
                world.destroyBlock(blockpos, true);
                zombie.counterBrokenBlocks++;
            }
        }
    }


    /**
     * должна ли EntityAIBase начать выполнение.
     */
    public boolean shouldExecute() {

        if(zombie.counterBrokenBlocks > 13){
            if(this.zombie.getVillagerInventory().isEmpty()){
                zombie.counterBrokenBlocks = 0;
            }
        }

        if(searchForDestination()){
            return true;
        }
        return false;
    }

    private boolean searchForDestination()
    {
        //TODO зафиксируй в переменной значение
        int i = 64;
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
}
