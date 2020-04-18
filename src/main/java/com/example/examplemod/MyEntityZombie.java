package com.example.examplemod;

import com.example.examplemod.AI.*;

import net.minecraft.entity.ai.*;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.monster.EntityZombie;
import net.minecraft.entity.passive.EntityVillager;
import net.minecraft.init.Items;
import net.minecraft.inventory.InventoryBasic;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.pathfinding.PathNavigateGround;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.lwjgl.Sys;

import javax.annotation.Nullable;
import java.lang.reflect.Field;
import java.util.HashSet;
import java.util.Set;


public class MyEntityZombie extends EntityZombie {

    public HashSet<BlockPos> main = new HashSet<>();

    public InventoryBasic inventory = new InventoryBasic("Items", false, 20);

    //Считает сколько блоков разбито
    public int counterBrokenBlocks = 0;

    public MyEntityZombie(World worldIn) {
        super(worldIn);
        init();
    }

    public void init(){
        setRevengeTarget(null);
        tasks.taskEntries.clear();
        this.isImmuneToFire = true;
        setAlwaysRenderNameTag(true);
        ((PathNavigateGround)this.getNavigator()).setBreakDoors(true);

        this.tasks.addTask(1, new EntityAIMoveIndoors(this));
        this.tasks.addTask(2, new EntityAIRestrictOpenDoor(this));
        this.tasks.addTask(2, new EntityAIOpenDoor(this, true));
        this.tasks.addTask(4, new EntityAlFarmFirst(this));
        this.tasks.addTask(3, new EntityAIDoor(this));
    }

    public void onLivingUpdate()
    {
        super.onLivingUpdate();
        this.world.profiler.startSection("looting");
        if (!this.world.isRemote && this.canPickUpLoot() && !this.dead && net.minecraftforge.event.ForgeEventFactory.getMobGriefingEvent(this.world, this))
        {
            for (EntityItem entityitem : this.world.getEntitiesWithinAABB(EntityItem.class, this.getEntityBoundingBox().grow(1.0D, 0.0D, 1.0D)))
            {
                if (!entityitem.isDead && !entityitem.getItem().isEmpty() && !entityitem.cannotPickup())
                {
                    this.updateEquipmentIfNeeded(entityitem);
                }
            }
        }

        this.world.profiler.endSection();
    }

    protected void updateEquipmentIfNeeded(EntityItem itemEntity)
    {
        ItemStack itemstack = itemEntity.getItem();
        Item item = itemstack.getItem();

        if (this.canVillagerPickupItem(item))
        {
            ItemStack itemstack1 = inventory.addItem(itemstack);

            if (itemstack1.isEmpty())
            {
                itemEntity.setDead();
            }
            else
            {
                itemstack.setCount(itemstack1.getCount());
            }
        }
    }

    private boolean canVillagerPickupItem(Item itemIn)
    {
        return itemIn == Items.WHEAT || itemIn == Items.WHEAT_SEEDS ;
    }

    public boolean canPickUpLoot()
    {
        if(counterBrokenBlocks >= 13){
            return false;
        }
        return true;
    }


    /**
     * Returns true if villager has seeds, potatoes or carrots in inventory
     */
    public boolean isFarmItemInInventory()
    {
        for (int i = 0; i < this.inventory.getSizeInventory(); ++i)
        {
            ItemStack itemstack = this.inventory.getStackInSlot(i);

            if (!itemstack.isEmpty() && (itemstack.getItem() == Items.WHEAT_SEEDS))
            {
                return true;
            }
        }

        return false;
    }

    public boolean wantsMoreFood()
    {
        return true;
    }

    public InventoryBasic getVillagerInventory()
    {
        return this.inventory;
    }


    public Set<EntityAITasks.EntityAITaskEntry> reflect(){

        Set<EntityAITasks.EntityAITaskEntry> name = null; //no getter =(

        try {
            Field field = EntityAITasks.class.getDeclaredField("executingTaskEntries");
            field.setAccessible(true);
            name = (Set<EntityAITasks.EntityAITaskEntry>) field.get(tasks);
        } catch (NoSuchFieldException | IllegalAccessException e) {
            e.printStackTrace();
        }
        return name;
    }

    public EntityItem zombiDropItem(Item item, float offsetY, BlockPos blockPos)
    {

        ItemStack stack = new ItemStack(item, 1, 0);
        if (stack.isEmpty())
        {
            return null;
        }
        else
        {
            EntityItem entityitem = new EntityItem(this.world, blockPos.getX()+0.5D, blockPos.getY()+ (double)offsetY, blockPos.getZ()+0.5D, stack);
            entityitem.setDefaultPickupDelay();
            if (captureDrops)
                this.capturedDrops.add(entityitem);
            else
                this.world.spawnEntity(entityitem);
            return entityitem;
        }
    }

}
