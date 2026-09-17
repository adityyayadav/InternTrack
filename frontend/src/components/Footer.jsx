import React from 'react';
import { Link } from 'react-router-dom';
import { Rocket, Mail, MessageSquare, Globe } from 'lucide-react';

export default function Footer() {
    return (
        <footer style={{
            borderTop: '1px solid var(--border-subtle)',
            marginTop: 'auto',
            padding: 'var(--space-8) 0 var(--space-4)',
            background: 'hsla(var(--bg-surface), 0.5)'
        }}>
            <div className="container" style={{ display: 'grid', gridTemplateColumns: 'repeat(auto-fit, minmax(200px, 1fr))', gap: 'var(--space-6)' }}>

                <div>
                    <div className="flex-center" style={{ gap: '0.5rem', justifyContent: 'flex-start', marginBottom: 'var(--space-4)' }}>
                        <Rocket color="hsl(var(--primary-accent))" size={20} />
                        <span style={{ fontFamily: 'var(--font-display)', fontWeight: 700, fontSize: '1.25rem' }}>
                            InternTrack
                        </span>
                    </div>
                    <p style={{ color: 'hsl(var(--text-muted))', fontSize: '0.9rem', marginBottom: 'var(--space-4)' }}>
                        Bridging the gap between academic institutions and top-tier tech internships through intelligent matching.
                    </p>
                    <div className="flex-center" style={{ gap: '1rem', justifyContent: 'flex-start' }}>
                        <a href="#" style={{ color: 'hsl(var(--text-muted))' }}><MessageSquare size={20} /></a>
                        <a href="#" style={{ color: 'hsl(var(--text-muted))' }}><Mail size={20} /></a>
                        <a href="#" style={{ color: 'hsl(var(--text-muted))' }}><Globe size={20} /></a>
                    </div>
                </div>

                <div>
                    <h4 style={{ fontSize: '1rem', marginBottom: 'var(--space-4)', color: 'hsl(var(--text-primary))' }}>Platform</h4>
                    <ul style={{ listStyle: 'none', display: 'flex', flexDirection: 'column', gap: '0.5rem' }}>
                        <li><Link to="/opportunities" style={{ color: 'hsl(var(--text-secondary))', fontSize: '0.9rem' }}>Browse Opportunities</Link></li>
                        <li><Link to="/applications" style={{ color: 'hsl(var(--text-secondary))', fontSize: '0.9rem' }}>My Applications</Link></li>
                        <li><Link to="/dashboard" style={{ color: 'hsl(var(--text-secondary))', fontSize: '0.9rem' }}>Student Dashboard</Link></li>
                        <li><a href="#" style={{ color: 'hsl(var(--text-secondary))', fontSize: '0.9rem' }}>Faculty Portal</a></li>
                    </ul>
                </div>

                <div>
                    <h4 style={{ fontSize: '1rem', marginBottom: 'var(--space-4)', color: 'hsl(var(--text-primary))' }}>Resources</h4>
                    <ul style={{ listStyle: 'none', display: 'flex', flexDirection: 'column', gap: '0.5rem' }}>
                        <li><a href="#" style={{ color: 'hsl(var(--text-secondary))', fontSize: '0.9rem' }}>Documentation</a></li>
                        <li><a href="#" style={{ color: 'hsl(var(--text-secondary))', fontSize: '0.9rem' }}>Resume Guide</a></li>
                        <li><a href="#" style={{ color: 'hsl(var(--text-secondary))', fontSize: '0.9rem' }}>Interview Prep</a></li>
                        <li><a href="#" style={{ color: 'hsl(var(--text-secondary))', fontSize: '0.9rem' }}>Help Center</a></li>
                    </ul>
                </div>
            </div>

            <div className="container" style={{
                borderTop: '1px solid var(--border-subtle)',
                marginTop: 'var(--space-6)',
                paddingTop: 'var(--space-4)',
                display: 'flex',
                justifyContent: 'space-between',
                alignItems: 'center',
                flexWrap: 'wrap',
                gap: '1rem'
            }}>
                <p style={{ color: 'hsl(var(--text-muted))', fontSize: '0.8rem' }}>
                    &copy; {new Date().getFullYear()} InternTrack System. All rights reserved.
                </p>
                <div style={{ display: 'flex', gap: '1rem' }}>
                    <a href="#" style={{ color: 'hsl(var(--text-muted))', fontSize: '0.8rem' }}>Privacy Policy</a>
                    <a href="#" style={{ color: 'hsl(var(--text-muted))', fontSize: '0.8rem' }}>Terms of Service</a>
                </div>
            </div>
        </footer>
    );
}
