import React, { useState } from 'react'
import api from '../../api/api'
import { useAuth } from '../../context/AuthContext'
import { useNavigate } from 'react-router-dom'
import "./styles.scss"

const Login = () => {
  const [username, setUsername] = useState("");
  const [password, setPassword] = useState("");
  const [error, setError] = useState(null);
  const { login } = useAuth();
  const navigate = useNavigate()

  const handleSubmit = async (e) => {
    e.preventDefault();
    setError(null);
    try {
      const res = await api.post("auth/login", {
        username, password
      })
      const token = res.data.token
      localStorage.setItem("token", token)
      const meResponse = await api.get("/auth/me")
      login(token, meResponse.data);
      navigate("/");
    } catch (err) {
      setError(err.response?.data?.message || 'Login failed')
    }
  }
  return (
    <div className='loginContainer'>
      <div className='loginTitle'>Login with your credentials</div>
      <div className='registerForm'>
        <form onSubmit={handleSubmit}>
          <div className='inputSection'>
            <h2 className='formLabel'>Your Username</h2>
            <input
              type="text"
              placeholder="Username"
              value={username}
              onChange={e => setUsername(e.target.value)}
              required
            />
          </div>
          <div className='inputSection'>
            <h2 className='formLabel'>Your Password</h2>
            <input
              type="password"
              placeholder="Password"
              value={password}
              onChange={e => setPassword(e.target.value)}
              required
            />
          </div>
          {error && <p className="error">{error}</p>}
          <div className='inputSection'>
            <button className='loginButton' type="submit">Login</button>
          </div>
        </form>
      </div>
    </div>
  )
}

export default Login