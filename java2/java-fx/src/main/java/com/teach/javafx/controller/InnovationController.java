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

public class InnovationController extends ToolController {
    @FXML
    private TableView<Map> dataTableView;  //学生信息表
    @FXML
    private TableColumn<Map,String> contentColumn;   //学生信息表 编号列
    @FXML
    private TableColumn<Map,String> typeColumn; //学生信息表 名称列
    @FXML
    private TableColumn<Map,String> dateColumn;  //学生信息表 院系列
    @FXML
    private TableColumn<Map,String> awardeeColumn; //学生信息表 专业列


    @FXML
    private TextField contentField; //学生信息  学号输入域
    @FXML
    private ComboBox<OptionItem> typeComboBox;  //学生信息  性别输入域
    @FXML
    private DatePicker datePick;  //学生信息  出生日期选择域
    @FXML
    private TextField awardeeField; //学生信息  专业输入域


    @FXML
    private TextField numNameTextField;  //查询 姓名学号输入域

    private Integer innovationId = null;  //当前编辑修改的学生的主键

    private ArrayList<Map> innovationList = new ArrayList();  // 学生信息列表数据
    private List<OptionItem> typeList;   //性别选择列表数据
    private ObservableList<Map> observableList= FXCollections.observableArrayList();  // TableView渲染列表


    /**
     * 将学生数据集合设置到面板上显示
     */
    private void setTableViewData() {
        observableList.clear();
        for (int j = 0; j < innovationList.size(); j++) {
            observableList.addAll(FXCollections.observableArrayList(innovationList.get(j)));
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
        res = HttpRequestUtil.request("/api/innovation/getInnovationList",req); //从后台获取所有学生信息列表集合
        if(res != null && res.getCode()== 0) {
            innovationList = (ArrayList<Map>)res.getData();
        }
        contentColumn.setCellValueFactory(new MapValueFactory("content"));  //设置列值工程属性
        typeColumn.setCellValueFactory(new MapValueFactory<>("typeName"));
        dateColumn.setCellValueFactory(new MapValueFactory<>("date"));
        awardeeColumn.setCellValueFactory(new MapValueFactory<>("awardee"));

        TableView.TableViewSelectionModel<Map> tsm = dataTableView.getSelectionModel();
        ObservableList<Integer> list = tsm.getSelectedIndices();
        list.addListener(this::onTableRowSelect);
        setTableViewData();
        typeComboBox.getItems().clear();
        typeList = HttpRequestUtil.getDictionaryOptionItemList("HDLX");

        typeComboBox.getItems().addAll(typeList);
        datePick.setConverter(new LocalDateStringConverter("yyyy-MM-dd"));



    }

    /**
     * 清除学生表单中输入信息
     */
    public void clearPanel(){
        innovationId = null;
        contentField.setText("");
        typeComboBox.getSelectionModel().select(-1);
        datePick.getEditor().setText("");
        awardeeField.setText("");

    }

    protected void changeInnovationInfo() {
        Map form = dataTableView.getSelectionModel().getSelectedItem();
        if(form == null) {
            clearPanel();
            return;
        }
        innovationId = CommonMethod.getInteger(form,"innovationId");
        DataRequest req = new DataRequest();
        req.put("innovationId",innovationId);
        DataResponse res = HttpRequestUtil.request("/api/innovation/getInnovationInfo",req);
        if(res.getCode() != 0){
            MessageDialog.showDialog(res.getMsg());
            return;
        }
        form = (Map)res.getData();
        contentField.setText(CommonMethod.getString(form, "content"));
        typeComboBox.getSelectionModel().select(CommonMethod.getOptionItemIndexByValue(typeList, CommonMethod.getString(form, "type")));
        datePick.getEditor().setText(CommonMethod.getString(form, "date"));
        awardeeField.setText(CommonMethod.getString(form, "awardee"));

    }
    /**
     * 点击学生列表的某一行，根据studentId ,从后台查询学生的基本信息，切换学生的编辑信息
     */

    public void onTableRowSelect(ListChangeListener.Change<? extends Integer> change){
        changeInnovationInfo();
    }

    /**
     * 点击查询按钮，从从后台根据输入的串，查询匹配的学生在学生列表中显示
     */
    @FXML
    protected void onQueryButtonClick() {
        String numName = numNameTextField.getText();
        DataRequest req = new DataRequest();
        req.put("numName",numName);
        DataResponse res = HttpRequestUtil.request("/api/innovation/getInnovationList",req);
        if(res != null && res.getCode()== 0) {
            innovationList = (ArrayList<Map>)res.getData();
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
    @FXML
    protected void onFlashButtonClick()
    {
        initialize();
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
        innovationId = CommonMethod.getInteger(form,"innovationId");
        DataRequest req = new DataRequest();
        req.put("innovationId", innovationId);
        DataResponse res = HttpRequestUtil.request("/api/innovation/innovationDelete",req);
        if(res.getCode() == 0) {
            MessageDialog.showDialog("删除成功！");
            onFlashButtonClick();
        }
        else {
            MessageDialog.showDialog(res.getMsg());
        }
    }
    /**
     * 点击保存按钮，保存当前编辑的学生信息，如果是新添加的学生，后台添加学生
     */
    @FXML
    protected void onSaveButtonClick() {

        Map form = new HashMap();
        form.put("content",contentField.getText());
        if(typeComboBox.getSelectionModel() != null && typeComboBox.getSelectionModel().getSelectedItem() != null)
            form.put("type",typeComboBox.getSelectionModel().getSelectedItem().getValue());
        form.put("date",datePick.getEditor().getText());
        form.put("awardee",awardeeField.getText());

        DataRequest req = new DataRequest();
        req.put("innovationId", innovationId);
        req.put("form", form);
        DataResponse res = HttpRequestUtil.request("/api/innovation/innovationEditSave",req);
        if(res.getCode() == 0) {
            innovationId = CommonMethod.getIntegerFromObject(res.getData());
            MessageDialog.showDialog("提交成功！");
        }
        else {
            MessageDialog.showDialog(res.getMsg());
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

    /**
     * 导出学生信息表的示例 重写ToolController 中的doExport 这里给出了一个导出学生基本信息到Excl表的示例， 后台生成Excl文件数据，传回前台，前台将文件保存到本地
     */


    }

