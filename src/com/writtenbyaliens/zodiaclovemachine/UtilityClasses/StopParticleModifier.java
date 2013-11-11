package com.writtenbyaliens.zodiaclovemachine.UtilityClasses;

import java.util.Date;

import org.andengine.entity.particle.Particle;
import org.andengine.entity.particle.ParticleSystem;
import org.andengine.entity.particle.modifier.IParticleModifier;

public class StopParticleModifier implements IParticleModifier {
	private long stoptime = 0;
	private float interval = 0;
	private ParticleSystem particlesystem = null;

	public StopParticleModifier(ParticleSystem particlesystem, float interval) {
		this.particlesystem = particlesystem;
		this.interval = interval; // How many seconds this particlesystem will
									// be alive before stop spawning
	}

	@Override
	public void onInitializeParticle(Particle particle) {
		stoptime = new Date().getTime() + (long) (interval * 1000);
	}

	@Override
	public void onUpdateParticle(Particle particle) {
		if (new Date().getTime() > stoptime) {
			particlesystem.setParticlesSpawnEnabled(false);
		}
	}
}