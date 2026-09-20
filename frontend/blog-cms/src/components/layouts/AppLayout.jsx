import React from 'react'
import Navbar from '../common/Navbar/Navbar'
import { ToastContainer } from 'react-toastify';
import 'react-toastify/dist/ReactToastify.css';

const AppLayout = ({ children }) => {
    return (
        <>
            <Navbar />
            <ToastContainer position="top-right" autoClose={3000} hideProgressBar={false} newestOnTop closeOnClick pauseOnHover />
            <main className="pageContainer">
                {children}
            </main>
        </>
    )
}

export default AppLayout