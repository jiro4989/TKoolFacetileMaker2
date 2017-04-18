package application.outputViewer;

import javafx.geometry.Insets;
import javafx.scene.control.Button;

/**
 * 自身のグリッドペインでの行列番号を保持するだけの拡張クラス。
 * @author jiro
 *
 */
public class MyButton extends Button{
	private final int row;
	private final int column;

	public MyButton(String title, int aRow, int aColumn) {
		super(title);

		row = aRow;
		column = aColumn;
		setStyle("-fx-font-size: 28pt; -fx-base: #ffffff;");
		setPadding(new Insets(0,0,0,0));
	}
	
	int getRow() {
		return row;
	}
	
	int getColumn() {
		return column;
	}
	
	@Override
	public String toString() {
		return String.format("title: %s, row: %d, column: %d", getText(), row, column);
	}
}
