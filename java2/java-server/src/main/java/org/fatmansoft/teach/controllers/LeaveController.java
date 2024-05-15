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
@RequestMapping("/api/leave")

public class LeaveController {
    @Autowired
    private PersonRepository personRepository;  //人员数据操作自动注入
    @Autowired
    private StudentRepository studentRepository;  //学生数据操作自动注入
    @Autowired
    private UserRepository userRepository;  //学生数据操作自动注入
    @Autowired
    private LeaveRepository leaveRepository;
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

    public synchronized Integer getNewLeaveId(){
        Integer  id = leaveRepository.getMaxId();  // 查询最大的id
        if(id == null)
            id = 1;
        else
            id = id+1;
        return id;
    };
    public Map getMapFromLeave(Leave s) {
        Map m = new HashMap();
        Student st;
        st=s.getStudent();

        if(s == null)
            return m;
        m.put("leaveId", s.getLeaveId());
        m.put("name",s.getName());
        m.put("num",s.getNum());
        m.put("reason",s.getReason());
        m.put("start",s.getStart());
        m.put("studentId",st.getStudentId());
        m.put("end", s.getEnd());


        return m;
    }
    public List getLeaveMapList(String numName) {
        List dataList = new ArrayList();
        List<Leave> sList = leaveRepository.findLeaveListByNumName(numName);  //数据库查询操作
        if(sList == null || sList.size() == 0)
            return dataList;
        for(int i = 0; i < sList.size();i++) {
            dataList.add(getMapFromLeave(sList.get(i)));
        }
        return dataList;
    }

    public List getSingleLeaveMapList(Integer studentId) {
        List dataList = new ArrayList();
        List<Leave> sList = leaveRepository.findLeaveListByStudentId(studentId);
        if(sList == null || sList.size() == 0)
            return dataList;
        for(int i = 0; i < sList.size();i++) {
            dataList.add(getMapFromLeave(sList.get(i)));
        }
        return dataList;
    }

    public List getSingleLeaveMapList2(Integer studentId,String numName) {
        List dataList = new ArrayList();
        List<Leave> sList = leaveRepository.findLeaveListByStudentIdAndNumName(studentId,numName);
        if(sList == null || sList.size() == 0)
            return dataList;
        for(int i = 0; i < sList.size();i++) {
            dataList.add(getMapFromLeave(sList.get(i)));
        }
        return dataList;
    }
    @PostMapping("/getLeaveItemOptionList")
    public OptionItemList getLeaveItemOptionList(@Valid @RequestBody DataRequest dataRequest) {
        List<Leave> sList = leaveRepository.findLeaveListByNumName("");  //数据库查询操作
        OptionItem item;
        List<OptionItem> itemList = new ArrayList();
        for (Leave s : sList) {
            itemList.add(new OptionItem(s.getLeaveId(),s.getName(),s.getReason()));
        }
        return new OptionItemList(0, itemList);
    }

    @PostMapping("/getLeaveList")
    @PreAuthorize("hasRole('ADMIN')")
    public DataResponse getLeaveList(@Valid @RequestBody DataRequest dataRequest) {
        String numName= dataRequest.getString("numName");
        List dataList = getLeaveMapList(numName);
        return CommonMethod.getReturnData(dataList);  //按照测试框架规范会送Map的list
    }

    @PostMapping("/getSingleLeaveList")
    @PreAuthorize("hasRole('STUDENT')")
    public DataResponse getSingleLeaveList(@Valid @RequestBody DataRequest dataRequest) {
        Integer userId = CommonMethod.getUserId();
        Optional<User> uOp = userRepository.findByUserId(userId);  // 查询获得 user对象
        if(!uOp.isPresent())
            return CommonMethod.getReturnMessageError("用户不存在！");
        User u = uOp.get();
        Optional<Student> sOp= studentRepository.findByPersonPersonId(u.getUserId());  // 查询获得 Student对象
        if(!sOp.isPresent())
            return CommonMethod.getReturnMessageError("学生不存在！");
        Student st= sOp.get();

        List dataList = getSingleLeaveMapList(st.getStudentId());
        return CommonMethod.getReturnData(dataList);  //按照测试框架规范会送Map的list
    }
    @PostMapping("/getSingleLeaveList2")
    @PreAuthorize("hasRole('STUDENT')")
    public DataResponse getSingleLeaveList2(@Valid @RequestBody DataRequest dataRequest) {
        String numName= dataRequest.getString("numName");
        Integer userId = CommonMethod.getUserId();
        Optional<User> uOp = userRepository.findByUserId(userId);  // 查询获得 user对象
        if(!uOp.isPresent())
            return CommonMethod.getReturnMessageError("用户不存在！");
        User u = uOp.get();
        Optional<Student> sOp= studentRepository.findByPersonPersonId(u.getUserId());  // 查询获得 Student对象
        if(!sOp.isPresent())
            return CommonMethod.getReturnMessageError("学生不存在！");
        Student st= sOp.get();

        List dataList = getSingleLeaveMapList2(st.getStudentId(),numName);
        return CommonMethod.getReturnData(dataList);  //按照测试框架规范会送Map的list
    }

    @PostMapping("/leaveDelete")
    @PreAuthorize(" hasRole('ADMIN')")
    public DataResponse leaveDelete(@Valid @RequestBody DataRequest dataRequest) {
        Integer leaveId = dataRequest.getInteger("leaveId");  //获取student_id值
        Leave s= null;
        Optional<Leave> op;
        if(leaveId != null) {
            op= leaveRepository.findById(leaveId);   //查询获得实体对象
            if(op.isPresent()) {
                s = op.get();
            }
        }
        if(s != null) {
            leaveRepository.delete(s);    //首先数据库永久删除学生信息
        }
        return CommonMethod.getReturnMessageOK();  //通知前端操作正常
    }

    @PostMapping("/leaveSingleDelete")
    @PreAuthorize(" hasRole('STUDENT')")
    public DataResponse leaveSingleDelete(@Valid @RequestBody DataRequest dataRequest) {
        Integer leaveId = dataRequest.getInteger("leaveId");  //获取student_id值
        Leave s= null;
        Optional<Leave> op;
        if(leaveId != null) {
            op= leaveRepository.findById(leaveId);   //查询获得实体对象
            if(op.isPresent()) {
                s = op.get();
            }
        }
        if(s != null) {
            leaveRepository.delete(s);    //首先数据库永久删除学生信息
            // 然后数据库永久删除学生信息
        }
        return CommonMethod.getReturnMessageOK();  //通知前端操作正常
    }
    @PostMapping("/getLeaveInfo")
    @PreAuthorize("hasRole('ADMIN')")
    public DataResponse getLeaveInfo(@Valid @RequestBody DataRequest dataRequest) {
        Integer leaveId = dataRequest.getInteger("leaveId");
        Leave s= null;
        Optional<Leave> op;
        if(leaveId != null) {
            op= leaveRepository.findById(leaveId); //根据学生主键从数据库查询学生的信息
            if(op.isPresent()) {
                s = op.get();
            }
        }
        return CommonMethod.getReturnData(getMapFromLeave(s)); //这里回传包含学生信息的Map对象
    }

    @PostMapping("/getSingleLeaveInfo")
    @PreAuthorize("hasRole('STUDENT')")
    public DataResponse getSingleLeaveInfo(@Valid @RequestBody DataRequest dataRequest) {
        Integer leaveId = dataRequest.getInteger("leaveId");
        Leave s= null;
        Optional<Leave> op;
        if(leaveId != null) {
            op= leaveRepository.findById(leaveId); //根据学生主键从数据库查询学生的信息
            if(op.isPresent()) {
                s = op.get();
            }
        }
        return CommonMethod.getReturnData(getMapFromLeave(s)); //这里回传包含学生信息的Map对象
    }
    /**
     * studentEditSave 前端学生信息提交服务
     * 前端把所有数据打包成一个Json对象作为参数传回后端，后端直接可以获得对应的Map对象form, 再从form里取出所有属性，复制到
     * 实体对象里，保存到数据库里即可，如果是添加一条记录， id 为空，这是先 new Person, User,Student 计算新的id， 复制相关属性，保存，如果是编辑原来的信息，
     * studentId不为空。则查询出实体对象，复制相关属性，保存后修改数据库信息，永久修改
     * @return  新建修改学生的主键 student_id 返回前端
     */
    @PostMapping("/leaveEditSave")
    @PreAuthorize(" hasRole('ADMIN')")
    public DataResponse leaveEditSave(@Valid @RequestBody DataRequest dataRequest) {
        Integer leaveId = dataRequest.getInteger("leaveId");
        Map form = dataRequest.getMap("form"); //参数获取Map对象

        Leave s= null;

        Optional<Leave> op;

        if(leaveId != null) {
            op= leaveRepository.findById(leaveId);  //查询对应数据库中主键为id的值的实体对象
            if(op.isPresent()) {
                s = op.get();
            }
        }

        if(s == null) {

            s = new Leave();   // 创建实体对象
            s.setLeaveId(getNewLeaveId());

            leaveRepository.saveAndFlush(s);  //插入新的Student记录
        }
        s.setName(CommonMethod.getString(form,"name"));
        s.setNum(CommonMethod.getString(form,"num"));
        s.setReason(CommonMethod.getString(form,"reason"));
        s.setStart(CommonMethod.getString(form,"start"));
        s.setEnd(CommonMethod.getString(form,"end"));
        leaveRepository.save(s);  //修改保存学生信息
        return CommonMethod.getReturnData(s.getLeaveId());  // 将studentId返回前端
    }

    @PostMapping("/leaveSingleEditSave")
    @PreAuthorize(" hasRole('STUDENT')")
    public DataResponse leaveSingleEditSave(@Valid @RequestBody DataRequest dataRequest) {
        Integer leaveId = dataRequest.getInteger("leaveId");
        Map form = dataRequest.getMap("form"); //参数获取Map对象
        String num = CommonMethod.getString(form,"num");  //Map 获取属性的值
        Leave s= null;

        Optional<Leave> op;
        if(leaveId != null) {
            op= leaveRepository.findById(leaveId);  //查询对应数据库中主键为id的值的实体对象
            if(op.isPresent()) {
                s = op.get();
            }
        }

        if(s == null) {
            s = new Leave();   // 创建实体对象
            leaveId=getNewLeaveId();
            s.setLeaveId(leaveId);

            leaveRepository.saveAndFlush(s);  //插入新的Student记录
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
        s.setReason(CommonMethod.getString(form,"reason"));
        s.setStart(CommonMethod.getString(form,"start"));
        s.setEnd(CommonMethod.getString(form,"end"));
        s.setName(st.getPerson().getName());
        s.setNum(st.getPerson().getNum());

        leaveRepository.save(s);  //修改保存学生信息
        return CommonMethod.getReturnData(s.getLeaveId());  // 将studentId返回前端
    }

}
