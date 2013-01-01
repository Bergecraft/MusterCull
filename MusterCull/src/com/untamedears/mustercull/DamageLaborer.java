package com.untamedears.mustercull;

import java.util.List;
import java.util.Random;

import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;

/**
 * This class performs damage to mobs using the DAMAGE CullType.
 * @author Celdecea
 */
public class DamageLaborer extends Laborer {

	/**
	 * Constructor which takes a reference to the main plug-in class.
	 * @param pluginInstance A reference to the main plug-in class.
	 */
	public DamageLaborer(MusterCull pluginInstance) {
		super(pluginInstance);
	}
	
	
	/**
	 * Repeating damage method for the class.
	 */
	@Override
	public void run() {
		
		int overHardMobLimit = this.getPluginInstance().overHardMobLimit();
		
		if (overHardMobLimit > 0) {
			
			List<LivingEntity> mobs = this.getPluginInstance().getMobs();
			
			Random random = new Random();
			int mobcount = mobs.size();
			int toKillIndex;
			Entity mob;
			
			for (int c = overHardMobLimit; c > 0; c--) {
				//TODO It might be better to produce a smaller list randomly then kill everything in it
				if (mobcount <= 0) break;
				toKillIndex = random.nextInt(mobcount);
				mobcount--;
				mob = mobs.get(toKillIndex);
				mobs.remove(toKillIndex);
				
				/*XXX could entity be null or dead here? possible bug!*/
				/*XXX What is max entity hp? That's what I mean by "100". */
				this.getPluginInstance().damageEntity(mob, 100);
			}
			
		}
		
		if (this.getPluginInstance().isPaused(CullType.DAMAGE)) {
			return;
		}

		int damageCalls = this.getPluginInstance().getDamageCalls();
		
		for (int i = 0; i < damageCalls; i++)
		{
			EntityLimitPair entityLimitPair = this.getPluginInstance().getNextEntity();
			
			if (entityLimitPair == null) {
				return;
			}
			
			Entity entity = entityLimitPair.getEntity();
			
			if (entity == null || entity.isDead()) {
				return;
			}
			
			ConfigurationLimit limit = entityLimitPair.getLimit();
			
			if (limit.getCulling() != CullType.DAMAGE) {
				return;
			}
			
			Random random = new Random();
			
			// Loop through entities in range and count similar entities.
			int count = 0;
			
			for (Entity otherEntity : entity.getNearbyEntities(limit.getRange(), limit.getRange(), limit.getRange())) {
				if (0 == otherEntity.getType().compareTo(entity.getType())) {
					
					count += 1;
					
					// If we've reached a limit for this entity, go ahead and damage it.
					if (count >= limit.getLimit()) {
						
						if (random.nextInt(100) < this.getPluginInstance().getDamageChance()) {
							this.getPluginInstance().damageEntity(entity, this.getPluginInstance().getDamage());
						}
						
						return;
					}
				}
			}
		}
	}
	


}
