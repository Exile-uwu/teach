package com.teach.javafx.controller;

import com.teach.javafx.controller.base.MessageDialog;
import com.teach.javafx.controller.base.ToolController;
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

public class RelationController extends ToolController {
    @FXML
    private TableView<Map> dataTableView;  //学生信息表
    @FXML
    private TableColumn<Map,String> numColumn;   //学生信息表 编号列
    @FXML
    private TableColumn<Map,String> nameColumn; //学生信息表 名称列
    @FXML
    private TableColumn<Map,String> dadColumn;  //学生信息表 院系列
    @FXML
    private TableColumn<Map,String> dadPhoneColumn; //学生信息表 专业列
    @FXML
    private TableColumn<Map,String> momColumn;  //学生信息表 院系列
    @FXML
    private TableColumn<Map,String> momPhoneColumn; //学生信息表 专业列


    @FXML
    private TextField numField; //学生信息  学号输入域
    @FXML
    private TextField nameField; //学生信息  学号输入域

    @FXML
    private TextField dadField; //学生信息  专业输入域
    @FXML
    private TextField dadPhoneField; //学生信息  学号输入域
    @FXML
    private TextField momField; //学生信息  学号输入域

    @FXML
    private TextField momPhoneField; //学生信息  专业输入域


    @FXML
    private TextField numNameTextField;  //查询 姓名学号输入域

    private Integer relationId = null;  //当前编辑修改的学生的主键

    private ArrayList<Map> relationList = new ArrayList();  // 学生信息列表数据

    private ObservableList<Map> observableList= FXCollections.observableArrayList();  // TableView渲染列表
    private void setTableViewData() {
        observableList.clear();
        for (int j = 0; j < relationList.size(); j++) {
            observableList.addAll(FXCollections.observableArrayList(relationList.get(j)));
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
        res = HttpRequestUtil.request("/api/relation/getRelationList",req); //从后台获取所有学生信息列表集合
        if(res != null && res.getCode()== 0) {
            relationList = (ArrayList<Map>)res.getData();
        }
        numColumn.setCellValueFactory(new MapValueFactory("num"));  //设置列值工程属性
        nameColumn.setCellValueFactory(new MapValueFactory<>("name"));
        dadColumn.setCellValueFactory(new MapValueFactory<>("dad"));
        dadPhoneColumn.setCellValueFactory(new MapValueFactory<>("dadPhone"));
        momColumn.setCellValueFactory(new MapValueFactory<>("mom"));
        momPhoneColumn.setCellValueFactory(new MapValueFactory<>("momPhone"));
        TableView.TableViewSelectionModel<Map> tsm = dataTableView.getSelectionModel();
        ObservableList<Integer> list = tsm.getSelectedIndices();
        list.addListener(this::onTableRowSelect);
        setTableViewData();

    }

    /**
     * 清除学生表单中输入信息
     */
    public void clearPanel(){
        relationId = null;
        numField.setText("");
        nameField.setText("");
        dadField.setText("");
        dadPhoneField.setText("");
        momField.setText("");
        momPhoneField.setText("");

    }

    protected void changeInnovationInfo() {
        Map form = dataTableView.getSelectionModel().getSelectedItem();
        if(form == null) {
            clearPanel();
            return;
        }
        relationId = CommonMethod.getInteger(form,"relationId");
        DataRequest req = new DataRequest();
        req.put("relationId",relationId);
        DataResponse res = HttpRequestUtil.request("/api/relation/getRelationInfo",req);
        if(res.getCode() != 0){
            MessageDialog.showDialog(res.getMsg());
            return;
        }
        form = (Map)res.getData();
        numField.setText(CommonMethod.getString(form, "num"));
        nameField.setText(CommonMethod.getString(form, "name"));
        dadField.setText(CommonMethod.getString(form, "dad"));
        dadPhoneField.setText(CommonMethod.getString(form, "dadPhone"));
        momField.setText(CommonMethod.getString(form, "mom"));
        momPhoneField.setText(CommonMethod.getString(form, "momPhone"));

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
        DataResponse res = HttpRequestUtil.request("/api/relation/getRelationList",req);
        if(res != null && res.getCode()== 0) {
            relationList = (ArrayList<Map>)res.getData();
            setTableViewData();
        }
    }
    @FXML
    protected void onSaveButtonClick() {

        Map form = new HashMap();
        form.put("dad",dadField.getText());
        form.put("dadPhone",dadPhoneField.getText());
        form.put("mom",momField.getText());
        form.put("momPhone",momPhoneField.getText());
        DataRequest req = new DataRequest();
        req.put("relationId", relationId);
        req.put("form", form);
        DataResponse res = HttpRequestUtil.request("/api/relation/relationEditSave",req);
        if(res.getCode() == 0) {

            relationId = CommonMethod.getIntegerFromObject(res.getData());
            if(relationId==-1){
                MessageDialog.alertDialog("你只能对该信息进行修改而无法进行添加！");
            }
            else{
            MessageDialog.showDialog("提交成功！");
            }
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

    @FXML
    protected void onFlashButtonClick()
    {
        initialize();
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
