package com.example.itemfacing.mixin;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.entity.ItemEntityRenderer;
import net.minecraft.world.entity.item.ItemEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(ItemEntityRenderer.class)
public abstract class ItemEntityRendererMixin {

	/**
	 * Vanilla spins dropped items by ItemEntity.getSpin(age, bobOffset). Replace that angle
	 * with one that points the front of the item back at the player, so every item faces
	 * you no matter where you stand. The bobbing up and down is left untouched.
	 *
	 * require = 0: if this ever fails to match, the mod just does nothing instead of
	 * crashing the game.
	 */
	@Redirect(
		method = "submit",
		at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/item/ItemEntity;getSpin(FF)F"),
		require = 0
	)
	private float itemfacing$faceThePlayer(float ageInTicks, float bobOffset) {
		var player = Minecraft.getInstance().player;
		if (player == null) {
			return ItemEntity.getSpin(ageInTicks, bobOffset);
		}
		// Player yaw 0 looks toward +Z; the item front must point the opposite way.
		return (float) Math.PI - (float) Math.toRadians(player.getYRot());
	}
}
