package org.fatmansoft.teach.models;
import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
import java.util.Date;
@Entity
@Table(	name = "achievement",
        uniqueConstraints = {
        })
public class Achievement {


    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer achievementId;

    @ManyToOne
    @JoinColumn(name = "studentId")
    private Student student;

    @Size(max = 20)
    private String content;

    @Size(max = 50)
    private String level;

    @Size(max = 20)
    private String date;
    @Size(max = 50)
    private String awardee;

    @Size(max = 50)
    private String awardeeNum;

    public Integer getAchievementId() {
        return achievementId;
    }

    public void setAchievementId(Integer achievementId) {
        this.achievementId = achievementId;
    }


    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public Student getStudent() {
        return student;
    }

    public void setStudent(Student student) {
        this.student = student;
    }

    public String getLevel() {
        return level;
    }

    public void setLevel(String level) {
        this.level = level;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getAwardee() {
        return awardee;
    }

    public void setAwardee(String awardee) {
        this.awardee = awardee;
    }

    public String getAwardeeNum() {
        return awardeeNum;
    }

    public void setAwardeeNum(String awardeeNum) {
        this.awardeeNum = awardeeNum;
    }


}