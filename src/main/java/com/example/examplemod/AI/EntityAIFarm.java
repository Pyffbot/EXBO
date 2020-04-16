package com.example.examplemod.AI;

import com.example.examplemod.MyEntityZombie;
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
import net.minecraft.init.Items;
import net.minecraft.inventory.InventoryBasic;
import net.minecraft.item.ItemStack;
import net.minecraft.pathfinding.PathNavigateFlying;
import net.minecraft.pathfinding.PathNavigateGround;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.lwjgl.Sys;

public class EntityAIFarm extends EntityAIMoveToBlock {

    private final MyEntityZombie zombie;

    /**
     * 0 => собираем уражай , 1 => сажаем , -1 => неделаем ничего
     */
    private int currentTask;
    // проверка есть у жителя семена , кортофель или морковь
    private boolean hasFarmItem = true;
    // проверка нужно или еще еды
    private boolean wantsToReapStuff = true;

    //сколько клеток обходим поиском
    private final int searchLength;

    //сколько будем ждать перед проверкой
    private int timeoutCounter;
    private int maxStayTicks;


    public EntityAIFarm(MyEntityZombie creature, double speedIn, int length) {
        super((EntityCreature) creature, speedIn, length);
        this.zombie = creature;
        this.searchLength = length;
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
            if (block instanceof BlockCrops && ((BlockCrops) block).isMaxAge(iblockstate) && this.wantsToReapStuff && (this.currentTask == 0 || this.currentTask < 0)) {
                this.currentTask = 0;
                zombie.setCustomNameTag("Wheat genocide " + zombie.inventory.getSizeInventory());
                return true;
            }

            if (iblockstate.getMaterial() == Material.AIR && this.hasFarmItem && (this.currentTask == 1 || this.currentTask < 0)) {
                this.currentTask = 1;
                zombie.setCustomNameTag("i plant wheat " + zombie.inventory.getSizeInventory());
                return true;
            }
        }
        return false;
    }

    @Override
    public void updateTask() {
        super.updateTask();
        this.zombie.getLookHelper().setLookPosition((double) this.destinationBlock.getX() + 0.5D, (double) (this.destinationBlock.getY() + 1), (double) this.destinationBlock.getZ() + 0.5D, 10.0F, (float) this.zombie.getVerticalFaceSpeed());

        if (this.getIsAboveDestination())//проверяем выше ли мы уровня пшенички
        {
            World world = this.zombie.world;
            BlockPos blockpos = this.destinationBlock.up();
            IBlockState iblockstate = world.getBlockState(blockpos);
            Block block = iblockstate.getBlock();

            //срываем пшеничку если она подросла и вообще пшеничка
            if (this.currentTask == 0 && block instanceof BlockCrops && ((BlockCrops) block).isMaxAge(iblockstate)) {
                world.destroyBlock(blockpos, true);
                block.getDrops(world, blockpos, iblockstate, 1).forEach(zombie.inventory::addItem);
            }

            // если мы в состоянии сажать тогда ищем блоки в которые мона чет посадить
            if (this.currentTask == 1 && iblockstate.getMaterial() == Material.AIR) {
                InventoryBasic inventorybasic = zombie.inventory;

                for (int i = 0; i < inventorybasic.getSizeInventory(); ++i) {
                    ItemStack itemstack = inventorybasic.getStackInSlot(i);
                    boolean flag = false;

                    if (!itemstack.isEmpty()) {
                        if (itemstack.getItem() == Items.WHEAT) {
                            world.setBlockState(blockpos, Blocks.WHEAT.getDefaultState(), 3);
                            flag = true;
                        }
                    }

                    if (flag) {
                        itemstack.shrink(1);

                        if (itemstack.isEmpty()) {
                            inventorybasic.setInventorySlotContents(i, ItemStack.EMPTY);
                        }

                        break;
                    }
                }

            }

            this.currentTask = -1;
            zombie.setCustomNameTag("Recreation " + zombie.inventory.getSizeInventory());
        }
    }

    /**
     * Returns whether an in-progress EntityAIBase should continue executing
     */
    public boolean shouldContinueExecuting() {
        return this.currentTask >= 0;
    }

    public boolean shouldExecute() {
        return this.searchForDestination();
    }

    private boolean searchForDestination() {
        int i = this.searchLength;
        int j = 1;
        BlockPos blockpos = new BlockPos(this.zombie);

        for (int k = 0; k <= 1; k = k > 0 ? -k : 1 - k) {
            for (int l = 0; l < i; ++l) {
                for (int i1 = 0; i1 <= l; i1 = i1 > 0 ? -i1 : 1 - i1) {
                    for (int j1 = i1 < l && i1 > -l ? l : 0; j1 <= l; j1 = j1 > 0 ? -j1 : 1 - j1) {
                        BlockPos blockpos1 = blockpos.add(i1, k - 1, j1);

                        if (this.zombie.isWithinHomeDistanceFromPosition(blockpos1) && this.shouldMoveTo(this.zombie.world, blockpos1)) {
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
