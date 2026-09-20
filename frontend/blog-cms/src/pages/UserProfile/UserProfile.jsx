import React from 'react';
import { useParams, Navigate } from 'react-router-dom';
import { useSelector } from 'react-redux';
import { selectCurrentUser, selectAuthLoading } from '../../features/auth/authSelectors';
import ProfileView from '../../components/users/ProfileView/ProfileView';

const UserProfile = () => {
    const { userId } = useParams();
    const authUser = useSelector(selectCurrentUser);
    const authLoading = useSelector(selectAuthLoading);

    if (!authLoading && authUser && Number(userId) === authUser.id) {
        return <Navigate to="/profile/me" replace />;
    }

    return (
        <div>
            <ProfileView userId={userId} isMe={false} />
        </div>
    );
};

export default UserProfile;