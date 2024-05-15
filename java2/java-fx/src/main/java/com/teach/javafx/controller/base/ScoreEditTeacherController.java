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
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.MapValueFactory;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ScoreEditTeacherController extends ToolController{

        @FXML
        private TableView<Map> dataTableView;  //学生信息表
        @FXML
        private TableColumn<Map, String> numColumn;   //学生信息表 编号列
        @FXML
        private TableColumn<Map, String> nameColumn; //学生信息表 名称列
        @FXML
        private TableColumn<Map, String> creditColumn;  //学生信息表 院系列
        @FXML
        private TableColumn<Map, String> markColumn; //学生信息表 专业列

        @FXML
        private TableColumn<Map, String> teacherNameColumn;//学生信息表 地址列
        @FXML
        private TableColumn<Map, String> studentNameColumn;//学生信息表 地址列
        @FXML
        private TableColumn<Map, String> studentNumColumn;//学生信息表 地址列


        @FXML
        private TextField numField; //学生信息  学号输入域
        @FXML
        private TextField nameField;  //学生信息  名称输入域
        @FXML
        private TextField creditField; //学生信息  院系输入域
        @FXML
        private TextField normalField; //学生信息  专业输入域
        @FXML
        private TextField examField; //学生信息  专业输入域

        @FXML
        private TextField teacherNameField;  //学生信息  地址输入域
        @FXML
        private TextField proportionField;  //学生信息  地址输入域
        @FXML
        private TextField studentNumField;  //学生信息  地址输入域


        @FXML
        private TextField numNameTextField;  //查询 姓名学号输入域


        private Integer scoreId = null;  //当前编辑修改的学生的主键

        private ArrayList<Map> scoreList = new ArrayList();  // 学生信息列表数据

        private ObservableList<Map> observableList = FXCollections.observableArrayList();  // TableView渲染列表

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
            DataRequest req = new DataRequest();
            req.put("numName", "");
            res = HttpRequestUtil.request("/api/score/getTeacherScoreList", req); //从后台获取所有学生信息列表集合
            if (res != null && res.getCode() == 0) {
                scoreList = (ArrayList<Map>) res.getData();
            }
            numColumn.setCellValueFactory(new MapValueFactory("num"));  //设置列值工程属性
            nameColumn.setCellValueFactory(new MapValueFactory<>("name"));
            creditColumn.setCellValueFactory(new MapValueFactory<>("credit"));
            markColumn.setCellValueFactory(new MapValueFactory<>("mark"));
            studentNameColumn.setCellValueFactory(new MapValueFactory<>("studentName"));
            studentNumColumn.setCellValueFactory(new MapValueFactory<>("studentNum"));

            teacherNameColumn.setCellValueFactory(new MapValueFactory<>("teacherNameName"));


            TableView.TableViewSelectionModel<Map> tsm = dataTableView.getSelectionModel();
            ObservableList<Integer> list = tsm.getSelectedIndices();
            list.addListener(this::onTableRowSelect);
            setTableViewData();


        }

        /**
         * 清除学生表单中输入信息
         */
        public void clearPanel() {
            scoreId = null;
            numField.setText("");
            nameField.setText("");
            creditField.setText("");
            normalField.setText("");
            examField.setText("");

        }

        protected void changeScoreInfo() {
            Map form = dataTableView.getSelectionModel().getSelectedItem();
            if (form == null) {
                clearPanel();
                return;
            }
            scoreId = CommonMethod.getInteger(form, "scoreId");
            DataRequest req = new DataRequest();
            req.put("scoreId", scoreId);
            DataResponse res = HttpRequestUtil.request("/api/score/getSingleScoreInfo", req);
            if (res.getCode() != 0) {
                MessageDialog.showDialog(res.getMsg());
                return;
            }
            form = (Map) res.getData();

            normalField.setText(CommonMethod.getString(form, "normal"));
            examField.setText(CommonMethod.getString(form, "exam"));
            proportionField.setText(CommonMethod.getString(form, "proportion"));


        }

        /**
         * 点击学生列表的某一行，根据studentId ,从后台查询学生的基本信息，切换学生的编辑信息
         */

        public void onTableRowSelect(ListChangeListener.Change<? extends Integer> change) {
            changeScoreInfo();
        }

        /**
         * 点击查询按钮，从从后台根据输入的串，查询匹配的学生在学生列表中显示
         */
        @FXML
        protected void onQueryButtonClick() {
            String numName = numNameTextField.getText();
            DataRequest req = new DataRequest();
            req.put("numName", numName);
            DataResponse res = HttpRequestUtil.request("/api/score/getTeacherScoreList2", req);
            if (res != null && res.getCode() == 0) {
                scoreList = (ArrayList<Map>) res.getData();
                setTableViewData();
            }
        }

        @FXML
        protected void onFlashButtonClick() {
            initialize();
        }

        @FXML
        protected void onGradeButtonClick() {
            Map form = new HashMap();
            Integer num1,num2,mark=0;
            double proportion;
            if(normalField.getText().length()==0||examField.getText().length()==0||proportionField.getText().length()==0){
                MessageDialog.alertDialog("你有未填项，打分失败！");
                return;
            }
            num1=Integer.parseInt(normalField.getText());
            num2=Integer.parseInt(examField.getText());
            proportion=Double.parseDouble(proportionField.getText());

            if(num1>100||num1<0)
            {
                MessageDialog.alertDialog("平时分必须是0-100的数字！");
                return;
            }
            if(num2>100||num2<0)
            {
                MessageDialog.alertDialog("考试得分必须是0-100的数字！");
                return;
            }
            if(proportion>1||proportion<0)
            {
                MessageDialog.alertDialog("考试得分占比必须是0-1的数字！");
                return;
            }
            mark= (int) (num1*(1-proportion)+num2*proportion);
            form.put("mark",mark);

            DataRequest req = new DataRequest();
            req.put("scoreId", scoreId);
            req.put("form", form);
            DataResponse res = HttpRequestUtil.request("/api/score/scoreTeacherEditSave",req);

            if(res.getCode() == 0) {
                scoreId = CommonMethod.getIntegerFromObject(res.getData());
                MessageDialog.showDialog("打分成功！");
            }
            else {
                MessageDialog.alertDialog(res.getMsg());
            }
        }

        /**
         * 添加新学生， 清空输入信息， 输入相关信息，点击保存即可添加新的学生
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
        public void doNew() {
            clearPanel();
        }


    }

