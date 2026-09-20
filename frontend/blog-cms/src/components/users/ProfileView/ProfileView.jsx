import React, { useEffect, useState, useRef } from 'react';
import { useSelector, useDispatch } from 'react-redux';
import { selectCurrentUser } from '../../../features/auth/authSelectors';
import { updateUser } from '../../../features/auth/authSlice';
import api from '../../../api/api';
import ProfilePostsBody from '../ProfilePostsBody/ProfilePostsBody';
import LoginIcon from '../../common/LoginIcon/LoginIcon';
import { FiCamera, FiFileText, FiHeart, FiEye, FiMessageSquare } from 'react-icons/fi';
import { Link } from 'react-router-dom';
import { toast } from 'react-toastify';
import './styles.scss';

const ProfileView = ({ userId, isMe = false }) => {
    const dispatch = useDispatch();
    const currentUser = useSelector(selectCurrentUser);
    const fileInputRef = useRef(null);

    const [stats, setStats] = useState(null);
    const [statsLoading, setStatsLoading] = useState(true);
    const [statsError, setStatsError] = useState(null);

    const [avatarPreview, setAvatarPreview] = useState(null);
    const [uploadingAvatar, setUploadingAvatar] = useState(false);

    const [activeTab, setActiveTab] = useState('posts');

    const [posts, setPosts] = useState([]);
    const [postsLoading, setPostsLoading] = useState(true);
    const [postsError, setPostsError] = useState(null);

    const [comments, setComments] = useState([]);
    const [commentsLoading, setCommentsLoading] = useState(false);
    const [commentsError, setCommentsError] = useState(null);
    const [commentsFetched, setCommentsFetched] = useState(false);

    const targetUserId = isMe ? currentUser?.id : userId;

    useEffect(() => {
        const fetchProfileStats = async () => {
            setStatsLoading(true);
            try {
                const endpoint = isMe ? '/users/me/stats' : `/users/${userId}/stats`;
                const res = await api.get(endpoint);
                setStats(res.data);
                setAvatarPreview(res.data.avatarUrl);
            } catch (err) {
                console.error(err);
                setStatsError('Failed to load profile statistics');
            } finally {
                setStatsLoading(false);
            }
        };

        if (isMe && !currentUser) return;
        if (targetUserId) {
            fetchProfileStats();
        }
    }, [userId, isMe, currentUser, targetUserId]);

    useEffect(() => {
        const fetchUserPosts = async () => {
            if (!targetUserId) return;
            setPostsLoading(true);
            try {
                const res = await api.get(`/users/${targetUserId}/posts?page=0&size=20`);
                const fetchedPosts = res.data.content || [];

                const likeStatuses = await Promise.all(
                    fetchedPosts.map(post =>
                        api.get(`/posts/${post.id}/likes/me`)
                            .then(r => r.data)
                            .catch(() => false)
                    )
                );

                const enrichedPosts = fetchedPosts.map((post, idx) => ({
                    ...post,
                    didUserLike: likeStatuses[idx]
                }));

                setPosts(enrichedPosts);
            } catch (err) {
                console.error(err);
                setPostsError('Failed to load posts');
            } finally {
                setPostsLoading(false);
            }
        };

        fetchUserPosts();
    }, [targetUserId]);

    useEffect(() => {
        if (activeTab === 'comments' && !commentsFetched && targetUserId) {
            const fetchUserComments = async () => {
                setCommentsLoading(true);
                try {
                    const res = await api.get(`/users/${targetUserId}/comments?page=0&size=20`);
                    setComments(res.data.content || []);
                    setCommentsFetched(true);
                } catch (err) {
                    console.error(err);
                    setCommentsError('Failed to load comments');
                } finally {
                    setCommentsLoading(false);
                }
            };
            fetchUserComments();
        }
    }, [activeTab, commentsFetched, targetUserId]);

    const handleCameraClick = () => {
        if (fileInputRef.current) {
            fileInputRef.current.click();
        }
    };

    const handleFileChange = async (e) => {
        const file = e.target.files?.[0];
        if (!file) return;

        const localPreview = URL.createObjectURL(file);
        setAvatarPreview(localPreview);

        const formData = new FormData();
        formData.append("file", file);
        try {
            setUploadingAvatar(true);
            const res = await api.post("users/me/avatar", formData, {
                headers: { "Content-Type": "multipart/form-data" }
            });
            const { avatarUrl } = res.data;
            setAvatarPreview(avatarUrl);
            dispatch(updateUser({ avatarUrl }));
            toast.success("Profile avatar updated successfully!");
        } catch (err) {
            console.error("Avatar upload failed", err);
            setAvatarPreview(stats?.avatarUrl);
            toast.error("Failed to update avatar");
        } finally {
            setUploadingAvatar(false);
        }
    };

    const toggleLike = async (postId) => {
        try {
            setPosts(prev =>
                prev.map(post => {
                    if (post.id !== postId) return post;
                    return {
                        ...post,
                        didUserLike: !post.didUserLike,
                        likesCount: post.didUserLike ? post.likesCount - 1 : post.likesCount + 1
                    };
                })
            );

            const targetPost = posts.find(p => p.id === postId);
            if (targetPost) {
                if (targetPost.didUserLike) {
                    await api.delete(`/posts/${postId}/unlike`);
                } else {
                    await api.post(`/posts/${postId}/like`);
                }
            }
        } catch (err) {
            console.error(err);
        }
    };

    const formatDate = (dateStr) => {
        if (!dateStr) return '';
        return new Date(dateStr).toLocaleDateString("en-US", {
            year: "numeric",
            month: "short",
            day: "numeric"
        });
    };

    if (statsLoading) {
        return <div className="profileLoading"><p>Loading profile...</p></div>;
    }

    if (statsError) {
        return <div className="profileError"><p>{statsError}</p></div>;
    }

    return (
        <div className="profileViewContainer">
            {/* Header Section */}
            <div className="profileCardHeader">
                <div className="profileHeaderTop">
                    <div className="avatarWrapper">
                        {avatarPreview ? (
                            <img src={avatarPreview} alt="avatar" className="avatar" />
                        ) : (
                            <LoginIcon />
                        )}

                        {isMe && (
                            <>
                                <button
                                    className="avatarEditButton"
                                    onClick={handleCameraClick}
                                    title="Update avatar"
                                    disabled={uploadingAvatar}
                                >
                                    <FiCamera size={16} />
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

                    <div className="profileInfo">
                        <h2 className="username">{stats?.username}</h2>
                        <span className="roleBadge">{stats?.role || 'User'}</span>
                    </div>
                </div>

                {/* Statistics Row */}
                <div className="profileStatsGrid">
                    <div className="statBox">
                        <div className="statIconWrapper posts">
                            <FiFileText size={20} />
                        </div>
                        <div className="statContent">
                            <span className="statValue">{stats?.totalPosts ?? 0}</span>
                            <span className="statLabel">Total Posts</span>
                        </div>
                    </div>

                    <div className="statBox">
                        <div className="statIconWrapper likes">
                            <FiHeart size={20} />
                        </div>
                        <div className="statContent">
                            <span className="statValue">{stats?.totalLikes ?? 0}</span>
                            <span className="statLabel">Total Likes</span>
                        </div>
                    </div>

                    <div className="statBox">
                        <div className="statIconWrapper views">
                            <FiEye size={20} />
                        </div>
                        <div className="statContent">
                            <span className="statValue">{stats?.totalViews ?? 0}</span>
                            <span className="statLabel">Total Views</span>
                        </div>
                    </div>
                </div>
            </div>

            {/* Segmented Control */}
            <div className="segmentedControl">
                <button
                    className={`segmentBtn ${activeTab === 'posts' ? 'active' : ''}`}
                    onClick={() => setActiveTab('posts')}
                >
                    <FiFileText size={16} />
                    <span>Posts ({stats?.totalPosts ?? 0})</span>
                </button>
                <button
                    className={`segmentBtn ${activeTab === 'comments' ? 'active' : ''}`}
                    onClick={() => setActiveTab('comments')}
                >
                    <FiMessageSquare size={16} />
                    <span>Comments</span>
                </button>
            </div>

            {/* Tab Body */}
            <div className="profileTabBody">
                {activeTab === 'posts' && (
                    <div className="postsTabSection">
                        {postsLoading ? (
                            <p className="tabStateText">Loading posts...</p>
                        ) : postsError ? (
                            <p className="tabStateText error">{postsError}</p>
                        ) : posts.length === 0 ? (
                            <p className="tabStateText">No posts created yet.</p>
                        ) : (
                            <ProfilePostsBody posts={posts} onToggleLike={toggleLike} />
                        )}
                    </div>
                )}

                {activeTab === 'comments' && (
                    <div className="commentsTabSection">
                        {commentsLoading ? (
                            <p className="tabStateText">Loading comments...</p>
                        ) : commentsError ? (
                            <p className="tabStateText error">{commentsError}</p>
                        ) : comments.length === 0 ? (
                            <p className="tabStateText">No comments written yet.</p>
                        ) : (
                            <div className="commentsList">
                                {comments.map(c => (
                                    <div className="userCommentCard" key={c.id}>
                                        <div className="commentPostHeader">
                                            <span className="commentContextLabel">Commented on post:</span>
                                            <Link to={`/${c.postId}`} className="commentPostTitleLink">
                                                {c.postTitle}
                                            </Link>
                                            <span className="commentDate">• {formatDate(c.createdAt)}</span>
                                        </div>
                                        <p className="commentBodyText">{c.content}</p>
                                    </div>
                                ))}
                            </div>
                        )}
                    </div>
                )}
            </div>
        </div>
    );
};

export default ProfileView;
