import React, { useState } from 'react'
import './styles.scss'
import api from '../../api/api';
const WritingPad = () => {
    const [title, setTitle] = useState("");
    const [content, setContent] = useState("");
    const [images, setImages] = useState([]);
    const [error, setError] = useState(null);
    const [success, setSuccess] = useState(false)
    const handleImageChange = (e) => {
        const selected=Array.from(e.target.files);
        setImages((prev) => [...prev, ...selected]);
    };
    const removeImage = (indexToRemove) => {
        setImages((prev) => prev.filter((_, index) => index !== indexToRemove))
    }
    const handleSubmit = async (e) => {
        e.preventDefault();
        setError(null);
        try {
            const formData = new FormData();
            formData.append("title", title);
            formData.append("content", content);
            images.forEach((img) => {
                formData.append("images", img);
            });
            await api.post("/posts", formData, {
                headers: {
                    "Content-Type": "multipart/form-data",
                }
            })
            setSuccess(true);
            setTitle("");
            setContent("");
            setImages([]);
        } catch (err) {
            setError(err.response?.data?.message || "Failed to publish article")
        }
    }
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
                        <textarea
                            type="text"
                            placeholder="Write the content of the blog..."
                            value={content}
                            onChange={e => setContent(e.target.value)}
                            rows={10}
                            required
                        />
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
                    {images.length > 0 && (
                        <div className="imagePreviewGrid">
                            {images.map((img, index) => (
                                <div key={index} className="imagePreviewWrapper">
                                    <img
                                        key={index}
                                        src={URL.createObjectURL(img)}
                                        alt="preview"
                                        className="imagePreview"
                                    />
                                    <button
                                        type="button"
                                        className="removeImageButton"
                                        onClick={()=>removeImage(index)}
                                    >
                                        x
                                    </button>
                                </div>
                            ))}
                        </div>
                    )}

                    {error && <p className="error">{error}</p>}
                    {success && <p className="success">Account created successfully</p>}
                    <div className='inputSection'>
                        <button type="submit" className='createPostButton'>Create</button>
                    </div>
                </form>
            </div>
        </div>
    )
}

export default WritingPad