package org.fatmansoft.teach.models;
import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
import java.util.Date;
@Entity
@Table(	name = "Leave",
        uniqueConstraints = {
        })
public class Leave {


    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer leaveId;


    @Size(max = 50)
    private String name;



    @Size(max = 50)
    private String num;


    @ManyToOne
    @JoinColumn(name = "studentId")
    private Student student;

    @Size(max = 1000)
    private String reason;
    private String start;
    private String end;

    public String getStart() {
        return start;
    }

    public void setStart(String start) {
        this.start = start;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    public String getEnd() {
        return end;
    }

    public void setEnd(String end) {
        this.end = end;
    }

    public Integer getLeaveId() {
        return leaveId;
    }

    public void setLeaveId(Integer leaveId) {
        this.leaveId = leaveId;
    }

    public String getNum() {
        return num;
    }

    public void setNum(String num) {
        this.num = num;
    }

    public Student getStudent() {
        return student;
    }

    public void setStudent(Student student) {
        this.student = student;
    }




}
