package org.fatmansoft.teach.models;
import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
import java.util.Date;
@Entity
@Table(	name = "Relation",
        uniqueConstraints = {
        })
public class Relation {


    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer relationId;


    @Size(max = 50)
    private String name;

    @Size(max = 50)
    private String num;
    @Size(max = 50)
    private String dad;

    @Size(max = 50)
    private String mom;



    @Size(max = 50)
    private String dadPhone;

    @Size(max = 50)
    private String momPhone;


    @ManyToOne
    @JoinColumn(name = "studentId")
    private Student student;

    public Integer getRelationId() {
        return relationId;
    }

    public void setRelationId(Integer relationId) {
        this.relationId = relationId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getNum() {
        return num;
    }

    public void setNum(String num) {
        this.num = num;
    }
    public String getDad() {
        return dad;
    }

    public void setDad(String dad) {
        this.dad = dad;
    }

    public String getMom() {
        return mom;
    }

    public void setMom(String mom) {
        this.mom = mom;
    }

    public String getDadPhone() {
        return dadPhone;
    }

    public void setDadPhone(String dadPhone) {
        this.dadPhone = dadPhone;
    }

    public String getMomPhone() {
        return momPhone;
    }

    public void setMomPhone(String momPhone) {
        this.momPhone = momPhone;
    }

    public Student getStudent() {
        return student;
    }

    public void setStudent(Student student) {
        this.student = student;
    }


}
