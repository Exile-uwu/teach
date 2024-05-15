package org.fatmansoft.teach.repository;

import org.fatmansoft.teach.models.Assess;
import org.fatmansoft.teach.models.Innovation;
import org.fatmansoft.teach.models.Relation;
import org.fatmansoft.teach.models.Student;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface AssessRepository  extends JpaRepository<Assess,Integer> {
    @Query(value = "select max(assessId) from Assess  ")
    Integer getMaxId();
    @Query(value = "from Assess where ?1='' or name like %?1%")
    List<Assess> findAssessListByNumName(String numName);

    @Query(value = "from Assess s where s.studentId = ?1 and s.acceptorId <=500")
    List<Assess> findAssessListByStudentId(Integer studentId);
    @Query(value = "from Assess s where s.studentId = ?1 and s.acceptorId <=500 and s.content like %?2%")
    List<Assess> findAssessListByStudentIdAndNumName(Integer studentId,String numName);

    @Query(value = "from Assess s where s.studentId = ?1 and s.acceptorId >500")
    List<Assess> findAssessTeacherListByStudentId(Integer studentId);

    @Query(value = "from Assess where acceptorId = ?1")
    List<Assess> findAssessListByAcceptor(String acceptorId);
}
