import React, { useState } from 'react'
import api from '../../api/api';
import './styles.scss'
const Register = () => {
    const [username, setUsername] = useState("");
    const [password, setPassword] = useState("");
    const [email, setEmail] = useState("");
    const [error, setError] = useState(null)
    const [success, setSuccess] = useState(false)

    const handleSubmit = async (e) => {
        e.preventDefault();
        setError(null);
        try {
            await api.post("/auth/register", {
                username,
                email,
                password
            })
            setSuccess(true);
            setUsername("");
            setEmail("")
            setPassword("");
        } catch (err) {
            setError(err.response?.data?.message || "Registration failed")
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
                        <button type="submit" className='registerButton'>Register</button>
                    </div>
                </form>
            </div>
        </div>
    )
}

export default Register