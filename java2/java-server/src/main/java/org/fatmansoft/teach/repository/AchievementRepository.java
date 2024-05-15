package org.fatmansoft.teach.repository;

import org.fatmansoft.teach.models.Achievement;
import org.fatmansoft.teach.models.Innovation;
import org.fatmansoft.teach.models.Student;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface AchievementRepository extends JpaRepository<Achievement,Integer> {
    @Query(value = "select max(achievementId) from Achievement  ")
    Integer getMaxId();

    @Query(value = "from Achievement where ?1='' or content like %?1% or awardee like %?1% or awardeeNum like %?1%")
    List<Achievement> findAchievementListByNumName(String numName);

    @Query(value = "from Achievement s where s.student.id = ?1")
    List<Achievement> findAchievementListByStudentId(Integer studentId);

    @Query(value = "from Achievement s where s.student.id = ?1 and s.content like %?2%")
    List<Achievement> findAchievementListByStudentIdAndNumName(Integer studentId, String numName);
}
