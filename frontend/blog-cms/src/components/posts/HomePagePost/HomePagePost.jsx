import React, { useEffect, useState } from 'react'
import './styles.scss'
import { getPreviewImage } from '../../../utils/cloudinary';
import { FaHeart, FaRegHeart, FaEye, FaComment } from "react-icons/fa";
import api from '../../../api/api';
import { Link } from 'react-router-dom';

const HomePagePost = ({ post }) => {
    const postId = post.id;
    const previewImage = post.previewImage;
    const [error, setError] = useState(null);
    const [didUserLike, setDidUserLike] = useState(false);
    const [likesCount, setLikesCount] = useState(post.likesCount);

    const formatDate = (dateString) => {
        if (!dateString) return ""
        return new Intl.DateTimeFormat("en-US", {
            year: "numeric",
            month: "short",
            day: "numeric",
        }).format(new Date(dateString))
    }

    useEffect(() => {
        const fetchDidUserLike = async () => {
            try {
                const res = await api.get(`/posts/${postId}/likes/me`);
                setDidUserLike(res.data);
            } catch (err) {
                setError("Unable to determine like status")
            }
        }
        fetchDidUserLike();
    }, [postId]);

    useEffect(() => {
        const fetchLikesCount = async () => {
            try {
                const res = await api.get(`/posts/${postId}/likes/count`);
                setLikesCount(res.data);
            } catch (err) {
                setError("Unable to fetch likes count")
            }
        }
        fetchLikesCount();
    }, [didUserLike]);
    const toggleLike = async () => {
        try {
            if (didUserLike) {
                await api.delete(`/posts/${postId}/unlike`)
            } else {
                await api.post(`/posts/${postId}/like`)
            }
            setDidUserLike(prev => !prev);
        } catch (err) {
            setError("Like toggle failed", err);
        }
    }
    return (
        <div className='postPreview'>
            <div className="authorRow">
                <Link to={`/users/${post.authorId}`} className="authorInfo">
                    <img
                        src={post.authorAvatarUrl || "/default-avatar.png"}
                        alt={post.authorUsername}
                        className="authorAvatarPostStyle"
                    />
                    <span className="authorName">{post.authorUsername}</span>
                </Link>
                <span className="dot">•</span>
                <span className="postDate">
                    {formatDate(post.createdAt)}
                </span>
            </div>

            <h2>{post.title}</h2>
            <div className='imageContainer'>
                {previewImage && (
                    <img
                        src={getPreviewImage(previewImage)}
                        alt={post.title}
                        loading="lazy"
                        className="postImage"
                    />
                )}
            </div>

            <p>{post.excerpt}....</p>
            <div className="postStats">
                <span className="stat">
                    <FaEye />
                    {post.viewsCount}
                </span>
                <span onClick={(e) => {
                    e.preventDefault();
                    e.stopPropagation();
                    toggleLike();
                }}>
                    <span className="stat like">
                        <span className="likeIconWrapper">
                            {didUserLike ? (
                                <FaHeart className="likedIcon" />
                            ) : (
                                <FaRegHeart className="likeIcon" />
                            )}
                        </span>

                        <span className="likeCount">{likesCount}</span>
                    </span>

                </span>
                <span className="stat">
                    <FaComment />
                    {post.commentsCount}
                </span>
            </div>

        </div>
    )
}

export default HomePagePost