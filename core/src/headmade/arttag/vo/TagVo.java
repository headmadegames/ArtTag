package headmade.arttag.vo;

public class TagVo {

	private static final String	TAG	= TagVo.class.getName();

	public String				tag;
	public int					countTag;
	public int					countTagNotMatched;

	public TagVo(String tag) {
		this.tag = tag;
	}

	@Override
	public String toString() {
		return "TagVo [tag=" + tag + ", countTag=" + countTag + ", countTagNotMatched=" + countTagNotMatched + "]";
	}
}
