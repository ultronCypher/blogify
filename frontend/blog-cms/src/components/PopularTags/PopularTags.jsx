import React, { useState, useEffect } from 'react';
import { FiTag } from 'react-icons/fi';
import api from '../../api/api';
import './styles.scss';

const PopularTags = ({ activeTagSlug, selectedTagSlugs = [], onSelectTag }) => {
    const [tags, setTags] = useState([]);
    const [loading, setLoading] = useState(true);
    const [error, setError] = useState(null);

    useEffect(() => {
        const fetchPopularTags = async () => {
            try {
                const res = await api.get('/tags/popular?limit=10');
                setTags(res.data);
            } catch (err) {
                setError("Failed to load popular tags");
            } finally {
                setLoading(false);
            }
        };
        fetchPopularTags();
    }, []);

    if (loading) return <div className="popularTagsSection"><p className="sidebarStatusText">Loading tags...</p></div>;
    if (error || tags.length === 0) return null;

    const activeSlugs = Array.isArray(selectedTagSlugs) && selectedTagSlugs.length > 0
        ? selectedTagSlugs
        : (activeTagSlug ? [activeTagSlug] : []);

    return (
        <div className="popularTagsSection">
            <div className="popularTagsTitleSection">
                <div className="titleWithIcon">
                    <FiTag className="sectionIcon" />
                    <h3>Popular Tags</h3>
                </div>
            </div>

            <div className="popularTagsList">
                {tags.map((tag) => {
                    const isActive = activeSlugs.includes(tag.slug);
                    const count = tag.postCount ?? tag.count ?? 0;
                    return (
                        <button
                            key={tag.id}
                            className={`popularTagItem ${isActive ? 'active' : ''}`}
                            onClick={() => onSelectTag && onSelectTag(tag.slug)}
                        >
                            <span className="tagName">#{tag.name}</span>
                            <span className="tagCount">{count} {count === 1 ? 'post' : 'posts'}</span>
                        </button>
                    );
                })}
            </div>
        </div>
    );
};

export default PopularTags;
