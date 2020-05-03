package com.jiro4989.tkfm.fileList;

import java.io.File;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.IntStream;

import com.jiro4989.tkfm.MainController;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.control.SelectionMode;
import javafx.scene.input.DragEvent;
import javafx.scene.input.Dragboard;
import javafx.scene.input.TransferMode;

/**
 * トリミングするファイルの名前とパスを管理するクラス。
 * ファイル名がまったく同じでもパスが異なる場合があるため、
 * ファイルの管理はLinkedListを二つファイル名がkeyになっているもので
 * ファイルパスを管理するLinkedListからパスを取得する方法で、
 * ファイルを取得している。
 * @author jiro
 *
 */
public class FileListHBoxController {
	private MainController mainController;
	private LinkedList<String> fileNameList = new LinkedList<>();
	private LinkedList<String> filePathList = new LinkedList<>();

	@FXML ListView<String> fileListView;
	private ObservableList<String> observableList = FXCollections.observableArrayList();
	@FXML private Button insertButton;
	@FXML private Button clearButton;
	@FXML private Button listDeleteButton;
	@FXML private Button listClearButton;

	@FXML
	private void initialize() {
		fileListView.setOnDragOver(e -> dragOver(e));
		fileListView.setOnDragDropped(e -> dragDropped(e));
		fileListView.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
		fileListView.getSelectionModel().selectedItemProperty().addListener(e -> changeSelection());
		insertButton.setOnAction(e -> insertImages(0));
		clearButton.setOnAction(e -> clearOutputImages());
		listDeleteButton.setOnAction(e -> deleteFile());
		listClearButton.setOnAction(e -> clearFiles());

		fileListView.setItems(observableList);
	}

	/**
	 * グリッドペインに割り振られた番号の位置から
	 * 選択中のファイルのイメージを全て貼り付ける。
	 * @param min 貼り付け開始番号
	 */
	public void insertImages(int min) {
		if (!fileListView.getSelectionModel().isEmpty()) {
			ObservableList<Integer> fileIndices = fileListView.getSelectionModel().getSelectedIndices();

			int max = fileIndices.size();
			IntStream.range(min, Math.min(max+min, 8))
				.forEach(i -> {
					int index = fileIndices.get(i-min);
					fileListView.getSelectionModel().select(index);
					changeSelection();
					String filePath = filePathList.get(index);
					int column = 3 < i ? i - 4 : i;
					int row = i / 4;
					mainController.setTrimmingImage(filePath, column, row);
				});
		}
	}

	/**
	 * 出力画像パネルのイメージをクリアする。
	 */
	private void clearOutputImages() {
		mainController.clearOutputImages();
	}

	/**
	 * 最初に選択したファイルをリストから削除する。
	 */
	public void deleteFile() {
		if (!fileListView.getSelectionModel().isEmpty()) {
			int index = fileListView.getSelectionModel().getSelectedIndex();
			fileListView.getItems().remove(index);
			fileNameList.remove(index);
			filePathList.remove(index);
		}
		clearImageView();
	}

	/**
	 * リストの全てのファイルを削除する。
	 */
	public void clearFiles() {
		if (!fileListView.getSelectionModel().isEmpty()) {
			fileListView.getItems().clear();
			fileNameList.clear();
			filePathList.clear();
		}
		clearImageView();
	}
	
	/**
	 * イメージビューに登録している画像をクリアする。
	 */
	private void clearImageView() {
		if (fileListView.getSelectionModel().isEmpty()) {
			mainController.clearImageView();
		}
	}

	/**
	 * リストビューの選択が変更された場合に呼び出される。
	 */
	private void changeSelection() {
		if (!fileListView.getSelectionModel().isEmpty()){
			mainController.sendFileName(getFilePath());
		}
	}

	/**
	 * ドラッグオーバーでファイルを受け取る
	 */
	@FXML
	private void dragOver(DragEvent event){
		Dragboard board = event.getDragboard();
		if (board.hasFiles()) {
			event.acceptTransferModes(TransferMode.COPY);
		}
	}

	/**
	 * ドロップでリストに格納
	 */
	@FXML
	private void dragDropped(DragEvent event){
		Dragboard board = event.getDragboard();
		if (board.hasFiles()) {
			List<File> list = board.getFiles();
			addFiles(list);
			event.setDropCompleted(true);
			event.acceptTransferModes(TransferMode.COPY);
		} else{
			event.setDropCompleted(false);
		}
	}
	
	/**
	 * ドラッグオーバー、あるいはメニューの開くから取得したファイルをリストに追加する。
	 * @param files
	 */
	public void addFiles(List<File> files) {
		files.stream()
			.filter(f -> f.getName().endsWith(".png"))
			.sorted()
			.forEach(f -> {
				fileNameList.add(f.getName());
				filePathList.add(f.getPath());
				fileListView.getItems().add(f.getName());
			});
	}

	/**
	 * 選択中のリストのファイルパスを返す。
	 * @return
	 */
	public String getFilePath() {
		if (!fileListView.getSelectionModel().isEmpty()){
			int index = fileListView.getSelectionModel().getSelectedIndex();
			String filePath = filePathList.get(index);
			return filePath;
		}
		return null;
	}

	/**
	 * コントローラをセットする。
	 * @param aMainController
	 */
	public void setMainController(MainController aMainController) {
		mainController = aMainController;
	}
}
