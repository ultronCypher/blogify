import React, { useEffect, useRef, useState } from 'react'
import './styles.scss'
import { FiCamera } from "react-icons/fi";
import api from '../../../api/api';
import toast from 'react-hot-toast';
import { useAuth } from '../../../context/AuthContext'

const ProfileHeader = ({ user, isMe }) => {
    const fileInputRef = useRef(null);
    const { updateUser } = useAuth();
    const [avatarPreview, setAvatarPreview] = useState(user?.avatarUrl);
    const [loading, setLoading] = useState(false);

    useEffect(() => {
        setAvatarPreview(user?.avatarUrl);
    }, [user?.avatarUrl]);
    const handleCameraClick = () => {
        fileInputRef.current.click();
    }
    const handleFileChange = async (e) => {
        const file = e.target.files[0];
        if (!file) return;
        const localPreview = URL.createObjectURL(file);
        setAvatarPreview(localPreview);

        const formData = new FormData();
        formData.append("file", file);
        try {
            setLoading(true);
            const res = await api.post("users/me/avatar",
                formData,
                {
                    headers: {
                        "Content-Type": "multipart/form-data",
                    },
                })
            const { avatarUrl } = res.data;
            setAvatarPreview(avatarUrl);
            updateUser({avatarUrl});
            toast.success("Profile updated successfully");
        } catch (err) {
            console.error("Avatar upload failed", err);
            setAvatarPreview(user?.avatarUrl);
            toast.error("Failed to update avatar");
        } finally {
            setLoading(false);
        }
    }

    return (
        <div className='profilePage'>
            <div style={{
                height: "1rem"
            }}>
            </div>
            <div className="profileHeader">
                <div className="avatarWrapper">
                    <img
                        src={avatarPreview || "/default-avatar.png"}
                        className="avatar"
                        alt="avatar"
                    />
                    {isMe && (
                        <>
                            <button
                                className="avatarEditButton"
                                onClick={handleCameraClick}
                            >
                                <FiCamera size={18} />
                            </button>
                            <input
                                type="file"
                                accept="image/*"
                                ref={fileInputRef}
                                style={{ display: "none" }}
                                onChange={handleFileChange}
                            />
                        </>
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

export default ProfileHeader