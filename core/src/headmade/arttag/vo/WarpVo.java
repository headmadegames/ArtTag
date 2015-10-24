package headmade.arttag.vo;

public class WarpVo {

	private static final String TAG = WarpVo.class.getName();

	public String	direction;
	public String	room;

	public WarpVo(String direction, String room) {
		super();
		this.direction = direction;
		this.room = room;
	}
}
