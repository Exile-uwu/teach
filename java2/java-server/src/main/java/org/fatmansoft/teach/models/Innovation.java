package org.fatmansoft.teach.models;
import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
import java.util.Date;
@Entity
@Table(	name = "innovation",
        uniqueConstraints = {
        })
public class Innovation {


    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer innovationId;

    @ManyToOne
    @JoinColumn(name = "studentId")
    private Student student;

    @Size(max = 20)
    private String content;

    @Size(max = 50)
    private String type;

    @Size(max = 20)
    private String date;

    private String awardee;

    public Integer getInnovationId() {
        return innovationId;
    }

    public void setInnovationId(Integer innovationId) {
        this.innovationId = innovationId;
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

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
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


}
