import React from 'react'
import { Link } from 'react-router-dom'
import { FiUser, FiLogOut, FiLogIn, FiUserPlus } from 'react-icons/fi'
import './styles.scss'

const Modal = ({ isAuthenticated, onLogout, onClose }) => {
    return (
        <div className='modalContainer'>
            <div className='modalCollection'>
                {isAuthenticated ? (
                    <>
                        <Link to="/profile/me" className="modalItem" onClick={onClose}>
                            <FiUser className="modalItemIcon" />
                            <span>Profile</span>
                        </Link>
                        <button className="modalItem" onClick={onLogout}>
                            <FiLogOut className="modalItemIcon" />
                            <span>Logout</span>
                        </button>
                    </>
                ) : (
                    <>
                        <Link to="/login" className="modalItem" onClick={onClose}>
                            <FiLogIn className="modalItemIcon" />
                            <span>Login</span>
                        </Link>
                        <Link to="/register" className="modalItem" onClick={onClose}>
                            <FiUserPlus className="modalItemIcon" />
                            <span>Register</span>
                        </Link>
                    </>
                )}
            </div>
        </div>
    )
}

export default Modal