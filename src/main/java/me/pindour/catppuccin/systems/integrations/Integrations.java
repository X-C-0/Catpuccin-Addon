package me.pindour.catppuccin.systems.integrations;

import me.pindour.catppuccin.CatppuccinAddon;
import me.pindour.catppuccin.systems.integrations.addons.NoraIntegration;
import meteordevelopment.meteorclient.addons.AddonManager;
import meteordevelopment.meteorclient.systems.System;
import meteordevelopment.meteorclient.systems.Systems;
import meteordevelopment.meteorclient.utils.PostInit;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.function.Consumer;

public class Integrations extends System<Integrations> {
    private static final Map<Class<? extends Integration>, Integration> INTEGRATIONS = new HashMap<>();

    public Integrations() {
        super("integrations");
    }

    public static Integrations get() {
        return Systems.get(Integrations.class);
    }

    @PostInit
    public static void initIntegrations() {
        register(NoraIntegration.class);

        if (!INTEGRATIONS.isEmpty()) CatppuccinAddon.LOG.info("Loaded {} integrations.", INTEGRATIONS.size());
    }

    private static <T extends Integration> void register(Class<T> integrationClass) {
        try {
            T instance = integrationClass.getDeclaredConstructor().newInstance();

            if (isAddonPresent(instance.getAddonPackage())) {
                INTEGRATIONS.put(integrationClass, instance);
                CatppuccinAddon.LOG.debug("Loaded integration: {} for: {}", integrationClass.getSimpleName(), instance.getAddonPackage());
            }
        } catch (Exception e) {
            CatppuccinAddon.LOG.error("Failed to load integration: {}", integrationClass.getSimpleName(), e);
        }
    }

    @SuppressWarnings("unchecked")
    public <T extends Integration> Optional<T> of(Class<T> integrationClass) {
        return Optional.ofNullable((T) INTEGRATIONS.get(integrationClass));
    }

    public static <T extends Integration> void ifPresent(Class<T> integrationClass, Consumer<T> action) {
        get().of(integrationClass).ifPresent(action);
    }

    private static boolean isAddonPresent(String addonPackage) {
        return AddonManager.ADDONS
                .stream()
                .anyMatch(addon -> addon.getPackage().equalsIgnoreCase(addonPackage));
    }
}
