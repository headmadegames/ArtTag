/*******************************************************************************
 *    Copyright 2015 Headmade Games
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 *******************************************************************************/
package headmade.arttag.assets;

import com.badlogic.gdx.assets.loaders.ParticleEffectLoader;
import com.badlogic.gdx.assets.loaders.ParticleEffectLoader.ParticleEffectParameter;

public class AssetParticles {
	private static final String TAG = AssetParticles.class.getName();

	private static final String PARTICLES_PATH = "particles/";

	private static final ParticleEffectParameter skinParameter = new ParticleEffectLoader.ParticleEffectParameter();

	static {
		// init Skin
		// skinParameter.1
		// ParticleEffectLoader loader = new ParticleEffectLoader(resolver);
		// loader.load(am, fileName, file, param)
	}

	// @formatter:off
	// @Asset(Particle.class)
	 public static final String
	 smoke = PARTICLES_PATH + "smokebomb.fx";
	// sound2 = SOUNDS_PATH + "sound2.wav";

}
