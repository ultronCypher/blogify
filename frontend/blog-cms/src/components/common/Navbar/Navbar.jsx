import React, { useState, useRef, useEffect } from 'react'
import './styles.scss'
import LoginIcon from '../LoginIcon/LoginIcon'
import Modal from '../Modal/Modal'
import { useAuth } from '../../../context/AuthContext'
import { Link } from 'react-router-dom'
import { FiHome, FiEdit3, FiChevronDown } from 'react-icons/fi'

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
    <header className="navbarHeader">
      <div className="navbarContainer">
        <Link to="/" className="brandLink">
          <h2 className="titleName">
            Blogify<span className="brandDot">.</span>
          </h2>
        </Link>
        <div className="navbarButtonsSection">
          <Link to="/" className="navLink">
            <button className="homeButton">
              <FiHome className="btnIcon" />
              <span>Home</span>
            </button>
          </Link>
          <Link to="/request" className="navLink">
            <button className="publishButton">
              <FiEdit3 className="btnIcon" />
              <span>Start Publishing</span>
            </button>
          </Link>
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
            {user && <span className="usernameStyle">{user.username}</span>}
            <FiChevronDown className={`dropdownChevron ${showAuthModal ? 'open' : ''}`} />
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
    </header>
  )
}

export default Navbar