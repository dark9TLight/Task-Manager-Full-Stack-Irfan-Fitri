package com.interview.taskmanager.controller;

import com.interview.taskmanager.dto.TaskRequest;
import com.interview.taskmanager.entity.Task;
import com.interview.taskmanager.entity.Task.TaskPriority;
import com.interview.taskmanager.entity.Task.TaskStatus;
import com.interview.taskmanager.entity.User;
import com.interview.taskmanager.repository.TaskRepository;
import com.interview.taskmanager.repository.UserRepository;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/tasks")
@CrossOrigin(origins = "http://localhost:3000")
public class TaskController {
    
    private static final Logger logger = LoggerFactory.getLogger(TaskController.class);
    
    @Autowired
    private TaskRepository taskRepository;
    
    @Autowired
    private UserRepository userRepository;
    
    @GetMapping
    public ResponseEntity<List<Task>> getAllTasks(Authentication auth,
                                                   @RequestParam(required = false) String status,
                                                   @RequestParam(required = false) String priority,
                                                   @RequestParam(required = false) String search,
                                                   @RequestParam(required = false) String sortBy) {
        String username = auth.getName();
        User user = userRepository.findByUsername(username).orElseThrow();
        
        logger.info("Fetching tasks for user: {} with filters - status: {}, priority: {}, search: {}, sortBy: {}", 
                    username, status, priority, search, sortBy);
        
        List<Task> tasks;
        
        if (search != null && !search.isEmpty()) {
            tasks = taskRepository.searchByTitle(user.getId(), search);
        } else if (status != null) {
            tasks = taskRepository.findByUserIdAndStatus(user.getId(), TaskStatus.valueOf(status));
        } else if (priority != null) {
            tasks = taskRepository.findByUserIdAndPriority(user.getId(), TaskPriority.valueOf(priority));
        } else if (sortBy != null) {
            tasks = taskRepository.findByUserIdOrderBy(user.getId(), sortBy);
        } else {
            tasks = taskRepository.findByUserId(user.getId());
        }
        
        logger.info("Retrieved {} tasks for user: {}", tasks.size(), username);
        return ResponseEntity.ok(tasks);
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<?> getTaskById(@PathVariable Long id, Authentication auth) {
        String username = auth.getName();
        User user = userRepository.findByUsername(username).orElseThrow();
        
        logger.info("Fetching task {} for user: {}", id, username);
        
        Task task = taskRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Task not found"));
        
        if (!task.getUser().getId().equals(user.getId())) {
            logger.warn("Unauthorized access attempt to task {} by user: {}", id, username);
            return ResponseEntity.status(403).body("Access denied");
        }
        
        return ResponseEntity.ok(task);
    }
    
    @PostMapping
    public ResponseEntity<Task> createTask(@Valid @RequestBody TaskRequest request, Authentication auth) {
        String username = auth.getName();
        User user = userRepository.findByUsername(username).orElseThrow();
        
        logger.info("Creating new task for user: {} - Title: {}", username, request.getTitle());
        
        Task task = new Task();
        task.setTitle(request.getTitle());
        task.setDescription(request.getDescription());
        task.setStatus(request.getStatus() != null ? request.getStatus() : TaskStatus.TODO);
        task.setPriority(request.getPriority() != null ? request.getPriority() : TaskPriority.MEDIUM);
        task.setDueDate(request.getDueDate());
        task.setUser(user);
        
        Task savedTask = taskRepository.save(task);
        logger.info("Task created successfully with ID: {} for user: {}", savedTask.getId(), username);
        
        return ResponseEntity.ok(savedTask);
    }
    
    @PutMapping("/{id}")
    public ResponseEntity<?> updateTask(@PathVariable Long id, 
                                       @Valid @RequestBody TaskRequest request,
                                       Authentication auth) {
        String username = auth.getName();
        User user = userRepository.findByUsername(username).orElseThrow();
        
        logger.info("Updating task {} for user: {}", id, username);
        
        Task task = taskRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Task not found"));
        
        if (!task.getUser().getId().equals(user.getId())) {
            logger.warn("Unauthorized update attempt on task {} by user: {}", id, username);
            return ResponseEntity.status(403).body("Access denied");
        }
        
        task.setTitle(request.getTitle());
        task.setDescription(request.getDescription());
        task.setStatus(request.getStatus());
        task.setPriority(request.getPriority());
        task.setDueDate(request.getDueDate());
        
        Task updatedTask = taskRepository.save(task);
        logger.info("Task {} updated successfully for user: {}", id, username);
        
        return ResponseEntity.ok(updatedTask);
    }
    
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteTask(@PathVariable Long id, Authentication auth) {
        String username = auth.getName();
        User user = userRepository.findByUsername(username).orElseThrow();
        
        logger.info("Deleting task {} for user: {}", id, username);
        
        Task task = taskRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Task not found"));
        
        if (!task.getUser().getId().equals(user.getId())) {
            logger.warn("Unauthorized delete attempt on task {} by user: {}", id, username);
            return ResponseEntity.status(403).body("Access denied");
        }
        
        taskRepository.delete(task);
        logger.info("Task {} deleted successfully for user: {}", id, username);
        
        return ResponseEntity.ok("Task deleted successfully");
    }
}