import { Link } from 'react-router-dom'
import { getPreviewImage } from '../../../utils/cloudinary' 
import { FaHeart, FaRegHeart, FaEye, FaComment } from 'react-icons/fa'
import './styles.scss'
const ProfilePostsBody = ({ posts, onToggleLike }) => {
    if (posts.length === 0) {
        return <p className="emptyText">No posts yet</p>
    }
    return (
        <div className="homePageFeedPosts">
            {posts.map(post => (
                <Link key={post.id} to={`/${post.id}`}>
                    <div className="postPreview">
                        <h2>{post.title}</h2>

                        <div className="imageContainer">
                            {post.previewImage && (
                                <img
                                    src={getPreviewImage(post.previewImage)}
                                    alt={post.title}
                                    loading="lazy"
                                    className="postImage"
                                />
                            )}
                        </div>

                        <p className="excerpt">{post.excerpt} ...</p>

                        <div className="postStats">
                            <span className="stat">
                                <FaEye /> {post.viewsCount}
                            </span>

                            <span
                                className="stat like"
                                onClick={(e) => {
                                    e.preventDefault()
                                    e.stopPropagation()
                                    onToggleLike(post.id)
                                }}
                            >
                                <span className="likeIconWrapper">
                                    {post.didUserLike
                                        ? <FaHeart className="likedIcon" />
                                        : <FaRegHeart className="likeIcon" />
                                    }
                                </span>
                                <span className="likeCount">{post.likesCount}</span>
                            </span>

                            <span className="stat">
                                <FaComment /> {post.commentsCount}
                            </span>
                        </div>
                    </div>
                </Link>
            ))}
        </div>
    )
}

export default ProfilePostsBody
