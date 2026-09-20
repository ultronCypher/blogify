import React, { useState, useEffect } from 'react'
import { useDispatch, useSelector } from 'react-redux'
import { registerUser, clearAuthError, resetRegisterSuccess } from '../../features/auth/authSlice'
import { selectAuthError, selectAuthLoading, selectRegisterSuccess } from '../../features/auth/authSelectors'
import './styles.scss'

import { toast } from 'react-toastify';
import { useNavigate } from 'react-router-dom';

const Register = () => {
    const [username, setUsername] = useState("");
    const [password, setPassword] = useState("");
    const [email, setEmail] = useState("");

    const dispatch = useDispatch();
    const navigate = useNavigate();
    const error = useSelector(selectAuthError);
    const loading = useSelector(selectAuthLoading);
    const success = useSelector(selectRegisterSuccess);

    useEffect(() => {
        dispatch(clearAuthError());
        dispatch(resetRegisterSuccess());
    }, [dispatch]);

    const handleSubmit = async (e) => {
        e.preventDefault();
        const actionResult = await dispatch(registerUser({ username, email, password }));
        if (registerUser.fulfilled.match(actionResult)) {
            toast.success("Account created successfully! Please login.");
            setUsername("");
            setEmail("");
            setPassword("");
            navigate("/login");
        } else if (registerUser.rejected.match(actionResult)) {
            toast.error(actionResult.payload || "Registration failed");
        }
    }

    return (
        <div className='registerContainer'>
            <div className='registerTitle'>Create your account</div>
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
                        <h2 className='formLabel'>Your Email ID</h2>
                        <input
                            type="email"
                            placeholder="Email ID"
                            value={email}
                            onChange={e => setEmail(e.target.value)}
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
                    {success && <p className="success">Account created successfully</p>}
                    <div className='inputSection'>
                        <button type="submit" className='registerButton' disabled={loading}>
                            {loading ? "Registering..." : "Register"}
                        </button>
                    </div>
                </form>
            </div>
        </div>
    )
}

export default Register