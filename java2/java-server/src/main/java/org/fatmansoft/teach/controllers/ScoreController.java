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
@RequestMapping("/api/score")
public class ScoreController {
    @Autowired
    private PersonRepository personRepository;  //人员数据操作自动注入
    @Autowired
    private CourseRepository courseRepository;  //人员数据操作自动注入
    @Autowired
    private StudentRepository studentRepository;  //学生数据操作自动注入
    @Autowired
    private TeacherRepository teacherRepository;  //学生数据操作自动注入
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

    public synchronized Integer getNewScoreId(){
        Integer  id = scoreRepository.getMaxId();  // 查询最大的id
        if(id == null)
            id = 1;
        else
            id = id+1;
        return id;
    };


    public Map getMapFromScore(Score s) {
        Map m = new HashMap();
        Student st;
        st=s.getStudent();
        Course c;
        c=s.getCourse();
        Person p;
        p=st.getPerson();


        if(s == null)
            return m;

        m.put("scoreId", s.getScoreId());
        m.put("studentId",st.getStudentId());
        m.put("courseId",c.getCourseId());
        m.put("teacherId",c.getTeacherId());

        m.put("num",c.getNum());
        m.put("name",c.getName());
        m.put("credit",c.getCredit());
        String teacherName = c.getTeacherName();
        m.put("teacherName",teacherName);
        m.put("teacherNameName", ComDataUtil.getInstance().getDictionaryLabelByValue("LS", teacherName));

        m.put("studentName",p.getName());
        m.put("studentNum",p.getNum());

        m.put("mark",s.getMark());
        if(s.getMark()==null)
        {
            m.put("isComplete","正在修读");
        }
        else
        {
            m.put("isComplete","已修读");
        }
        m.put("homework",c.getHomework());
        String time = c.getTime();
        m.put("time",time);
        m.put("timeName", ComDataUtil.getInstance().getDictionaryLabelByValue("TIME", time));
        m.put("address",c.getAddress());



        return m;
    }

    /**
     *  getStudentMapList 根据输入参数查询得到学生数据的 Map List集合 参数为空 查出说有学生， 参数不为空，查出人员编号或人员名称 包含输入字符串的学生

     * @return  Map List 集合
     */


    public List getScoreMapList(Integer studentId) {
        List dataList = new ArrayList();
        List<Score> sList = scoreRepository.findScoreListByStudentId(studentId);  //数据库查询操作
        if(sList == null || sList.size() == 0)
            return dataList;
        for(int i = 0; i < sList.size();i++) {
            dataList.add(getMapFromScore(sList.get(i)));
        }
        return dataList;
    }
    public List getScoreMapList2(Integer studentId,String courseNum) {
        List dataList = new ArrayList();
        List<Score> sList = scoreRepository.findScoreListByStudentIdAndCourseNum(studentId,courseNum);  //数据库查询操作
        if(sList == null || sList.size() == 0)
            return dataList;
        for(int i = 0; i < sList.size();i++) {
            dataList.add(getMapFromScore(sList.get(i)));
        }
        return dataList;
    }

    public List getTeacherScoreMapList(Integer teacherId) {
        List dataList = new ArrayList();
        List<Score> sList = scoreRepository.findScoreListByTeacherId(teacherId);  //数据库查询操作
        if(sList == null || sList.size() == 0)
            return dataList;
        for(int i = 0; i < sList.size();i++) {
            dataList.add(getMapFromScore(sList.get(i)));
        }
        return dataList;
    }
    public List getTeacherScoreMapList2(Integer teacherId,String courseNum) {
        List dataList = new ArrayList();
        List<Score> sList = scoreRepository.findScoreListByTeacherIdAndCourseNum(teacherId,courseNum);  //数据库查询操作
        if(sList == null || sList.size() == 0)
            return dataList;
        for(int i = 0; i < sList.size();i++) {
            dataList.add(getMapFromScore(sList.get(i)));
        }
        return dataList;
    }




    /**
     *  getStudentList 学生管理 点击查询按钮请求
     *  前台请求参数 numName 学号或名称的 查询串
     * 返回前端 存储学生信息的 MapList 框架会自动将Map转换程用于前后台传输数据的Json对象，Map的嵌套结构和Json的嵌套结构类似
     * @return
     */

    /*@PostMapping("/getScoreItemOptionList")
    public OptionItemList getScoreItemOptionList(@Valid @RequestBody DataRequest dataRequest) {
        List<Fee> sList = scoreRepository.findFeeListByNumName("");  //数据库查询操作
        OptionItem item;
        List<OptionItem> itemList = new ArrayList();
        for (Fee s : sList) {
            itemList.add(new OptionItem(s.getFeeId(), s.getDay(), s.getMoney()+"-"+s.getDay()));//此处可能有问题-hzd
        }
        return new OptionItemList(0, itemList);
    }*/

    /*@PostMapping("/getScoreList")
    @PreAuthorize("hasRole('ADMIN')")
    public DataResponse getScoreList(@Valid @RequestBody DataRequest dataRequest) {
        String numName= dataRequest.getString("numName");
        List dataList = getScoreMapList(numName);
        return CommonMethod.getReturnData(dataList);  //按照测试框架规范会送Map的list
    }*/

    @PostMapping("/getStudentScoreList")
    @PreAuthorize("hasRole('STUDENT')")
    public DataResponse getStudentScoreList(@Valid @RequestBody DataRequest dataRequest) {
        Integer userId = CommonMethod.getUserId();
        Optional<User> uOp = userRepository.findByUserId(userId);  // 查询获得 user对象
        if(!uOp.isPresent())
            return CommonMethod.getReturnMessageError("用户不存在！");
        User u = uOp.get();
        Optional<Student> sOp= studentRepository.findByPersonPersonId(u.getUserId());  // 查询获得 Student对象
        if(!sOp.isPresent())
            return CommonMethod.getReturnMessageError("学生不存在！");
        Student st= sOp.get();

        List dataList = getScoreMapList(st.getStudentId());
        return CommonMethod.getReturnData(dataList);  //按照测试框架规范会送Map的list
    }

    @PostMapping("/getStudentScoreList2")
    @PreAuthorize("hasRole('STUDENT')")
    public DataResponse getStudentScoreList2(@Valid @RequestBody DataRequest dataRequest) {
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

        List dataList = getScoreMapList2(st.getStudentId(),numName);
        return CommonMethod.getReturnData(dataList);  //按照测试框架规范会送Map的list
    }

    @PostMapping("/getTeacherScoreList")
    @PreAuthorize("hasRole('TEACHER')")
    public DataResponse getTeacherScoreList(@Valid @RequestBody DataRequest dataRequest) {
        Integer userId = CommonMethod.getUserId();
        Optional<User> uOp = userRepository.findByUserId(userId);  // 查询获得 user对象
        User u = uOp.get();
        Optional<Teacher> sOp= teacherRepository.findByPersonPersonId(u.getUserId());  // 查询获得 Student对象
        Teacher t= sOp.get();

        List dataList = getTeacherScoreMapList(t.getTeacherId());
        return CommonMethod.getReturnData(dataList);  //按照测试框架规范会送Map的list
    }

    @PostMapping("/getTeacherScoreList2")
    @PreAuthorize("hasRole('TEACHER')")
    public DataResponse getTeacherScoreList2(@Valid @RequestBody DataRequest dataRequest) {
        String numName= dataRequest.getString("numName");
        Integer userId = CommonMethod.getUserId();
        Optional<User> uOp = userRepository.findByUserId(userId);  // 查询获得 user对象
        User u = uOp.get();
        Optional<Teacher> sOp= teacherRepository.findByPersonPersonId(u.getUserId());  // 查询获得 Student对象
        Teacher t= sOp.get();

        List dataList = getTeacherScoreMapList2(t.getTeacherId(),numName);
        return CommonMethod.getReturnData(dataList);  //按照测试框架规范会送Map的list
    }




    /**
     * studentDelete 删除学生信息Web服务 Student页面的列表里点击删除按钮则可以删除已经存在的学生信息， 前端会将该记录的id 回传到后端，方法从参数获取id，查出相关记录，调用delete方法删除
     * 这里注意删除顺序，应为user关联person,Student关联Person 所以要先删除Student,User，再删除Person
     * @param dataRequest  前端studentId 药删除的学生的主键 student_id
     * @return  正常操作
     */



    /**
     * getStudentInfo 前端点击学生列表时前端获取学生详细信息请求服务
     * @param dataRequest 从前端获取 studentId 查询学生信息的主键 student_id
     * @return  根据studentId从数据库中查出数据，存在Map对象里，并返回前端
     */

    /*@PostMapping("/getFeeInfo")
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
    }*/

    @PostMapping("/getSingleScoreInfo")
    @PreAuthorize("hasRole('STUDENT')")
    public DataResponse getSingleScoreInfo(@Valid @RequestBody DataRequest dataRequest) {
        Integer scoreId = dataRequest.getInteger("scoreId");
        Score s= null;
        Optional<Score> op;
        if(scoreId != null) {
            op= scoreRepository.findById(scoreId); //根据学生主键从数据库查询学生的信息
            if(op.isPresent()) {
                s = op.get();
            }
        }
        return CommonMethod.getReturnData(getMapFromScore(s)); //这里回传包含学生信息的Map对象
    }
    @PostMapping("/scoreDelete")
    @PreAuthorize(" hasRole('STUDENT')")
    public DataResponse scoreDelete(@Valid @RequestBody DataRequest dataRequest) {
        Map form = dataRequest.getMap("form"); //参数获取Map对象

        Optional<Course>C=courseRepository.findCourseByNum(CommonMethod.getString(form,"num"));


        Integer scoreId = dataRequest.getInteger("scoreId");  //获取student_id值
        Score s= null;
        Optional<Score> op;
        if(scoreId != null) {
            op= scoreRepository.findById(scoreId);   //查询获得实体对象
            if(op.isPresent()) {
                s = op.get();
            }
        }
        if(s.getMark()!=null)
        {
            return CommonMethod.getReturnMessageError("无法退选已修读完成的课程");
        }
        if(s != null) {
            scoreRepository.delete(s);
            Integer c=0;
            c=Integer.parseInt(C.get().getCapacity());
            c++;
            C.get().setCapacity(c.toString());//首先数据库永久删除学生信息
            courseRepository.save(C.get());

            // 然后数据库永久删除学生信息
        }

        return CommonMethod.getReturnMessageOK();  //通知前端操作正常
    }

    @PostMapping("/GPA")
    @PreAuthorize(" hasRole('STUDENT')")
    public DataResponse GPA(@Valid @RequestBody DataRequest dataRequest) {
        Integer studentId = dataRequest.getInteger("studentId");
        double sum=0;
        Integer denominator=0;
        List<Score> sList = scoreRepository.findScoreListByStudentId(studentId);
        for(int i=0;i<sList.size();i++)
        {
            Course c = sList.get(i).getCourse();
            if(sList.get(i).getMark()!=null&&sList.get(i).getMark()>=60)
            {
                sum+=(sList.get(i).getMark()-50)/10*Integer.parseInt(c.getCredit());
                denominator+=Integer.parseInt(c.getCredit());
            }
        }
        double GPA=0;
        if(denominator!=0)
        {
            GPA=sum/denominator;

        }
        else
        {
            return CommonMethod.getReturnMessageError("该学生无可计算成绩！");
        }
        Student st;
        st=sList.get(0).getStudent();
        st.setGPA(String.format("%.2f",GPA));
        studentRepository.save(st);
        return CommonMethod.getReturnMessageOK();
        //通知前端操作正常
    }

    /**
     * studentEditSave 前端学生信息提交服务
     * 前端把所有数据打包成一个Json对象作为参数传回后端，后端直接可以获得对应的Map对象form, 再从form里取出所有属性，复制到
     * 实体对象里，保存到数据库里即可，如果是添加一条记录， id 为空，这是先 new Person, User,Student 计算新的id， 复制相关属性，保存，如果是编辑原来的信息，
     * studentId不为空。则查询出实体对象，复制相关属性，保存后修改数据库信息，永久修改
     * @return  新建修改学生的主键 student_id 返回前端
     */
    @PostMapping("/scoreEditSave")
    @PreAuthorize("hasRole('STUDENT')")
    public DataResponse scoreEditSave(@Valid @RequestBody DataRequest dataRequest) {
        Integer scoreId = dataRequest.getInteger("scoreId");
        Integer userId = CommonMethod.getUserId();
        Optional<User> uOp = userRepository.findByUserId(userId);  // 查询获得 user对象
        if(!uOp.isPresent())
            return CommonMethod.getReturnMessageError("用户不存在！");
        User u = uOp.get();
        Optional<Student> sOp= studentRepository.findByPersonPersonId(u.getUserId());  // 查询获得 Student对象
        if(!sOp.isPresent())
            return CommonMethod.getReturnMessageError("学生不存在！");
        Student st= sOp.get();
        Map form = dataRequest.getMap("form"); //参数获取Map对象
        Score sc=null;
        Score s= null;
        Optional<Course>C=courseRepository.findCourseByNum(CommonMethod.getString(form,"num"));
        List<Score>op=scoreRepository.findScoreListByStudentIdAndCourseNum(st.getStudentId(),CommonMethod.getString(form,"num"));
        List<Score>op2=scoreRepository.findScoreListByStudentIdAndTime(st.getStudentId(),CommonMethod.getString(form,"time"));


        if(op.size()!=0){
            return CommonMethod.getReturnData(-1);
        }
        if(op2.size()!=0){
            return CommonMethod.getReturnData(-2);
        }

        if(s == null) {
            s = new Score();   // 创建实体对象
            scoreId=getNewScoreId();
            s.setScoreId(scoreId);

            scoreRepository.saveAndFlush(s);  //插入新的Student记录
        }
        s.setStudent(st);
        s.setCourse(C.get());
        C.get().setCapacity(CommonMethod.getString(form,"capacity"));
        s.setTeacherId(C.get().getTeacherId());
        s.setCourseNum(CommonMethod.getString(form,"num"));
        s.setMark(CommonMethod.getInteger(form,"mark"));


        scoreRepository.save(s);  //修改保存学生信息
        return CommonMethod.getReturnData(s.getScoreId());  // 将studentId返回前端
    }

    @PostMapping("/scoreTeacherEditSave")
    @PreAuthorize("hasRole('TEACHER')")
    public DataResponse scoreTeacherEditSave(@Valid @RequestBody DataRequest dataRequest) {
        Integer scoreId = dataRequest.getInteger("scoreId");
        Map form = dataRequest.getMap("form"); //参数获取Map对象

        Score s= null;
        Optional<Score> op;
        if(scoreId != null) {
            op= scoreRepository.findById(scoreId);  //查询对应数据库中主键为id的值的实体对象
            if(op.isPresent()) {
                s = op.get();
            }
        }
        if(s == null) {
            s = new Score();   // 创建实体对象
            scoreId=getNewScoreId();
            s.setScoreId(scoreId);

            scoreRepository.saveAndFlush(s);  //插入新的Student记录
        }

        s.setMark(CommonMethod.getInteger(form,"mark"));



        scoreRepository.save(s);  //修改保存学生信息
        return CommonMethod.getReturnData(s.getScoreId());  // 将studentId返回前端
    }




}
