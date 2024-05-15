package org.fatmansoft.teach.repository;

import org.fatmansoft.teach.models.Innovation;
import org.fatmansoft.teach.models.Student;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface InnovationRepository  extends JpaRepository<Innovation,Integer> {
    @Query(value = "select max(innovationId) from Innovation  ")
    Integer getMaxId();

    /*Optional<Innovation> findByInnovationNum(String num);
    List<Innovation> findByInnovationName(String name);*/


    @Query(value = "from Innovation where ?1='' or content like %?1% or awardee like %?1% ")
    List<Innovation> findInnovationListByNumName(String numName);

    @Query(value = "from Innovation s where s.student.id = ?1")
    List<Innovation> findInnovationListByStudentId(Integer studentId);

    @Query(value = "from Innovation s where s.student.id = ?1 and s.content like %?2%")
    List<Innovation> findInnovationListByStudentIdAndNumName(Integer studentId,String numName);
}
