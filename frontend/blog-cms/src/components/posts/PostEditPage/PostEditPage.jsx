import React, { useState, useEffect } from 'react'
import api from '../../../api/api'
import "./styles.scss"
import { useNavigate, useParams, Link } from "react-router-dom";

const PostEditPage = () => {
    const { postId } = useParams();
    const navigate = useNavigate();
    const [title, setTitle] = useState("");
    const [content, setContent] = useState("");
    const [loading, setLoading] = useState(true);
    const [saving, setSaving] = useState(false);
    const [error, setError] = useState(null);
    const autoGrow = (e) => {
        e.target.style.height = "auto";
        e.target.style.height = e.target.scrollHeight + "px";
    };

    useEffect(() => {
        const fetchPost = async () => {
            try {
                const res = await api.get(`/posts/${postId}`);
                setTitle(res.data.title);
                setContent(res.data.content);
            } catch (err) {
                setError("Failed to load post");
            } finally {
                setLoading(false);
            }
        };
        fetchPost();
    }, [postId]);
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
            navigate(`/posts/${postId}`);
        } catch (err) {
            setError("Failed to save changes");
        } finally {
            setSaving(false);
        }
    };

    if (loading) return <p>Loading editor...</p>;
    if (error) return <p className="errorText">{error}</p>;
    return (
        <div className="postEditPageContainer">
            <div className="editCard">
                <div className="editHeader">
                    <Link to={`/posts/${postId}`} className="backLink">
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