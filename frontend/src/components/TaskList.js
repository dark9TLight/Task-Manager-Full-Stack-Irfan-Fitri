import React, { useState, useEffect } from 'react';
import { useNavigate } from 'react-router-dom';
import authService from '../services/authService';
import api from '../services/api';
import TaskForm from './TaskForm';
import Navbar from './Navbar';

function TaskList() {
  const navigate = useNavigate();
  const [tasks, setTasks] = useState([]);
  const [filteredTasks, setFilteredTasks] = useState([]);
  const [loading, setLoading] = useState(true);
  const [showForm, setShowForm] = useState(false);
  const [editingTask, setEditingTask] = useState(null);
  const [searchTerm, setSearchTerm] = useState('');
  const [statusFilter, setStatusFilter] = useState('ALL');
  const [priorityFilter, setPriorityFilter] = useState('ALL');
  const [sortBy, setSortBy] = useState('createdAt');

  useEffect(() => {
    fetchTasks();
  }, []);

  useEffect(() => {
    filterAndSortTasks();
  }, [tasks, searchTerm, statusFilter, priorityFilter, sortBy]);

  const fetchTasks = async () => {
    try {
      const response = await api.get('/tasks');
      setTasks(response.data);
      setLoading(false);
    } catch (error) {
      console.error('Error fetching tasks:', error);
      setLoading(false);
    }
  };

  const filterAndSortTasks = () => {
    let filtered = [...tasks];

    if (searchTerm) {
      filtered = filtered.filter(task =>
        task.title.toLowerCase().includes(searchTerm.toLowerCase()) ||
        task.description?.toLowerCase().includes(searchTerm.toLowerCase())
      );
    }

    if (statusFilter !== 'ALL') {
      filtered = filtered.filter(task => task.status === statusFilter);
    }

    if (priorityFilter !== 'ALL') {
      filtered = filtered.filter(task => task.priority === priorityFilter);
    }

    filtered.sort((a, b) => {
      if (sortBy === 'priority') {
        const priorityOrder = { URGENT: 4, HIGH: 3, MEDIUM: 2, LOW: 1 };
        return priorityOrder[b.priority] - priorityOrder[a.priority];
      } else if (sortBy === 'dueDate') {
        return new Date(a.dueDate || '9999') - new Date(b.dueDate || '9999');
      } else {
        return new Date(b.createdAt) - new Date(a.createdAt);
      }
    });

    setFilteredTasks(filtered);
  };

  const handleDelete = async (id) => {
    if (window.confirm('Are you sure you want to delete this task?')) {
      try {
        await api.delete(`/tasks/${id}`);
        fetchTasks();
      } catch (error) {
        console.error('Error deleting task:', error);
        alert('Failed to delete task');
      }
    }
  };

  const handleEdit = (task) => {
    setEditingTask(task);
    setShowForm(true);
  };

  const handleFormClose = () => {
    setShowForm(false);
    setEditingTask(null);
    fetchTasks();
  };

  const handleLogout = () => {
    authService.logout();
    navigate('/login');
  };

  const getStatusClass = (status) => {
    switch (status) {
      case 'TODO': return 'status-todo';
      case 'IN_PROGRESS': return 'status-progress';
      case 'COMPLETED': return 'status-completed';
      default: return '';
    }
  };

  const getPriorityClass = (priority) => {
    switch (priority) {
      case 'URGENT': return 'priority-urgent';
      case 'HIGH': return 'priority-high';
      case 'MEDIUM': return 'priority-medium';
      case 'LOW': return 'priority-low';
      default: return '';
    }
  };

  if (loading) {
    return <div className="loading">Loading tasks...</div>;
  }

  return (
    <div className="task-container">
      <Navbar onLogout={handleLogout} />

      <div className="task-header">
        <h1>My Tasks</h1>
        <button className="btn-primary" onClick={() => setShowForm(true)}>
          + Add Task
        </button>
      </div>

      <div className="task-filters">
        <input
          type="text"
          placeholder="Search tasks..."
          value={searchTerm}
          onChange={(e) => setSearchTerm(e.target.value)}
          className="search-input"
        />

        <select value={statusFilter} onChange={(e) => setStatusFilter(e.target.value)}>
          <option value="ALL">All Status</option>
          <option value="TODO">To Do</option>
          <option value="IN_PROGRESS">In Progress</option>
          <option value="COMPLETED">Completed</option>
        </select>

        <select value={priorityFilter} onChange={(e) => setPriorityFilter(e.target.value)}>
          <option value="ALL">All Priority</option>
          <option value="URGENT">Urgent</option>
          <option value="HIGH">High</option>
          <option value="MEDIUM">Medium</option>
          <option value="LOW">Low</option>
        </select>

        <select value={sortBy} onChange={(e) => setSortBy(e.target.value)}>
          <option value="createdAt">Sort by Date</option>
          <option value="priority">Sort by Priority</option>
          <option value="dueDate">Sort by Due Date</option>
        </select>
      </div>

      <div className="task-list">
        {filteredTasks.length === 0 ? (
          <div className="no-tasks">No tasks found. Create your first task!</div>
        ) : (
          filteredTasks.map(task => (
            <div key={task.id} className="task-card">
              <div className="task-card-header">
                <h3>{task.title}</h3>
                <div className="task-badges">
                  <span className={`badge ${getStatusClass(task.status)}`}>
                    {task.status.replace('_', ' ')}
                  </span>
                  <span className={`badge ${getPriorityClass(task.priority)}`}>
                    {task.priority}
                  </span>
                </div>
              </div>
              
              {task.description && (
                <p className="task-description">{task.description}</p>
              )}
              
              {task.dueDate && (
                <p className="task-due-date">Due: {new Date(task.dueDate).toLocaleDateString()}</p>
              )}
              
              <div className="task-actions">
                <button className="btn-edit" onClick={() => handleEdit(task)}>
                  Edit
                </button>
                <button className="btn-delete" onClick={() => handleDelete(task.id)}>
                  Delete
                </button>
              </div>
            </div>
          ))
        )}
      </div>

      {showForm && (
        <TaskForm
          task={editingTask}
          onClose={handleFormClose}
        />
      )}
    </div>
  );
}

export default TaskList;
