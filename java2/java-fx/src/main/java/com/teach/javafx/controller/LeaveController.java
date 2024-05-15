package com.teach.javafx.controller;

import com.teach.javafx.controller.base.LocalDateStringConverter;
import com.teach.javafx.controller.base.ToolController;
import com.teach.javafx.request.*;
import com.teach.javafx.util.CommonMethod;
import com.teach.javafx.controller.base.MessageDialog;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.MapValueFactory;
import javafx.stage.FileChooser;

import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class LeaveController extends ToolController {
    @FXML
    private TableView<Map> dataTableView;  //学生信息表
    @FXML
    private TableColumn<Map,String> numColumn;   //学生信息表 编号列
    @FXML
    private TableColumn<Map,String> nameColumn; //学生信息表 名称列
    @FXML
    private TableColumn<Map,String> reasonColumn;  //学生信息表 院系列
    @FXML
    private TableColumn<Map,String> startColumn; //学生信息表 专业列
    @FXML
    private TableColumn<Map,String> endColumn; //学生信息表 班级列

    @FXML
    private TextField numField; //学生信息  学号输入域
    @FXML
    private TextField nameField;  //学生信息  名称输入域
    @FXML
    private TextField reasonField; //学生信息  院系输入域
    @FXML
    private TextField startField; //学生信息  专业输入域
    @FXML
    private TextField endField; //学生信息  班级输入域

    @FXML
    private DatePicker startPick;  //学生信息  出生日期选择域
    @FXML
    private DatePicker endPick;  //学生信息  出生日期选择域

    @FXML
    private TextField numNameTextField;  //查询 姓名学号输入域

    private Integer leaveId = null;  //当前编辑修改的学生的主键

    private ArrayList<Map> leaveList = new ArrayList();  // 学生信息列表数据
    private ObservableList<Map> observableList= FXCollections.observableArrayList();  // TableView渲染列表
    private void setTableViewData() {
        observableList.clear();
        for (int j = 0; j < leaveList.size(); j++) {
            observableList.addAll(FXCollections.observableArrayList(leaveList.get(j)));
        }
        dataTableView.setItems(observableList);
    }
    @FXML
    public void initialize() {
        DataResponse res;
        DataRequest req =new DataRequest();
        req.put("numName","");
        res = HttpRequestUtil.request("/api/leave/getLeaveList",req); //从后台获取所有学生信息列表集合
        if(res != null && res.getCode()== 0) {
            leaveList = (ArrayList<Map>)res.getData();
        }
        numColumn.setCellValueFactory(new MapValueFactory("num"));  //设置列值工程属性
        nameColumn.setCellValueFactory(new MapValueFactory<>("name"));
        reasonColumn.setCellValueFactory(new MapValueFactory<>("reason"));
        startColumn.setCellValueFactory(new MapValueFactory<>("start"));
        endColumn.setCellValueFactory(new MapValueFactory<>("end"));
        TableView.TableViewSelectionModel<Map> tsm = dataTableView.getSelectionModel();
        ObservableList<Integer> list = tsm.getSelectedIndices();
        list.addListener(this::onTableRowSelect);
        setTableViewData();
        endPick.setConverter(new LocalDateStringConverter("yyyy-MM-dd"));
        startPick.setConverter(new LocalDateStringConverter("yyyy-MM-dd"));
    }
    public void clearPanel(){
        leaveId = null;
        numField.setText("");
        nameField.setText("");
        reasonField.setText("");
        startPick.getEditor().setText("");
        endPick.getEditor().setText("");
    }
    protected void changeLeaveInfo() {
        Map form = dataTableView.getSelectionModel().getSelectedItem();
        if(form == null) {
            clearPanel();
            return;
        }
        leaveId = CommonMethod.getInteger(form,"leaveId");
        DataRequest req = new DataRequest();
        req.put("leaveId",leaveId);
        DataResponse res = HttpRequestUtil.request("/api/leave/getLeaveInfo",req);
        if(res.getCode() != 0){
            MessageDialog.showDialog(res.getMsg());
            return;
        }
        form = (Map)res.getData();
        numField.setText(CommonMethod.getString(form, "num"));
        nameField.setText(CommonMethod.getString(form, "name"));
        reasonField.setText(CommonMethod.getString(form, "reason"));
        startPick.getEditor().setText(CommonMethod.getString(form, "start"));
        endPick.getEditor().setText(CommonMethod.getString(form, "end"));
    }
    public void onTableRowSelect(ListChangeListener.Change<? extends Integer> change){
        changeLeaveInfo();
    }
    @FXML
    protected void onQueryButtonClick() {
        String numName = numNameTextField.getText();
        DataRequest req = new DataRequest();
        req.put("numName",numName);
        DataResponse res = HttpRequestUtil.request("/api/leave/getLeaveList",req);
        if(res != null && res.getCode()== 0) {
            leaveList = (ArrayList<Map>)res.getData();
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
        leaveId = CommonMethod.getInteger(form,"leaveId");
        DataRequest req = new DataRequest();
        req.put("leaveId", leaveId);
        DataResponse res = HttpRequestUtil.request("/api/leave/leaveDelete",req);
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
        if(numField.getText().equals("")) {
            MessageDialog.alertDialog("学号为空，不能修改");
            return;
        }
        Map form = new HashMap();
        form.put("num",numField.getText());
        form.put("name",nameField.getText());
        form.put("reason",reasonField.getText());
        form.put("start",startPick.getEditor().getText());
        form.put("end",endPick.getEditor().getText());
        DataRequest req = new DataRequest();
        req.put("leaveId", leaveId);
        req.put("form", form);
        DataResponse res = HttpRequestUtil.request("/api/leave/leaveEditSave",req);
        if(res.getCode() == 0) {
            leaveId = CommonMethod.getIntegerFromObject(res.getData());
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
