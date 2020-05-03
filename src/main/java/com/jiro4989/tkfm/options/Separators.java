package application.options;

import java.util.stream.IntStream;

public enum Separators {
	UNDER_SCORE("'_'(アンダースコア)", "_"),
	HYPHEN("'-'(ハイフン)", "-");
	
	private Separators(String aText, String aSeparator) {
		separatorText = aText;
		separator = aSeparator;
	}
	
	private final String separatorText;
	private final String separator;
	
	public String getSeparatorText() {
		return separatorText;
	}
	
	public String getSeparator() {
		return separator;
	}
	
	/**
	 * コンボボックスに表示するためのテキストを返す。
	 * @return
	 */
	public static String[] getSeparatorsText() {
		Separators[] values = Separators.values();
		int size = values.length;
		String[] separators = new String[size];
		IntStream.range(0, size)
			.forEach(i -> separators[i] = values[i].getSeparatorText());
		return separators;
	}
	
	/**
	 * 文字列の末尾が、Separatorsの定数の区切り線のいずれかとマッチした場合trueを返す。
	 * いずれともマッチしなかった場合、つまり末尾に区切り線が存在しない場合はfalseを返す。
	 * @param target
	 * @return
	 */
	public static boolean anyMatchEnds(String target) {
		Separators[] separators = values();
		for (Separators sep : separators) {
			if (target.endsWith(sep.getSeparator())) {
				return true;
			}
		}
		return false;
	}
	
	/**
	 * Separators定数の名前と一致した場合、一致した定数を返す。
	 * 一致するものが存在しなかった場合はnullを返す。
	 * @param enumName
	 * @return
	 */
	public static Separators getMatchedConstant(String enumName) {
		Separators[] separators = values();
		for (Separators sep : separators) {
			if (sep.name().equals(enumName)) {
				return sep;
			}
		}
		return null;
	}
}
