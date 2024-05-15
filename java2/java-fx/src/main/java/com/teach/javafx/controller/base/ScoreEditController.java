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
import javafx.scene.control.ComboBox;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.MapValueFactory;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ScoreEditController extends ToolController{
    @FXML
    private TableView<Map> dataTableView;  //学生信息表
    @FXML
    private TableColumn<Map,String> numColumn;   //学生信息表 编号列
    @FXML
    private TableColumn<Map,String> nameColumn; //学生信息表 名称列
    @FXML
    private TableColumn<Map,String> creditColumn;  //学生信息表 院系列
    @FXML
    private TableColumn<Map,String> markColumn; //学生信息表 专业列
    @FXML
    private TableColumn<Map,String> timeColumn; //学生信息表 专业列
    @FXML
    private TableColumn<Map,String> addressColumn; //学生信息表 专业列
    @FXML
    private TableColumn<Map,String> isCompleteColumn; //学生信息表 专业列

    @FXML
    private TableColumn<Map,String> homeworkColumn;//学生信息表 地址列
    @FXML
    private TableColumn<Map,String> teacherNameColumn;//学生信息表 地址列


    @FXML
    private TextField numField; //学生信息  学号输入域
    @FXML
    private TextField nameField;  //学生信息  名称输入域
    @FXML
    private TextField creditField; //学生信息  院系输入域
    @FXML
    private TextField markField; //学生信息  专业输入域

    @FXML
    private TextField homeworkField;  //学生信息  地址输入域
    @FXML
    private TextField timeField; //学生信息  院系输入域
    @FXML
    private TextField addressField; //学生信息  专业输入域

    @FXML
    private TextField isCompleteField;  //学生信息  地址输入域
    @FXML
    private ComboBox<OptionItem> teacherNameComboBox;  //学生信息  性别输入域
    @FXML
    private ComboBox<OptionItem> timeComboBox;  //学生信息  性别输入域


    @FXML
    private TextField numNameTextField;  //查询 姓名学号输入域

    private List<OptionItem> teacherNameList;   //性别选择列表数据
    private List<OptionItem> timeList;   //性别选择列表数据


    private Integer scoreId = null;  //当前编辑修改的学生的主键

    private ArrayList<Map> scoreList = new ArrayList();  // 学生信息列表数据

    private ObservableList<Map> observableList= FXCollections.observableArrayList();  // TableView渲染列表

    private void setTableViewData() {
        observableList.clear();
        for (int j = 0; j < scoreList.size(); j++) {
            observableList.addAll(FXCollections.observableArrayList(scoreList.get(j)));
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
        res = HttpRequestUtil.request("/api/score/getStudentScoreList",req); //从后台获取所有学生信息列表集合
        if(res != null && res.getCode()== 0) {
            scoreList = (ArrayList<Map>)res.getData();
        }
        numColumn.setCellValueFactory(new MapValueFactory("num"));  //设置列值工程属性
        nameColumn.setCellValueFactory(new MapValueFactory<>("name"));
        creditColumn.setCellValueFactory(new MapValueFactory<>("credit"));
        markColumn.setCellValueFactory(new MapValueFactory<>("mark"));
        homeworkColumn.setCellValueFactory(new MapValueFactory<>("homework"));
        timeColumn.setCellValueFactory(new MapValueFactory<>("timeName"));
        addressColumn.setCellValueFactory(new MapValueFactory<>("address"));
        teacherNameColumn.setCellValueFactory(new MapValueFactory<>("teacherNameName"));
        isCompleteColumn.setCellValueFactory(new MapValueFactory<>("isComplete"));


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
        scoreId = null;
        numField.setText("");
        nameField.setText("");
        creditField.setText("");
        teacherNameComboBox.getSelectionModel().select(-1);
        markField.setText("");
        homeworkField.setText("");

    }

    protected void changeScoreInfo() {
        Map form = dataTableView.getSelectionModel().getSelectedItem();
        if(form == null) {
            clearPanel();
            return;
        }
        scoreId = CommonMethod.getInteger(form,"scoreId");
        DataRequest req = new DataRequest();
        req.put("scoreId",scoreId);
        DataResponse res = HttpRequestUtil.request("/api/score/getSingleScoreInfo",req);
        if(res.getCode() != 0){
            MessageDialog.showDialog(res.getMsg());
            return;
        }
        form = (Map)res.getData();
        numField.setText(CommonMethod.getString(form, "num"));
        nameField.setText(CommonMethod.getString(form, "name"));
        creditField.setText(CommonMethod.getString(form, "credit"));
        markField.setText(CommonMethod.getString(form, "mark"));
        homeworkField.setText(CommonMethod.getString(form, "homework"));
        teacherNameComboBox.getSelectionModel().select(CommonMethod.getOptionItemIndexByValue(teacherNameList, CommonMethod.getString(form, "teacherName")));
        timeComboBox.getSelectionModel().select(CommonMethod.getOptionItemIndexByValue(timeList, CommonMethod.getString(form, "time")));
        addressField.setText(CommonMethod.getString(form, "address"));
        isCompleteField.setText(CommonMethod.getString(form, "isComplete"));


    }
    /**
     * 点击学生列表的某一行，根据studentId ,从后台查询学生的基本信息，切换学生的编辑信息
     */

    public void onTableRowSelect(ListChangeListener.Change<? extends Integer> change){
        changeScoreInfo();
    }

    /**
     * 点击查询按钮，从从后台根据输入的串，查询匹配的学生在学生列表中显示
     */
    @FXML
    protected void onQueryButtonClick() {
        String numName = numNameTextField.getText();
        DataRequest req = new DataRequest();
        req.put("numName",numName);
        DataResponse res = HttpRequestUtil.request("/api/score/getStudentScoreList2",req);
        if(res != null && res.getCode()== 0) {
            scoreList = (ArrayList<Map>)res.getData();
            setTableViewData();
        }
    }
    @FXML
    protected void onFlashButtonClick()
    {
        initialize();
    }
    @FXML
    protected void onQuitButtonClick() {
        Map form = dataTableView.getSelectionModel().getSelectedItem();
        if(form == null) {
            MessageDialog.alertDialog("没有选择，不能退选");
            return;
        }
        int ret = MessageDialog.choiceDialog("确认要退选吗?");
        if(ret != MessageDialog.CHOICE_YES) {
            return;
        }
        scoreId = CommonMethod.getInteger(form,"scoreId");

        DataRequest req = new DataRequest();
        req.put("scoreId", scoreId);
        req.put("form", form);
        DataResponse res = HttpRequestUtil.request("/api/score/scoreDelete",req);
        if(res.getCode() == 0) {
            MessageDialog.showDialog("退选成功！");
            onFlashButtonClick();
        }
        else {
            MessageDialog.alertDialog(res.getMsg());
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

    /**
     * 点击保存按钮，保存当前编辑的学生信息，如果是新添加的学生，后台添加学生
     */


    /**
     * doNew() doSave() doDelete() 重写 ToolController 中的方法， 实现选择 新建，保存，删除 对学生的增，删，改操作
     */
    public void doNew(){
        clearPanel();
    }


}
