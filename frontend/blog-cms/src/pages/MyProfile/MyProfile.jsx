import React, { useEffect, useState } from 'react'
import api from '../../api/api'
import ProfileHeader from '../../components/users/ProfileHeader/ProfileHeader';
const MyProfile = () => {
    const [user,setUser]=useState(null);
    useEffect(()=>{
        api.get("/users/me")
        .then(res=>setUser(res.data));
    },[]);
    return (
        <div>
            <ProfileHeader user={user} isMe />
        </div>
    )
}

export default MyProfile