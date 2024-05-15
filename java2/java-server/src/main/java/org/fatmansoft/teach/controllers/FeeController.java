package org.fatmansoft.teach.controllers;

import org.fatmansoft.teach.models.Course;
import org.fatmansoft.teach.models.Fee;
import org.fatmansoft.teach.models.Student;
import org.fatmansoft.teach.models.User;
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
@RequestMapping("/api/fee")
public class FeeController {
    @Autowired
    private PersonRepository personRepository;  //人员数据操作自动注入
    @Autowired
    private StudentRepository studentRepository;  //人员数据操作自动注入
    @Autowired
    private UserRepository userRepository;  //学生数据操作自动注入
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
    public synchronized Integer getNewFeeId(){
        Integer  id = feeRepository.getMaxId();  // 查询最大的id
        if(id == null)
            id = 1;
        else
            id = id+1;
        return id;
    };
    public Map getMapFromFee(Fee s) {
        Map m = new HashMap();

        if(s == null)
            return m;

        m.put("feeId", s.getFeeId());
        m.put("studentId",s.getStudentId());

        m.put("money",s.getMoney());
        m.put("name",s.getName());

        m.put("day",s.getDay());
        m.put("remark",s.getRemark());


        return m;
    }

    /**
     *  getStudentMapList 根据输入参数查询得到学生数据的 Map List集合 参数为空 查出说有学生， 参数不为空，查出人员编号或人员名称 包含输入字符串的学生
     * @param numName 输入参数
     * @return  Map List 集合
     */
    public List getFeeMapList(String numName) {
        List dataList = new ArrayList();
        List<Fee> sList = feeRepository.findFeeListByNumName(numName);  //数据库查询操作
        if(sList == null || sList.size() == 0)
            return dataList;
        for(int i = 0; i < sList.size();i++) {
            dataList.add(getMapFromFee(sList.get(i)));
        }
        return dataList;
    }

    public List getSingleFeeMapList(Integer studentId) {
        List dataList = new ArrayList();
        List<Fee> sList = feeRepository.findListByStudent(studentId);  //数据库查询操作
        if(sList == null || sList.size() == 0)
            return dataList;
        for(int i = 0; i < sList.size();i++) {
            dataList.add(getMapFromFee(sList.get(i)));
        }
        return dataList;
    }
    public List getSingleFeeMapList2(Integer studentId,String numName) {
        List dataList = new ArrayList();
        List<Fee> sList = feeRepository.findFeeListByStudentIdAndRemark(studentId,numName);  //数据库查询操作
        if(sList == null || sList.size() == 0)
            return dataList;
        for(int i = 0; i < sList.size();i++) {
            dataList.add(getMapFromFee(sList.get(i)));
        }
        return dataList;
    }




    /**
     *  getStudentList 学生管理 点击查询按钮请求
     *  前台请求参数 numName 学号或名称的 查询串
     * 返回前端 存储学生信息的 MapList 框架会自动将Map转换程用于前后台传输数据的Json对象，Map的嵌套结构和Json的嵌套结构类似
     * @return
     */

    @PostMapping("/getFeeItemOptionList")
    public OptionItemList getFeeItemOptionList(@Valid @RequestBody DataRequest dataRequest) {
        List<Fee> sList = feeRepository.findFeeListByNumName("");  //数据库查询操作
        OptionItem item;
        List<OptionItem> itemList = new ArrayList();
        for (Fee s : sList) {
            itemList.add(new OptionItem(s.getFeeId(), s.getDay(), s.getMoney()+"-"+s.getDay()));//此处可能有问题-hzd
        }
        return new OptionItemList(0, itemList);
    }

    @PostMapping("/getFeeList")
    @PreAuthorize("hasRole('ADMIN')")
    public DataResponse getFeeList(@Valid @RequestBody DataRequest dataRequest) {
        String numName= dataRequest.getString("numName");
        List dataList = getFeeMapList(numName);
        return CommonMethod.getReturnData(dataList);  //按照测试框架规范会送Map的list
    }

    @PostMapping("/getSingleFeeList")
    @PreAuthorize("hasRole('STUDENT')")
    public DataResponse getSingleFeeList(@Valid @RequestBody DataRequest dataRequest) {
        Integer userId = CommonMethod.getUserId();
        Optional<User> uOp = userRepository.findByUserId(userId);  // 查询获得 user对象
        if(!uOp.isPresent())
            return CommonMethod.getReturnMessageError("用户不存在！");
        User u = uOp.get();
        Optional<Student> sOp= studentRepository.findByPersonPersonId(u.getUserId());  // 查询获得 Student对象
        if(!sOp.isPresent())
            return CommonMethod.getReturnMessageError("学生不存在！");
        Student st= sOp.get();

        List dataList = getSingleFeeMapList(st.getStudentId());
        return CommonMethod.getReturnData(dataList);  //按照测试框架规范会送Map的list
    }
    @PostMapping("/getSingleFeeList2")
    @PreAuthorize("hasRole('STUDENT')")
    public DataResponse getSingleFeeList2(@Valid @RequestBody DataRequest dataRequest) {
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

        List dataList = getSingleFeeMapList2(st.getStudentId(),numName);
        return CommonMethod.getReturnData(dataList);  //按照测试框架规范会送Map的list
    }




    /**
     * studentDelete 删除学生信息Web服务 Student页面的列表里点击删除按钮则可以删除已经存在的学生信息， 前端会将该记录的id 回传到后端，方法从参数获取id，查出相关记录，调用delete方法删除
     * 这里注意删除顺序，应为user关联person,Student关联Person 所以要先删除Student,User，再删除Person
     * @param dataRequest  前端studentId 药删除的学生的主键 student_id
     * @return  正常操作
     */

    @PostMapping("/feeDelete")
    @PreAuthorize(" hasRole('ADMIN')")
    public DataResponse feeDelete(@Valid @RequestBody DataRequest dataRequest) {
        Integer feeId = dataRequest.getInteger("feeId");  //获取student_id值
        Fee s= null;
        Optional<Fee> op;
        if(feeId != null) {
            op= feeRepository.findById(feeId);   //查询获得实体对象
            if(op.isPresent()) {
                s = op.get();
            }
        }
        if(s != null) {
            feeRepository.delete(s);    //首先数据库永久删除学生信息
            // 然后数据库永久删除学生信息
        }
        return CommonMethod.getReturnMessageOK();  //通知前端操作正常
    }

    @PostMapping("/feeSingleDelete")
    @PreAuthorize(" hasRole('STUDENT')")
    public DataResponse feeSingleDelete(@Valid @RequestBody DataRequest dataRequest) {
        Integer feeId = dataRequest.getInteger("feeId");  //获取student_id值
        Fee s= null;
        Optional<Fee> op;
        if(feeId != null) {
            op= feeRepository.findById(feeId);   //查询获得实体对象
            if(op.isPresent()) {
                s = op.get();
            }
        }
        if(s != null) {
            feeRepository.delete(s);    //首先数据库永久删除学生信息
            // 然后数据库永久删除学生信息
        }
        return CommonMethod.getReturnMessageOK();  //通知前端操作正常
    }

    /**
     * getStudentInfo 前端点击学生列表时前端获取学生详细信息请求服务
     * @param dataRequest 从前端获取 studentId 查询学生信息的主键 student_id
     * @return  根据studentId从数据库中查出数据，存在Map对象里，并返回前端
     */

    @PostMapping("/getFeeInfo")
    @PreAuthorize("hasRole('ADMIN')")
    public DataResponse getFeeInfo(@Valid @RequestBody DataRequest dataRequest) {
        Integer feeId = dataRequest.getInteger("feeId");
        Fee s= null;
        Optional<Fee> op;
        if(feeId != null) {
            op= feeRepository.findById(feeId); //根据学生主键从数据库查询学生的信息
            if(op.isPresent()) {
                s = op.get();
            }
        }
        return CommonMethod.getReturnData(getMapFromFee(s)); //这里回传包含学生信息的Map对象
    }

    @PostMapping("/getSingleFeeInfo")
    @PreAuthorize("hasRole('STUDENT')")
    public DataResponse getSingleFeeInfo(@Valid @RequestBody DataRequest dataRequest) {
        Integer feeId = dataRequest.getInteger("feeId");
        Fee s= null;
        Optional<Fee> op;
        if(feeId != null) {
            op= feeRepository.findById(feeId); //根据学生主键从数据库查询学生的信息
            if(op.isPresent()) {
                s = op.get();
            }
        }
        return CommonMethod.getReturnData(getMapFromFee(s)); //这里回传包含学生信息的Map对象
    }

    /**
     * studentEditSave 前端学生信息提交服务
     * 前端把所有数据打包成一个Json对象作为参数传回后端，后端直接可以获得对应的Map对象form, 再从form里取出所有属性，复制到
     * 实体对象里，保存到数据库里即可，如果是添加一条记录， id 为空，这是先 new Person, User,Student 计算新的id， 复制相关属性，保存，如果是编辑原来的信息，
     * studentId不为空。则查询出实体对象，复制相关属性，保存后修改数据库信息，永久修改
     * @return  新建修改学生的主键 student_id 返回前端
     */
    @PostMapping("/feeEditSave")
    @PreAuthorize(" hasRole('ADMIN')")
    public DataResponse feeEditSave(@Valid @RequestBody DataRequest dataRequest) {
        Integer feeId = dataRequest.getInteger("feeId");
        Map form = dataRequest.getMap("form"); //参数获取Map对象
        String num = CommonMethod.getString(form,"num");  //Map 获取属性的值
        Fee s= null;

        Optional<Fee> op;
        if(feeId != null) {
            op= feeRepository.findById(feeId);  //查询对应数据库中主键为id的值的实体对象
            if(op.isPresent()) {
                s = op.get();
            }
        }

        if(s == null) {
            s = new Fee();   // 创建实体对象
            feeId=getNewFeeId();
            s.setFeeId(feeId);

            feeRepository.saveAndFlush(s);  //插入新的Student记录
        }

        s.setMoney(CommonMethod.getDouble(form,"money"));


        s.setDay(CommonMethod.getString(form,"day"));
        s.setRemark(CommonMethod.getString(form,"remark"));


        feeRepository.save(s);  //修改保存学生信息
        return CommonMethod.getReturnData(s.getFeeId());  // 将studentId返回前端
    }

    @PostMapping("/feeSingleEditSave")
    @PreAuthorize(" hasRole('STUDENT')")
    public DataResponse feeSingleEditSave(@Valid @RequestBody DataRequest dataRequest) {
        Integer feeId = dataRequest.getInteger("feeId");
        Map form = dataRequest.getMap("form"); //参数获取Map对象
        String num = CommonMethod.getString(form,"num");  //Map 获取属性的值
        Fee s= null;

        Optional<Fee> op;
        if(feeId != null) {
            op= feeRepository.findById(feeId);  //查询对应数据库中主键为id的值的实体对象
            if(op.isPresent()) {
                s = op.get();
            }
        }

        if(s == null) {
            s = new Fee();   // 创建实体对象
            feeId=getNewFeeId();
            s.setFeeId(feeId);

            feeRepository.saveAndFlush(s);  //插入新的Student记录
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

        s.setMoney(CommonMethod.getDouble(form,"money"));
        s.setStudentId(st.getStudentId());
        s.setDay(CommonMethod.getString(form,"day"));
        s.setRemark(CommonMethod.getString(form,"remark"));
        s.setName(st.getPerson().getName());

        feeRepository.save(s);  //修改保存学生信息
        return CommonMethod.getReturnData(s.getFeeId());  // 将studentId返回前端
    }

}
