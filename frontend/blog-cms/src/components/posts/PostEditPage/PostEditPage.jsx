import React, { useState, useEffect } from 'react'
import api from '../../../api/api'
import "./styles.scss"
import { useNavigate, useParams, Link } from "react-router-dom";
import { useAuth } from '../../../context/AuthContext';

const PostEditPage = () => {
    const { postId } = useParams();
    const navigate = useNavigate();
    const { user: currentUser, loading: authLoading } = useAuth();
    const [title, setTitle] = useState("");
    const [content, setContent] = useState("");
    const [loading, setLoading] = useState(true);
    const [saving, setSaving] = useState(false);
    const [error, setError] = useState(null);
    const [unauthorized, setUnauthorized] = useState(false);

    const autoGrow = (e) => {
        e.target.style.height = "auto";
        e.target.style.height = e.target.scrollHeight + "px";
    };

    useEffect(() => {
        if (authLoading) return;

        if (!currentUser) {
            setUnauthorized(true);
            setLoading(false);
            return;
        }

        const fetchPost = async () => {
            try {
                const res = await api.get(`/posts/${postId}`);
                if (!res.data.author || res.data.author.id !== currentUser.id) {
                    setUnauthorized(true);
                } else {
                    setTitle(res.data.title);
                    setContent(res.data.content);
                }
            } catch (err) {
                setError("Failed to load post");
            } finally {
                setLoading(false);
            }
        };
        fetchPost();
    }, [postId, currentUser, authLoading]);

    const handleSave = async () => {
        if (!title.trim() || !content.trim()) {
            setError("Title and content cannot be empty");
            return;
        }

        try {
            setSaving(true);
            await api.put(`/posts/${postId}`, {
                title,
                content,
            });
            navigate(`/${postId}`);
        } catch (err) {
            setError("Failed to save changes. You may not have permission.");
        } finally {
            setSaving(false);
        }
    };

    if (loading || authLoading) return <p className="loadingText">Loading editor...</p>;
    
    if (unauthorized) {
        return (
            <div className="postEditPageContainer">
                <div className="editCard">
                    <h2>Access Denied</h2>
                    <p style={{ margin: "1rem 0", color: "#6b7280" }}>
                        You do not have permission to edit this post.
                    </p>
                    <Link to={`/${postId}`} className="backLink">
                        ← Back to post
                    </Link>
                </div>
            </div>
        );
    }

    if (error) return <p className="errorText">{error}</p>;

    return (
        <div className="postEditPageContainer">
            <div className="editCard">
                <div className="editHeader">
                    <Link to={`/${postId}`} className="backLink">
                        ← Back to post
                    </Link>

                    <button
                        className="saveButton"
                        onClick={handleSave}
                        disabled={saving}
                    >
                        {saving ? "Saving..." : "Save"}
                    </button>
                </div>
                <div className="editForm">
                    <textarea
                        className="editTitle"
                        value={title}
                        onChange={
                            (e) => {
                                setTitle(e.target.value);
                                autoGrow(e);
                            }
                        }
                        placeholder="Post title"
                        rows={2}
                    />
                    <textarea
                        className="editContent"
                        value={content}
                        onChange={
                            (e) => {
                                setContent(e.target.value);
                                autoGrow(e);
                            }
                        }
                        placeholder="Write your post..."
                        rows={14}
                    />
                </div>
            </div>
        </div>
    )
}

export default PostEditPage