package org.fatmansoft.teach.repository;

import org.fatmansoft.teach.models.Course;
import org.fatmansoft.teach.models.Innovation;
import org.fatmansoft.teach.models.Student;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
/**
 * Course 数据操作接口，主要实现Course数据的查询操作
 */

@Repository
public interface CourseRepository extends JpaRepository<Course,Integer> {
    @Query(value = "select max(courseId) from Course  ")
    Integer getMaxId();


    Optional<Course> findCourseByCourseId(Integer courseId);
    Optional<Course> findCourseByNum(String num);
    @Query(value = "from Course where ?1='' ")
    List<Course> findByCourseName(String name);


    List<Course> findCourseListByTeacherId(Integer teacherId);
    @Query(value = "from Course c where c.teacherId = ?1 and c.name like %?2%")
    List<Course> findCourseListByTeacherIdAndName(Integer teacherId,String numName);

    @Query(value = "from Course where ?1='' or num like %?1% or name like %?1%")
    List<Course> findCourseListByNumName(String numName);

    @Query(value = "from Course c where num=?1")
    List<Course> findCourseListByNum(String numName);


}
