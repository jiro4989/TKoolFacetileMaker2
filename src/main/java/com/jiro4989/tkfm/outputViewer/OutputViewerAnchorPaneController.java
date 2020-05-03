package application.outputViewer;

import java.util.Arrays;

import application.MainController;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.image.Image;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.GridPane;

public class OutputViewerAnchorPaneController {
	private MainController mainController;
	
	@FXML private GridPane panelGridPane;
	private MyButton[] buttons = {
			new MyButton("1", 0, 0),
			new MyButton("2", 0, 1),
			new MyButton("3", 0, 2),
			new MyButton("4", 0, 3),
			new MyButton("5", 1, 0),
			new MyButton("6", 1, 1),
			new MyButton("7", 1, 2),
			new MyButton("8", 1, 3),
	};
	
	private MyImageView[] imageViews = {
			new MyImageView(0, 0),
			new MyImageView(1, 0),
			new MyImageView(2, 0),
			new MyImageView(3, 0),
			new MyImageView(0, 1),
			new MyImageView(1, 1),
			new MyImageView(2, 1),
			new MyImageView(3, 1),
	};

	/**
	 * グリッドペインに拡張ボタンと、その上に重ねて拡張イメージビューを配置する。
	 */
	@FXML
	private void initialize() {
		Arrays.stream(buttons)
			.forEach(b -> {
				b.setPrefSize(144, 144);
				b.setOnAction(e -> setTrimmingImage(b));
				int column = b.getColumn();
				int row = b.getRow();
				panelGridPane.add(b, column, row);

				int index = getIndex(column, row);
				imageViews[index].addEventFilter(MouseEvent.MOUSE_CLICKED, event -> setTrimmingImage(b));
				panelGridPane.add(imageViews[index], column, row);
			});
	}

	public void setMainController(MainController aMainController) {
		mainController = aMainController;
	}
	
	/**
	 * ボタンクリックした位置のグリッドペインのパネルにトリミングしたイメージをセットする。
	 * @param button
	 */
	private void setTrimmingImage(MyButton button) {
		String filePath = mainController.getFilePath();
		int column = button.getColumn();
		int row = button.getRow();
		setTrimmingImage(filePath, column, row);
	}
	
	public void setTrimmingImage(String filePath, int column, int row) {
		if (filePath != null) {
			Image image = mainController.getTrimmingImage();
			double x = mainController.getX();
			double y = mainController.getY();
			double rate = mainController.getRate();
			MyImageView imageView = new MyImageView(image, x, y, rate, filePath, column, row);
			int index = getIndex(column, row);
			imageViews[index].setImageInformation(imageView);
			imageViews[index].setImage(imageView.getImage());

			int width = mainController.getTKoolVersion().getWidth();
			imageViews[index].setFitWidth(width);
			imageViews[index].setFitHeight(width);
			
		}
	}
	
	/**
	 * 行列番号を配列の番号に変換して返す。
	 * @param column
	 * @param row
	 * @return
	 */
	private int getIndex(int column, int row) {
		return column + row * 4;
	}
	
	public ObservableList<Node> getPanelImagesList() {
		return panelGridPane.getChildren();
	}

	/**
	 * グリッドペインに登録しているイメージを初期化する。
	 */
	public void clearImages() {
		Arrays.stream(imageViews)
			.forEach(image -> {
				image.setImageInformation(new MyImageView(image.getColumn(), image.getRow()));
				image.setImage(null);
			});
	}

	/**
	 * グリッドペインやボタン、イメージビューの幅を変更する。
	 * @param width
	 */
	public void changeVersion(double width) {
		panelGridPane.setPrefSize(width * 4, width * 2);
		Arrays.stream(buttons)
			.forEach(b -> b.setPrefWidth(width));
		Arrays.stream(imageViews)
			.forEach(i -> {
				i.setFitWidth(width);
				i.setFitHeight(width);
			});
	}
}
