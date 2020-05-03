package application.options;

import java.util.stream.IntStream;

public enum Numberings {
	NUMBERING1("1, 2, 3...", "%1$1d"),
	NUMBERING01("01, 02, 03...", "%1$02d"),
	NUMBERING001("001, 002, 003...", "%1$03d");
	
	private Numberings(String aText, String aFormat) {
		text = aText;
		format = aFormat;
	}
	
	private final String text;
	private final String format;

	public String getNumberingText() {
		return text;
	}
	
	public String getFormat() {
		return format;
	}
	
	public static String[] getNumberingsText() {
		Numberings[] values = Numberings.values();
		int size = values.length;
		String[] numberings = new String[size];
		IntStream.range(0, size)
			.forEach(i -> numberings[i] = values[i].getNumberingText());
		return numberings;
	}

	public static Numberings getMatchedConstant(String enumName) {
		Numberings[] numberings = values();
		for (Numberings num : numberings) {
			if (num.name().equals(enumName)) {
				return num;
			}
		}
		return null;
	}
}
