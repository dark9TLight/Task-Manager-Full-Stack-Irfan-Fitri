import React from 'react';
import { BrowserRouter as Router, Routes, Route, Navigate } from 'react-router-dom';
import Login from './components/Login';
import Register from './components/Register';
import TaskList from './components/TaskList';
import authService from './services/authService';
import './App.css';

function App() {
  const isAuthenticated = authService.isAuthenticated();

  return (
    <Router>
      <div className="App">
        <Routes>
          <Route 
            path="/login" 
            element={!isAuthenticated ? <Login /> : <Navigate to="/tasks" />} 
          />
          <Route 
            path="/register" 
            element={!isAuthenticated ? <Register /> : <Navigate to="/tasks" />} 
          />
          <Route 
            path="/tasks" 
            element={isAuthenticated ? <TaskList /> : <Navigate to="/login" />} 
          />
          <Route 
            path="/" 
            element={<Navigate to={isAuthenticated ? "/tasks" : "/login"} />} 
          />
        </Routes>
      </div>
    </Router>
  );
}

export default App;
