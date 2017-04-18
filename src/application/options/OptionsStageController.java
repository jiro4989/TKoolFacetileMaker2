package application.options;

import java.io.File;
import java.util.Arrays;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.ToggleButton;
import javafx.stage.Window;

/**
 * オプション設定画面のコントローラクラス。
 * 更新した値がMainControllerに渡されるのは、OKボタンがクリックされた場合のみで、
 * キャンセル、あるいは閉じるで終了した場合、値は更新されない。
 * @author jiro
 *
 */
public class OptionsStageController {
	private Options options;
	/**
	 * キャンセルボタンが押されたときに返す初期オプション。
	 */
	private Options initialOptions;
	private Separators separators;
	private Numberings numberings;

	// **************************************************
	// 保存設定タブ
	// **************************************************
	@FXML private ToggleButton separatorToggleButton;

	@FXML private ComboBox<String> separatorComboBox;
	private ObservableList<String> separatroItems = FXCollections.observableArrayList(Separators.getSeparatorsText());

	@FXML private ComboBox<String> numberingComboBox;
	private ObservableList<String> numberingItems = FXCollections.observableArrayList(Numberings.getNumberingsText());

	@FXML private Label fileNamePreviewLabel;

	// **************************************************
	// フォント設定タブ
	// **************************************************
	@FXML private ComboBox<Integer> fontSizeComboBox;
	ObservableList<Integer> fontSizeItems = FXCollections.observableArrayList(8, 9, 10, 11, 12, 13, 14);

	// **************************************************
	// 終了ボタン
	// **************************************************
	@FXML private Button okButton;
	@FXML private Button cancelButton;
	
	@FXML
	public void initialize(){
		separatorToggleButton.setOnAction(e -> changeToggleButtonState(separatorToggleButton));
		separatorComboBox.setOnAction(e -> updatePreviewLabel());
		numberingComboBox.setOnAction(e -> updatePreviewLabel());
		okButton.setOnAction(e -> ok());
		cancelButton.setOnAction(e -> cancel());

		separatorComboBox.setItems(separatroItems);
		numberingComboBox.setItems(numberingItems);
		separatorComboBox.getSelectionModel().select(0);
		numberingComboBox.getSelectionModel().select(0);
		
		fontSizeComboBox.setItems(fontSizeItems);
		fontSizeComboBox.getSelectionModel().select(4);
	}
	
	private void ok() {
		boolean separator = separatorToggleButton.isSelected();
		int fontSize = fontSizeComboBox.getSelectionModel().getSelectedItem();
		options = new Options(separator, separators, numberings, fontSize);
		getWindow().hide();
	}

	private void cancel() {
		options = new Options(initialOptions);
		getWindow().hide();
	}

	/**
	 * トグルボタンのテキストを変更する。
	 * @param toggleButton
	 */
	private void changeToggleButtonState(ToggleButton toggleButton) {
		String text = toggleButton.isSelected() ? "ON" : "OFF";
		toggleButton.setText(text);
		updatePreviewLabel();
	}
	
	/**
	 * 各種設定項目に応じたプレビューラベルのテキストに整形してセットする。
	 */
	private void updatePreviewLabel() {
		changeOptions();
		File file = options.makeFormatedFile("sample.png", 1);
		fileNamePreviewLabel.setText(file.getName());
	}
	
	/**
	 * ボタンのいるウィンドウを返す。
	 * @return
	 */
	private Window getWindow() {
		return okButton.getScene().getWindow();
	}
	
	public Options getOptions() {
		return options;
	}
	
	/**
	 * オプション設定を変更し、MainControllerに渡すOptionsインスタンスを更新する。
	 */
	private void changeOptions() {
		boolean separator = separatorToggleButton.isSelected();
		changeSeparators();
		changeNumberings();
		int fontSize = fontSizeComboBox.getSelectionModel().getSelectedItem();

		options = new Options(separator, separators, numberings, fontSize);
	}

	/**
	 * セパレータのインスタンスを変更する。
	 */
	private void changeSeparators() {
		int index = separatorComboBox.getSelectionModel().getSelectedIndex();
		Arrays.stream(Separators.values())
			.filter(s -> s.ordinal() == index)
			.forEach(s -> separators = s);
	}
	
	/**
	 * ナンバリングフォーマットのインスタンスを変更する。
	 */
	private void changeNumberings() {
		int index = numberingComboBox.getSelectionModel().getSelectedIndex();
		Arrays.stream(Numberings.values())
			.filter(s -> s.ordinal() == index)
			.forEach(s -> numberings = s);
	}
	

	/**
	 * 渡されたオプションインスタンスを元に書くパネルの状態を変更する。
	 * @param anOptions
	 */
	public void setOptions(Options anOptions) {
		options = new Options(anOptions);
		initialOptions= new Options(options);
		separatorToggleButton.setSelected(options.getSeparatorSwitch());
		String text = separatorToggleButton.isSelected() ? "ON" : "OFF";
		separatorToggleButton.setText(text);
		separatorComboBox.getSelectionModel().select(options.getSeparator().ordinal());
		numberingComboBox.getSelectionModel().select(options.getNumbering().ordinal());
		fontSizeComboBox.getSelectionModel().select(options.getFontSize()-8);
		
		updatePreviewLabel();
	}
}
