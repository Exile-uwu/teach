package org.fatmansoft.teach.repository;

import org.fatmansoft.teach.models.*;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface RelationRepository extends JpaRepository<Relation, Integer> {

    @Query(value = "select max(relationId) from Relation  ")
    Integer getMaxId();
    @Query(value = "from Relation where ?1='' or name like %?1% or num like %?1%")
    List<Relation> findRelationListByNumName(String numName);

    @Query(value = "from Relation s where s.student.id = ?1")
    List<Relation> findRelationListByStudentId(Integer studentId);



}
