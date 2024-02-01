package application;

import java.io.IOException;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.text.Text;
import javafx.stage.Stage;

public class Popup extends HBox{

	@FXML
	private Text popupText;
	@FXML
	private ImageView imageView;
	@FXML
	private Button myButton;
	@FXML
	private AnchorPane popupPane;
	
	Image checkmark = new Image(getClass().getResourceAsStream("checkmark.png"));
	Image xmark = new Image(getClass().getResourceAsStream("xmark.png"));
	
	public enum PopupType{
		SUCCESS,
		ERROR
	}
	private String message;
	private PopupType popupType;
	
	public Popup() {
		FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("Notification.fxml"));
		fxmlLoader.setRoot(this);
		fxmlLoader.setController(this);
		
		try {
			fxmlLoader.load();
		}catch(IOException exception) {
			throw new RuntimeException(exception);
		}
	}
	
	public String getMessage() {
		return message;
	}
	
	public void setMessage(String message) {
		this.message = message;
		popupText.setText(this.message);
	}
	
	public PopupType getCardType() {
		return popupType;
	}
	
	public void setPopupType(PopupType popupType) {
		this.popupType = popupType;
		updateNotificationImage();
	}
	
	private void updateNotificationImage() {
		if(popupType == PopupType.SUCCESS) {
			imageView.setImage(checkmark);
			popupText.setText("Success!");
		}else if(popupType == PopupType.ERROR) {
			imageView.setImage(xmark);
			popupText.setText("Not successful");
		}
	}
}
