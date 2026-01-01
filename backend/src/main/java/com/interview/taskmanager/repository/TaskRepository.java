// TaskRepository.java
package com.interview.taskmanager.repository;

import com.interview.taskmanager.entity.Task;
import com.interview.taskmanager.entity.Task.TaskStatus;
import com.interview.taskmanager.entity.Task.TaskPriority;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface TaskRepository extends JpaRepository<Task, Long> {
    
    List<Task> findByUserId(Long userId);
    
    List<Task> findByUserIdAndStatus(Long userId, TaskStatus status);
    
    List<Task> findByUserIdAndPriority(Long userId, TaskPriority priority);
    
    @Query("SELECT t FROM Task t WHERE t.user.id = :userId " +
           "AND LOWER(t.title) LIKE LOWER(CONCAT('%', :keyword, '%'))")
    List<Task> searchByTitle(@Param("userId") Long userId, @Param("keyword") String keyword);
    
    @Query("SELECT t FROM Task t WHERE t.user.id = :userId " +
           "ORDER BY " +
           "CASE WHEN :sortBy = 'priority' THEN t.priority END DESC, " +
           "CASE WHEN :sortBy = 'dueDate' THEN t.dueDate END, " +
           "CASE WHEN :sortBy = 'createdAt' THEN t.createdAt END DESC")
    List<Task> findByUserIdOrderBy(@Param("userId") Long userId, @Param("sortBy") String sortBy);
}
