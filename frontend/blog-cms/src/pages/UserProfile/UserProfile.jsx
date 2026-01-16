import React, { useEffect, useState } from 'react'
import { useParams, Navigate } from 'react-router-dom'
import api from '../../api/api'
import AuthorProfileHeader from '../AuthorProfileHeader/AuthorProfileHeader'
import ProfilePostsBody from '../../components/users/ProfilePostsBody/ProfilePostsBody'
import './styles.scss'
import { useAuth } from '../../context/AuthContext'

const UserProfile = () => {
    const { userId } = useParams();

    const [user, setUser] = useState(null);
    const [posts, setPosts] = useState([]);
    const [loading, setLoading] = useState(true);
    const { user: authUser, loading: authLoading } = useAuth();
    if(!authLoading && authUser && Number(userId)===authUser.id){
        return <Navigate to="/profile/me" replace/>
    }
    useEffect(() => {
        const fetchUserProfile = async () => {
            try {
                const userRes = await api.get(`/users/${userId}`)
                const postsRes = await api.get(`/users/${userId}/posts?page=0&size=10`)
                const fetchedPosts = postsRes.data.content
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
                setUser(userRes.data)
                setPosts(enrichedPosts)
            } catch (err) {
                console.log(err);
            } finally {
                setLoading(false);
            }
        }
        fetchUserProfile();
    }, [userId]);

    const toggleLike = async (postId) => {
        setPosts(prev =>
            prev.map(post =>
                post.id === postId
                    ? {
                        ...post,
                        didUserLike: !post.didUserLike,
                        likesCount: post.didUserLike
                            ? post.likesCount - 1
                            : post.likesCount + 1
                    }
                    : post
            )
        )
        const post = posts.find(p => p.id === postId)
        try {
            if (post.didUserLike) {
                await api.delete(`/posts/${postId}/unlike`)
            } else {
                await api.post(`/posts/${postId}/like`)
            }
        } catch (err) {
            console.error(err)
        }
    }
    if (!user) return <div>Loading profile...</div>;
    return (
        <div>
            <AuthorProfileHeader user={user} posts={posts} />
            <div style={{
                padding: "2.5rem",
                paddingTop: "0"
            }}>
                <ProfilePostsBody
                    posts={posts}
                    onToggleLike={toggleLike}
                />
            </div>
        </div>
    )
}

export default UserProfile