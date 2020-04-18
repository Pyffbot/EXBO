package com.example.examplemod.AI;

import com.example.examplemod.MyEntityZombie;
import net.minecraft.block.Block;
import net.minecraft.block.BlockCrops;
import net.minecraft.block.BlockDoor;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityCreature;
import net.minecraft.entity.ai.EntityAIMoveToBlock;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.inventory.InventoryBasic;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.lwjgl.Sys;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.PriorityQueue;
import java.util.Set;

import static net.minecraft.block.BlockDoor.HALF;

public class EntityAIDoor extends EntityAIMoveToBlock {

    public MyEntityZombie zombie;

    public EntityAIDoor(MyEntityZombie zombie) {
        super(zombie, 1.6, 64);
        this.zombie = zombie;
    }

    /**
     * Верните true, чтобы установить данную позицию в качестве пункта назначения
     */
    protected boolean shouldMoveTo(World worldIn, BlockPos pos) {

        pos = pos.up();
        IBlockState iblockstate = worldIn.getBlockState(pos);
        Block block = iblockstate.getBlock(); //получаем тип блока над землей

        if (block instanceof BlockDoor && iblockstate.getValue(HALF) == BlockDoor.EnumDoorHalf.LOWER) {

            if(!zombie.main.contains(pos)){
                return true;
            }
        }

        return false;
    }

    public void updateTask()
    {
        super.updateTask();
        this.zombie.getLookHelper().setLookPosition((double)this.destinationBlock.getX() + 0.5D, (this.destinationBlock.getY() + 1), (double)this.destinationBlock.getZ() + 0.5D, 10.0F, (float)this.zombie.getVerticalFaceSpeed());
        if (this.zombie.getDistanceSqToCenter(this.destinationBlock.up()) < 3.0D)
        {
            World world = this.zombie.world;
            BlockPos blockpos = this.destinationBlock.up();
            IBlockState iblockstate = world.getBlockState(blockpos);
            Block block = iblockstate.getBlock();
            if (block instanceof BlockDoor)
            {

                ((BlockDoor) block).toggleDoor(this.zombie.world, blockpos, true);

                InventoryBasic inventorybasic = this.zombie.getVillagerInventory();

                for (int i = 0; i < inventorybasic.getSizeInventory(); ++i) {
                    ItemStack itemstack = inventorybasic.getStackInSlot(i);
                    if(!itemstack.isEmpty()){
                        if (!(itemstack.getItem() == Items.AIR))
                        {
                            zombie.zombiDropItem(itemstack.getItem(), 0 , blockpos);


                            itemstack.shrink(1);

                            if (itemstack.isEmpty())
                            {
                                inventorybasic.setInventorySlotContents(i, ItemStack.EMPTY);
                            }

                            zombie.main.add(blockpos);

                            break;
                        }
                    }
                }
            }
        }
    }


    /**
     * должна ли EntityAIBase начать выполнение.
     */
    public boolean shouldExecute() {
        return searchForDestination() && (!this.zombie.getVillagerInventory().isEmpty()) && zombie.counterBrokenBlocks > 13;
    }


    private boolean searchForDestination()
    {
        //TODO зафиксируй в переменной значение
        int i = 64;
        int j = 1;
        BlockPos blockpos = new BlockPos(this.zombie);

        for (int k = 0; k <= 4; k = k > 0 ? -k : 1 - k)
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
        zombie.main.clear();

        return false;
    }
}
