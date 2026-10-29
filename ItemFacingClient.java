package com.example.itemfacing;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;

import java.util.ArrayList;
import java.util.List;

public class ItemFacingClient implements ClientModInitializer {

	@Override
	public void onInitializeClient() {
		HudRenderCallback.EVENT.register(ItemFacingClient::renderItemInfo);
	}

	/** Shows the normal item tooltip (name, enchantments, lore...) when you look at a dropped item. */
	private static void renderItemInfo(GuiGraphics graphics, DeltaTracker deltaTracker) {
		Minecraft mc = Minecraft.getInstance();
		if (mc.player == null || mc.level == null || mc.options.hideGui || mc.screen != null) {
			return;
		}
		if (!(mc.crosshairPickEntity instanceof ItemEntity itemEntity)) {
			return;
		}

		ItemStack stack = itemEntity.getItem();
		if (stack.isEmpty()) {
			return;
		}

		TooltipFlag flag = mc.options.advancedItemTooltips ? TooltipFlag.Default.ADVANCED : TooltipFlag.Default.NORMAL;
		List<Component> lines = new ArrayList<>(stack.getTooltipLines(Item.TooltipContext.of(mc.level), mc.player, flag));
		if (lines.isEmpty()) {
			return;
		}

		// Put the stack size next to the name, e.g. "Diamond x12".
		if (stack.getCount() > 1) {
			lines.set(0, Component.empty().append(lines.get(0)).append(Component.literal(" x" + stack.getCount())));
		}

		Font font = mc.font;
		int width = 0;
		for (Component line : lines) {
			width = Math.max(width, font.width(line));
		}
		int height = lines.size() * (font.lineHeight + 1);

		int x = graphics.guiWidth() / 2 + 14;
		int y = graphics.guiHeight() / 2 - height / 2;

		graphics.fill(x - 4, y - 4, x + width + 4, y + height + 3, 0xD0100010);
		graphics.fill(x - 4, y - 4, x + width + 4, y - 3, 0xFF5000A0);
		graphics.fill(x - 4, y + height + 2, x + width + 4, y + height + 3, 0xFF5000A0);

		int lineY = y;
		for (Component line : lines) {
			graphics.drawString(font, line, x, lineY, 0xFFFFFFFF);
			lineY += font.lineHeight + 1;
		}
	}
}
