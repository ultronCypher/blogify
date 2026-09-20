import React from 'react';
import ProfileView from '../../components/users/ProfileView/ProfileView';

const MyProfile = () => {
    return (
        <div>
            <ProfileView isMe={true} />
        </div>
    );
};

export default MyProfile;