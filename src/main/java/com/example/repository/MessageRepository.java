package com.example.repository;

import com.example.entity.Message;
import java.util.List;

import javax.transaction.Transactional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface MessageRepository extends JpaRepository<Message, Integer> {

    List<Message> findByPostedBy(Integer postedBy);

    @Modifying
    @Transactional
    @Query("DELETE FROM Message m WHERE m.messageId = :id")
    int deleteByPostedBy(@Param("id") Integer id);
}
