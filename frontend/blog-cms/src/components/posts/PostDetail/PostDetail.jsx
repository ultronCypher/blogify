import React, { useEffect, useState, useRef } from 'react'
import { useParams, useNavigate, Link } from 'react-router-dom';
import api from '../../../api/api';
import { useAuth } from '../../../context/AuthContext';
import './styles.scss'
import CommentContainer from '../../comments/CommentContainer/CommentContainer';
import { FiMoreVertical, FiEdit2, FiTrash2 } from "react-icons/fi"

const PostDetail = () => {
    const { postId } = useParams();
    const navigate = useNavigate();
    const { user: currentUser } = useAuth();
    const [postDetails, setPostDetails] = useState();
    const [comments, setComments] = useState([]);
    const [loading, setLoading] = useState(true);
    const [error, setError] = useState(null);
    const [newComment, setNewComment] = useState("");
    const [submitting, setSubmitting] = useState(false);
    const [commentsCount, setCommentsCount] = useState(0);
    const [showMenu, setShowMenu] = useState(false);
    const menuRef = useRef(null);

    const formatDate = (dateString) => {
        if (!dateString) return ""
        return new Intl.DateTimeFormat("en-US", {
            year: "numeric",
            month: "short",
            day: "numeric",
        }).format(new Date(dateString))
    }

    useEffect(() => {
        const handleClickOutside = (e) => {
            if (menuRef.current && !menuRef.current.contains(e.target)) {
                setShowMenu(false);
            }
        };
        document.addEventListener("mousedown", handleClickOutside);
        return () => {
            document.removeEventListener("mousedown", handleClickOutside);
        };
    }, []);

    useEffect(() => {
        const fetchData = async () => {
            setLoading(true)
            try {
                const [postRes, commentsRes, countRes] = await Promise.all([
                    api.get(`/posts/${postId}`),
                    api.get(`/posts/${postId}/comments`),
                    api.get(`/posts/${postId}/comments/count`)
                ])
                setPostDetails(postRes.data)
                setComments(commentsRes.data)
                setCommentsCount(countRes.data)
            } catch (err) {
                setError("Failed to load post")
            } finally {
                setLoading(false)
            }
        }
        fetchData()
    }, [postId]);

    const handleSubmitComment = async () => {
        if (!newComment.trim()) return;
        try {
            setSubmitting(true);
            const res = await api.post(`/posts/${postId}/comments`, {
                content: newComment
            });
            setComments(prev => [res.data, ...prev]);
            setCommentsCount(prev => prev + 1);
            setNewComment("");
        } catch (err) {
            setError("Failed to post comment");
        } finally {
            setSubmitting(false);
        }
    }

    const handleDeletePost = async () => {
        if (!window.confirm("Are you sure you want to delete this post?")) return;
        try {
            await api.delete(`/posts/${postId}`);
            navigate("/");
        } catch (err) {
            alert("Failed to delete post.");
        }
    };

    if (loading || !postDetails) return <p className="loadingText">Loading post details...</p>
    if (error) return <p className="errorText">{error}</p>
    if (!postDetails) return null;

    const canEdit = currentUser && (currentUser.id === postDetails.author?.id);

    return (
        <div className='postDetailPageContainer'>
            <div className='postDetailPageLayout'>
                <div className='postDetailsSection'>
                    <div className="authorRow">
                        <Link to={`/users/${postDetails.author?.id}`} className="authorInfo">
                            <img
                                src={postDetails.authorAvatarUrl || "/default-avatar.png"}
                                alt={postDetails.author?.username}
                                className="authorAvatarPostStyle"
                            />
                            <span className="authorName">{postDetails.author?.username}</span>
                        </Link>
                        <span className="dot">•</span>
                        <span className="postDate">
                            {formatDate(postDetails.createdAt)}
                        </span>

                        {canEdit && (
                            <div className="actionMenuContainer" ref={menuRef}>
                                <button
                                    className="menuTrigger"
                                    onClick={() => setShowMenu(prev => !prev)}
                                    title="Post actions"
                                >
                                    <FiMoreVertical size={20} />
                                </button>
                                {showMenu && (
                                    <div className="actionDropdown">
                                        <Link
                                            to={`/posts/${postId}/edit`}
                                            className="actionMenuItem"
                                            onClick={() => setShowMenu(false)}
                                        >
                                            <FiEdit2 size={16} />
                                            <span>Edit</span>
                                        </Link>
                                        <button
                                            className="actionMenuItem delete"
                                            onClick={() => {
                                                setShowMenu(false);
                                                handleDeletePost();
                                            }}
                                        >
                                            <FiTrash2 size={16} />
                                            <span>Delete</span>
                                        </button>
                                    </div>
                                )}
                            </div>
                        )}
                    </div>

                    <div className="titleSection">
                        <h2>{postDetails?.title}</h2>
                    </div>

                    {postDetails?.images && postDetails.images.length > 0 && (
                        <div className='imagesLayoutSection'>
                            {postDetails.images.map((imgSrc, idx) => (
                                <div className="imageWrapper" key={idx}>
                                    <img src={imgSrc} alt={`Post image ${idx + 1}`} />
                                </div>
                            ))}
                        </div>
                    )}

                    <p className="postContent">{postDetails?.content}</p>

                    <div className="commentSection">
                        <h3>Comment Section ({commentsCount})</h3>
                        <div className="commentInputBox">
                            <textarea
                                placeholder="Write a comment..."
                                value={newComment}
                                onChange={(e) => setNewComment(e.target.value)}
                                rows={3}
                            />
                            <button
                                disabled={submitting || !newComment.trim()}
                                onClick={handleSubmitComment}
                            >
                                {submitting ? "Posting..." : "Post"}
                            </button>
                        </div>
                        <div>
                            <div className='commentContainer'>
                                {comments.map(comment => (
                                    <CommentContainer comment={comment} key={comment.id} />
                                ))}
                            </div>
                        </div>
                    </div>
                </div>
            </div>
        </div>
    )
}

export default PostDetail