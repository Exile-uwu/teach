package org.fatmansoft.teach.repository;

import org.fatmansoft.teach.models.Course;
import org.fatmansoft.teach.models.Fee;
import org.fatmansoft.teach.models.Innovation;
import org.fatmansoft.teach.models.Score;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Score 数据操作接口，主要实现Score数据的查询操作
 * List<Score> findByStudentStudentId(Integer studentId);  根据关联的Student的student_id查询获得List<Score>对象集合,  命名规范
 */

@Repository
public interface ScoreRepository extends JpaRepository<Score,Integer> {
    @Query(value = "select max(scoreId) from Score  ")
    Integer getMaxId();

    List<Score> findByStudentStudentId(Integer studentId);

    @Query(value = "from Score s where s.student.id = ?1")
    List<Score> findScoreListByStudentId(Integer studentId);

    @Query(value = "from Score s where s.student.id = ?1 and s.courseNum like %?2%")
    List<Score> findScoreListByStudentIdAndCourseNum(Integer studentId,String courseNum);

    @Query(value = "from Score s where s.student.id = ?1 and s.course.time =?2")
    List<Score> findScoreListByStudentIdAndTime(Integer studentId,String time);

    @Query(value= "from Score where teacherId=?1")
    List<Score> findScoreListByTeacherId(Integer teacherId);

    @Query(value = "from Score s where s.teacherId = ?1 and s.courseNum like %?2%")
    List<Score> findScoreListByTeacherIdAndCourseNum(Integer teacherId,String courseNum);


}
