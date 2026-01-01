#!/bin/bash

# Base directory
BASE_DIR="src/main/java/com/interview/taskmanager"

# Create all necessary Java files with placeholder content
echo "Creating Java files..."

# Main Application
cat > $BASE_DIR/TaskManagerApplication.java << 'EOF'
package com.interview.taskmanager;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class TaskManagerApplication {
    public static void main(String[] args) {
        SpringApplication.run(TaskManagerApplication.class, args);
    }
}
EOF

echo "✅ TaskManagerApplication.java created"
echo "📝 Now you need to copy the content for each file from the artifacts I provided earlier"
echo ""
echo "Files you need to create:"
echo "1. Entity: User.java, Task.java"
echo "2. Repository: UserRepository.java, TaskRepository.java"
echo "3. Controller: AuthController.java, TaskController.java"
echo "4. Security: JwtUtil.java, CustomUserDetailsService.java, JwtAuthenticationFilter.java"
echo "5. Config: SecurityConfig.java"
echo "6. DTO: AuthRequest.java, RegisterRequest.java, AuthResponse.java, TaskRequest.java"
