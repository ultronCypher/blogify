import React, { useState, useEffect } from 'react'
import api from '../../api/api';
import { Link } from 'react-router-dom';
import './styles.scss'

const TopContributors = () => {
    const [topContributors, setTopContributors] = useState([]);
    const [error, setError] = useState(null);
    const [loading, setLoading] = useState(true);
    useEffect(() => {
        const fetchTopContributors = async () => {
            try {
                const res = api.get("/posts/contributors/top")
                setTopContributors((await res).data);
            } catch (err) {
                console.log(err);
            } finally {
                setLoading(false);
            }
        }
        fetchTopContributors();
    })
    return (
        <div className='topContributorsSection'>
            <div className='topContributorsTitleSection'>
                <h3>Top Contributors</h3>
            </div>
            <div className="topLikedList">
                {topContributors.map((contributor, idx) => (
                    <div className="topLikedItem" key={contributor.userId}>

                        <div className="contributorLayout ">
                            <p className="rank">{idx + 1}</p>
                            <Link to={`/users/${contributor.userId}`} className="contributorClickableLink">
                                <div className="contributorAvatar">
                                    <img src={contributor.avatarUrl} alt="" />
                                </div>
                                <p>{contributor.username}</p>
                            </Link>
                        </div>

                        <div className="contributedPostsCount">
                            {contributor.postCount}
                        </div>
                    </div>
                ))}
            </div>
        </div>
    )
}

export default TopContributors