import React, { useState, useEffect } from 'react'
import { Link } from 'react-router-dom'
import { FaHeart, FaRegHeart, FaEye, FaComment } from "react-icons/fa";
import './styles.scss'
import api from '../../api/api';

const TopLikedPost = () => {
    const [topLikedPosts, setTopLikedPosts] = useState([]);
    const [error, setError] = useState(null);
    const [loading, setLoading] = useState(true);
    const [timeRange, setTimeRange] = useState("ALL_TIME");
    useEffect(() => {
        const fetchTopLikedPosts = async () => {
            try {
                const res = await api.get(`/posts/top-liked?range=${timeRange}`);
                setTopLikedPosts(res.data);
            } catch (err) {
                setError("Error fetching posts: ", err);
            } finally{
                setLoading(false);
            }
        }
        fetchTopLikedPosts();
    }, [timeRange]);
    if (loading) return <p>Loading posts...</p>
    if (error) return <p>{error}</p>
    return (
        <div className='mostLikedPostsSection'>
            <div className='mostLikedPostsTitleSection'>
                <h3>Top Liked Posts</h3>
                <div className="timeRangeDropdown">
                    <select
                        id='range'
                        value={timeRange}
                        onChange={(e) => setTimeRange(e.target.value)}
                    >
                        <option value="DAILY">Today</option>
                        <option value="WEEKLY">This Week</option>
                        <option value="MONTHLY">This Month</option>
                        <option value="YEARLY">This Year</option>
                        <option value="ALL_TIME">All Time</option>
                    </select>
                </div>
            </div>
            <div className="topLikedList">
                {topLikedPosts.map((post, idx) => (
                    <div className="topLikedItem" key={post.id}>
                        <span className="rank">{idx + 1}</span>

                        <div className="postMeta">
                            <Link to={`/${post.id}`} className="postTitleClickableLink">
                                {post.title}
                            </Link>
                            <span className="author">by {post.authorUsername}</span>
                        </div>

                        <div className="likesCount">
                            <FaHeart className="likedIcon" /> {post.likesCount}
                        </div>
                    </div>
                ))}
            </div>
        </div>
    )
}

export default TopLikedPost