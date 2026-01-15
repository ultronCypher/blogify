import React from 'react'
import Navbar from '../common/Navbar/Navbar'
import { Toaster } from 'react-hot-toast';
const AppLayout = ({ children }) => {
    return (
        <>
            <Navbar />
            <Toaster position="top-center" reverseOrder={false} />
            <main className="pageContainer">
                {children}
            </main>
        </>
    )
}

export default AppLayout