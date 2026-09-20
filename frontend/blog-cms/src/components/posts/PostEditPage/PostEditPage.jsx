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