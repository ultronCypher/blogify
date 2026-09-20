import React, { useEffect } from 'react';
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
import { Link } from 'react-router-dom';
import TopLikedPost from '../../components/TopLikedPost/TopLikedPost';
import TopContributors from '../../components/TopContributors/TopContributors';
import './styles.scss';

const Home = () => {
    const dispatch = useDispatch();
    const posts = useSelector(selectPosts);
    const currentPage = useSelector(selectCurrentPage);
    const totalPages = useSelector(selectTotalPages);
    const pageSize = useSelector(selectPageSize);
    const status = useSelector(selectPostsStatus);
    const error = useSelector(selectPostsError);

    useEffect(() => {
        dispatch(fetchPosts({ page: 0, size: 10 }));
    }, [dispatch]);

    const handlePageChange = (pageIndex) => {
        if (pageIndex >= 0 && pageIndex < totalPages && pageIndex !== currentPage) {
            dispatch(fetchPosts({ page: pageIndex, size: pageSize }));
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
                    {status === 'loading' && (
                        <div className="postsLoadingState">
                            <p>Loading posts...</p>
                        </div>
                    )}

                    {status === 'failed' && (
                        <div className="postsErrorState">
                            <p>Error: {error}</p>
                            <button onClick={() => dispatch(fetchPosts({ page: currentPage, size: pageSize }))} className="retryBtn">
                                Retry
                            </button>
                        </div>
                    )}

                    {status !== 'loading' && status !== 'failed' && posts.length === 0 && (
                        <div className="postsEmptyState">
                            <p>No posts available.</p>
                        </div>
                    )}

                    {status !== 'failed' && posts.map(post => (
                        <Link key={post.id} to={`/${post.id}`}>
                            <HomePagePost post={post} />
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
                    <TopLikedPost />
                    <TopContributors />
                </div>
            </div>
        </div>
    );
};

export default Home;