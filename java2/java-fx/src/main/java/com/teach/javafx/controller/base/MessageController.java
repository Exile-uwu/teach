package com.teach.javafx.controller.base;

import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.*;
import javafx.stage.Stage;

/**
 * MessageController 登录交互控制类 对应 base/message-dialog.fxml
 *  @FXML  属性 对应fxml文件中的
 *  @FXML 方法 对应于fxml文件中的 on***Click的属性
 */

public class MessageController {
    @FXML
    private TextFlow textFLow;

    private Text text;
    private Stage stage;
    /**
     * 页面加载对象创建完成初始话方法，页面中控件属性的设置，初始数据显示等初始操作都在这里完成，其他代码都事件处理方法里
     */
    @FXML
    public void initialize() {
        text = new Text("");
        text.setFill(Color.BLACK);
        text.setFont(Font.font("Arial", FontWeight.BOLD, 16));
        textFLow.getChildren().add(text);
        textFLow.setLineSpacing(5);
        textFLow.setDisable(false);
        textFLow.setStyle("-fx-padding: 10;"
                +"-fx-background-color:white;");
    }

    @FXML
    public void okButtonClick(){
        stage.close();
    }
    public Stage getStage() {
        return stage;
    }

    public void setStage(Stage stage) {
        this.stage = stage;
    }
    public void showDialog(String msg) {
        text.setText(msg);
        this.stage.show();
    }
}
