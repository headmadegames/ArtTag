package headmade.arttag.assets;

import com.badlogic.gdx.assets.loaders.SkinLoader;
import com.badlogic.gdx.assets.loaders.SkinLoader.SkinParameter;

public class AssetTextures {
















	private static final String TAG = AssetTextures.class.getName();
	private static final String IMAGES_FOLDER = "images/";

	public static final String	
	animal1 = 		IMAGES_FOLDER + "animal_11002341854_1a79c6e20e_z.jpg",
	animal2 = 		IMAGES_FOLDER + "animal_11065770075_54a02308a5_z.jpg",
	animal3 = 		IMAGES_FOLDER + "animal_11203197786_f7cd8b1ace_z.jpg",
	animal4 = 		IMAGES_FOLDER + "animal_11278768523_371b964534_z.jpg",
	architecture1 = IMAGES_FOLDER + "architecture_10999020454_695fc0f4ba_z.jpg",
	architecture2 = IMAGES_FOLDER + "architecture_11022860273_353a9313ef_z.jpg",
	architecture3 = IMAGES_FOLDER + "architecture_11101183145_3610268d87_z.jpg",
	architecture4 = IMAGES_FOLDER + "architecture_11221120694_9ec79ff0f8_z.jpg",
	architecture5 = IMAGES_FOLDER + "architecture_11288726236_401da20811_z.jpg",
	flora1 = 		IMAGES_FOLDER + "flora_11090981066_e4ca747983_o.jpg",
	heraldy1 = 		IMAGES_FOLDER + "heraldy_11056880885_69cfc5e2cd_o.jpg",
	heraldy2 = 		IMAGES_FOLDER + "heraldy_11219129233_2cb4557a77_z.jpg",
	letter1 = 		IMAGES_FOLDER + "letter_11002065965_ff6097c65c_o.jpg",
	letter2 = 		IMAGES_FOLDER + "letter_11062410543_9329334d3b_o.jpg",
	letter3 = 		IMAGES_FOLDER + "letter_11080573054_68bf23a2f5_o.jpg",
	letter4 = 		IMAGES_FOLDER + "letter_11098384706_a6dd33a6a1_o.jpg",
	map1 = 			IMAGES_FOLDER + "map_11216557314_1feff90fc8_z.jpg",
	map2 = 			IMAGES_FOLDER + "map_12459585214_8c23124170_z.jpg",
	music1 = 		IMAGES_FOLDER + "music_10998926324_370f3845a8_z.jpg",
	music2 = 		IMAGES_FOLDER + "music_11003096144_6db729e554_z.jpg",
	music3 = 		IMAGES_FOLDER + "music_11275458153_56a2dab7ef_z.jpg",
	people1 = 		IMAGES_FOLDER + "people_11005021165_46f5dd169e_z.jpg",
	people2 = 		IMAGES_FOLDER + "people_11066422203_36e67002db_z.jpg",
	people3 = 		IMAGES_FOLDER + "people_11069424503_0c243138ed_z.jpg",
	people4 = 		IMAGES_FOLDER + "people_11153303395_3f8fc3f944_z.jpg",
	plant1 = 		IMAGES_FOLDER + "flora_10997812453_16427e0d19_z.jpg",
	plant2 = 		IMAGES_FOLDER + "flora_11223952904_b4bc5e8f1e_z.jpg",
	portrait1 = 	IMAGES_FOLDER + "portrait_11124002064_a682fd63e6_z.jpg",
	portrait2 = 	IMAGES_FOLDER + "portrait_11131982834_7c0b7ccd06_z.jpg",
	portrait3 = 	IMAGES_FOLDER + "portrait_11203315676_b6cc8dc67e_z.jpg",
	portrait4 = 	IMAGES_FOLDER + "portrait_11295650295_d1a088c70a_z.jpg",
	symbol1 = 		IMAGES_FOLDER + "symbol_11002252444_046f479827_z.jpg",
	symbol2 = 		IMAGES_FOLDER + "symbol_11093153086_21f4e46bee_o.jpg",
	vehicle1 = 		IMAGES_FOLDER + "vehicle_11297716323_b42c05af8d_z.jpg";

	public static final String[]	ALL_IMAGES	= {
		animal1,
		animal2,
		animal3,
		animal4,
		architecture1,  
		architecture2,  
		architecture3,  
		architecture4,  
		architecture5,  
		flora1,
		heraldy1,
		heraldy2,
		letter1,
		letter2,
		letter3,
		letter4,
		map1,	
		map2,	
		music1,
		music2,
		music3,
		people1,
		people2,
		people3,
		people4,
		plant1,
		plant2,
		portrait1,
		portrait2,
		portrait3,
		portrait4,
		symbol1,
		symbol2,
		vehicle1
	};


	public static final String	
	paper 		= 	"paper",
	frame		=	"frame",
	frame2 		= 	"frame2",
	frame2Large = 	"frame2_color",
	player 		= 	"magpie",
	guard 		= 	"guard00",
	button 		= 	"button",
	joystick 	= 	"joystick",
	placeholder1 =	"placeholder1",
	placeholder2 =	"placeholder2",
	placeholder3 =	"placeholder3", 
	artTreachery = 	"large/title_roman_caps";

	public static final String[]	ALL_PLACEHOLDERS	= {placeholder1, placeholder2, placeholder3};

	//	@formatter:off
	//	@Asset(NinePatch.class)
	//	public static final String
	//	block9 = "block",
	//	blockDown9 = "blockDown";

	//	@Asset(value = TextureAtlas.class)
	//	public static final String atlas = Assets.GAME_ATLAS;

	public static final SkinParameter skinParameter = new SkinLoader.SkinParameter(Assets.GAME_ATLAS);
	//    static {
	//		// init Skin
	//    	skinParameter.
	//	}

	//	@Asset(value = Skin.class, params = "skinParameter")
	public static final String
	skin = Assets.PACKS_BASE + Assets.PACK + ".json";


}
