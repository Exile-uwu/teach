package org.fatmansoft.teach.controllers;

import org.fatmansoft.teach.models.Leave;
import org.fatmansoft.teach.models.Relation;
import org.fatmansoft.teach.models.Student;
import org.fatmansoft.teach.models.User;
import org.fatmansoft.teach.payload.request.DataRequest;
import org.fatmansoft.teach.payload.response.DataResponse;
import org.fatmansoft.teach.payload.response.OptionItem;
import org.fatmansoft.teach.payload.response.OptionItemList;
import org.fatmansoft.teach.repository.*;
import org.fatmansoft.teach.service.BaseService;
import org.fatmansoft.teach.util.CommonMethod;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.*;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/relation")

public class RelationController{
    @Autowired
    private PersonRepository personRepository;  //人员数据操作自动注入
    @Autowired
    private StudentRepository studentRepository;  //学生数据操作自动注入
    @Autowired
    private UserRepository userRepository;  //学生数据操作自动注入
    @Autowired
    private RelationRepository relationRepository;
    @Autowired
    private UserTypeRepository userTypeRepository; //用户类型数据操作自动注入
    @Autowired
    private PasswordEncoder encoder;  //密码服务自动注入
    @Autowired
    private ScoreRepository scoreRepository;  //成绩数据操作自动注入
    @Autowired
    private FeeRepository feeRepository;  //消费数据操作自动注入
    @Autowired
    private BaseService baseService;   //基本数据处理数据操作自动注入

    public synchronized Integer getNewRelationId(){
        Integer  id = relationRepository.getMaxId();  // 查询最大的id
        if(id == null)
            id = 1;
        else
            id = id+1;
        return id;
    };
    public Map getMapFromRelation(Relation s) {
        Map m = new HashMap();
        Student st;
        st=s.getStudent();

        if(s == null)
            return m;
        m.put("relationId", s.getRelationId());
        m.put("dad",s.getDad());
        m.put("mom",s.getMom());
        m.put("dadPhone",s.getDadPhone());
        m.put("momPhone",s.getMomPhone());
        m.put("studentId",st.getStudentId());
        m.put("name",st.getPerson().getName());
        m.put("num",st.getPerson().getNum());

        return m;
    }
    public List getRelationMapList(String numName) {
        List dataList = new ArrayList();
        List<Relation> sList = relationRepository.findRelationListByNumName(numName);  //数据库查询操作
        if(sList == null || sList.size() == 0)
            return dataList;
        for(int i = 0; i < sList.size();i++) {
            dataList.add(getMapFromRelation(sList.get(i)));
        }
        return dataList;
    }

    public List getSingleRelationMapList(Integer studentId) {
        List dataList = new ArrayList();
        List<Relation> sList = relationRepository.findRelationListByStudentId(studentId);
        if(sList == null || sList.size() == 0)
            return dataList;
        for(int i = 0; i < sList.size();i++) {
            dataList.add(getMapFromRelation(sList.get(i)));
        }
        return dataList;
    }


    @PostMapping("/getRelationItemOptionList")
    public OptionItemList getRelationItemOptionList(@Valid @RequestBody DataRequest dataRequest) {
        List<Relation> sList = relationRepository.findRelationListByNumName("");  //数据库查询操作
        OptionItem item;
        List<OptionItem> itemList = new ArrayList();
        for (Relation s : sList) {
            itemList.add(new OptionItem(s.getRelationId(),s.getDad(),s.getMom()));
        }
        return new OptionItemList(0, itemList);
    }

    @PostMapping("/getRelationList")
    @PreAuthorize("hasRole('ADMIN')")
    public DataResponse getRelationList(@Valid @RequestBody DataRequest dataRequest) {
        String numName= dataRequest.getString("numName");
        List dataList = getRelationMapList(numName);
        return CommonMethod.getReturnData(dataList);  //按照测试框架规范会送Map的list
    }

    @PostMapping("/getSingleRelationList")
    @PreAuthorize("hasRole('STUDENT')")
    public DataResponse getSingleRelationList(@Valid @RequestBody DataRequest dataRequest) {
        Integer userId = CommonMethod.getUserId();
        Optional<User> uOp = userRepository.findByUserId(userId);  // 查询获得 user对象
        if(!uOp.isPresent())
            return CommonMethod.getReturnMessageError("用户不存在！");
        User u = uOp.get();
        Optional<Student> sOp= studentRepository.findByPersonPersonId(u.getUserId());  // 查询获得 Student对象
        if(!sOp.isPresent())
            return CommonMethod.getReturnMessageError("学生不存在！");
        Student st= sOp.get();

        List dataList = getSingleRelationMapList(st.getStudentId());
        return CommonMethod.getReturnData(dataList);  //按照测试框架规范会送Map的list
    }


    @PostMapping("/relationDelete")
    @PreAuthorize(" hasRole('ADMIN')")
    public DataResponse relationDelete(@Valid @RequestBody DataRequest dataRequest) {
        Integer relationId = dataRequest.getInteger("relationId");  //获取student_id值
        Relation s= null;
        Optional<Relation> op;
        if(relationId != null) {
            op= relationRepository.findById(relationId);   //查询获得实体对象
            if(op.isPresent()) {
                s = op.get();
            }
        }
        if(s != null) {
            relationRepository.delete(s);    //首先数据库永久删除学生信息
        }
        return CommonMethod.getReturnMessageOK();  //通知前端操作正常
    }

    @PostMapping("/relationSingleDelete")
    @PreAuthorize(" hasRole('STUDENT')")
    public DataResponse relationSingleDelete(@Valid @RequestBody DataRequest dataRequest) {
        Integer relationId = dataRequest.getInteger("relationId");  //获取student_id值
        Relation s= null;
        Optional<Relation> op;
        if(relationId != null) {
            op= relationRepository.findById(relationId);   //查询获得实体对象
            if(op.isPresent()) {
                s = op.get();
            }
        }
        if(s != null) {
            relationRepository.delete(s);    //首先数据库永久删除学生信息
            // 然后数据库永久删除学生信息
        }
        return CommonMethod.getReturnMessageOK();  //通知前端操作正常
    }
    @PostMapping("/getRelationInfo")
    @PreAuthorize("hasRole('ADMIN')")
    public DataResponse getRelationInfo(@Valid @RequestBody DataRequest dataRequest) {
        Integer relationId = dataRequest.getInteger("relationId");
        Relation s= null;
        Optional<Relation> op;
        if(relationId != null) {
            op= relationRepository.findById(relationId); //根据学生主键从数据库查询学生的信息
            if(op.isPresent()) {
                s = op.get();
            }
        }
        return CommonMethod.getReturnData(getMapFromRelation(s)); //这里回传包含学生信息的Map对象
    }

    @PostMapping("/getSingleRelationInfo")
    @PreAuthorize("hasRole('STUDENT')")
    public DataResponse getSingleRelationInfo(@Valid @RequestBody DataRequest dataRequest) {
        Integer relationId = dataRequest.getInteger("relationId");
        Relation s= null;
        Optional<Relation> op;
        if(relationId != null) {
            op= relationRepository.findById(relationId); //根据学生主键从数据库查询学生的信息
            if(op.isPresent()) {
                s = op.get();
            }
        }
        return CommonMethod.getReturnData(getMapFromRelation(s)); //这里回传包含学生信息的Map对象
    }
    /**
     * studentEditSave 前端学生信息提交服务
     * 前端把所有数据打包成一个Json对象作为参数传回后端，后端直接可以获得对应的Map对象form, 再从form里取出所有属性，复制到
     * 实体对象里，保存到数据库里即可，如果是添加一条记录， id 为空，这是先 new Person, User,Student 计算新的id， 复制相关属性，保存，如果是编辑原来的信息，
     * studentId不为空。则查询出实体对象，复制相关属性，保存后修改数据库信息，永久修改
     * @return  新建修改学生的主键 student_id 返回前端
     */
    @PostMapping("/relationEditSave")
    @PreAuthorize(" hasRole('ADMIN')")
    public DataResponse relationEditSave(@Valid @RequestBody DataRequest dataRequest) {
        Integer relationId = dataRequest.getInteger("relationId");
        Map form = dataRequest.getMap("form"); //参数获取Map对象

        Relation s= null;

        Optional<Relation> op;

        if(relationId != null) {
            op= relationRepository.findById(relationId);  //查询对应数据库中主键为id的值的实体对象
            if(op.isPresent()) {
                s = op.get();
            }
        }

        if(s == null) {
            return CommonMethod.getReturnData(-1);
        }
        s.setDad(CommonMethod.getString(form,"dad"));
        s.setMom(CommonMethod.getString(form,"mom"));
        s.setDadPhone(CommonMethod.getString(form,"dadPhone"));
        s.setMomPhone(CommonMethod.getString(form,"momPhone"));

        relationRepository.save(s);  //修改保存学生信息
        return CommonMethod.getReturnData(s.getRelationId());  // 将studentId返回前端
    }

    @PostMapping("/relationSingleEditSave")
    @PreAuthorize(" hasRole('STUDENT')")
    public DataResponse relationSingleEditSave(@Valid @RequestBody DataRequest dataRequest) {
        Integer relationId = dataRequest.getInteger("relationId");
        Map form = dataRequest.getMap("form"); //参数获取Map对象
        Relation s= null;
        Optional<Relation> op;
        if(relationId != null) {
            op= relationRepository.findById(relationId);  //查询对应数据库中主键为id的值的实体对象
            if(op.isPresent()) {
                s = op.get();
            }
        }

        if(s == null) {
            s = new Relation();   // 创建实体对象
            relationId=getNewRelationId();
            s.setRelationId(relationId);
            relationRepository.saveAndFlush(s);  //插入新的Student记录
        }
        Integer userId = CommonMethod.getUserId();
        Optional<User> uOp = userRepository.findByUserId(userId);  // 查询获得 user对象
        if(!uOp.isPresent())
            return CommonMethod.getReturnMessageError("用户不存在！");
        User u = uOp.get();
        Optional<Student> sOp= studentRepository.findByPersonPersonId(u.getUserId());  // 查询获得 Student对象
        if(!sOp.isPresent())
            return CommonMethod.getReturnMessageError("学生不存在！");
        Student st= sOp.get();
        s.setStudent(st);
        s.setDad(CommonMethod.getString(form,"dad"));
        s.setMom(CommonMethod.getString(form,"mom"));
        s.setDadPhone(CommonMethod.getString(form,"dadPhone"));
        s.setMomPhone(CommonMethod.getString(form,"momPhone"));
        s.setName(st.getPerson().getName());
        s.setNum(st.getPerson().getNum());

        relationRepository.save(s);  //修改保存学生信息
        return CommonMethod.getReturnData(s.getRelationId());  // 将studentId返回前端
    }
}
