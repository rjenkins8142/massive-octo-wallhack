package phasecarts;

import java.util.logging.Level;
import java.util.logging.Logger;

import net.minecraft.entity.item.EntityMinecart;
import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.Mod.EventHandler;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPostInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.network.NetworkMod;

@Mod(modid = PhaseCarts.modid, name = "Phase Carts", version = "0.1")
@NetworkMod(clientSideRequired = true, serverSideRequired = false)
public class PhaseCarts
{
	public static final String modid = "PhaseCarts";
	private static final Logger log = Logger.getLogger("PhaseCarts");
	
	@EventHandler
	public void preInit(FMLPreInitializationEvent event)
	{

	}

	@EventHandler
	public void load(FMLInitializationEvent event)
	{
		if (EntityMinecart.getCollisionHandler() != null)
		{
			log.log(Level.CONFIG, "Ooops! Existing Minecart Collision Handler detected, PhaseCarts is overwriting.");
		}
		EntityMinecart.setCollisionHandler(CollisionHandler.getInstance());
	}

	@EventHandler
	public void modsLoaded(FMLPostInitializationEvent event)
	{

	}

}
