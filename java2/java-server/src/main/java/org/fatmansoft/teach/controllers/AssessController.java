package org.fatmansoft.teach.controllers;

import org.fatmansoft.teach.models.*;
import org.fatmansoft.teach.payload.request.DataRequest;
import org.fatmansoft.teach.payload.response.DataResponse;
import org.fatmansoft.teach.payload.response.OptionItem;
import org.fatmansoft.teach.payload.response.OptionItemList;
import org.fatmansoft.teach.repository.*;
import org.fatmansoft.teach.service.BaseService;
import org.fatmansoft.teach.util.ComDataUtil;
import org.fatmansoft.teach.util.CommonMethod;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.*;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/assess")

public class AssessController{
    @Autowired
    private PersonRepository personRepository;  //人员数据操作自动注入
    @Autowired
    private StudentRepository studentRepository;  //学生数据操作自动注入
    @Autowired
    private UserRepository userRepository;  //学生数据操作自动注入
    @Autowired
    private AssessRepository assessRepository;
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

    public synchronized Integer getNewAssessId(){
        Integer  id = assessRepository.getMaxId();  // 查询最大的id
        if(id == null)
            id = 1;
        else
            id = id+1;
        return id;
    };
    public Map getMapFromAssess(Assess s) {
        Map m = new HashMap();
        Student st;


        if(s == null)
            return m;
        m.put("assessId", s.getAssessId());
        String name = s.getName();
        m.put("name",name);
        m.put("nameName", ComDataUtil.getInstance().getDictionaryLabelByValue("XS", name));
        m.put("content",s.getContent());
        m.put("date",s.getDate());
        m.put("studentId",s.getStudentId());
        m.put("acceptorId",Integer.parseInt(name));


        return m;
    }

    public Map getMapFromAssessTeacher(Assess s) {
        Map m = new HashMap();
        Student st;


        if(s == null)
            return m;
        m.put("assessId", s.getAssessId());
        String name = s.getName();
        m.put("name",name);
        m.put("nameName", ComDataUtil.getInstance().getDictionaryLabelByValue("LS", name));
        m.put("content",s.getContent());
        m.put("date",s.getDate());
        m.put("studentId",s.getStudentId());
        m.put("acceptorId",Integer.parseInt(name)+500);


        return m;
    }
    public List getAssessMapList(String numName) {
        List dataList = new ArrayList();
        List<Assess> sList = assessRepository.findAssessListByNumName(numName);  //数据库查询操作
        if(sList == null || sList.size() == 0)
            return dataList;
        for(int i = 0; i < sList.size();i++) {
            dataList.add(getMapFromAssess(sList.get(i)));
        }
        return dataList;
    }

    public List getSingleAssessMapList(Integer studentId) {
        List dataList = new ArrayList();
        List<Assess> sList = assessRepository.findAssessListByStudentId(studentId);
        if(sList == null || sList.size() == 0)
            return dataList;
        for(int i = 0; i < sList.size();i++) {
            dataList.add(getMapFromAssess(sList.get(i)));
        }
        return dataList;
    }
    public List getSingleAssessMapList2(Integer studentId,String numName) {
        List dataList = new ArrayList();
        List<Assess> sList = assessRepository.findAssessListByStudentIdAndNumName(studentId,numName);
        if(sList == null || sList.size() == 0)
            return dataList;
        for(int i = 0; i < sList.size();i++) {
            dataList.add(getMapFromAssess(sList.get(i)));
        }
        return dataList;
    }

    public List getTeacherAssessMapList(Integer studentId) {
        List dataList = new ArrayList();
        List<Assess> sList = assessRepository.findAssessTeacherListByStudentId(studentId);
        if(sList == null || sList.size() == 0)
            return dataList;
        for(int i = 0; i < sList.size();i++) {
            dataList.add(getMapFromAssessTeacher(sList.get(i)));
        }
        return dataList;
    }






    @PostMapping("/getAssessList")
    @PreAuthorize("hasRole('ADMIN')")
    public DataResponse getAssessList(@Valid @RequestBody DataRequest dataRequest) {
        String numName= dataRequest.getString("numName");
        List dataList = getAssessMapList(numName);
        return CommonMethod.getReturnData(dataList);  //按照测试框架规范会送Map的list
    }

    @PostMapping("/getSingleAssessList")
    @PreAuthorize("hasRole('STUDENT')")
    public DataResponse getSingleAssessList(@Valid @RequestBody DataRequest dataRequest) {
        Integer userId = CommonMethod.getUserId();
        Optional<User> uOp = userRepository.findByUserId(userId);  // 查询获得 user对象
        if(!uOp.isPresent())
            return CommonMethod.getReturnMessageError("用户不存在！");
        User u = uOp.get();
        Optional<Student> sOp= studentRepository.findByPersonPersonId(u.getUserId());  // 查询获得 Student对象
        if(!sOp.isPresent())
            return CommonMethod.getReturnMessageError("学生不存在！");
        Student st= sOp.get();

        List dataList = getSingleAssessMapList(st.getStudentId());
        return CommonMethod.getReturnData(dataList);  //按照测试框架规范会送Map的list
    }
    @PostMapping("/getSingleAssessList2")
    @PreAuthorize("hasRole('STUDENT')")
    public DataResponse getSingleAssessList2(@Valid @RequestBody DataRequest dataRequest) {
        Integer userId = CommonMethod.getUserId();
        String numName= dataRequest.getString("numName");
        Optional<User> uOp = userRepository.findByUserId(userId);  // 查询获得 user对象
        if(!uOp.isPresent())
            return CommonMethod.getReturnMessageError("用户不存在！");
        User u = uOp.get();
        Optional<Student> sOp= studentRepository.findByPersonPersonId(u.getUserId());  // 查询获得 Student对象
        if(!sOp.isPresent())
            return CommonMethod.getReturnMessageError("学生不存在！");
        Student st= sOp.get();

        List dataList = getSingleAssessMapList2(st.getStudentId(),numName);
        return CommonMethod.getReturnData(dataList);  //按照测试框架规范会送Map的list
    }

    @PostMapping("/getTeacherAssessList")
    @PreAuthorize("hasRole('STUDENT')")
    public DataResponse getTeacherAssessList(@Valid @RequestBody DataRequest dataRequest) {
        Integer userId = CommonMethod.getUserId();
        Optional<User> uOp = userRepository.findByUserId(userId);  // 查询获得 user对象
        if(!uOp.isPresent())
            return CommonMethod.getReturnMessageError("用户不存在！");
        User u = uOp.get();
        Optional<Student> sOp= studentRepository.findByPersonPersonId(u.getUserId());  // 查询获得 Student对象
        if(!sOp.isPresent())
            return CommonMethod.getReturnMessageError("学生不存在！");
        Student st= sOp.get();

        List dataList = getTeacherAssessMapList(st.getStudentId());
        return CommonMethod.getReturnData(dataList);  //按照测试框架规范会送Map的list
    }


    @PostMapping("/assessDelete")
    @PreAuthorize(" hasRole('ADMIN')")
    public DataResponse assessDelete(@Valid @RequestBody DataRequest dataRequest) {
        Integer assessId = dataRequest.getInteger("assessId");  //获取student_id值
        Assess s= null;
        Optional<Assess> op;
        if(assessId != null) {
            op= assessRepository.findById(assessId);   //查询获得实体对象
            if(op.isPresent()) {
                s = op.get();
            }
        }
        if(s != null) {
            assessRepository.delete(s);    //首先数据库永久删除学生信息
        }
        return CommonMethod.getReturnMessageOK();  //通知前端操作正常
    }

    @PostMapping("/assessSingleDelete")
    @PreAuthorize(" hasRole('STUDENT')")
    public DataResponse assessSingleDelete(@Valid @RequestBody DataRequest dataRequest) {
        Integer assessId = dataRequest.getInteger("assessId");  //获取student_id值
        Assess s= null;
        Optional<Assess> op;
        if(assessId != null) {
            op= assessRepository.findById(assessId);   //查询获得实体对象
            if(op.isPresent()) {
                s = op.get();
            }
        }
        if(s != null) {
            assessRepository.delete(s);    //首先数据库永久删除学生信息
            // 然后数据库永久删除学生信息
        }
        return CommonMethod.getReturnMessageOK();  //通知前端操作正常
    }
    @PostMapping("/getAssessInfo")
    @PreAuthorize("hasRole('ADMIN')")
    public DataResponse getAssessInfo(@Valid @RequestBody DataRequest dataRequest) {
        Integer assessId = dataRequest.getInteger("assessId");
        Assess s= null;
        Optional<Assess> op;
        if(assessId != null) {
            op= assessRepository.findById(assessId); //根据学生主键从数据库查询学生的信息
            if(op.isPresent()) {
                s = op.get();
            }
        }
        return CommonMethod.getReturnData(getMapFromAssess(s)); //这里回传包含学生信息的Map对象
    }

    @PostMapping("/getSingleAssessInfo")
    @PreAuthorize("hasRole('STUDENT')")
    public DataResponse getSingleAssessInfo(@Valid @RequestBody DataRequest dataRequest) {
        Integer assessId = dataRequest.getInteger("assessId");
        Assess s= null;
        Optional<Assess> op;
        if(assessId != null) {
            op= assessRepository.findById(assessId); //根据学生主键从数据库查询学生的信息
            if(op.isPresent()) {
                s = op.get();
            }
        }
        return CommonMethod.getReturnData(getMapFromAssess(s)); //这里回传包含学生信息的Map对象
    }
    /**
     * studentEditSave 前端学生信息提交服务
     * 前端把所有数据打包成一个Json对象作为参数传回后端，后端直接可以获得对应的Map对象form, 再从form里取出所有属性，复制到
     * 实体对象里，保存到数据库里即可，如果是添加一条记录， id 为空，这是先 new Person, User,Student 计算新的id， 复制相关属性，保存，如果是编辑原来的信息，
     * studentId不为空。则查询出实体对象，复制相关属性，保存后修改数据库信息，永久修改
     * @return  新建修改学生的主键 student_id 返回前端
     */


    @PostMapping("/assessSingleEditSave")
    @PreAuthorize(" hasRole('STUDENT')")
    public DataResponse assessSingleEditSave(@Valid @RequestBody DataRequest dataRequest) {
        Integer assessId = dataRequest.getInteger("assessId");
        Map form = dataRequest.getMap("form"); //参数获取Map对象
        Assess s= null;
        Optional<Assess> op;
        if(assessId != null) {
            op= assessRepository.findById(assessId);  //查询对应数据库中主键为id的值的实体对象
            if(op.isPresent()) {
                s = op.get();
            }
        }

        if(s == null) {
            s = new Assess();   // 创建实体对象
            assessId=getNewAssessId();
            s.setAssessId(assessId);
            assessRepository.saveAndFlush(s);  //插入新的Student记录
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
        s.setStudentId(st.getStudentId());
        s.setName(CommonMethod.getString(form,"name"));
        s.setContent(CommonMethod.getString(form,"content"));
        s.setDate(CommonMethod.getString(form,"date"));
        s.setAcceptorId(CommonMethod.getString(form,"name"));



        assessRepository.save(s);  //修改保存学生信息
        return CommonMethod.getReturnData(s.getAssessId());  // 将studentId返回前端
    }

    @PostMapping("/assessTeacherEditSave")
    @PreAuthorize(" hasRole('STUDENT')")
    public DataResponse assessTeacherEditSave(@Valid @RequestBody DataRequest dataRequest) {
        Integer assessId = dataRequest.getInteger("assessId");
        Map form = dataRequest.getMap("form"); //参数获取Map对象
        Assess s= null;
        Optional<Assess> op;
        if(assessId != null) {
            op= assessRepository.findById(assessId);  //查询对应数据库中主键为id的值的实体对象
            if(op.isPresent()) {
                s = op.get();
            }
        }

        if(s == null) {
            s = new Assess();   // 创建实体对象
            assessId=getNewAssessId();
            s.setAssessId(assessId);
            assessRepository.saveAndFlush(s);  //插入新的Student记录
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
        s.setStudentId(st.getStudentId());
        s.setName(CommonMethod.getString(form,"name"));
        s.setContent(CommonMethod.getString(form,"content"));
        s.setDate(CommonMethod.getString(form,"date"));
        s.setAcceptorId(String.valueOf(Integer.parseInt(s.getName())+500));



        assessRepository.save(s);  //修改保存学生信息
        return CommonMethod.getReturnData(s.getAssessId());  // 将studentId返回前端
    }
}
