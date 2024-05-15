package org.fatmansoft.teach.repository;

import org.fatmansoft.teach.models.*;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface LeaveRepository extends JpaRepository<Leave, Integer> {

    @Query(value = "select max(leaveId) from Leave  ")
    Integer getMaxId();
    @Query(value = "from Leave where ?1='' or reason like %?1% or name like %?1% or num like %?1%")
    List<Leave> findLeaveListByNumName(String numName);

    @Query(value = "from Leave s where s.student.id = ?1")
    List<Leave> findLeaveListByStudentId(Integer studentId);

    @Query(value = "from Leave s where s.student.id = ?1 and s.reason like %?2%")
    List<Leave> findLeaveListByStudentIdAndNumName(Integer studentId,String numName);

}