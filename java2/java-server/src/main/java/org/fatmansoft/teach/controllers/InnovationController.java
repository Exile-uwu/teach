package org.fatmansoft.teach.controllers;

import org.fatmansoft.teach.models.Course;
import org.fatmansoft.teach.models.Innovation;
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
@RequestMapping("/api/innovation")
public class InnovationController {
    @Autowired
    private PersonRepository personRepository;  //人员数据操作自动注入
    @Autowired
    private StudentRepository studentRepository;  //人员数据操作自动注入
    @Autowired
    private InnovationRepository innovationRepository;  //学生数据操作自动注入
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
    public synchronized Integer getNewInnovationId(){
        Integer  id = innovationRepository.getMaxId();  // 查询最大的id
        if(id == null)
            id = 1;
        else
            id = id+1;
        return id;
    };
    public Map getMapFromInnovation(Innovation s) {
        Map m = new HashMap();
        Student st;
        st=s.getStudent();


        if(s == null)
            return m;

        m.put("innovationId", s.getInnovationId());
        m.put("studentId",st.getStudentId());

        m.put("content",s.getContent());
        String type = s.getType();
        m.put("type",type);
        m.put("typeName", ComDataUtil.getInstance().getDictionaryLabelByValue("HDLX", type)); //性别类型的值转换成数据类型名

        m.put("date",s.getDate());
        m.put("awardee",s.getAwardee());


        return m;
    }

    /**
     *  getStudentMapList 根据输入参数查询得到学生数据的 Map List集合 参数为空 查出说有学生， 参数不为空，查出人员编号或人员名称 包含输入字符串的学生
     * @param numName 输入参数
     * @return  Map List 集合
     */
    public List getInnovationMapList(String numName) {
        List dataList = new ArrayList();
        List<Innovation> sList = innovationRepository.findInnovationListByNumName(numName);  //数据库查询操作
        if(sList == null || sList.size() == 0)
            return dataList;
        for(int i = 0; i < sList.size();i++) {
            dataList.add(getMapFromInnovation(sList.get(i)));
        }
        return dataList;
    }

    public List getSingleInnovationMapList(Integer studentId) {
        List dataList = new ArrayList();
        List<Innovation> sList = innovationRepository.findInnovationListByStudentId(studentId);
        if(sList == null || sList.size() == 0)
            return dataList;
        for(int i = 0; i < sList.size();i++) {
            dataList.add(getMapFromInnovation(sList.get(i)));
        }
        return dataList;
    }

    public List getSingleInnovationMapList2(Integer studentId,String numName) {
        List dataList = new ArrayList();
        List<Innovation> sList = innovationRepository.findInnovationListByStudentIdAndNumName(studentId,numName);
        if(sList == null || sList.size() == 0)
            return dataList;
        for(int i = 0; i < sList.size();i++) {
            dataList.add(getMapFromInnovation(sList.get(i)));
        }
        return dataList;
    }




    /**
     *  getStudentList 学生管理 点击查询按钮请求
     *  前台请求参数 numName 学号或名称的 查询串
     * 返回前端 存储学生信息的 MapList 框架会自动将Map转换程用于前后台传输数据的Json对象，Map的嵌套结构和Json的嵌套结构类似
     * @return
     */

    @PostMapping("/getInnovationItemOptionList")
    public OptionItemList getInnovationItemOptionList(@Valid @RequestBody DataRequest dataRequest) {
        List<Innovation> sList = innovationRepository.findInnovationListByNumName("");  //数据库查询操作
        OptionItem item;
        List<OptionItem> itemList = new ArrayList();
        for (Innovation s : sList) {
            itemList.add(new OptionItem(s.getInnovationId(), s.getContent(), s.getContent()+"-"+s.getType()));//此处可能有问题-hzd
        }
        return new OptionItemList(0, itemList);
    }

    @PostMapping("/getInnovationList")
    @PreAuthorize("hasRole('ADMIN')")
    public DataResponse getInnovationList(@Valid @RequestBody DataRequest dataRequest) {
        String numName= dataRequest.getString("numName");
        List dataList = getInnovationMapList(numName);
        return CommonMethod.getReturnData(dataList);  //按照测试框架规范会送Map的list
    }

    @PostMapping("/getSingleInnovationList")
    @PreAuthorize("hasRole('STUDENT')")
    public DataResponse getSingleInnovationList(@Valid @RequestBody DataRequest dataRequest) {
        Integer userId = CommonMethod.getUserId();
        Optional<User> uOp = userRepository.findByUserId(userId);  // 查询获得 user对象
        if(!uOp.isPresent())
            return CommonMethod.getReturnMessageError("用户不存在！");
        User u = uOp.get();
        Optional<Student> sOp= studentRepository.findByPersonPersonId(u.getUserId());  // 查询获得 Student对象
        if(!sOp.isPresent())
            return CommonMethod.getReturnMessageError("学生不存在！");
        Student st= sOp.get();

        List dataList = getSingleInnovationMapList(st.getStudentId());
        return CommonMethod.getReturnData(dataList);  //按照测试框架规范会送Map的list
    }

    @PostMapping("/getSingleInnovationList2")
    @PreAuthorize("hasRole('STUDENT')")
    public DataResponse getSingleInnovationList2(@Valid @RequestBody DataRequest dataRequest) {
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

        List dataList = getSingleInnovationMapList2(st.getStudentId(),numName);
        return CommonMethod.getReturnData(dataList);  //按照测试框架规范会送Map的list
    }




    /**
     * studentDelete 删除学生信息Web服务 Student页面的列表里点击删除按钮则可以删除已经存在的学生信息， 前端会将该记录的id 回传到后端，方法从参数获取id，查出相关记录，调用delete方法删除
     * 这里注意删除顺序，应为user关联person,Student关联Person 所以要先删除Student,User，再删除Person
     * @param dataRequest  前端studentId 药删除的学生的主键 student_id
     * @return  正常操作
     */

    @PostMapping("/innovationDelete")
    @PreAuthorize(" hasRole('ADMIN')")
    public DataResponse innovationDelete(@Valid @RequestBody DataRequest dataRequest) {
        Integer innovationId = dataRequest.getInteger("innovationId");  //获取student_id值
        Innovation s= null;
        Optional<Innovation> op;
        if(innovationId != null) {
            op= innovationRepository.findById(innovationId);   //查询获得实体对象
            if(op.isPresent()) {
                s = op.get();
            }
        }
        if(s != null) {
            innovationRepository.delete(s);    //首先数据库永久删除学生信息
            // 然后数据库永久删除学生信息
        }
        return CommonMethod.getReturnMessageOK();  //通知前端操作正常
    }

    @PostMapping("/innovationSingleDelete")
    @PreAuthorize(" hasRole('STUDENT')")
    public DataResponse innovationSingleDelete(@Valid @RequestBody DataRequest dataRequest) {
        Integer innovationId = dataRequest.getInteger("innovationId");  //获取student_id值
        Innovation s= null;
        Optional<Innovation> op;
        if(innovationId != null) {
            op= innovationRepository.findById(innovationId);   //查询获得实体对象
            if(op.isPresent()) {
                s = op.get();
            }
        }
        if(s != null) {
            innovationRepository.delete(s);    //首先数据库永久删除学生信息
            // 然后数据库永久删除学生信息
        }
        return CommonMethod.getReturnMessageOK();  //通知前端操作正常
    }

    /**
     * getStudentInfo 前端点击学生列表时前端获取学生详细信息请求服务
     * @param dataRequest 从前端获取 studentId 查询学生信息的主键 student_id
     * @return  根据studentId从数据库中查出数据，存在Map对象里，并返回前端
     */

    @PostMapping("/getInnovationInfo")
    @PreAuthorize("hasRole('ADMIN')")
    public DataResponse getInnovationInfo(@Valid @RequestBody DataRequest dataRequest) {
        Integer innovationId = dataRequest.getInteger("innovationId");
        Innovation s= null;
        Optional<Innovation> op;
        if(innovationId != null) {
            op= innovationRepository.findById(innovationId); //根据学生主键从数据库查询学生的信息
            if(op.isPresent()) {
                s = op.get();
            }
        }
        return CommonMethod.getReturnData(getMapFromInnovation(s)); //这里回传包含学生信息的Map对象
    }

    @PostMapping("/getSingleInnovationInfo")
    @PreAuthorize("hasRole('STUDENT')")
    public DataResponse getSingleInnovationInfo(@Valid @RequestBody DataRequest dataRequest) {
        Integer innovationId = dataRequest.getInteger("innovationId");
        Innovation s= null;
        Optional<Innovation> op;
        if(innovationId != null) {
            op= innovationRepository.findById(innovationId); //根据学生主键从数据库查询学生的信息
            if(op.isPresent()) {
                s = op.get();
            }
        }
        return CommonMethod.getReturnData(getMapFromInnovation(s)); //这里回传包含学生信息的Map对象
    }

    /**
     * studentEditSave 前端学生信息提交服务
     * 前端把所有数据打包成一个Json对象作为参数传回后端，后端直接可以获得对应的Map对象form, 再从form里取出所有属性，复制到
     * 实体对象里，保存到数据库里即可，如果是添加一条记录， id 为空，这是先 new Person, User,Student 计算新的id， 复制相关属性，保存，如果是编辑原来的信息，
     * studentId不为空。则查询出实体对象，复制相关属性，保存后修改数据库信息，永久修改
     * @return  新建修改学生的主键 student_id 返回前端
     */
    @PostMapping("/innovationEditSave")
    @PreAuthorize(" hasRole('ADMIN')")
    public DataResponse innovationEditSave(@Valid @RequestBody DataRequest dataRequest) {
        Integer innovationId = dataRequest.getInteger("innovationId");
        Map form = dataRequest.getMap("form"); //参数获取Map对象
        String num = CommonMethod.getString(form,"num");  //Map 获取属性的值
        Innovation s= null;

        Optional<Innovation> op;
        if(innovationId != null) {
            op= innovationRepository.findById(innovationId);  //查询对应数据库中主键为id的值的实体对象
            if(op.isPresent()) {
                s = op.get();
            }
        }

        if(s == null) {
            s = new Innovation();   // 创建实体对象
            innovationId=getNewInnovationId();
            s.setInnovationId(innovationId);
            innovationRepository.saveAndFlush(s);  //插入新的Student记录
        }

        s.setContent(CommonMethod.getString(form,"content"));

        s.setType(CommonMethod.getString(form,"type"));
        s.setDate(CommonMethod.getString(form,"date"));
        s.setAwardee(CommonMethod.getString(form,"awardee"));

        innovationRepository.save(s);  //修改保存学生信息
        return CommonMethod.getReturnData(s.getInnovationId());  // 将studentId返回前端
    }

    @PostMapping("/innovationSingleEditSave")
    @PreAuthorize(" hasRole('STUDENT')")
    public DataResponse innovationSingleEditSave(@Valid @RequestBody DataRequest dataRequest) {
        Integer innovationId = dataRequest.getInteger("innovationId");
        Map form = dataRequest.getMap("form"); //参数获取Map对象
        String num = CommonMethod.getString(form,"num");  //Map 获取属性的值
        Innovation s= null;

        Optional<Innovation> op;
        if(innovationId != null) {
            op= innovationRepository.findById(innovationId);  //查询对应数据库中主键为id的值的实体对象
            if(op.isPresent()) {
                s = op.get();
            }
        }

        if(s == null) {
            s = new Innovation();   // 创建实体对象
            innovationId=getNewInnovationId();
            s.setInnovationId(innovationId);

            innovationRepository.saveAndFlush(s);  //插入新的Student记录
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

        s.setContent(CommonMethod.getString(form,"content"));
        s.setStudent(st);
        s.setType(CommonMethod.getString(form,"type"));
        s.setDate(CommonMethod.getString(form,"date"));
        s.setAwardee(st.getPerson().getName());

        innovationRepository.save(s);  //修改保存学生信息
        return CommonMethod.getReturnData(s.getInnovationId());  // 将studentId返回前端
    }

}
