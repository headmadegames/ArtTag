package headmade.arttag;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.physics.box2d.Contact;
import com.badlogic.gdx.physics.box2d.ContactImpulse;
import com.badlogic.gdx.physics.box2d.ContactListener;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.Manifold;

import headmade.arttag.screens.ArtTagScreen;

public class ArtTagContactListener implements ContactListener {
	private static final String TAG = ArtTagContactListener.class.getName();

	private final ArtTagScreen artTag;

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

		if (isGuardLight(contact.getFixtureA())) {
			handleEndContactWithGuardLight(contact, contact.getFixtureA(), contact.getFixtureB());
		} else if (isGuardLight(contact.getFixtureB())) {
			handleEndContactWithGuardLight(contact, contact.getFixtureB(), contact.getFixtureA());
		}
	}

	@Override
	public void preSolve(Contact contact, Manifold oldManifold) {
		// Gdx.app.log(TAG, "Begin contact between " + contact.getFixtureA().getFilterData().categoryBits + " and "
		// + contact.getFixtureB().getFilterData().categoryBits);
	}

	@Override
	public void postSolve(Contact contact, ContactImpulse impulse) {

		// if (isPlayer(contact.getFixtureA())) {
		// if (isGuard(contact.getFixtureB())) {
		// Gdx.app.log(TAG, "Game Over!");
		// }
		// } else if (isPlayer(contact.getFixtureB())) {
		// if (isGuard(contact.getFixtureA())) {
		// Gdx.app.log(TAG, "Game Over!");
		// }
		// }
		// Gdx.app.log(TAG, "Begin contact between " + contact.getFixtureA().getFilterData().categoryBits + " and "
		// + contact.getFixtureB().getFilterData().categoryBits);
	}

	private void handleBeginContactWithPlayer(Contact contact, Fixture fixPlayer, Fixture fixOther) {
		if (isArtTrigger(fixOther)) {
			// if (artTag.currentArt != null) {
			Player.instance.isTouchingArt = true;
			Gdx.app.log(TAG, "Player touching " + artTag.getCurrentArt());
			// }
		} else if (isExit(fixOther)) {
			Gdx.app.log(TAG, "Start touching exit");
			Player.instance.isTouchingExit = true;
		} else if (isDoor(fixOther)) {
			Gdx.app.log(TAG, "Start touching door");
			Player.instance.isTouchingDoor = true;
		} else if (isGuard(fixOther)) {
			Gdx.app.log(TAG, "Game Over!");
		} else if (isGuardLight(fixOther)) {
			handleBeginContactWithGuardLight(contact, fixOther, fixPlayer);
		}
	}

	private void handleBeginContactWithPlayerLight(Contact contact, Fixture fixLight, Fixture fixOther) {
		if (isArtTrigger(fixOther)) {
			Player.instance.artInView.add(fixOther);
		} else if (isGuard(fixOther)) {
			Gdx.app.log(TAG, "Guard is in Playerlight!");
			final Guard g = (Guard) fixOther.getBody().getUserData();
			g.isTouchingPlayerLightCone = true;
		}
	}

	private void handleBeginContactWithGuardLight(Contact contact, Fixture fixLight, Fixture fixOther) {
		if (isPlayer(fixOther)) {
			Gdx.app.log(TAG, "Player entered Guards FOV!");
			final Guard g = (Guard) fixLight.getUserData();
			g.playerInView.add(fixOther);
		}
	}

	private void handleEndContactWithPlayer(Contact contact, Fixture fixPlayer, Fixture fixOther) {
		if (isArtTrigger(fixOther)) {
			Gdx.app.log(TAG, "Player no longer touching " + artTag.getCurrentArt());
			Player.instance.isTouchingArt = false;
		} else if (isExit(fixOther)) {
			Gdx.app.log(TAG, "End touching exit");
			Player.instance.isTouchingExit = false;
		} else if (isDoor(fixOther)) {
			Gdx.app.log(TAG, "End touching door");
			Player.instance.isTouchingDoor = false;
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
			final Guard g = (Guard) fixOther.getBody().getUserData();
			g.isTouchingPlayerLightCone = false;
		}
	}

	private void handleEndContactWithGuardLight(Contact contact, Fixture fixLight, Fixture fixOther) {
		if (isPlayer(fixOther)) {
			final Guard g = (Guard) fixLight.getUserData();
			Gdx.app.log(TAG, "Guard can no longer see player!");
			g.playerInView.removeValue(fixOther, true);
			g.isAlert = false;
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

	private boolean isGuardLight(Fixture fix) {
		return fix.getFilterData().categoryBits == ArtTag.CAT_GUARDLIGHT;
	}

	private boolean isGuard(Fixture fix) {
		return fix.getFilterData().categoryBits == ArtTag.CAT_GUARD;
	}

	private boolean isExit(Fixture fix) {
		return fix.getFilterData().categoryBits == ArtTag.CAT_EXIT;
	}

	private boolean isDoor(Fixture fix) {
		return fix.getFilterData().categoryBits == ArtTag.CAT_DOOR;
	}
}
