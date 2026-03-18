import React from "react";
import { Link, useNavigate } from "react-router-dom";
import "../styles/Navbar.css";

function Navbar() {
  const navigate = useNavigate();

  const handleLogout = () => {
    localStorage.removeItem("token");
    navigate("/");
  };

  return (
    <nav className="navbar">
      <div className="nav-brand">📄 QP&nbsp;Generator</div>
      <div className="nav-links">
        <Link to="/dashboard">Dashboard</Link>
        <Link to="/upload">Upload</Link>
        <Link to="/generate">Generate</Link>
        <button className="btn btn-secondary logout-btn" onClick={handleLogout}>
          Logout
        </button>
      </div>
    </nav>
  );
}

export default Navbar;
