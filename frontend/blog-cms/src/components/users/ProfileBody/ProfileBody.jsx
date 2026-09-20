import React, { useEffect, useState, } from 'react'
import api from '../../../api/api'
import { getPreviewImage } from '../../../utils/cloudinary'
import './styles.scss'
import { useAuth } from '../../../context/AuthContext'
import { FaHeart, FaRegHeart, FaEye, FaComment } from 'react-icons/fa'
import { Link } from 'react-router-dom'
import ProfilePostsBody from '../ProfilePostsBody/ProfilePostsBody'

const ProfileBody = () => {
    const { user } = useAuth();
    const [posts, setPosts] = useState([]);
    const [loading, setLoading] = useState(true);

    useEffect(() => {
        if (!user) return;
        const fetchPosts = async () => {
            try {
                const res = await api.get(`/users/${user.id}/posts?page=0&size=10`);
                const fetchedPosts = res.data.content
                const likeStatuses = await Promise.all(
                    fetchedPosts.map(post =>
                        api.get(`/posts/${post.id}/likes/me`)
                            .then(res => res.data)
                            .catch(() => false)
                    )
                )
                const enrichedPosts = fetchedPosts.map((post, idx) => ({
                    ...post,
                    didUserLike: likeStatuses[idx]
                }))
                setPosts(enrichedPosts);
            } catch (err) {
                console.log(err);
            } finally {
                setLoading(false);
            }
        }
        fetchPosts();
    }, [user]);
    const toggleLike = async (postId) => {
        try {
            setPosts(prev =>
                prev.map(post => {
                    if (post.id !== postId) return post

                    return {
                        ...post,
                        didUserLike: !post.didUserLike,
                        likesCount: post.didUserLike
                            ? post.likesCount - 1
                            : post.likesCount + 1
                    }
                })
            )

            const post = posts.find(p => p.id === postId)

            if (post.didUserLike) {
                await api.delete(`/posts/${postId}/unlike`)
            } else {
                await api.post(`/posts/${postId}/like`)
            }

        } catch (err) {
            console.error(err)
        }
    }
    if (loading) return <div>Loading</div>
    return (
        <ProfilePostsBody posts={posts} onToggleLike={toggleLike} />
    )
}

export default ProfileBody