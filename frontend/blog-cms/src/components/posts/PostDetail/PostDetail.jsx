import React, { useEffect, useState } from 'react'
import { useParams } from 'react-router-dom';
import api from '../../../api/api';
import { useAuth } from '../../../context/AuthContext';
import './styles.scss'
import { Link } from 'react-router-dom';
import CommentContainer from '../../comments/CommentContainer/CommentContainer';
import { FiEdit2 } from "react-icons/fi"

const PostDetail = () => {
    const { postId } = useParams();
    const { user: currentUser } = useAuth();
    const [postDetails, setPostDetails] = useState();
    const [comments, setComments] = useState([]);
    const [loading, setLoading] = useState(true);
    const [error, setError] = useState(null);
    const [newComment, setNewComment] = useState("");
    const [submitting, setSubmitting] = useState(false);
    const [commentsCount, setCommentsCount] = useState(0);

    const formatDate = (dateString) => {
        if (!dateString) return ""
        return new Intl.DateTimeFormat("en-US", {
            year: "numeric",
            month: "short",
            day: "numeric",
        }).format(new Date(dateString))
    }

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

    if (loading || !postDetails) return <p>Loading post details...</p>
    if (error) return <p>{error}</p>
    if (!postDetails) return null;
    console.log("currentUser:", currentUser)
    console.log("post author:", postDetails.author)

    const canEdit = currentUser && (currentUser.id === postDetails.author.id)
    return (
        <div className='postDetailPageContainer'>
            <div className='postDetailPageLayout'>

                <div className='postDetailsSection'>
                    <div className="authorRow">
                        <Link to={`/users/${postDetails.author.id}`} className="authorInfo">
                            <img
                                src={postDetails.authorAvatarUrl || "/default-avatar.png"}
                                alt={postDetails.author.username}
                                className="authorAvatarPostStyle"
                            />
                            <span className="authorName">{postDetails.author.username}</span>
                        </Link>
                        <span className="dot">•</span>
                        <span className="postDate">
                            {formatDate(postDetails.createdAt)}
                        </span>
                    </div>
                    <div className="titleSection">
                        <h2>{postDetails?.title}</h2>
                        <Link
                            to={`/posts/${postId}/edit`}
                            className="editIconButton"
                            title="Edit post"
                        >
                            <FiEdit2 size={24} />
                        </Link>
                    </div>
                    <div className='imagesLayoutSection'>
                        {

                            postDetails?.images.map((imgSrc) => (
                                <div className="imageWrapper">
                                    <img src={imgSrc} />
                                </div>
                            ))
                        }
                    </div>
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
                                {
                                    comments.map(comment => (
                                        <CommentContainer comment={comment} />
                                    ))
                                }
                            </div>
                        </div>
                    </div>
                </div>
            </div>
        </div>
    )
}

export default PostDetail