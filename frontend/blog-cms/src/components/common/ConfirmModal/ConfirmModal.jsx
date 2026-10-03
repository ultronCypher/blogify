import React, { useEffect } from 'react';
import { FiAlertTriangle } from 'react-icons/fi';
import './styles.scss';

const ConfirmModal = ({
    isOpen,
    title = "Delete Post?",
    message = "Are you sure you want to delete this post? This action cannot be undone.",
    confirmText = "Delete",
    cancelText = "Cancel",
    onConfirm,
    onCancel,
    loading = false
}) => {
    useEffect(() => {
        const handleKeyDown = (e) => {
            if (e.key === 'Escape' && isOpen && !loading) {
                onCancel();
            }
        };
        document.addEventListener('keydown', handleKeyDown);
        return () => document.removeEventListener('keydown', handleKeyDown);
    }, [isOpen, loading, onCancel]);

    if (!isOpen) return null;

    return (
        <div className="confirmModalOverlay" onClick={() => !loading && onCancel()}>
            <div className="confirmModalCard" onClick={(e) => e.stopPropagation()}>
                <div className="confirmModalHeader">
                    <div className="warningIconWrapper">
                        <FiAlertTriangle className="warningIcon" />
                    </div>
                    <h3>{title}</h3>
                </div>
                <p className="confirmModalMessage">{message}</p>

                <div className="confirmModalActions">
                    <button
                        type="button"
                        className="cancelBtn"
                        onClick={onCancel}
                        disabled={loading}
                    >
                        {cancelText}
                    </button>
                    <button
                        type="button"
                        className="dangerBtn"
                        onClick={onConfirm}
                        disabled={loading}
                    >
                        {loading ? "Deleting..." : confirmText}
                    </button>
                </div>
            </div>
        </div>
    );
};

export default ConfirmModal;
