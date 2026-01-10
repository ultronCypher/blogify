import { useState } from 'react';
import { BrowserRouter, Routes, Route } from "react-router-dom";
import Navbar from './components/common/Navbar/Navbar';
import Home from './pages/Home/Home';
import Login from './pages/Login/Login';
import Register from './pages/Register/Register';
import WritingPad from './pages/WritingPad/WritingPad';
import './App.css'
import PostDetail from './components/posts/PostDetail/PostDetail';
function App() {
  return (
    <BrowserRouter>
      <Navbar />
      <main className="pageContainer">
        <Routes>
          <Route path="/" element={<Home />} />
          <Route path="/login" element={<Login />} />
          <Route path="/register" element={<Register />} />
          <Route path="/request" element={<WritingPad />} />
          <Route path="/request" element={<WritingPad />} />
          <Route path="/:postId" element={<PostDetail />} />
        </Routes>
      </main>
    </BrowserRouter>
  )
}

export default App
