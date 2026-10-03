import React, { useEffect, useState } from 'react';
import { useDispatch, useSelector } from 'react-redux';
import { fetchPosts } from '../../features/posts/postSlice';
import {
    selectPosts,
    selectCurrentPage,
    selectTotalPages,
    selectPageSize,
    selectPostsStatus,
    selectPostsError
} from '../../features/posts/postSelectors';
import HomePagePost from '../../components/posts/HomePagePost/HomePagePost';
import { Link, useSearchParams } from 'react-router-dom';
import TopLikedPost from '../../components/TopLikedPost/TopLikedPost';
import TopContributors from '../../components/TopContributors/TopContributors';
import PopularTags from '../../components/PopularTags/PopularTags';
import api from '../../api/api';
import './styles.scss';

const Home = () => {
    const dispatch = useDispatch();
    const [searchParams, setSearchParams] = useSearchParams();

    const tagsParam = searchParams.get('tags') || searchParams.get('tag');
    const tagMode = searchParams.get('tagMode') || 'ALL';

    const selectedTagSlugs = tagsParam
        ? tagsParam.split(',').map(s => s.trim()).filter(Boolean)
        : [];

    const posts = useSelector(selectPosts);
    const currentPage = useSelector(selectCurrentPage);
    const totalPages = useSelector(selectTotalPages);
    const pageSize = useSelector(selectPageSize);
    const status = useSelector(selectPostsStatus);
    const error = useSelector(selectPostsError);

    const [filterTags, setFilterTags] = useState([]);

    useEffect(() => {
        const fetchAvailableTags = async () => {
            try {
                const res = await api.get('/tags/popular?limit=8');
                setFilterTags(res.data);
            } catch (err) {
                console.error("Failed to load filter tags", err);
            }
        };
        fetchAvailableTags();
    }, []);

    useEffect(() => {
        dispatch(fetchPosts({ page: 0, size: pageSize, tags: selectedTagSlugs, tagMode }));
    }, [dispatch, tagsParam, tagMode]);

    const handleToggleTag = (slug) => {
        if (!slug) {
            setSearchParams({});
            return;
        }
        let updated;
        if (selectedTagSlugs.includes(slug)) {
            updated = selectedTagSlugs.filter(s => s !== slug);
        } else {
            updated = [...selectedTagSlugs, slug];
        }

        if (updated.length === 0) {
            setSearchParams({});
        } else {
            setSearchParams({ tags: updated.join(','), tagMode });
        }
    };

    const handleModeChange = (newMode) => {
        if (selectedTagSlugs.length > 0) {
            setSearchParams({ tags: selectedTagSlugs.join(','), tagMode: newMode });
        }
    };

    const handleClearFilters = () => {
        setSearchParams({});
    };

    const handlePageChange = (pageIndex) => {
        if (pageIndex >= 0 && pageIndex < totalPages && pageIndex !== currentPage) {
            dispatch(fetchPosts({ page: pageIndex, size: pageSize, tags: selectedTagSlugs, tagMode }));
            window.scrollTo({ top: 0, behavior: 'smooth' });
        }
    };

    const renderPageNumbers = () => {
        if (totalPages <= 1) return null;

        const pages = [];
        const maxPagesToShow = 5;
        let startPage = Math.max(0, currentPage - 2);
        let endPage = Math.min(totalPages - 1, startPage + maxPagesToShow - 1);

        if (endPage - startPage + 1 < maxPagesToShow) {
            startPage = Math.max(0, endPage - maxPagesToShow + 1);
        }

        if (startPage > 0) {
            pages.push(
                <button key={0} onClick={() => handlePageChange(0)} className="paginationNumBtn">
                    1
                </button>
            );
            if (startPage > 1) {
                pages.push(<span key="start-ellipsis" className="paginationEllipsis">...</span>);
            }
        }

        for (let i = startPage; i <= endPage; i++) {
            pages.push(
                <button
                    key={i}
                    onClick={() => handlePageChange(i)}
                    className={`paginationNumBtn ${i === currentPage ? 'active' : ''}`}
                >
                    {i + 1}
                </button>
            );
        }

        if (endPage < totalPages - 1) {
            if (endPage < totalPages - 2) {
                pages.push(<span key="end-ellipsis" className="paginationEllipsis">...</span>);
            }
            pages.push(
                <button key={totalPages - 1} onClick={() => handlePageChange(totalPages - 1)} className="paginationNumBtn">
                    {totalPages}
                </button>
            );
        }

        return pages;
    };

    return (
        <div className='homePageContainer'>
            <div className='homePageLayout'>
                <div className='homePageFeedPosts'>
                    <div className="multiTagFilterContainer">
                        <div className="tagFilterBar">
                            <button
                                className={`tagFilterPill ${selectedTagSlugs.length === 0 ? 'active' : ''}`}
                                onClick={handleClearFilters}
                            >
                                All Posts
                            </button>
                            {filterTags.map((tag) => {
                                const isSelected = selectedTagSlugs.includes(tag.slug);
                                return (
                                    <button
                                        key={tag.id}
                                        className={`tagFilterPill ${isSelected ? 'active' : ''}`}
                                        onClick={() => handleToggleTag(tag.slug)}
                                    >
                                        #{tag.name} {isSelected && '✓'}
                                    </button>
                                );
                            })}
                            {selectedTagSlugs.length > 0 && (
                                <button className="clearFilterBtn" onClick={handleClearFilters}>
                                    Clear filters ✕
                                </button>
                            )}
                        </div>

                        {selectedTagSlugs.length > 0 && (
                            <div className="tagModeBar">
                                <span className="modeTitle">Match mode:</span>
                                <div className="modeOptions">
                                    <label className={`modeRadioLabel ${tagMode === 'ALL' ? 'selected' : ''}`}>
                                        <input
                                            type="radio"
                                            name="tagMode"
                                            value="ALL"
                                            checked={tagMode === 'ALL'}
                                            onChange={() => handleModeChange('ALL')}
                                        />
                                        All selected tags (AND)
                                    </label>
                                    <label className={`modeRadioLabel ${tagMode === 'ANY' ? 'selected' : ''}`}>
                                        <input
                                            type="radio"
                                            name="tagMode"
                                            value="ANY"
                                            checked={tagMode === 'ANY'}
                                            onChange={() => handleModeChange('ANY')}
                                        />
                                        Any selected tag (OR)
                                    </label>
                                </div>
                            </div>
                        )}
                    </div>

                    {status === 'loading' && (
                        <div className="postsLoadingState">
                            <p>Loading posts...</p>
                        </div>
                    )}

                    {status === 'failed' && (
                        <div className="postsErrorState">
                            <p>Error: {error}</p>
                            <button onClick={() => dispatch(fetchPosts({ page: currentPage, size: pageSize, tags: selectedTagSlugs, tagMode }))} className="retryBtn">
                                Retry
                            </button>
                        </div>
                    )}

                    {status !== 'loading' && status !== 'failed' && posts.length === 0 && (
                        <div className="postsEmptyState">
                            <p>{selectedTagSlugs.length > 0 ? `No posts found matching the selected tag filters.` : 'No posts available.'}</p>
                        </div>
                    )}

                    {status !== 'failed' && posts.map(post => (
                        <Link key={post.id} to={`/${post.id}`}>
                            <HomePagePost post={post} onSelectTag={handleToggleTag} />
                        </Link>
                    ))}

                    {totalPages > 1 && (
                        <div className="paginationContainer">
                            <button
                                className="paginationBtn"
                                disabled={currentPage === 0 || status === 'loading'}
                                onClick={() => handlePageChange(currentPage - 1)}
                            >
                                Previous
                            </button>

                            <div className="paginationPages">
                                {renderPageNumbers()}
                            </div>

                            <button
                                className="paginationBtn"
                                disabled={currentPage >= totalPages - 1 || status === 'loading'}
                                onClick={() => handlePageChange(currentPage + 1)}
                            >
                                Next
                            </button>
                        </div>
                    )}
                </div>
                <div className='hallOfFameSection'>
                    <PopularTags
                        selectedTagSlugs={selectedTagSlugs}
                        onSelectTag={handleToggleTag}
                    />
                    <TopLikedPost />
                    <TopContributors />
                </div>
            </div>
        </div>
    );
};

export default Home;