/*
 * Copyright (c) 2016, 2017, 2018, 2019 FabricMC
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package fzzyhmstrs.emi_loot.mixins;

import fzzyhmstrs.emi_loot.eventhandlers.WorldGenThingy;
import net.minecraft.registry.DynamicRegistryManager;
import net.minecraft.server.MinecraftServer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = MinecraftServer.class, priority = 10000)
public abstract class MinecraftServerMixin {
    @Shadow
    public abstract DynamicRegistryManager.Immutable getRegistryManager();

    @Inject(method = "<init>", at = @At(value = "RETURN"))
    private void emi_loot$biomePlacedFeaturesAfterFabric(CallbackInfo ci) {
        WorldGenThingy.doStuff(getRegistryManager());
    }
}
