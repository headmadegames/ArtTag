package headmade.arttag;

import headmade.arttag.screens.ArtTagScreen;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.physics.box2d.Contact;
import com.badlogic.gdx.physics.box2d.ContactImpulse;
import com.badlogic.gdx.physics.box2d.ContactListener;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.Manifold;

public class ArtTagContactListener implements ContactListener {
	private static final String	TAG	= ArtTagContactListener.class.getName();

	private final ArtTagScreen	artTag;

	public ArtTagContactListener(ArtTagScreen artTagScreen) {
		this.artTag = artTagScreen;
	}

	@Override
	public void beginContact(Contact contact) {
		// Gdx.app.log(TAG, "Begin contact between " + contact.getFixtureA().getFilterData().categoryBits + " and "
		// + contact.getFixtureB().getFilterData().categoryBits);

		if (isPlayer(contact.getFixtureA())) {
			handleBeginContactWithPlayer(contact, contact.getFixtureA(), contact.getFixtureB());
		} else if (isPlayer(contact.getFixtureB())) {
			handleBeginContactWithPlayer(contact, contact.getFixtureB(), contact.getFixtureA());

		} else if (isPlayerLight(contact.getFixtureA())) {
			handleBeginContactWithPlayerLight(contact, contact.getFixtureA(), contact.getFixtureB());
		} else if (isPlayerLight(contact.getFixtureB())) {
			handleBeginContactWithPlayerLight(contact, contact.getFixtureB(), contact.getFixtureA());
		}
	}

	@Override
	public void endContact(Contact contact) {
		// Gdx.app.log(TAG, "End contact between " + contact.getFixtureA().getFilterData().categoryBits + " and "
		// + contact.getFixtureB().getFilterData().categoryBits);

		if (isPlayer(contact.getFixtureA())) {
			handleEndContactWithPlayer(contact, contact.getFixtureA(), contact.getFixtureB());
		} else if (isPlayer(contact.getFixtureB())) {
			handleEndContactWithPlayer(contact, contact.getFixtureB(), contact.getFixtureA());
		}

		if (isPlayerLight(contact.getFixtureA())) {
			handleEndContactWithPlayerLight(contact, contact.getFixtureA(), contact.getFixtureB());
		} else if (isPlayerLight(contact.getFixtureB())) {
			handleEndContactWithPlayerLight(contact, contact.getFixtureB(), contact.getFixtureA());
		}
	}

	@Override
	public void preSolve(Contact contact, Manifold oldManifold) {
		// Gdx.app.log(TAG, "Begin contact between " + contact.getFixtureA().getFilterData().categoryBits + " and "
		// + contact.getFixtureB().getFilterData().categoryBits);
	}

	@Override
	public void postSolve(Contact contact, ContactImpulse impulse) {
		// Gdx.app.log(TAG, "Begin contact between " + contact.getFixtureA().getFilterData().categoryBits + " and "
		// + contact.getFixtureB().getFilterData().categoryBits);
	}

	private void handleBeginContactWithPlayer(Contact contact, Fixture fixPlayer, Fixture fixOther) {
		if (isArtTrigger(fixOther)) {
			// if (artTag.currentArt != null) {
			Player.instance.isTouchingArt = true;
			Gdx.app.log(TAG, "Player touching " + artTag.currentArt);
			// }
		} else if (isExit(fixOther)) {
			Player.instance.isTouchingExit = true;
		} else if (isGuard(fixOther)) {
			Gdx.app.log(TAG, "Game Over!");
		}
	}

	private void handleBeginContactWithPlayerLight(Contact contact, Fixture fixLight, Fixture fixOther) {
		if (isArtTrigger(fixOther)) {
			Player.instance.artInView.add(fixOther);
		} else if (isGuard(fixOther)) {
			Gdx.app.log(TAG, "Guard is in Playerlight!");
		}
	}

	private void handleEndContactWithPlayer(Contact contact, Fixture fixPlayer, Fixture fixOther) {
		if (isArtTrigger(fixOther)) {
			Gdx.app.log(TAG, "Player no longer touching " + artTag.currentArt);
			Player.instance.isTouchingArt = false;
		} else if (isExit(fixOther)) {
			Player.instance.isTouchingExit = false;
		}
	}

	private void handleEndContactWithPlayerLight(Contact contact, Fixture fixLight, Fixture fixOther) {
		if (isArtTrigger(fixOther)) {
			if (Player.instance.artInView.removeValue(fixOther, true)) {
				Gdx.app.log(TAG, "removing art from view");
			} else {
				Gdx.app.error(TAG, "Tried to remove art from view, but it found no match");
			}
		} else if (isGuard(fixOther)) {
			Gdx.app.log(TAG, "Guard is no longer in Playerlight!");
		}
	}

	private boolean isArtTrigger(Fixture fixOther) {
		return fixOther.getFilterData().categoryBits == ArtTag.CAT_ARTTRIGGER;
	}

	private boolean isPlayer(Fixture fix) {
		return fix.getFilterData().categoryBits == ArtTag.CAT_PLAYER;
	}

	private boolean isPlayerLight(Fixture fix) {
		return fix.getFilterData().categoryBits == ArtTag.CAT_PLAYERLIGHT;
	}

	private boolean isGuard(Fixture fix) {
		return fix.getFilterData().categoryBits == ArtTag.CAT_GUARD;
	}

	private boolean isExit(Fixture fix) {
		return fix.getFilterData().categoryBits == ArtTag.CAT_EXIT;
	}
}
