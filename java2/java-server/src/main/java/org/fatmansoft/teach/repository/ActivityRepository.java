package org.fatmansoft.teach.repository;

import org.fatmansoft.teach.models.Activity;
import org.fatmansoft.teach.models.Activity;
import org.fatmansoft.teach.models.Innovation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface ActivityRepository extends JpaRepository<Activity,Integer> {
    @Query(value = "select max(activityId) from Activity  ")
    Integer getMaxId();

    /*Optional<Innovation> findByInnovationNum(String num);
    List<Innovation> findByInnovationName(String name);*/


    @Query(value = "from Activity where ?1='' ")
    List<Activity> findActivityListByNumName(String numName);

    @Query(value = "from Activity s where s.student.id = ?1")
    List<Activity> findActivityListByStudentId(Integer studentId);

    @Query(value = "from Activity s where s.student.id = ?1 and s.name like %?2%")
    List<Activity> findActivityListByStudentIdAndName(Integer studentId,String numName);

}
