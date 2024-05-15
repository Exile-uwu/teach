package org.fatmansoft.teach.controllers;

import org.fatmansoft.teach.models.Achievement;
import org.fatmansoft.teach.models.Person;
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
@RequestMapping("/api/achievement")
public class AchievementController {
    @Autowired
    private PersonRepository personRepository;  //人员数据操作自动注入
    @Autowired
    private StudentRepository studentRepository;  //人员数据操作自动注入
    @Autowired
    private AchievementRepository achievementRepository;  //学生数据操作自动注入
    @Autowired
    private UserRepository userRepository;  //学生数据操作自动注入
    @Autowired
    private PasswordEncoder encoder;  //密码服务自动注入
    @Autowired
    private ScoreRepository scoreRepository;  //成绩数据操作自动注入
    @Autowired
    private FeeRepository feeRepository;  //消费数据操作自动注入
    @Autowired
    private BaseService baseService;   //基本数据处理数据操作自动注入
    public synchronized Integer getNewAchievementId(){
        Integer  id = achievementRepository.getMaxId();  // 查询最大的id
        if(id == null)
            id = 1;
        else
            id = id+1;
        return id;
    };
    public Map getMapFromAchievement(Achievement s) {
        Map m = new HashMap();
        Student st;
        st=s.getStudent();


        if(s == null)
            return m;

        m.put("achievementId", s.getAchievementId());
        m.put("studentId",st.getStudentId());

        m.put("content",s.getContent());
        String level = s.getLevel();
        m.put("level",level);
        m.put("levelName", ComDataUtil.getInstance().getDictionaryLabelByValue("LEVEL", level)); //性别类型的值转换成数据类型名
        m.put("date",s.getDate());
        m.put("awardee",st.getPerson().getName());
        m.put("awardeeNum",s.getAwardeeNum());


        return m;
    }

    /**
     *  getStudentMapList 根据输入参数查询得到学生数据的 Map List集合 参数为空 查出说有学生， 参数不为空，查出人员编号或人员名称 包含输入字符串的学生
     * @param numName 输入参数
     * @return  Map List 集合
     */
    public List getAchievementMapList(String numName) {
        List dataList = new ArrayList();
        List<Achievement> sList = achievementRepository.findAchievementListByNumName(numName);  //数据库查询操作
        if(sList == null || sList.size() == 0)
            return dataList;
        for(int i = 0; i < sList.size();i++) {
            dataList.add(getMapFromAchievement(sList.get(i)));
        }
        return dataList;
    }

    public List getSingleAchievementMapList(Integer studentId) {
        List dataList = new ArrayList();
        List<Achievement> sList = achievementRepository.findAchievementListByStudentId(studentId);  //数据库查询操作
        if(sList == null || sList.size() == 0)
            return dataList;
        for(int i = 0; i < sList.size();i++) {
            dataList.add(getMapFromAchievement(sList.get(i)));
        }
        return dataList;
    }

    public List getSingleAchievementMapList2(Integer studentId,String numName) {
        List dataList = new ArrayList();
        List<Achievement> sList = achievementRepository.findAchievementListByStudentIdAndNumName(studentId,numName);  //数据库查询操作
        if(sList == null || sList.size() == 0)
            return dataList;
        for(int i = 0; i < sList.size();i++) {
            dataList.add(getMapFromAchievement(sList.get(i)));
        }
        return dataList;
    }



    @PostMapping("/getAchievementItemOptionList")
    public OptionItemList getAchievementItemOptionList(@Valid @RequestBody DataRequest dataRequest) {
        List<Achievement> sList = achievementRepository.findAchievementListByNumName("");  //数据库查询操作
        OptionItem item;
        List<OptionItem> itemList = new ArrayList();
        for (Achievement s : sList) {
            itemList.add(new OptionItem(s.getAchievementId(), s.getContent(), s.getContent()+"-"+s.getAwardee()));
        }
        return new OptionItemList(0, itemList);
    }

    @PostMapping("/getAchievementList")
    @PreAuthorize("hasRole('ADMIN')")
    public DataResponse getAchievementList(@Valid @RequestBody DataRequest dataRequest) {
        String numName= dataRequest.getString("numName");
        List dataList = getAchievementMapList(numName);
        return CommonMethod.getReturnData(dataList);  //按照测试框架规范会送Map的list
    }

    @PostMapping("/getSingleAchievementList")
    @PreAuthorize("hasRole('STUDENT')")
    public DataResponse getSingleAchievementList(@Valid @RequestBody DataRequest dataRequest) {
        Integer userId = CommonMethod.getUserId();
        Optional<User> uOp = userRepository.findByUserId(userId);  // 查询获得 user对象
        if(!uOp.isPresent())
            return CommonMethod.getReturnMessageError("用户不存在！");
        User u = uOp.get();
        Optional<Student> sOp= studentRepository.findByPersonPersonId(u.getUserId());  // 查询获得 Student对象
        if(!sOp.isPresent())
            return CommonMethod.getReturnMessageError("学生不存在！");
        Student st= sOp.get();

        List dataList = getSingleAchievementMapList(st.getStudentId());
        return CommonMethod.getReturnData(dataList);  //按照测试框架规范会送Map的list
    }

    @PostMapping("/getSingleAchievementList2")
    @PreAuthorize("hasRole('STUDENT')")
    public DataResponse getSingleAchievementList2(@Valid @RequestBody DataRequest dataRequest) {
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

        List dataList = getSingleAchievementMapList2(st.getStudentId(),numName);
        return CommonMethod.getReturnData(dataList);  //按照测试框架规范会送Map的list
    }




    /**
     * studentDelete 删除学生信息Web服务 Student页面的列表里点击删除按钮则可以删除已经存在的学生信息， 前端会将该记录的id 回传到后端，方法从参数获取id，查出相关记录，调用delete方法删除
     * 这里注意删除顺序，应为user关联person,Student关联Person 所以要先删除Student,User，再删除Person
     * @param dataRequest  前端studentId 药删除的学生的主键 student_id
     * @return  正常操作
     */

    @PostMapping("/achievementDelete")
    @PreAuthorize(" hasRole('ADMIN')")
    public DataResponse achievementDelete(@Valid @RequestBody DataRequest dataRequest) {
        Integer achievementId = dataRequest.getInteger("achievementId");  //获取student_id值
        Achievement s= null;
        Optional<Achievement> op;
        if(achievementId != null) {
            op= achievementRepository.findById(achievementId);   //查询获得实体对象
            if(op.isPresent()) {
                s = op.get();
            }
        }
        if(s != null) {
            achievementRepository.delete(s);    //首先数据库永久删除学生信息
            // 然后数据库永久删除学生信息
        }
        return CommonMethod.getReturnMessageOK();  //通知前端操作正常
    }

    @PostMapping("/achievementSingleDelete")
    @PreAuthorize(" hasRole('STUDENT')")
    public DataResponse achievementSingleDelete(@Valid @RequestBody DataRequest dataRequest) {
        Integer achievementId = dataRequest.getInteger("achievementId");  //获取student_id值
        Achievement s= null;
        Optional<Achievement> op;
        if(achievementId != null) {
            op= achievementRepository.findById(achievementId);   //查询获得实体对象
            if(op.isPresent()) {
                s = op.get();
            }
        }
        if(s != null) {
            achievementRepository.delete(s);    //首先数据库永久删除学生信息
            // 然后数据库永久删除学生信息
        }
        return CommonMethod.getReturnMessageOK();  //通知前端操作正常
    }

    /**
     * getStudentInfo 前端点击学生列表时前端获取学生详细信息请求服务
     * @param dataRequest 从前端获取 studentId 查询学生信息的主键 student_id
     * @return  根据studentId从数据库中查出数据，存在Map对象里，并返回前端
     */

    @PostMapping("/getAchievementInfo")
    @PreAuthorize("hasRole('ADMIN')")
    public DataResponse getAchievementInfo(@Valid @RequestBody DataRequest dataRequest) {
        Integer achievementId = dataRequest.getInteger("achievementId");
        Achievement s= null;
        Optional<Achievement> op;
        if(achievementId != null) {
            op= achievementRepository.findById(achievementId); //根据学生主键从数据库查询学生的信息
            if(op.isPresent()) {
                s = op.get();
            }
        }
        return CommonMethod.getReturnData(getMapFromAchievement(s)); //这里回传包含学生信息的Map对象
    }

    @PostMapping("/getSingleAchievementInfo")
    @PreAuthorize("hasRole('STUDENT')")
    public DataResponse getSingleAchievementInfo(@Valid @RequestBody DataRequest dataRequest) {
        Integer achievementId = dataRequest.getInteger("achievementId");
        Achievement s= null;
        Optional<Achievement> op;
        if(achievementId != null) {
            op= achievementRepository.findById(achievementId); //根据学生主键从数据库查询学生的信息
            if(op.isPresent()) {
                s = op.get();
            }
        }
        return CommonMethod.getReturnData(getMapFromAchievement(s)); //这里回传包含学生信息的Map对象
    }

    /**
     * studentEditSave 前端学生信息提交服务
     * 前端把所有数据打包成一个Json对象作为参数传回后端，后端直接可以获得对应的Map对象form, 再从form里取出所有属性，复制到
     * 实体对象里，保存到数据库里即可，如果是添加一条记录， id 为空，这是先 new Person, User,Student 计算新的id， 复制相关属性，保存，如果是编辑原来的信息，
     * studentId不为空。则查询出实体对象，复制相关属性，保存后修改数据库信息，永久修改
     * @return  新建修改学生的主键 student_id 返回前端
     */
    @PostMapping("/achievementEditSave")
    @PreAuthorize(" hasRole('ADMIN')")
    public DataResponse achievementEditSave(@Valid @RequestBody DataRequest dataRequest) {
        Integer achievementId = dataRequest.getInteger("achievementId");
        Map form = dataRequest.getMap("form"); //参数获取Map对象
        String num = CommonMethod.getString(form,"num");  //Map 获取属性的值
        Achievement s= null;

        Optional<Achievement> op;
        if(achievementId != null) {
            op= achievementRepository.findById(achievementId);  //查询对应数据库中主键为id的值的实体对象
            if(op.isPresent()) {
                s = op.get();
            }
        }

        if(s == null) {
            s = new Achievement();   // 创建实体对象
            achievementId=getNewAchievementId();
            s.setAchievementId(achievementId);

            achievementRepository.saveAndFlush(s);  //插入新的Student记录
        }

        Optional<Student> st= studentRepository.findByPersonNum(CommonMethod.getString(form,"awardeeNum"));// 查询获得 Student对象
        if(!st.isPresent()){
            return CommonMethod.getReturnData(-1);
        }

        s.setContent(CommonMethod.getString(form,"content"));

        s.setLevel(CommonMethod.getString(form,"level"));
        s.setDate(CommonMethod.getString(form,"date"));
        s.setAwardee(st.get().getPerson().getName());
        s.setStudent(st.get());
        s.setAwardeeNum(CommonMethod.getString(form,"awardeeNum"));

        achievementRepository.save(s);  //修改保存学生信息
        return CommonMethod.getReturnData(s.getAchievementId());  // 将studentId返回前端
    }

    @PostMapping("/achievementSingleEditSave")
    @PreAuthorize(" hasRole('STUDENT')")
    public DataResponse achievementSingleEditSave(@Valid @RequestBody DataRequest dataRequest) {
        Integer achievementId = dataRequest.getInteger("achievementId");
        Map form = dataRequest.getMap("form"); //参数获取Map对象
        String num = CommonMethod.getString(form,"num");  //Map 获取属性的值
        Achievement s= null;

        Optional<Achievement> op;
        if(achievementId != null) {
            op= achievementRepository.findById(achievementId);  //查询对应数据库中主键为id的值的实体对象
            if(op.isPresent()) {
                s = op.get();
            }
        }

        if(s == null) {
            s = new Achievement();   // 创建实体对象
            achievementId=getNewAchievementId();
            s.setAchievementId(achievementId);

            achievementRepository.saveAndFlush(s);  //插入新的Student记录
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
        s.setLevel(CommonMethod.getString(form,"level"));
        s.setDate(CommonMethod.getString(form,"date"));
        s.setAwardee(st.getPerson().getName());
        s.setAwardeeNum(CommonMethod.getString(form,"awardeeNum"));

        achievementRepository.save(s);  //修改保存学生信息
        return CommonMethod.getReturnData(s.getAchievementId());  // 将studentId返回前端
    }

}
