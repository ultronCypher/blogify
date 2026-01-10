import React, { useEffect, useState } from 'react'
import Modal from '../../components/common/Modal/Modal'
import HomePagePost from '../../components/posts/HomePagePost/HomePagePost'
import api from '../../api/api'
import { Link } from 'react-router-dom'
import { FaHeart, FaRegHeart, FaEye, FaComment } from "react-icons/fa";
import './styles.scss'
import TopLikedPost from '../../components/TopLikedPost/TopLikedPost'
const Home = () => {
    const [posts, setPosts] = useState([]);
    const [loading, setLoading] = useState(true);
    const [error, setError] = useState(null);
    useEffect(() => {
        const fetchPosts = async () => {
            try {
                const res = await api.get("/posts");
                setPosts(res.data);
            } catch (err) {
                setError("Failed to load posts");
            } finally {
                setLoading(false);
            }
        }
        fetchPosts();
    }, [])
    if (loading) return <p>Loading posts...</p>
    if (error) return <p>{error}</p>
    return (
        <>
            <div className='homePageContainer'>
                <div className='homePageLayout'>
                    <div className='homePageFeedPosts'>
                        {
                            posts.map(post => (
                                <Link to={`/${post.id}`}>
                                    <HomePagePost post={post} />
                                </Link>
                            ))
                        }
                    </div>
                    <div className='hallOfFameSection'>
                        <TopLikedPost />
                    </div>
                </div>
            </div>
        </>
    )
}

export default Home