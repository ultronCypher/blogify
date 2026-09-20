import { useState } from 'react';
import { BrowserRouter, Routes, Route } from "react-router-dom";
import Navbar from './components/common/Navbar/Navbar';
import Home from './pages/Home/Home';
import Login from './pages/Login/Login';
import Register from './pages/Register/Register';
import WritingPad from './pages/WritingPad/WritingPad';
import MyProfile from './pages/MyProfile/MyProfile';
import AppLayout from './components/layouts/AppLayout';
import './App.css'
import PostDetail from './components/posts/PostDetail/PostDetail';
import UserProfile from './pages/UserProfile/UserProfile';
import PostEditPage from './components/posts/PostEditPage/PostEditPage';

function App() {
  return (
    <BrowserRouter>
      <AppLayout>
        <Routes>
          <Route path="/" element={<Home />} />
          <Route path="/login" element={<Login />} />
          <Route path="/register" element={<Register />} />
          <Route path="/request" element={<WritingPad />} />
          {/* <Route path="/request" element={<WritingPad />} /> */}
          <Route path="/:postId" element={<PostDetail />} />
          <Route path="/profile/me" element={<MyProfile />} />
          <Route path="/users/:userId" element={<UserProfile />} />
          <Route path="/posts/:postId/edit" element={<PostEditPage />} />

        </Routes>
      </AppLayout>
    </BrowserRouter>
  )
}

export default App
