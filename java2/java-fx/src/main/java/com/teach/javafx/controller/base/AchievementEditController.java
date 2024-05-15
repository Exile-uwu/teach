package com.teach.javafx.controller.base;

import com.teach.javafx.request.DataRequest;
import com.teach.javafx.request.DataResponse;
import com.teach.javafx.request.HttpRequestUtil;
import com.teach.javafx.request.OptionItem;
import com.teach.javafx.util.CommonMethod;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.MapValueFactory;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
public class AchievementEditController extends ToolController{
    @FXML
    private TableView<Map> dataTableView;  //学生信息表
    @FXML
    private TableColumn<Map,String> levelColumn;   //学生信息表 编号列
    @FXML
    private TableColumn<Map,String> contentColumn; //学生信息表 名称列
    @FXML
    private TableColumn<Map,String> dateColumn;  //学生信息表 院系列
    @FXML
    private TableColumn<Map,String> awardeeColumn; //学生信息表 专业列
    @FXML
    private TableColumn<Map,String> awardeeNumColumn; //学生信息表 专业列


    @FXML
    private TextField contentField; //学生信息  学号输入域
    @FXML
    private ComboBox<OptionItem> levelComboBox;  //学生信息  性别输入域
    @FXML
    private DatePicker datePick;  //学生信息  出生日期选择域
    @FXML
    private TextField awardeeField; //学生信息  专业输入域
    @FXML
    private TextField awardeeNumField; //学生信息  专业输入域


    @FXML
    private TextField numNameTextField;  //查询 姓名学号输入域

    private Integer achievementId = null;  //当前编辑修改的学生的主键

    private ArrayList<Map> achievementList = new ArrayList();  // 学生信息列表数据
    private List<OptionItem> levelList;   //性别选择列表数据
    private ObservableList<Map> observableList= FXCollections.observableArrayList();  // TableView渲染列表

    private void setTableViewData() {
        observableList.clear();
        for (int j = 0; j < achievementList.size(); j++) {
            observableList.addAll(FXCollections.observableArrayList(achievementList.get(j)));
        }
        dataTableView.setItems(observableList);
    }

    @FXML
    public void initialize() {
        DataResponse res;
        DataRequest req =new DataRequest();
        req.put("numName","");
        res = HttpRequestUtil.request("/api/achievement/getSingleAchievementList",req); //从后台获取所有学生信息列表集合
        if(res != null && res.getCode()== 0) {
            achievementList = (ArrayList<Map>)res.getData();
        }
        levelColumn.setCellValueFactory(new MapValueFactory("levelName"));  //设置列值工程属性
        contentColumn.setCellValueFactory(new MapValueFactory<>("content"));
        dateColumn.setCellValueFactory(new MapValueFactory<>("date"));
        awardeeColumn.setCellValueFactory(new MapValueFactory<>("awardee"));
        awardeeNumColumn.setCellValueFactory(new MapValueFactory<>("awardeeNum"));

        TableView.TableViewSelectionModel<Map> tsm = dataTableView.getSelectionModel();
        ObservableList<Integer> list = tsm.getSelectedIndices();
        list.addListener(this::onTableRowSelect);
        setTableViewData();
        levelComboBox.getItems().clear();
        levelList = HttpRequestUtil.getDictionaryOptionItemList("LEVEL");

        levelComboBox.getItems().addAll(levelList);
        datePick.setConverter(new LocalDateStringConverter("yyyy-MM-dd"));



    }

    public void clearPanel(){
        achievementId = null;
        contentField.setText("");
        levelComboBox.getSelectionModel().select(-1);
        datePick.getEditor().setText("");
        awardeeField.setText("");
        awardeeNumField.setText("");

    }

    protected void changeAchievementInfo() {
        Map form = dataTableView.getSelectionModel().getSelectedItem();
        if(form == null) {
            clearPanel();
            return;
        }
        achievementId = CommonMethod.getInteger(form,"achievementId");
        DataRequest req = new DataRequest();
        req.put("achievementId",achievementId);
        DataResponse res = HttpRequestUtil.request("/api/achievement/getSingleAchievementInfo",req);
        if(res.getCode() != 0){
            MessageDialog.showDialog(res.getMsg());
            return;
        }
        form = (Map)res.getData();
        contentField.setText(CommonMethod.getString(form, "content"));
        levelComboBox.getSelectionModel().select(CommonMethod.getOptionItemIndexByValue(levelList, CommonMethod.getString(form, "level")));
        datePick.getEditor().setText(CommonMethod.getString(form, "date"));
        awardeeField.setText(CommonMethod.getString(form, "awardee"));
        awardeeNumField.setText(CommonMethod.getString(form, "awardeeNum"));

    }

    public void onTableRowSelect(ListChangeListener.Change<? extends Integer> change){
        changeAchievementInfo();
    }

    @FXML
    protected void onQueryButtonClick() {
        String numName = numNameTextField.getText();
        DataRequest req = new DataRequest();
        req.put("numName",numName);
        DataResponse res = HttpRequestUtil.request("/api/achievement/getSingleAchievementList2",req);
        if(res != null && res.getCode()== 0) {
            achievementList = (ArrayList<Map>)res.getData();
            setTableViewData();
        }
    }

    @FXML
    protected void onAddButtonClick() {
        clearPanel();
    }

    @FXML
    protected void onFlashButtonClick()
    {
        initialize();
    }

    @FXML
    protected void onDeleteButtonClick() {
        Map form = dataTableView.getSelectionModel().getSelectedItem();
        if(form == null) {
            MessageDialog.alertDialog("没有选择，不能删除");
            return;
        }
        int ret = MessageDialog.choiceDialog("确认要删除吗?");
        if(ret != MessageDialog.CHOICE_YES) {
            return;
        }
        achievementId = CommonMethod.getInteger(form,"achievementId");
        DataRequest req = new DataRequest();
        req.put("achievementId", achievementId);
        DataResponse res = HttpRequestUtil.request("/api/achievement/achievementSingleDelete",req);
        if(res.getCode() == 0) {
            MessageDialog.showDialog("删除成功！");
            onQueryButtonClick();
        }
        else {
            MessageDialog.alertDialog(res.getMsg());
        }
    }

    @FXML
    protected void onSaveButtonClick() {

        Map form = new HashMap();

        form.put("content",contentField.getText());
        if(levelComboBox.getSelectionModel() != null && levelComboBox.getSelectionModel().getSelectedItem() != null)
            form.put("level",levelComboBox.getSelectionModel().getSelectedItem().getValue());
        form.put("date",datePick.getEditor().getText());
        form.put("awardee",awardeeField.getText());
        form.put("awardeeNum",awardeeNumField.getText());

        DataRequest req = new DataRequest();
        req.put("achievementId", achievementId);
        req.put("form", form);
        DataResponse res = HttpRequestUtil.request("/api/achievement/achievementSingleEditSave",req);
        if(res.getCode() == 0) {
            achievementId = CommonMethod.getIntegerFromObject(res.getData());
            MessageDialog.showDialog("提交成功！");
        }
        else {
            MessageDialog.alertDialog(res.getMsg());
        }
    }

    public void doNew(){
        clearPanel();
    }
    public void doSave(){
        onSaveButtonClick();
    }
    public void doDelete(){
        onDeleteButtonClick();
    }
}
