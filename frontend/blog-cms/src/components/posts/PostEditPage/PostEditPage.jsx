import React, { useState, useEffect } from 'react';
import api from '../../../api/api';
import "./styles.scss";
import { useNavigate, useParams, Link } from "react-router-dom";
import { useSelector } from 'react-redux';
import { selectCurrentUser, selectAuthLoading } from '../../../features/auth/authSelectors';
import ReactQuill from 'react-quill-new';
import 'react-quill-new/dist/quill.snow.css';
import { toast } from 'react-toastify';

const quillModules = {
    toolbar: [
        [{ 'header': [1, 2, 3, false] }],
        ['bold', 'italic', 'underline', 'strike'],
        [{ 'align': [] }],
        ['link'],
        ['clean']
    ]
};

const quillFormats = [
    'header', 'bold', 'italic', 'underline', 'strike', 'align', 'link'
];

const PostEditPage = () => {
    const { postId } = useParams();
    const navigate = useNavigate();
    const currentUser = useSelector(selectCurrentUser);
    const authLoading = useSelector(selectAuthLoading);
    const [title, setTitle] = useState("");
    const [content, setContent] = useState("");
    const [availableTags, setAvailableTags] = useState([]);
    const [selectedTagIds, setSelectedTagIds] = useState([]);
    const [loading, setLoading] = useState(true);
    const [saving, setSaving] = useState(false);
    const [error, setError] = useState(null);
    const [unauthorized, setUnauthorized] = useState(false);

    useEffect(() => {
        if (authLoading) return;

        if (!currentUser) {
            setUnauthorized(true);
            setLoading(false);
            return;
        }

        const fetchPostAndTags = async () => {
            try {
                const [postRes, tagsRes] = await Promise.all([
                    api.get(`/posts/${postId}`),
                    api.get('/tags')
                ]);

                if (!postRes.data.author || postRes.data.author.id !== currentUser.id) {
                    setUnauthorized(true);
                } else {
                    setTitle(postRes.data.title);
                    setContent(postRes.data.content);
                    setAvailableTags(tagsRes.data || []);
                    if (postRes.data.tags) {
                        setSelectedTagIds(postRes.data.tags.map(t => t.id));
                    }
                }
            } catch (err) {
                setError("Failed to load post");
            } finally {
                setLoading(false);
            }
        };
        fetchPostAndTags();
    }, [postId, currentUser, authLoading]);

    const toggleTag = (tagId) => {
        if (selectedTagIds.includes(tagId)) {
            setSelectedTagIds(prev => prev.filter(id => id !== tagId));
        } else {
            if (selectedTagIds.length >= 5) {
                toast.warning("A post can have a maximum of 5 tags.");
                return;
            }
            setSelectedTagIds(prev => [...prev, tagId]);
        }
    };

    const handleSave = async () => {
        const plainText = content.replace(/<[^>]*>/g, '').trim();
        if (!title.trim() || !plainText) {
            const msg = "Title and content cannot be empty";
            setError(msg);
            toast.error(msg);
            return;
        }

        try {
            setSaving(true);
            await api.put(`/posts/${postId}`, {
                title,
                content,
                tagIds: selectedTagIds
            });
            toast.success("Post updated successfully!");
            navigate(`/${postId}`);
        } catch (err) {
            const errorMsg = "Failed to save changes. You may not have permission.";
            setError(errorMsg);
            toast.error(errorMsg);
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
                    <input
                        className="editTitleInput"
                        value={title}
                        onChange={(e) => setTitle(e.target.value)}
                        placeholder="Post title"
                    />

                    <div className="inputSection" style={{ marginBottom: '1.25rem' }}>
                        <label className="formLabel" style={{ display: 'block', marginBottom: '0.4rem', fontWeight: 600, fontSize: '0.9rem', color: '#4b5563' }}>
                            Tags (select up to 5)
                        </label>
                        <div className="tagsSelectionContainer" style={{ display: 'flex', flexWrap: 'wrap', gap: '0.5rem' }}>
                            {availableTags.map((tag) => {
                                const isSelected = selectedTagIds.includes(tag.id);
                                return (
                                    <button
                                        type="button"
                                        key={tag.id}
                                        className={`tagBadgeSelect ${isSelected ? 'selected' : ''}`}
                                        onClick={() => toggleTag(tag.id)}
                                        style={{
                                            backgroundColor: isSelected ? '#E93F00' : '#f1f5f9',
                                            color: isSelected ? '#ffffff' : '#475569',
                                            border: `1px solid ${isSelected ? '#E93F00' : '#cbd5e1'}`,
                                            borderRadius: '20px',
                                            padding: '0.35rem 0.75rem',
                                            fontSize: '0.85rem',
                                            fontWeight: 500,
                                            cursor: 'pointer'
                                        }}
                                    >
                                        #{tag.name} {isSelected ? '✓' : '+'}
                                    </button>
                                );
                            })}
                        </div>
                    </div>

                    <div className="quillEditorWrapper">
                        <ReactQuill
                            theme="snow"
                            value={content}
                            onChange={setContent}
                            modules={quillModules}
                            formats={quillFormats}
                            placeholder="Write your post..."
                        />
                    </div>
                </div>
            </div>
        </div>
    );
};

export default PostEditPage;