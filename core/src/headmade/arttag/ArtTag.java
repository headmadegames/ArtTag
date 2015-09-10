package headmade.arttag;

import headmade.arttag.assets.Assets;
import headmade.arttag.screens.IntroScreen;
import headmade.arttag.screens.transitions.ScreenTransition;
import headmade.arttag.screens.transitions.ScreenTransitionFade;
import net.dermetfan.gdx.physics.box2d.Box2DUtils;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;

public class ArtTag extends DirectedGame {

	public static final String	BUTTON_A			= "A";
	public static final String	BUTTON_B			= "B";

	public final static int		VELOCITY_ITERS		= 6;
	public final static int		POSITION_ITERS		= 2;
	public final static int		MAX_FPS				= 60;
	public final static int		MIN_FPS				= 15;
	public final static float	MAX_STEPS			= 1f + MAX_FPS / MIN_FPS;
	public final static float	TIME_STEP			= 1f / MAX_FPS;
	public final static float	MAX_TIME_PER_FRAME	= TIME_STEP * MAX_STEPS;

	public static final float	UNIT_SCALE			= 1f / 64f;
	public static final int		RAYS_NUM			= 256;

	public static final short	CAT_LEVEL			= 0x0001;
	public static final short	CAT_PLAYER			= 0x0002;
	public static final short	CAT_GUARD			= 0x0004;
	public static final short	CAT_ARTTRIGGER		= 0x0008;
	public static final short	CAT_PLAYERLIGHT		= 0x0010;
	public static final short	CAT_LIGHT			= 0x0020;
	public static final short	CAT_EXIT			= 0x0040;

	public static final short	MASK_LEVEL			= -1;
	public static final short	MASK_PLAYER			= CAT_LEVEL | CAT_LIGHT | CAT_GUARD | CAT_ARTTRIGGER | CAT_EXIT;
	public static final short	MASK_PLAYERLIGHT	= CAT_LEVEL | CAT_GUARD | CAT_ARTTRIGGER;
	public static final short	MASK_LIGHT			= CAT_LEVEL | CAT_PLAYER | CAT_GUARD;
	public static final short	MASK_GUARD			= CAT_LEVEL | CAT_PLAYER | CAT_LIGHT | CAT_PLAYERLIGHT;
	public static final short	MASK_ARTTRIGGER		= CAT_PLAYER | CAT_PLAYERLIGHT;
	public static final short	MASK_EXIT			= CAT_PLAYER;

	public static final short	GROUP_LIGHT			= 0x0000;

	// settings
	public static final boolean	TOGGLE_LIGHT		= true;

	@Override
	public void create() {
		Box2DUtils.autoCache = true;

		batch = new SpriteBatch();

		// Load all assets
		Assets.instance.init();

		// Start game with Playground Screen
		final ScreenTransition transition = ScreenTransitionFade.init(0.0f);
		setScreen(new IntroScreen(this), transition);
	}

	@Override
	public void dispose() {
		super.dispose();
		Assets.instance.dispose();
	}

}
