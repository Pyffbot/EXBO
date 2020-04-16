package com.example.examplemod;

import com.example.examplemod.AI.EntityAIFarm;

import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.monster.EntityZombie;
import net.minecraft.init.Items;
import net.minecraft.inventory.InventoryBasic;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import org.lwjgl.Sys;


public class MyEntityZombie extends EntityZombie {

    public InventoryBasic inventory = new InventoryBasic("Items", false, 8);

    public MyEntityZombie(World worldIn) {
        super(worldIn);
        setRevengeTarget(null);
        tasks.taskEntries.clear();
        this.isImmuneToFire = true;
        setAlwaysRenderNameTag(true);
        tasks.addTask(0, new EntityAIFarm(this, 1,32));
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
        return itemIn == Items.BREAD || itemIn == Items.POTATO || itemIn == Items.CARROT || itemIn == Items.WHEAT || itemIn == Items.WHEAT_SEEDS || itemIn == Items.BEETROOT || itemIn == Items.BEETROOT_SEEDS;
    }

    public boolean canPickUpLoot()
    {
        return true;
    }

}
