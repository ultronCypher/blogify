import React, { useState, useEffect, useRef } from 'react';
import { useNavigate } from 'react-router-dom';
import { FiSearch, FiX, FiFileText, FiUser, FiLoader } from 'react-icons/fi';
import api from '../../../api/api';
import './styles.scss';

const GlobalSearch = () => {
    const [query, setQuery] = useState('');
    const [results, setResults] = useState({ posts: [], users: [] });
    const [loading, setLoading] = useState(false);
    const [error, setError] = useState(null);
    const [isOpen, setIsOpen] = useState(false);

    const searchRef = useRef(null);
    const navigate = useNavigate();

    useEffect(() => {
        const cleanQuery = query.trim();
        if (cleanQuery.length < 2) {
            setResults({ posts: [], users: [] });
            setIsOpen(false);
            setLoading(false);
            return;
        }

        const controller = new AbortController();
        setLoading(true);
        setError(null);

        const timer = setTimeout(async () => {
            try {
                const res = await api.get(`/search?q=${encodeURIComponent(cleanQuery)}&limit=5`, {
                    signal: controller.signal
                });
                setResults(res.data);
                setIsOpen(true);
            } catch (err) {
                if (err.name !== 'CanceledError' && err.code !== 'ERR_CANCELED') {
                    setError('Failed to fetch search results');
                    setIsOpen(true);
                }
            } finally {
                setLoading(false);
            }
        }, 300);

        return () => {
            clearTimeout(timer);
            controller.abort();
        };
    }, [query]);

    useEffect(() => {
        const handleClickOutside = (e) => {
            if (searchRef.current && !searchRef.current.contains(e.target)) {
                setIsOpen(false);
            }
        };

        const handleKeyDown = (e) => {
            if (e.key === 'Escape') {
                setIsOpen(false);
            }
        };

        document.addEventListener('mousedown', handleClickOutside);
        document.addEventListener('keydown', handleKeyDown);

        return () => {
            document.removeEventListener('mousedown', handleClickOutside);
            document.removeEventListener('keydown', handleKeyDown);
        };
    }, []);

    const handleSelectPost = (postId) => {
        setIsOpen(false);
        setQuery('');
        navigate(`/${postId}`);
    };

    const handleSelectUser = (userId) => {
        setIsOpen(false);
        setQuery('');
        navigate(`/users/${userId}`);
    };

    const hasResults = results.posts.length > 0 || results.users.length > 0;

    return (
        <div className="globalSearchContainer" ref={searchRef}>
            <div className="searchInputWrapper">
                <FiSearch className="searchIcon" />
                <input
                    type="text"
                    className="searchInput"
                    placeholder="Search posts, authors..."
                    value={query}
                    onChange={(e) => setQuery(e.target.value)}
                    onFocus={() => {
                        if (query.trim().length >= 2) setIsOpen(true);
                    }}
                />
                {loading ? (
                    <FiLoader className="searchSpinner" />
                ) : query ? (
                    <button className="clearSearchBtn" onClick={() => setQuery('')}>
                        <FiX />
                    </button>
                ) : null}
            </div>

            {isOpen && (
                <div className="searchDropdown">
                    {loading && (
                        <div className="searchStateMessage">
                            <FiLoader className="searchSpinnerInline" />
                            <span>Searching Blogify...</span>
                        </div>
                    )}

                    {!loading && error && (
                        <div className="searchStateMessage searchError">
                            <span>{error}</span>
                        </div>
                    )}

                    {!loading && !error && !hasResults && (
                        <div className="searchStateMessage">
                            <span>No matching posts or users found for "{query}"</span>
                        </div>
                    )}

                    {!loading && !error && hasResults && (
                        <div className="searchResultsList">
                            {results.posts.length > 0 && (
                                <div className="searchSection">
                                    <div className="sectionHeader">POSTS</div>
                                    <div className="sectionItems">
                                        {results.posts.map((post) => (
                                            <div
                                                key={post.id}
                                                className="searchResultItem"
                                                onClick={() => handleSelectPost(post.id)}
                                            >
                                                <div className="itemIconWrapper">
                                                    {post.imageUrl ? (
                                                        <img src={post.imageUrl} alt="" className="itemThumb" />
                                                    ) : (
                                                        <FiFileText className="itemDefaultIcon" />
                                                    )}
                                                </div>
                                                <div className="itemContent">
                                                    <span className="itemTitle">{post.title}</span>
                                                    <span className="itemSub">by {post.authorName}</span>
                                                </div>
                                                <span className="entityBadge postBadge">Post</span>
                                            </div>
                                        ))}
                                    </div>
                                </div>
                            )}

                            {results.users.length > 0 && (
                                <div className="searchSection">
                                    <div className="sectionHeader">USERS</div>
                                    <div className="sectionItems">
                                        {results.users.map((user) => (
                                            <div
                                                key={user.id}
                                                className="searchResultItem"
                                                onClick={() => handleSelectUser(user.id)}
                                            >
                                                <div className="itemIconWrapper">
                                                    {user.avatarUrl ? (
                                                        <img src={user.avatarUrl} alt="" className="userAvatarThumb" />
                                                    ) : (
                                                        <FiUser className="itemDefaultIcon" />
                                                    )}
                                                </div>
                                                <div className="itemContent">
                                                    <span className="itemTitle">{user.username}</span>
                                                </div>
                                                <span className="entityBadge userBadge">User</span>
                                            </div>
                                        ))}
                                    </div>
                                </div>
                            )}
                        </div>
                    )}
                </div>
            )}
        </div>
    );
};

export default GlobalSearch;
