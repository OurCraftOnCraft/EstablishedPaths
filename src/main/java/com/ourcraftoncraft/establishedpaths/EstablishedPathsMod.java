package com.ourcraftoncraft.establishedpaths;

import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.common.Mod;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Mod(EstablishedPathsMod.MODID)
public class EstablishedPathsMod {
    public static final String MODID = "establishedpaths";
    private static final Logger LOGGER = LogManager.getLogger();

    public EstablishedPathsMod() {
        IEventBus forgeEventBus = MinecraftForge.EVENT_BUS;

        // Register event handlers
        forgeEventBus.register(PathEvents.class);
        
        LOGGER.info("Established Paths mod loaded!");
    }
}


