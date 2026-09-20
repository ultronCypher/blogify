import React, { useState, useEffect } from 'react'
import { useDispatch, useSelector } from 'react-redux'
import { loginUser, clearAuthError } from '../../features/auth/authSlice'
import { selectAuthError, selectAuthLoading } from '../../features/auth/authSelectors'
import { useNavigate } from 'react-router-dom'
import "./styles.scss"

import { toast } from 'react-toastify';

const Login = () => {
  const [username, setUsername] = useState("");
  const [password, setPassword] = useState("");
  const dispatch = useDispatch();
  const navigate = useNavigate();
  const error = useSelector(selectAuthError);
  const loading = useSelector(selectAuthLoading);

  useEffect(() => {
    dispatch(clearAuthError());
  }, [dispatch]);

  const handleSubmit = async (e) => {
    e.preventDefault();
    const resultAction = await dispatch(loginUser({ username, password }));
    if (loginUser.fulfilled.match(resultAction)) {
      toast.success("Logged in successfully!");
      navigate("/");
    } else if (loginUser.rejected.match(resultAction)) {
      toast.error(resultAction.payload || "Login failed");
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
            <button className='loginButton' type="submit" disabled={loading}>
              {loading ? "Logging in..." : "Login"}
            </button>
          </div>
        </form>
      </div>
    </div>
  )
}

export default Login