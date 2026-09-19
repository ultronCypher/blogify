import React, { useState, useRef, useEffect } from 'react'
import './styles.scss'
import LoginIcon from '../LoginIcon/LoginIcon'
import Modal from '../Modal/Modal'
import { useAuth } from '../../../context/AuthContext'
import { Link } from 'react-router-dom'

const Navbar = () => {
  const { user, loading, logout } = useAuth();
  const [showAuthModal, setShowAuthModal] = useState(false);
  const authRefModal = useRef(null);

  const handleClickSilhouette = () => {
    setShowAuthModal((prev) => !prev);
  }

  const handleClose = () => {
    setShowAuthModal(false);
  }

  const handleLogout = () => {
    logout();
    setShowAuthModal(false);
  }

  useEffect(() => {
    const handleClickOutside = (e) => {
      if (authRefModal.current && !authRefModal.current.contains(e.target)) {
        setShowAuthModal(false);
      }
    }
    document.addEventListener("mousedown", handleClickOutside);
    return () => {
      document.removeEventListener("mousedown", handleClickOutside);
    }
  }, []);

  if (loading) return null;

  return (
    <div className='container'>
      <div className="titleName">
        <h2>Blogify</h2>
      </div>
      <div className='navbarButtonsSection'>
        <div>
          <Link to="/">
            <button className='homeButton'>Home</button>
          </Link>
        </div>
        <div>
          <Link to="/request">
            <button className='publishButton'>Start Publishing</button>
          </Link>
        </div>
      </div>
      <div className="authModalContainer" ref={authRefModal}>
        <div onClick={handleClickSilhouette} className="userMenu">
          <div className="avatarWrapperNavbar">
            {user?.avatarUrl ? (
              <img
                src={user.avatarUrl}
                alt="avatar"
                className="navbarAvatar"
              />
            ) : (
              <LoginIcon />
            )}
          </div>
          {user && <span className='usernameStyle'>{user.username}</span>}
        </div>
        {showAuthModal && (
          <Modal
            isAuthenticated={!!user}
            onLogout={handleLogout}
            onClose={handleClose}
          />
        )}
      </div>
    </div>
  )
}

export default Navbar