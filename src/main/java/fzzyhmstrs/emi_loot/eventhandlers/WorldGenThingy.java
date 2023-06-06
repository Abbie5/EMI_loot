package fzzyhmstrs.emi_loot.eventhandlers;

import com.google.common.collect.Sets;
import fzzyhmstrs.emi_loot.EMILoot;
import fzzyhmstrs.emi_loot.mixins.*;
import net.fabricmc.fabric.api.biome.v1.BiomeSelectionContext;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Block;
import net.minecraft.registry.*;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.registry.entry.RegistryEntryList;
import net.minecraft.registry.tag.TagKey;
import net.minecraft.structure.rule.AlwaysTrueRuleTest;
import net.minecraft.structure.rule.BlockMatchRuleTest;
import net.minecraft.structure.rule.RuleTest;
import net.minecraft.structure.rule.TagMatchRuleTest;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.intprovider.IntProvider;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.dimension.DimensionOptions;
import net.minecraft.world.gen.HeightContext;
import net.minecraft.world.gen.feature.ConfiguredFeature;
import net.minecraft.world.gen.feature.FeatureConfig;
import net.minecraft.world.gen.feature.OreFeatureConfig;
import net.minecraft.world.gen.feature.PlacedFeature;
import net.minecraft.world.gen.heightprovider.HeightProvider;
import net.minecraft.world.gen.heightprovider.TrapezoidHeightProvider;
import net.minecraft.world.gen.heightprovider.UniformHeightProvider;
import net.minecraft.world.gen.placementmodifier.CountPlacementModifier;
import net.minecraft.world.gen.placementmodifier.HeightRangePlacementModifier;
import net.minecraft.world.gen.placementmodifier.PlacementModifier;
import net.minecraft.world.gen.structure.Structure;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.function.Predicate;

public class WorldGenThingy {
    public static final WorldGenThingy INSTANCE = new WorldGenThingy();

    private WorldGenThingy() {
    }

    public void onEntryAdded(int rawId, Identifier id, PlacedFeature placedFeature) {
    }

    // copied from fabric api to prevent incompatibilities with quilted fabric api
    private static class BiomeSelectionContextImpl implements BiomeSelectionContext {
        private final DynamicRegistryManager dynamicRegistries;
        private final RegistryKey<Biome> key;
        private final Biome biome;
        private final RegistryEntry<Biome> entry;

        public BiomeSelectionContextImpl(DynamicRegistryManager dynamicRegistries, RegistryKey<Biome> key, Biome biome) {
            this.dynamicRegistries = dynamicRegistries;
            this.key = key;
            this.biome = biome;
            this.entry = dynamicRegistries.get(RegistryKeys.BIOME).getEntry(this.key).orElseThrow();
        }

        @Override
        public RegistryKey<Biome> getBiomeKey() {
            return key;
        }

        @Override
        public Biome getBiome() {
            return biome;
        }

        @Override
        public RegistryEntry<Biome> getBiomeRegistryEntry() {
            return entry;
        }

        @Override
        public Optional<RegistryKey<ConfiguredFeature<?, ?>>> getFeatureKey(ConfiguredFeature<?, ?> configuredFeature) {
            Registry<ConfiguredFeature<?, ?>> registry = dynamicRegistries.get(RegistryKeys.CONFIGURED_FEATURE);
            return registry.getKey(configuredFeature);
        }

        @Override
        public Optional<RegistryKey<PlacedFeature>> getPlacedFeatureKey(PlacedFeature placedFeature) {
            Registry<PlacedFeature> registry = dynamicRegistries.get(RegistryKeys.PLACED_FEATURE);
            return registry.getKey(placedFeature);
        }

        @Override
        public boolean validForStructure(RegistryKey<Structure> key) {
            Structure instance = dynamicRegistries.get(RegistryKeys.STRUCTURE).get(key);

            if (instance == null) {
                return false;
            }

            return instance.getValidBiomes().contains(getBiomeRegistryEntry());
        }

        @Override
        public Optional<RegistryKey<Structure>> getStructureKey(Structure structure) {
            Registry<Structure> registry = dynamicRegistries.get(RegistryKeys.STRUCTURE);
            return registry.getKey(structure);
        }

        @Override
        public boolean canGenerateIn(RegistryKey<DimensionOptions> dimensionKey) {
            DimensionOptions dimension = dynamicRegistries.get(RegistryKeys.DIMENSION).get(dimensionKey);

            if (dimension == null) {
                return false;
            }

            return dimension.chunkGenerator().getBiomeSource().getBiomes().stream().anyMatch(entry -> entry.value() == biome);
        }

        @Override
        public boolean hasTag(TagKey<Biome> tag) {
            Registry<Biome> biomeRegistry = dynamicRegistries.get(RegistryKeys.BIOME);
            return biomeRegistry.entryOf(getBiomeKey()).isIn(tag);
        }
    }

    private static void logBiomes(Collection<Identifier> biomeSet) {
        EMILoot.LOGGER.info("generates in biomes: ");
        for (var biomeId : biomeSet) {
            EMILoot.LOGGER.info("- " + biomeId);
        }
    }

    public static void doStuff(DynamicRegistryManager manager) {
        Map<PlacedFeature, Set<Identifier>> map = new HashMap<>();
        Registry<Biome> biomes = manager.get(RegistryKeys.BIOME);
        Map<Identifier, Set<Identifier>> biomesGeneratesInDimensions = new HashMap<>();

        Map<Identifier, Predicate<BiomeSelectionContext>> foundInDimensions = new HashMap<>();
        for (var key : manager.get(RegistryKeys.DIMENSION).getKeys()) {
            foundInDimensions.put(key.getValue(), c -> c.canGenerateIn(key));
        }

        for (Map.Entry<RegistryKey<Biome>, Biome> entry : biomes.getEntrySet()) {
            RegistryKey<Biome> key = entry.getKey();
            Biome biome = entry.getValue();
            Identifier biomeId = key.getValue();

            BiomeSelectionContext context = new BiomeSelectionContextImpl(manager, key, biome);
            for (var dimensionEntry : foundInDimensions.entrySet()) {
                if (dimensionEntry.getValue().test(context)) {
                    biomesGeneratesInDimensions.computeIfAbsent(dimensionEntry.getKey(), i -> new HashSet<>()).add(biomeId);
                }
            }


            for (RegistryEntryList<PlacedFeature> step : biome.getGenerationSettings().getFeatures()) {
                for (RegistryEntry<PlacedFeature> featureEntry : step) {
                    PlacedFeature placedFeature = featureEntry.value();
                    FeatureConfig config = placedFeature.feature().value().config();
                    if (config instanceof OreFeatureConfig ore) {
                        map.computeIfAbsent(placedFeature, k -> new HashSet<>()).add(entry.getKey().getValue());
                    }
                }
            }
        }

        for (var entry : map.entrySet()) {
            var placedFeature = entry.getKey();
            var placedFeatureId = manager.get(RegistryKeys.PLACED_FEATURE).getId(placedFeature);
            var placedFeatureRawId = manager.get(RegistryKeys.PLACED_FEATURE).getRawId(placedFeature);
            var biomeSet = entry.getValue();
            boolean justListBiomes = true;

            EMILoot.LOGGER.info("placed feature: " + placedFeatureId);

            Optional<RegistryKey<ConfiguredFeature<?, ?>>> thing = placedFeature.feature().getKey();
            EMILoot.LOGGER.info("config id: " + (thing.isPresent() ? thing.get().getValue().toString() : "none"));

            for (PlacementModifier modifier : placedFeature.placementModifiers()) {
                if (modifier instanceof HeightRangePlacementModifier heightRange) {
                    HeightProvider height = ((HeightRangePlacementModifierAccessor) heightRange).getHeightProvider();
                    if (height instanceof TrapezoidHeightProvider trapezoid) {
                        var accessor = (TrapezoidHeightProviderAccessor) trapezoid;
                        EMILoot.LOGGER.info("trapezoid " + accessor.getMinOffset() + ", " + accessor.getPlateau() + ", " + accessor.getMaxOffset());

                    } else if (height instanceof UniformHeightProvider uniform) {
                        var accessor = (UniformHeightProviderAccessor) uniform;
                        EMILoot.LOGGER.info("uniform " + accessor.getMinOffset() + ", " + accessor.getMaxOffset());
                    }
                } else if (modifier instanceof CountPlacementModifier countPlacementModifier) {
                    IntProvider countProvider = ((CountPlacementModifierAccessor) countPlacementModifier).getCount();
                    EMILoot.LOGGER.info(countProvider.getMin() + " <= count <= " + countProvider.getMax());
                }
            }

            var ore = (OreFeatureConfig) placedFeature.feature().value().config();

            for (OreFeatureConfig.Target target : ore.targets) {
                Block oreBlock = target.state.getBlock();
                EMILoot.LOGGER.info("ore: " + oreBlock.toString());
                RuleTest ruleTest = target.target;
                if (ruleTest instanceof AlwaysTrueRuleTest) {
                    EMILoot.LOGGER.info("replaces all");
                } else if (ruleTest instanceof TagMatchRuleTest tagMatchRuleTest) {
                    EMILoot.LOGGER.info("replaces all in tag: " + ((TagMatchRuleTestAccessor) tagMatchRuleTest).getTag());
                } else if (ruleTest instanceof BlockMatchRuleTest blockMatchRuleTest) {
                    EMILoot.LOGGER.info("replaces: " + ((BlockMatchRuleTestAccessor) blockMatchRuleTest).getBlock());
                }
            }

            EMILoot.LOGGER.info("reduced air exposure: " + ore.discardOnAirChance);


            for (var entryAgain : biomesGeneratesInDimensions.entrySet()) {
                var dimensionBiomeSet = entryAgain.getValue();
                var dimensionId = entryAgain.getKey();
                if (dimensionBiomeSet.equals(biomeSet)) {
                    // dont need to store whole list
                    EMILoot.LOGGER.info("generates in " + dimensionId);
                    justListBiomes = false;
                } else if (dimensionBiomeSet.containsAll(biomeSet)) {
                    // subset of dimension biomes
                    var difference = Sets.difference(dimensionBiomeSet, biomeSet);
                    if (difference.size() > biomeSet.size()) {
                        // only useful if the resulting set of biomes the ore doesnt generate in
                        // is smaller than the set of biomes that it does generate in
                        logBiomes(biomeSet);
                    } else {
                        EMILoot.LOGGER.info("generates in " + dimensionId + " biomes except: ");
                        for (var biomeId : difference) {
                            EMILoot.LOGGER.info("- " + biomeId);
                        }
                    }
                    justListBiomes = false;
                }
            }

            if (justListBiomes) {
                logBiomes(biomeSet);
            }
            EMILoot.LOGGER.info("\n\n");

        }

        EMILoot.LOGGER.info("done!");
    }

//    public record PlacedFeatureInfo(
//            int rawId, // PlacedFeature
//            int configuredFeatureRawId, // FeatureConfig
//            int minCount,
//            int maxCount,
//            int size,
//            float discardOnAirChance,
//            int minY,
//            int maxY,
//            int plateau, // 0 for triangle, (maxY-minY) for uniform
//            int dimensionRawId, // only set to dimension id if excluding biomes from that dimension, otherwise set to -1
//            List<Integer> oreRawIds, // Block
//            List<Integer> biomeRawIds // Biome
//    ) {
//        public static PlacedFeatureInfo of(DynamicRegistryManager manager, PlacedFeature placedFeature, @Nullable DimensionOptions dimension, boolean exclude, List<Biome> biomes, HeightContext heightContext) {
//            int rawId = manager.get(RegistryKeys.PLACED_FEATURE).getRawId(placedFeature);
//            int configuredFeatureRawId = manager.get(RegistryKeys.CONFIGURED_FEATURE).getRawId(placedFeature.feature().value());
//            OreFeatureConfig ore = (OreFeatureConfig) placedFeature.feature().value().config();
//
//            float discardOnAirChance = ore.discardOnAirChance;
//            int size = ore.size;
//
//            int minCount = 0;
//            int maxCount = 0;
//            int minY = 0;
//            int maxY = 0;
//            int plateau = 0;
//            for (PlacementModifier modifier : placedFeature.placementModifiers()) {
//                if (modifier instanceof CountPlacementModifier count) {
//                    IntProvider intProvider = ((CountPlacementModifierAccessor) count).getCount();
//                    minCount = intProvider.getMin();
//                    maxCount = intProvider.getMax();
//                } else if (modifier instanceof HeightRangePlacementModifier heightRange) {
//                    HeightProvider height = ((HeightRangePlacementModifierAccessor) heightRange).getHeightProvider();
//                    if (height instanceof TrapezoidHeightProvider trapezoid) {
//                        var accessor = (TrapezoidHeightProviderAccessor) trapezoid;
//                        plateau = accessor.getPlateau();
//                        minY = accessor.getMinOffset().getY(heightContext);
//                        maxY = accessor.getMaxOffset().getY(heightContext);
//                    } else if (height instanceof UniformHeightProvider uniform) {
//                        var accessor = (UniformHeightProviderAccessor) uniform;
//                        minY = accessor.getMinOffset().getY(heightContext);
//                        maxY = accessor.getMaxOffset().getY(heightContext);
//                        plateau = maxY - minY;
//                    }
//                }
//            }
//
//            int dimensionRawId = exclude ? manager.get(RegistryKeys.DIMENSION).getRawId(dimension) : -1;
//
//            Registry<Biome> biomeRegistry = manager.get(RegistryKeys.BIOME);
//            List<Integer> biomeRawIds = biomes
//                    .stream()
//                    .map(biomeRegistry::getRawId)
//                    .toList();
//
//            List<Integer> oreRawIds = ore.targets
//                    .stream()
//                    .map(t -> t.state)
//                    .map(AbstractBlock.AbstractBlockState::getBlock)
//                    .map(Registries.BLOCK::getRawId)
//                    .toList();
//
//            return new PlacedFeatureInfo(rawId, configuredFeatureRawId, minCount, maxCount, size, discardOnAirChance, minY, maxY, plateau, dimensionRawId, oreRawIds, biomeRawIds);
//        }
//    }
}
