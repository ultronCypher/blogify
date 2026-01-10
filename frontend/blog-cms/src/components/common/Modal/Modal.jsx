import React from 'react'
import { Link } from 'react-router-dom'
import './styles.scss'
const Modal = ({ isAuthenticated, onLogout }) => {
    return (
        <div className='modalContainer'>
            <div className='modalCollection'>
                {isAuthenticated ? (
                    <>
                        <Link to="/profile" className="modalItem">Profile</Link>
                        <button className="modalItem" onClick={onLogout}>
                            Logout
                        </button>
                    </>
                ) : null}
            </div>
        </div>
    )
}

export default Modal