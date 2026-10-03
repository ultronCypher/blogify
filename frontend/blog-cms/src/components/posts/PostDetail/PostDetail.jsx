import React, { useEffect, useState, useRef } from 'react'
import { useParams, useNavigate, Link } from 'react-router-dom';
import { useSelector } from 'react-redux';
import { selectCurrentUser } from '../../../features/auth/authSelectors';
import api from '../../../api/api';
import './styles.scss'
import CommentContainer from '../../comments/CommentContainer/CommentContainer';
import ConfirmModal from '../../common/ConfirmModal/ConfirmModal';
import LoginIcon from '../../common/LoginIcon/LoginIcon';
import { FiMoreVertical, FiEdit2, FiTrash2 } from "react-icons/fi"

import DOMPurify from 'dompurify';
import { toast } from 'react-toastify';

const PostDetail = () => {
    const { postId } = useParams();
    const navigate = useNavigate();
    const currentUser = useSelector(selectCurrentUser);
    const [postDetails, setPostDetails] = useState();
    const [comments, setComments] = useState([]);
    const [loading, setLoading] = useState(true);
    const [error, setError] = useState(null);
    const [newComment, setNewComment] = useState("");
    const [submitting, setSubmitting] = useState(false);
    const [commentsCount, setCommentsCount] = useState(0);
    const [showMenu, setShowMenu] = useState(false);
    const [showDeleteModal, setShowDeleteModal] = useState(false);
    const [deleting, setDeleting] = useState(false);
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
            toast.success("Comment added successfully!");
        } catch (err) {
            setError("Failed to post comment");
            toast.error("Failed to post comment");
        } finally {
            setSubmitting(false);
        }
    }

    const handleConfirmDelete = async () => {
        try {
            setDeleting(true);
            await api.delete(`/posts/${postId}`);
            toast.success("Post deleted successfully!");
            navigate("/");
        } catch (err) {
            toast.error("Failed to delete post.");
        } finally {
            setDeleting(false);
            setShowDeleteModal(false);
        }
    };

    if (loading || !postDetails) return <p className="loadingText">Loading post details...</p>
    if (error) return <p className="errorText">{error}</p>
    if (!postDetails) return null;

    const canEdit = currentUser && (currentUser.id === postDetails.author?.id);
    const authorAvatar = postDetails.author?.avatarUrl || postDetails.authorAvatarUrl;

    return (
        <div className='postDetailPageContainer'>
            <ConfirmModal
                isOpen={showDeleteModal}
                title="Delete Post?"
                message="Are you sure you want to delete this post? This action cannot be undone."
                confirmText="Delete"
                cancelText="Cancel"
                loading={deleting}
                onConfirm={handleConfirmDelete}
                onCancel={() => setShowDeleteModal(false)}
            />

            <div className='postDetailPageLayout'>
                <div className='postDetailsSection'>
                    <div className="authorRow">
                        <Link to={`/users/${postDetails.author?.id}`} className="authorInfo">
                            {authorAvatar ? (
                                <img
                                    src={authorAvatar}
                                    alt={postDetails.author?.username}
                                    className="authorAvatarPostStyle"
                                />
                            ) : (
                                <div className="avatarWrapperNavbar" style={{ width: '2rem', height: '2rem' }}>
                                    <LoginIcon />
                                </div>
                            )}
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
                                                setShowDeleteModal(true);
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

                    {postDetails?.tags && postDetails.tags.length > 0 && (
                        <div className="postDetailTagsContainer" style={{ display: 'flex', flexWrap: 'wrap', gap: '0.4rem', margin: '0.5rem 0 1rem 0' }}>
                            {postDetails.tags.map((tag) => (
                                <Link
                                    key={tag.id}
                                    to={`/?tag=${tag.slug}`}
                                    className="postTagBadgePill"
                                    style={{
                                        backgroundColor: '#f1f5f9',
                                        color: '#475569',
                                        fontSize: '0.8rem',
                                        fontWeight: 600,
                                        padding: '0.25rem 0.7rem',
                                        borderRadius: '16px',
                                        border: '1px solid #cbd5e1',
                                        textDecoration: 'none'
                                    }}
                                >
                                    #{tag.name}
                                </Link>
                            ))}
                        </div>
                    )}

                    {postDetails?.images && postDetails.images.length > 0 && (
                        <div className='imagesLayoutSection'>
                            {postDetails.images.map((imgSrc, idx) => (
                                <div className="imageWrapper" key={idx}>
                                    <img src={imgSrc} alt={`Post image ${idx + 1}`} />
                                </div>
                            ))}
                        </div>
                    )}

                    <div
                        className="postContent"
                        dangerouslySetInnerHTML={{ __html: DOMPurify.sanitize(postDetails?.content || '') }}
                    />

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