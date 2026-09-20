import React from 'react'

const AuthorProfileHeader = ({user}) => {
    return (
        <div className='profilePage'>
            <div style={{
                height: "1rem"
            }}>
            </div>
            <div className="profileHeader">
                <div className="avatarWrapper">
                    {user.avatarUrl ? (
                        <img
                            src={user?.avatarUrl}
                            alt="avatar"
                            className="avatar"
                        />
                    ) : (
                        <LoginIcon />
                    )}
                </div>
                <div className="profile-info">
                    <h2>{user?.username}</h2>
                    <p className="role">{user?.role}</p>
                </div>
            </div>
        </div>
    )
}

export default AuthorProfileHeader