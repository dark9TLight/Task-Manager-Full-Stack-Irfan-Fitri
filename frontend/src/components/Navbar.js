import React from 'react';
import authService from '../services/authService';

function Navbar({ onLogout }) {
  const user = authService.getCurrentUser();

  return (
    <nav className="navbar">
      <div className="navbar-brand">
        <h2>📋 Task Manager</h2>
      </div>
      <div className="navbar-user">
        <span className="user-info">Welcome, {user?.username}!</span>
        <button className="btn-logout" onClick={onLogout}>
          Logout
        </button>
      </div>
    </nav>
  );
}

export default Navbar;
