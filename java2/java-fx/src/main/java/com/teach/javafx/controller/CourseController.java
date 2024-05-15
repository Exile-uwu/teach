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
public class CourseController extends ToolController {
    @FXML
    private TableView<Map> dataTableView;  //学生信息表
    @FXML
    private TableColumn<Map,String> numColumn;   //学生信息表 编号列
    @FXML
    private TableColumn<Map,String> nameColumn; //学生信息表 名称列
    @FXML
    private TableColumn<Map,String> creditColumn;  //学生信息表 院系列
    @FXML
    private TableColumn<Map,Integer> capacityColumn;  //学生信息表 院系列
    @FXML
    private TableColumn<Map,String> teacherNameColumn; //学生信息表 专业列
    @FXML
    private TableColumn<Map,String> timeColumn; //学生信息表 班级列
    @FXML
    private TableColumn<Map,String> informationColumn; //学生信息表 证件号码列
    @FXML
    private TableColumn<Map,String> addressColumn;//学生信息表 地址列


    @FXML
    private TextField numField; //学生信息  学号输入域
    @FXML
    private TextField nameField;  //学生信息  名称输入域
    @FXML
    private TextField creditField; //学生信息  院系输入域
    @FXML
    private TextField teacherNameField; //学生信息  院系输入域
    @FXML
    private ComboBox<OptionItem> teacherNameComboBox;  //学生信息  性别输入域
    @FXML
    private ComboBox<OptionItem> timeComboBox;  //学生信息  性别输入域
    @FXML
    private TextField capacityField; //学生信息  班级输入域
    @FXML
    private TextField informationField; //学生信息  证件号码输入域
    @FXML
    private TextField addressField;  //学生信息  地址输入域


    @FXML
    private TextField numNameTextField;  //查询 姓名学号输入域


    private Integer courseId = null;  //当前编辑修改的学生的主键
    private List<OptionItem> teacherNameList;   //性别选择列表数据
    private List<OptionItem> timeList;   //性别选择列表数据

    private ArrayList<Map> courseList = new ArrayList();  // 学生信息列表数据
    private Integer scoreId = null;  //当前编辑修改的学生的主键

    private ArrayList<Map> scoreList = new ArrayList();  // 学生信息列表数据

    private ObservableList<Map> observableList= FXCollections.observableArrayList();  // TableView渲染列表

    private void setTableViewData() {
        observableList.clear();
        for (int j = 0; j < courseList.size(); j++) {
            observableList.addAll(FXCollections.observableArrayList(courseList.get(j)));
        }
        dataTableView.setItems(observableList);
    }

    /**
     * 页面加载对象创建完成初始化方法，页面中控件属性的设置，初始数据显示等初始操作都在这里完成，其他代码都事件处理方法里
     */

    @FXML
    public void initialize() {
        DataResponse res;
        DataRequest req =new DataRequest();
        req.put("numName","");
        res = HttpRequestUtil.request("/api/course/getCourseList",req); //从后台获取所有学生信息列表集合
        if(res != null && res.getCode()== 0) {
            courseList = (ArrayList<Map>)res.getData();
        }
        numColumn.setCellValueFactory(new MapValueFactory("num"));  //设置列值工程属性
        nameColumn.setCellValueFactory(new MapValueFactory<>("name"));
        creditColumn.setCellValueFactory(new MapValueFactory<>("credit"));
        capacityColumn.setCellValueFactory(new MapValueFactory<>("capacity"));
        teacherNameColumn.setCellValueFactory(new MapValueFactory<>("teacherNameName"));
        timeColumn.setCellValueFactory(new MapValueFactory<>("timeName"));
        informationColumn.setCellValueFactory(new MapValueFactory<>("information"));
        addressColumn.setCellValueFactory(new MapValueFactory<>("address"));

        TableView.TableViewSelectionModel<Map> tsm = dataTableView.getSelectionModel();
        ObservableList<Integer> list = tsm.getSelectedIndices();
        list.addListener(this::onTableRowSelect);
        setTableViewData();
        teacherNameComboBox.getItems().clear();
        timeComboBox.getItems().clear();
        teacherNameList = HttpRequestUtil.getDictionaryOptionItemList("LS");
        timeList = HttpRequestUtil.getDictionaryOptionItemList("TIME");

        teacherNameComboBox.getItems().addAll(teacherNameList);
        timeComboBox.getItems().addAll(timeList);



    }

    /**
     * 清除学生表单中输入信息
     */
    public void clearPanel(){
        courseId = null;
        numField.setText("");
        nameField.setText("");
        creditField.setText("");
        capacityField.setText("");
        teacherNameComboBox.getSelectionModel().select(-1);
        timeComboBox.getSelectionModel().select(-1);
        informationField.setText("");
        addressField.setText("");

    }

    protected void changeCourseInfo() {
        Map form = dataTableView.getSelectionModel().getSelectedItem();
        if(form == null) {
            clearPanel();
            return;
        }
        courseId = CommonMethod.getInteger(form,"courseId");
        DataRequest req = new DataRequest();
        req.put("courseId",courseId);
        DataResponse res = HttpRequestUtil.request("/api/course/getCourseInfo",req);
        if(res.getCode() != 0){
            MessageDialog.showDialog(res.getMsg());
            return;
        }
        form = (Map)res.getData();
        numField.setText(CommonMethod.getString(form, "num"));
        nameField.setText(CommonMethod.getString(form, "name"));
        creditField.setText(CommonMethod.getString(form, "credit"));
        capacityField.setText(CommonMethod.getString(form, "capacity"));
        teacherNameComboBox.getSelectionModel().select(CommonMethod.getOptionItemIndexByValue(teacherNameList, CommonMethod.getString(form, "teacherName")));
        timeComboBox.getSelectionModel().select(CommonMethod.getOptionItemIndexByValue(timeList, CommonMethod.getString(form, "time")));
        informationField.setText(CommonMethod.getString(form, "information"));
        addressField.setText(CommonMethod.getString(form, "address"));
    }
    /**
     * 点击学生列表的某一行，根据studentId ,从后台查询学生的基本信息，切换学生的编辑信息
     */

    public void onTableRowSelect(ListChangeListener.Change<? extends Integer> change){
        changeCourseInfo();
    }

    /**
     * 点击查询按钮，从从后台根据输入的串，查询匹配的学生在学生列表中显示
     */
    @FXML
    protected void onQueryButtonClick() {
        String numName = numNameTextField.getText();
        DataRequest req = new DataRequest();
        req.put("numName",numName);
        DataResponse res = HttpRequestUtil.request("/api/course/getCourseList",req);
        if(res != null && res.getCode()== 0) {
            courseList = (ArrayList<Map>)res.getData();
            setTableViewData();
        }
    }

    /**
     *  添加新学生， 清空输入信息， 输入相关信息，点击保存即可添加新的学生
     */
    @FXML
    protected void onAddButtonClick() {
        clearPanel();
    }

    /**
     * 点击删除按钮 删除当前编辑的学生的数据
     */
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
        courseId = CommonMethod.getInteger(form,"courseId");
        DataRequest req = new DataRequest();
        req.put("courseId", courseId);
        DataResponse res = HttpRequestUtil.request("/api/course/courseDelete",req);
        if(res.getCode() == 0) {
            MessageDialog.showDialog("删除成功！");
            onQueryButtonClick();
        }
        else {
            MessageDialog.alertDialog(res.getMsg());
        }
    }
    /**
     * 点击保存按钮，保存当前编辑的学生信息，如果是新添加的学生，后台添加学生
     */
    @FXML
    protected void onSaveButtonClick() {

        Map form = new HashMap();
        if(numField.getText().length()==0){
            MessageDialog.alertDialog("请填写课序号！");
            return;
        }
        if(Integer.parseInt(capacityField.getText())<0){
            MessageDialog.alertDialog("课容量不能小于0！");
            return;
        }
        if(Integer.parseInt(creditField.getText())<=0){
            MessageDialog.alertDialog("学分必须大于0！");
            return;
        }
        form.put("num",numField.getText());
        form.put("name",nameField.getText());
        form.put("credit",creditField.getText());
        form.put("capacity",capacityField.getText());
        if(teacherNameComboBox.getSelectionModel() != null && teacherNameComboBox.getSelectionModel().getSelectedItem() != null)
            form.put("teacherName",teacherNameComboBox.getSelectionModel().getSelectedItem().getValue());
        if(timeComboBox.getSelectionModel() != null && timeComboBox.getSelectionModel().getSelectedItem() != null)
            form.put("time",timeComboBox.getSelectionModel().getSelectedItem().getValue());
        form.put("information",informationField.getText());

        form.put("address",addressField.getText());

        DataRequest req = new DataRequest();
        req.put("courseId", courseId);
        req.put("form", form);
        DataResponse res = HttpRequestUtil.request("/api/course/courseEditSave",req);
        if(res.getCode() == 0) {
            courseId = CommonMethod.getIntegerFromObject(res.getData());
            if(courseId==-1){
                MessageDialog.alertDialog("该课序号已被使用");
            }
            else
            MessageDialog.showDialog("提交成功！");
        }

        else {
            MessageDialog.alertDialog(res.getMsg());
        }
    }
    @FXML
    protected void onFlashButtonClick()
    {
        initialize();
    }

    @FXML
    protected void onChooseButtonClick()
    {
        Map form = new HashMap();
        Integer c=0;
        if(numField.getText().length()==0){
            MessageDialog.alertDialog("没有选择，选课失败！");
            return;
        }
        c=Integer.parseInt(capacityField.getText());
        if(c<=0)
        {
            MessageDialog.alertDialog("课容量不足！选课失败！");
            return;
        }
        if(c>0)
            c--;
        form.put("num",numField.getText());
        form.put("name",nameField.getText());
        form.put("credit",creditField.getText());
        form.put("capacity",c.toString());
        if(teacherNameComboBox.getSelectionModel() != null && teacherNameComboBox.getSelectionModel().getSelectedItem() != null)
            form.put("teacherName",teacherNameComboBox.getSelectionModel().getSelectedItem().getValue());
        if(timeComboBox.getSelectionModel() != null && timeComboBox.getSelectionModel().getSelectedItem() != null)
            form.put("time",timeComboBox.getSelectionModel().getSelectedItem().getValue());
        form.put("information",informationField.getText());

        form.put("address",addressField.getText());

        DataRequest req = new DataRequest();
        req.put("scoreId", scoreId);
        req.put("form", form);
        DataResponse res = HttpRequestUtil.request("/api/score/scoreEditSave",req);
        if(res.getCode() == 0) {
            scoreId = CommonMethod.getIntegerFromObject(res.getData());
            if(scoreId==-1)
            MessageDialog.alertDialog("不能重复选课，选课失败");
        }
        if(res.getCode() == 0) {
            scoreId = CommonMethod.getIntegerFromObject(res.getData());
            if(scoreId==-2)
                MessageDialog.alertDialog("该课程与已选课程冲突，选课失败");
        }
        if(res.getCode() == 0) {
            scoreId = CommonMethod.getIntegerFromObject(res.getData());
            if(scoreId!=-1&&scoreId!=-2)
            MessageDialog.showDialog("选课成功！");
        }
        else {
            MessageDialog.alertDialog(res.getMsg());
        }
    }



    /**
     * doNew() doSave() doDelete() 重写 ToolController 中的方法， 实现选择 新建，保存，删除 对学生的增，删，改操作
     */
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
