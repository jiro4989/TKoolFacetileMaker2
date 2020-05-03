package application;

public enum TKoolVersion {
	MV(144), VXACE(96);
	
	private TKoolVersion(int aWidth) {
		width = aWidth;
	}
	
	public int getWidth() {
		return width;
	}
	
	private final int width;

	public static TKoolVersion getMatchedConstant(String enumName) {
		TKoolVersion[] versions = values();
		for (TKoolVersion version : versions) {
			if (version.name().equals(enumName)) {
				return version;
			}
		}
		return null;
	}
}
