import React from 'react';
import Navigation from './Navigation';
import Footer from './Footer';
import { Outlet } from 'react-router-dom';

export default function Layout() {
    return (
        <div className="page-wrapper">
            <Navigation />
            <main className="main-content">
                <Outlet />
            </main>
            <Footer />
        </div>
    );
}
