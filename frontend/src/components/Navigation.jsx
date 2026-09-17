import React, { useState, useEffect } from 'react';
import { Link, useLocation, useNavigate } from 'react-router-dom';
import { Menu, X, Rocket, Bell, User, LogOut, ChevronDown } from 'lucide-react';
import { useAuth } from '../context/AuthContext';
import api from '../lib/api';

export default function Navigation() {
    const [scrolled, setScrolled] = useState(false);
    const [mobileMenuOpen, setMobileMenuOpen] = useState(false);
    const [userMenuOpen, setUserMenuOpen] = useState(false);
    const [unreadCount, setUnreadCount] = useState(0);
    const location = useLocation();
    const navigate = useNavigate();
    const { user, session, signOut } = useAuth();

    useEffect(() => {
        const handleScroll = () => setScrolled(window.scrollY > 20);
        window.addEventListener('scroll', handleScroll);
        return () => window.removeEventListener('scroll', handleScroll);
    }, []);

    useEffect(() => {
        setMobileMenuOpen(false);
        setUserMenuOpen(false);
    }, [location.pathname]);

    // Fetch unread count if logged in
    useEffect(() => {
        if (session) {
            api.get('/api/v1/notifications/unread-count')
                .then(res => setUnreadCount(res.data?.unreadCount || 0))
                .catch(() => { });
        }
    }, [session, location.pathname]);

    const handleLogout = async () => {
        await signOut();
        navigate('/login');
    };

    const authNavLinks = [
        { name: 'Dashboard', path: '/dashboard' },
        { name: 'Opportunities', path: '/opportunities' },
        { name: 'Applications', path: '/applications' },
        { name: 'Reports', path: '/reports' },
    ];

    const publicNavLinks = [
        { name: 'Opportunities', path: '/opportunities' },
    ];

    const navLinks = session ? authNavLinks : publicNavLinks;

    return (
        <header className={`navbar flex-center ${scrolled ? 'scrolled' : ''}`}>
            <div className="container flex-between">
                <Link to="/" className="flex-center" style={{ gap: '0.5rem', textDecoration: 'none' }}>
                    <div className="flex-center" style={{ background: 'hsla(var(--primary-accent), 0.1)', padding: '0.5rem', borderRadius: 'var(--radius-md)' }}>
                        <Rocket color="hsl(var(--primary-accent))" size={24} />
                    </div>
                    <span style={{ fontFamily: 'var(--font-display)', fontWeight: 700, fontSize: '1.25rem', color: 'hsl(var(--text-primary))' }}>
                        Intern<span className="text-gradient">Track</span>
                    </span>
                </Link>

                <nav className="flex-center" style={{ gap: '1rem' }}>
                    <div className="desktop-only flex-center" style={{ gap: '0.25rem', marginRight: '1rem' }}>
                        {navLinks.map(link => (
                            <Link key={link.name} to={link.path} className={`nav-link ${location.pathname === link.path ? 'active' : ''}`}>
                                {link.name}
                            </Link>
                        ))}
                    </div>

                    {session ? (
                        <div className="desktop-only flex-center" style={{ gap: '0.75rem' }}>
                            {/* Notifications Bell */}
                            <Link to="/notifications" style={{ position: 'relative', display: 'inline-flex', color: 'hsl(var(--text-secondary))', padding: '0.5rem' }}>
                                <Bell size={22} />
                                {unreadCount > 0 && (
                                    <span style={{ position: 'absolute', top: '0', right: '0', background: 'hsl(var(--danger))', borderRadius: '50%', width: '18px', height: '18px', fontSize: '0.65rem', display: 'flex', alignItems: 'center', justifyContent: 'center', color: 'white', fontWeight: 700 }}>
                                        {unreadCount > 9 ? '9+' : unreadCount}
                                    </span>
                                )}
                            </Link>

                            {/* User Menu */}
                            <div style={{ position: 'relative' }}>
                                <button onClick={() => setUserMenuOpen(!userMenuOpen)} className="btn btn-ghost flex-center"
                                    style={{ gap: '0.5rem', padding: '0.5rem 0.75rem' }}>
                                    <div style={{ width: '28px', height: '28px', borderRadius: '50%', background: 'hsla(var(--primary-accent),0.2)', display: 'flex', alignItems: 'center', justifyContent: 'center' }}>
                                        <User size={16} color="hsl(var(--primary-accent))" />
                                    </div>
                                    <span style={{ fontSize: '0.875rem' }}>{user?.user_metadata?.full_name?.split(' ')[0] || 'Account'}</span>
                                    <ChevronDown size={16} />
                                </button>
                                {userMenuOpen && (
                                    <div className="glass-panel animate-fade-in" style={{ position: 'absolute', right: 0, top: 'calc(100% + 0.5rem)', minWidth: '200px', borderRadius: 'var(--radius-md)', padding: '0.5rem', zIndex: 100, boxShadow: 'var(--shadow-lg)' }}>
                                        <Link to="/profile" style={{ display: 'flex', alignItems: 'center', gap: '0.75rem', padding: '0.625rem 0.75rem', borderRadius: 'var(--radius-sm)', color: 'hsl(var(--text-primary))', textDecoration: 'none', fontSize: '0.875rem' }}>
                                            <User size={16} /> Profile & AI Tools
                                        </Link>
                                        <div style={{ height: '1px', background: 'var(--border-subtle)', margin: '0.25rem 0' }}></div>
                                        <button onClick={handleLogout} style={{ display: 'flex', alignItems: 'center', gap: '0.75rem', padding: '0.625rem 0.75rem', borderRadius: 'var(--radius-sm)', color: 'hsl(var(--danger))', background: 'none', border: 'none', cursor: 'pointer', width: '100%', fontSize: '0.875rem' }}>
                                            <LogOut size={16} /> Sign Out
                                        </button>
                                    </div>
                                )}
                            </div>
                        </div>
                    ) : (
                        <div className="desktop-only flex-center" style={{ gap: '0.75rem' }}>
                            <Link to="/login" className="btn btn-ghost">Log In</Link>
                            <Link to="/signup" className="btn btn-primary">Sign Up</Link>
                        </div>
                    )}

                    <button className="mobile-only btn btn-ghost" onClick={() => setMobileMenuOpen(!mobileMenuOpen)} style={{ padding: '0.5rem' }}>
                        {mobileMenuOpen ? <X size={24} /> : <Menu size={24} />}
                    </button>
                </nav>
            </div>

            {/* Mobile Menu */}
            {mobileMenuOpen && (
                <div className="mobile-menu glass-panel animate-fade-in" style={{ position: 'absolute', top: 'var(--header-height)', left: 0, width: '100%', padding: 'var(--space-4)', borderTop: '1px solid var(--glass-border)', display: 'flex', flexDirection: 'column', gap: '0.5rem', zIndex: 99 }}>
                    {navLinks.map(link => (
                        <Link key={link.name} to={link.path} className={`nav-link ${location.pathname === link.path ? 'active' : ''}`} onClick={() => setMobileMenuOpen(false)}>
                            {link.name}
                        </Link>
                    ))}
                    <div style={{ height: '1px', background: 'var(--border-subtle)', margin: '0.5rem 0' }}></div>
                    {session ? (
                        <>
                            <Link to="/profile" className="nav-link" onClick={() => setMobileMenuOpen(false)}>Profile</Link>
                            <Link to="/notifications" className="nav-link" onClick={() => setMobileMenuOpen(false)}>Notifications {unreadCount > 0 && `(${unreadCount})`}</Link>
                            <button onClick={handleLogout} className="btn btn-ghost" style={{ justifyContent: 'flex-start', color: 'hsl(var(--danger))' }}>Sign Out</button>
                        </>
                    ) : (
                        <>
                            <Link to="/login" className="btn btn-ghost" style={{ justifyContent: 'flex-start' }}>Log In</Link>
                            <Link to="/signup" className="btn btn-primary">Sign Up</Link>
                        </>
                    )}
                </div>
            )}

            <style>{`
        .desktop-only { display: flex; }
        .mobile-only { display: none; }
        @media (max-width: 768px) { .desktop-only { display: none !important; } .mobile-only { display: flex; } }
      `}</style>
        </header>
    );
}
