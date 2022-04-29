package io.github.theepicblock.polymc.impl.poly.entity;

import io.github.theepicblock.polymc.api.entity.EntityPoly;
import io.github.theepicblock.polymc.api.wizard.PlayerView;
import io.github.theepicblock.polymc.api.wizard.VItem;
import io.github.theepicblock.polymc.api.wizard.Wizard;
import io.github.theepicblock.polymc.api.wizard.WizardInfo;
import net.minecraft.entity.Entity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.server.network.ServerPlayerEntity;

public class MissingEntityPoly<T extends Entity> implements EntityPoly<T> {
    @Override
    public Wizard createWizard(WizardInfo info, T entity) {
        return new MissingEntityWizard<>(info, entity);
    }

    public static class MissingEntityWizard<T extends Entity> extends EntityWizard<T> {
        private static final ItemStack ITEM = new ItemStack(Items.RED_STAINED_GLASS_PANE);
        private final VItem item;

        public MissingEntityWizard(WizardInfo info, T entity) {
            super(info, entity);
            item = new VItem();
        }

        @Override
        public void onMove(PlayerView players) {
            players.forEach((player) -> item.move(player, this.getPosition(), (byte)0, (byte)0, true));
        }

        @Override
        public void addPlayer(ServerPlayerEntity playerEntity) {
            item.spawn(playerEntity, this.getPosition());
            item.setNoGravity(playerEntity, true);
            item.sendItem(playerEntity, ITEM);
        }

        @Override
        public void removePlayer(ServerPlayerEntity playerEntity) {
            item.remove(playerEntity);
        }
    }
}
