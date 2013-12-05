package phasecarts;

/*
import java.util.logging.Level;
import java.util.logging.Logger;

import org.apache.commons.lang3.builder.ReflectionToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
*/

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityMinecart;
import net.minecraft.entity.monster.EntityIronGolem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.MathHelper;
import net.minecraft.util.Vec3;
import net.minecraftforge.common.IMinecartCollisionHandler;

public final class CollisionHandler implements IMinecartCollisionHandler
{
	private static CollisionHandler instance;
	//private static final Logger log = Logger.getLogger("PhaseCarts");

	private CollisionHandler()
	{
	}

	public static CollisionHandler getInstance()
	{
		if (instance == null)
		{
			instance = new CollisionHandler();
		}
		return instance;
	}

	public void onEntityCollision(EntityMinecart cart, Entity other)
	{
		if ((cart.worldObj.isRemote) || (other == cart.riddenByEntity) || (other.isDead) || (cart.isDead))
		{
			return;
		}

		boolean isLiving = other instanceof EntityLivingBase;
		boolean isPlayer = other instanceof EntityPlayer;
		boolean isGolem = other instanceof EntityIronGolem;
		
		// Pretty much the standard MC collision handler for now.
		
		if (isLiving && !isPlayer && !isGolem && cart.canBeRidden()
				&& cart.motionX * cart.motionX + cart.motionZ * cart.motionZ > 0.01D
				&& cart.riddenByEntity == null && other.ridingEntity == null)
		{
			other.mountEntity(cart);
		}
		
		double d0 = other.posX - cart.posX;
		double d1 = other.posZ - cart.posZ;
		double d2 = d0 * d0 + d1 * d1;

		if (d2 >= 9.999999747378752E-5D)
		{
			d2 = (double)MathHelper.sqrt_double(d2);
			d0 /= d2;
			d1 /= d2;
			double d3 = 1.0D / d2;

			if (d3 > 1.0D)
			{
				d3 = 1.0D;
			}

			d0 *= d3;
			d1 *= d3;
			d0 *= 0.10000000149011612D;
			d1 *= 0.10000000149011612D;
			d0 *= (double)(1.0F - cart.entityCollisionReduction);
			d1 *= (double)(1.0F - cart.entityCollisionReduction);
			d0 *= 0.5D;
			d1 *= 0.5D;

			if (other instanceof EntityMinecart)
			{
				double d4 = other.posX - cart.posX;
				double d5 = other.posZ - cart.posZ;
				Vec3 vec3 = cart.worldObj.getWorldVec3Pool().getVecFromPool(d4, 0.0D, d5).normalize();
				Vec3 vec31 = cart.worldObj.getWorldVec3Pool().getVecFromPool((double)MathHelper.cos(cart.rotationYaw * (float)Math.PI / 180.0F), 0.0D, (double)MathHelper.sin(cart.rotationYaw * (float)Math.PI / 180.0F)).normalize();
				double d6 = Math.abs(vec3.dotProduct(vec31));
				
				//log.log(Level.INFO, "Cart collided with cart.");

				if (d6 < 0.800000011920929D)
				{
					return;
				}

				double d7 = other.motionX + cart.motionX;
				double d8 = other.motionZ + cart.motionZ;

				if (((EntityMinecart)other).isPoweredCart() && !cart.isPoweredCart())
				{
					cart.motionX *= 0.20000000298023224D;
					cart.motionZ *= 0.20000000298023224D;
					cart.addVelocity(other.motionX - d0, 0.0D, other.motionZ - d1);
					other.motionX *= 0.949999988079071D;
					other.motionZ *= 0.949999988079071D;
				}
				else if (!((EntityMinecart)other).isPoweredCart() && cart.isPoweredCart())
				{
					other.motionX *= 0.20000000298023224D;
					other.motionZ *= 0.20000000298023224D;
					other.addVelocity(cart.motionX + d0, 0.0D, cart.motionZ + d1);
					cart.motionX *= 0.949999988079071D;
					cart.motionZ *= 0.949999988079071D;
				}
				else
				{
					d7 /= 2.0D;
					d8 /= 2.0D;
					cart.motionX *= 0.20000000298023224D;
					cart.motionZ *= 0.20000000298023224D;
					cart.addVelocity(d7 - d0, 0.0D, d8 - d1);
					other.motionX *= 0.20000000298023224D;
					other.motionZ *= 0.20000000298023224D;
					other.addVelocity(d7 + d0, 0.0D, d8 + d1);
				}
			}
			else
			{
				/*
				if (isPlayer)
				{
					log.log(Level.INFO, "Cart collided with player.");
					log.log(Level.INFO, ReflectionToStringBuilder.toString(cart, ToStringStyle.SHORT_PREFIX_STYLE));
				}
				else if (isLiving)
				{
					log.log(Level.INFO, "Cart collided with living.");
				}
				else
				{
					log.log(Level.INFO, "Cart collided with other.");
				}
				*/
				cart.addVelocity(-d0, 0.0D, -d1);
				other.addVelocity(d0 / 4.0D, 0.0D, d1 / 4.0D);
			}
		}
		else
		{
			//log.log(Level.INFO, "Cart collided, but didn't really collide.");
		}
	}

	public AxisAlignedBB getCollisionBox(EntityMinecart cart, Entity other)
	{
		if ((other instanceof EntityPlayer))
		{
			return other.canBePushed() ? other.boundingBox : null;
		}

		return null;
	}

	public AxisAlignedBB getMinecartCollisionBox(EntityMinecart cart)
	{
		return getMinecartCollisionBox(cart, 0.2F);
	}

	private AxisAlignedBB getMinecartCollisionBox(EntityMinecart cart, float expand)
	{
		double yaw = Math.toRadians(cart.rotationYaw);
		double diff = (1.22F - 0.98F) / 2.0D + expand;
		double x = diff * Math.abs(Math.cos(yaw));
		double z = diff * Math.abs(Math.sin(yaw));
		return cart.boundingBox.expand(x, expand, z);
	}

	public AxisAlignedBB getBoundingBox(EntityMinecart cart)
	{
		if (cart == null || cart.isDead)
		{
			return null;
		}
		return cart.boundingBox;
		
	}

}
