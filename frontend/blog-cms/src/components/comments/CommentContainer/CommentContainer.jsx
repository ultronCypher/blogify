import React from 'react'
import './styles.scss'
const CommentContainer = ({ comment }) => {
    return (
        <div className='commentBox'>
            <div>
                <h3>{comment?.authorUsername}</h3>
                <p className="postDate">
                    {new Date(comment.createdAt).toLocaleDateString("en-IN", {
                        day: "numeric",
                        month: "long",
                        year: "numeric",
                    })}
                </p>
            </div>
            <p>{comment.content}</p>
        </div>
    )
}

export default CommentContainer