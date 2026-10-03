import React, { useState, useEffect } from 'react';
import { useNavigate } from 'react-router-dom';
import ReactQuill from 'react-quill-new';
import 'react-quill-new/dist/quill.snow.css';
import { toast } from 'react-toastify';
import api from '../../api/api';
import './styles.scss';

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

const WritingPad = () => {
    const navigate = useNavigate();
    const [title, setTitle] = useState("");
    const [content, setContent] = useState("");
    const [images, setImages] = useState([]);
    const [availableTags, setAvailableTags] = useState([]);
    const [selectedTagIds, setSelectedTagIds] = useState([]);
    const [submitting, setSubmitting] = useState(false);
    const [error, setError] = useState(null);

    useEffect(() => {
        const fetchTags = async () => {
            try {
                const res = await api.get('/tags');
                setAvailableTags(res.data);
            } catch (err) {
                console.error("Failed to load tags", err);
            }
        };
        fetchTags();
    }, []);

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

    const handleImageChange = (e) => {
        const selected = Array.from(e.target.files);
        setImages((prev) => [...prev, ...selected]);
    };

    const removeImage = (indexToRemove) => {
        setImages((prev) => prev.filter((_, index) => index !== indexToRemove));
    };

    const handleSubmit = async (e) => {
        e.preventDefault();
        setError(null);

        // Strip empty tags if needed
        const plainText = content.replace(/<[^>]*>/g, '').trim();
        if (!title.trim() || !plainText) {
            const msg = "Title and content cannot be empty";
            setError(msg);
            toast.error(msg);
            return;
        }

        try {
            setSubmitting(true);
            const formData = new FormData();
            formData.append("title", title);
            formData.append("content", content);
            images.forEach((img) => {
                formData.append("images", img);
            });
            selectedTagIds.forEach((tagId) => {
                formData.append("tagIds", tagId);
            });

            await api.post("/posts", formData, {
                headers: {
                    "Content-Type": "multipart/form-data",
                }
            });

            toast.success("Post created successfully.");
            navigate("/");
        } catch (err) {
            const errorMsg = err.response?.data?.message || "Failed to publish article";
            setError(errorMsg);
            toast.error(errorMsg);
        } finally {
            setSubmitting(false);
        }
    };

    return (
        <div className='createPostContainer'>
            <div className='createPostTitle'>Write your Blog entry</div>
            <div className='registerForm'>
                <form onSubmit={handleSubmit}>
                    <div className='inputSection'>
                        <h2 className='formLabel'>Title</h2>
                        <input
                            type="text"
                            placeholder="Title for the post"
                            value={title}
                            onChange={e => setTitle(e.target.value)}
                            required
                        />
                    </div>

                    <div className='inputSection'>
                        <h2 className='formLabel'>Body</h2>
                        <div className="quillEditorWrapper">
                            <ReactQuill
                                theme="snow"
                                value={content}
                                onChange={setContent}
                                modules={quillModules}
                                formats={quillFormats}
                                placeholder="Write the content of the blog..."
                            />
                        </div>
                    </div>

                    <div className="inputSection">
                        <h2 className="formLabel">Images</h2>
                        <input
                            type="file"
                            accept="image/*"
                            multiple
                            onChange={handleImageChange}
                        />
                    </div>

                    <div className="inputSection">
                        <h2 className="formLabel">Tags (select up to 5)</h2>
                        <div className="tagsSelectionContainer">
                            {availableTags.map((tag) => {
                                const isSelected = selectedTagIds.includes(tag.id);
                                return (
                                    <button
                                        type="button"
                                        key={tag.id}
                                        className={`tagBadgeSelect ${isSelected ? 'selected' : ''}`}
                                        onClick={() => toggleTag(tag.id)}
                                    >
                                        #{tag.name} {isSelected ? '✓' : '+'}
                                    </button>
                                );
                            })}
                        </div>
                    </div>

                    {images.length > 0 && (
                        <div className="imagePreviewGrid">
                            {images.map((img, index) => (
                                <div key={index} className="imagePreviewWrapper">
                                    <img
                                        src={URL.createObjectURL(img)}
                                        alt="preview"
                                        className="imagePreview"
                                    />
                                    <button
                                        type="button"
                                        className="removeImageButton"
                                        onClick={() => removeImage(index)}
                                    >
                                        x
                                    </button>
                                </div>
                            ))}
                        </div>
                    )}

                    {error && <p className="error">{error}</p>}

                    <div className='inputSection'>
                        <button type="submit" disabled={submitting} className='createPostButton'>
                            {submitting ? "Publishing..." : "Create"}
                        </button>
                    </div>
                </form>
            </div>
        </div>
    );
};

export default WritingPad;