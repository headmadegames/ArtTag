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
package headmade.arttag.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.scenes.scene2d.Stage;

import headmade.arttag.DirectedGame;

public class StageScreen extends AbstractGameScreen {

	public Stage getStage() {
		return stage;
	}

	private static final String TAG = StageScreen.class.getName();

	protected Stage stage;

	public StageScreen(DirectedGame game, Stage stage) {
		super(game);
		this.stage = stage;
	}

	public StageScreen(DirectedGame game) {
		super(game);
		if (stage != null) {
			stage.clear();
		}
		this.stage = new Stage(viewport, game.getBatch());
		// stage.addListener(new EventListener() {
		// @Override
		// public boolean handle(Event event) {
		// Gdx.app.debug(TAG, "Event caught" + event);
		// return false;
		// }
		// });
	}

	@Override
	public void dispose() {
		super.dispose();
		stage.dispose();
	}

	@Override
	public InputProcessor getInputProcessor() {
		return stage;
	}

	@Override
	public void render(float delta) {
		preAct(delta);
		stage.act(delta);
		preDraw(delta);
		Gdx.gl.glClearColor(1f, 1f, 1f, 1f);
		// Gdx.gl.glClearColor(0f, 0f, 0f, 1f);
		// Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT | GL20.GL_DEPTH_BUFFER_BIT
				| (Gdx.graphics.getBufferFormat().coverageSampling ? GL20.GL_COVERAGE_BUFFER_BIT_NV : 0));
		stage.draw();
	}

	protected void preAct(float delta) {

	}

	protected void preDraw(float delta) {

	}

	protected void rebuildStage() {

	}
}
