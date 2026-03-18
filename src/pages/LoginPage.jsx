import React, { useState } from "react";
import { loginUser } from "../api/auth";
import { useNavigate } from "react-router-dom";
import "../styles/LoginPage.css";

function LoginPage() {

  const navigate = useNavigate();

  const [form, setForm] = useState({
    username: "",
    password: ""
  });

  const handleChange = (e) => {

    setForm({
      ...form,
      [e.target.name]: e.target.value
    });

  };

  const handleLogin = async () => {

    try {

      const res = await loginUser(form);

      localStorage.setItem("token", res.data.token);

      alert("Login Successful");

      navigate("/dashboard");

    } catch (err) {

      alert("Invalid Credentials");

    }

  };

  return (
    <div className="page-wrapper">
      <div className="card">
        <div className="login-header">
          <div className="login-logo">📝</div>
          <h2>Login</h2>
        </div>

        <div className="form-group">
          <input
            name="username"
            placeholder="Username"
            onChange={handleChange}
          />
        </div>

        <div className="form-group">
          <input
            type="password"
            name="password"
            placeholder="Password"
            onChange={handleChange}
          />
        </div>

        <button className="btn" onClick={handleLogin}>Login</button>
      </div>
    </div>
  );
}

export default LoginPage;