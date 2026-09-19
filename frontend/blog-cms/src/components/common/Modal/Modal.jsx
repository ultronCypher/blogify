import React from 'react'
import { Link } from 'react-router-dom'
import './styles.scss'

const Modal = ({ isAuthenticated, onLogout, onClose }) => {
    return (
        <div className='modalContainer'>
            <div className='modalCollection'>
                {isAuthenticated ? (
                    <>
                        <Link to="/profile/me" className="modalItem" onClick={onClose}>
                            Profile
                        </Link>
                        <button className="modalItem" onClick={onLogout}>
                            Logout
                        </button>
                    </>
                ) : (
                    <>
                        <Link to="/login" className="modalItem" onClick={onClose}>
                            Login
                        </Link>
                        <Link to="/register" className="modalItem" onClick={onClose}>
                            Register
                        </Link>
                    </>
                )}
            </div>
        </div>
    )
}

export default Modal