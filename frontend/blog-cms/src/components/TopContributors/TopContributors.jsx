import React, { useState, useEffect } from 'react'
import api from '../../api/api';
import { Link } from 'react-router-dom';
import { FaUserCircle } from 'react-icons/fa';
import './styles.scss'

const TopContributors = () => {
    const [topContributors, setTopContributors] = useState([]);
    const [error, setError] = useState(null);
    const [loading, setLoading] = useState(true);

    useEffect(() => {
        const fetchTopContributors = async () => {
            try {
                const res = await api.get("/posts/contributors/top");
                setTopContributors(res.data);
            } catch (err) {
                console.error(err);
                setError("Failed to load top contributors");
            } finally {
                setLoading(false);
            }
        }
        fetchTopContributors();
    }, []);

    return (
        <div className='topContributorsSection'>
            <div className='topContributorsTitleSection'>
                <h3>Top Contributors</h3>
            </div>
            {loading ? (
                <div className="topContributorsState"><p>Loading contributors...</p></div>
            ) : error ? (
                <div className="topContributorsState error"><p>{error}</p></div>
            ) : topContributors.length === 0 ? (
                <div className="topContributorsState"><p>No contributors found.</p></div>
            ) : (
                <div className="topLikedList">
                    {topContributors.map((contributor, idx) => (
                        <div className="topLikedItem" key={contributor.userId}>
                            <div className="contributorLayout">
                                <p className="rank">{idx + 1}</p>
                                <Link to={`/users/${contributor.userId}`} className="contributorClickableLink">
                                    <div className="contributorAvatar">
                                        {contributor.avatarUrl ? (
                                            <img src={contributor.avatarUrl} alt={contributor.username} />
                                        ) : (
                                            <FaUserCircle size={28} color="#94a3b8" />
                                        )}
                                    </div>
                                    <p className="contributorUsername">{contributor.username}</p>
                                </Link>
                            </div>

                            <div className="contributedPostsCount" title="Total Posts">
                                {contributor.postCount} {contributor.postCount === 1 ? 'post' : 'posts'}
                            </div>
                        </div>
                    ))}
                </div>
            )}
        </div>
    )
}

export default TopContributors;