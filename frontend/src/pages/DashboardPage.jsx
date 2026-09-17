import React, { useEffect, useState } from 'react';
import { Link } from 'react-router-dom';
import { Activity, Bell, FileCheck, CheckCircle2, AlertCircle, TrendingUp, Clock, Loader } from 'lucide-react';
import api from '../lib/api';
import { useAuth } from '../context/AuthContext';

export default function DashboardPage() {
    const [data, setData] = useState(null);
    const [loading, setLoading] = useState(true);
    const [error, setError] = useState('');
    const { user } = useAuth();

    useEffect(() => {
        api.get('/api/v1/dashboard/me')
            .then(res => setData(res.data))
            .catch(err => setError(err.message))
            .finally(() => setLoading(false));
    }, []);

    const name = user?.user_metadata?.full_name?.split(' ')[0] || 'Student';

    if (loading) {
        return (
            <div style={{ display: 'flex', alignItems: 'center', justifyContent: 'center', minHeight: '60vh', gap: '1rem' }}>
                <Loader size={32} color="hsl(var(--primary-accent))" style={{ animation: 'spin 1s linear infinite' }} />
                <p style={{ color: 'hsl(var(--text-secondary))' }}>Loading your dashboard…</p>
                <style>{`@keyframes spin { to { transform: rotate(360deg); } }`}</style>
            </div>
        );
    }

    if (error) {
        return (
            <div className="container" style={{ padding: '2rem', marginTop: '2rem' }}>
                <div style={{ background: 'hsla(var(--danger),0.1)', border: '1px solid hsla(var(--danger),0.3)', borderRadius: 'var(--radius-md)', padding: '1.5rem', marginBottom: '2rem' }}>
                    <p style={{ color: 'hsl(var(--danger))' }}>⚠ Could not load dashboard data: {error}</p>
                </div>
                <FallbackDashboard name={name} />
            </div>
        );
    }

    const metrics = [
        { label: 'Active Applications', value: data?.activeApplications ?? 0, sub: 'internship applications', icon: Activity, color: 'var(--secondary-accent)' },
        { label: 'Pending Reports', value: data?.pendingReports ?? 0, sub: 'weekly reports due', icon: FileCheck, color: 'var(--warning)' },
        { label: 'Unread Notifications', value: data?.unreadNotifications ?? 0, sub: 'new messages', icon: Bell, color: 'var(--primary-accent)' },
        { label: 'Internship Progress', value: `${data?.weeklyReportProgress ?? 0}%`, sub: 'reports submitted', icon: TrendingUp, color: 'var(--success)' },
    ];

    return (
        <div className="container animate-fade-in" style={{ padding: 'var(--space-6) var(--space-4)', marginTop: '2rem' }}>
            <div className="flex-between" style={{ marginBottom: 'var(--space-6)', flexWrap: 'wrap', gap: '1rem' }}>
                <div>
                    <h2>Welcome back, {name} 👋</h2>
                    <p style={{ color: 'hsl(var(--text-secondary))' }}>Here's your internship progress overview.</p>
                </div>
                <Link to="/opportunities" className="btn btn-primary">Find Internships</Link>
            </div>

            {/* Metric Cards */}
            <div style={{ display: 'grid', gridTemplateColumns: 'repeat(auto-fit, minmax(220px, 1fr))', gap: 'var(--space-4)', marginBottom: 'var(--space-8)' }}>
                {metrics.map(m => (
                    <div key={m.label} className="card" style={{ display: 'flex', flexDirection: 'column', gap: '0.5rem' }}>
                        <div className="flex-between">
                            <h4 style={{ fontSize: '0.9rem', color: 'hsl(var(--text-secondary))', fontWeight: 500 }}>{m.label}</h4>
                            <div style={{ background: `hsla(${m.color.replace('var(--', '').replace(')', '')},0.1)`, padding: '0.5rem', borderRadius: 'var(--radius-md)' }}>
                                <m.icon color={`hsl(${m.color.replace('var(--', '').replace(')', '')},1)`} size={20} />
                            </div>
                        </div>
                        <p style={{ fontSize: '2rem', fontWeight: 700 }}>{m.value}</p>
                        <p style={{ color: 'hsl(var(--text-muted))', fontSize: '0.8rem' }}>{m.sub}</p>
                    </div>
                ))}
            </div>

            {/* Bottom Grid */}
            <div style={{ display: 'grid', gridTemplateColumns: '2fr 1fr', gap: 'var(--space-6)' }}>
                {/* Recent Activity */}
                <section className="card">
                    <h3 style={{ fontSize: '1.1rem', marginBottom: 'var(--space-4)' }}>Recent Activity</h3>
                    {data?.recentActivities?.length > 0 ? (
                        <div style={{ display: 'flex', flexDirection: 'column', gap: 'var(--space-3)' }}>
                            {data.recentActivities.map((a, i) => (
                                <div key={i} className="flex-center" style={{ justifyContent: 'flex-start', gap: '1rem' }}>
                                    <div style={{ background: 'hsla(var(--primary-accent),0.1)', padding: '0.65rem', borderRadius: 'var(--radius-full)', flexShrink: 0 }}>
                                        <CheckCircle2 color="hsl(var(--primary-accent))" size={18} />
                                    </div>
                                    <div>
                                        <p style={{ fontWeight: 500, fontSize: '0.9rem' }}>{a.title}</p>
                                        <p style={{ color: 'hsl(var(--text-muted))', fontSize: '0.8rem' }}>{a.subtitle}</p>
                                    </div>
                                </div>
                            ))}
                        </div>
                    ) : (
                        <div style={{ display: 'flex', flexDirection: 'column', gap: 'var(--space-3)' }}>
                            <div className="flex-center" style={{ justifyContent: 'flex-start', gap: '1rem' }}>
                                <div style={{ background: 'hsla(var(--success),0.1)', padding: '0.65rem', borderRadius: 'var(--radius-full)' }}>
                                    <CheckCircle2 color="hsl(var(--success))" size={18} />
                                </div>
                                <div>
                                    <p style={{ fontWeight: 500, fontSize: '0.9rem' }}>Profile Created</p>
                                    <p style={{ color: 'hsl(var(--text-muted))', fontSize: '0.8rem' }}>Your InternTrack profile is ready to use</p>
                                </div>
                            </div>
                            <div className="flex-center" style={{ justifyContent: 'flex-start', gap: '1rem' }}>
                                <div style={{ background: 'hsla(var(--primary-accent),0.1)', padding: '0.65rem', borderRadius: 'var(--radius-full)' }}>
                                    <AlertCircle color="hsl(var(--primary-accent))" size={18} />
                                </div>
                                <div>
                                    <p style={{ fontWeight: 500, fontSize: '0.9rem' }}>Browse Opportunities</p>
                                    <p style={{ color: 'hsl(var(--text-muted))', fontSize: '0.8rem' }}>Start by applying to an internship</p>
                                </div>
                            </div>
                        </div>
                    )}
                </section>

                {/* Action Items */}
                <section className="card">
                    <h3 style={{ fontSize: '1.1rem', marginBottom: 'var(--space-4)' }}>Action Items</h3>
                    <div style={{ display: 'flex', flexDirection: 'column', gap: 'var(--space-3)' }}>
                        <div style={{ padding: 'var(--space-3)', background: 'hsla(var(--bg-surface-elevated),0.5)', borderRadius: 'var(--radius-md)', border: '1px solid var(--border-subtle)' }}>
                            <div className="flex-center" style={{ justifyContent: 'flex-start', gap: '0.5rem', marginBottom: '0.4rem' }}>
                                <Clock size={14} color="hsl(var(--warning))" />
                                <h5 style={{ fontSize: '0.875rem' }}>Submit Weekly Report</h5>
                            </div>
                            <p style={{ color: 'hsl(var(--text-secondary))', fontSize: '0.8rem', marginBottom: '0.75rem' }}>Log your weekly progress</p>
                            <Link to="/reports" className="btn btn-secondary" style={{ width: '100%', fontSize: '0.82rem', padding: '0.5rem', textAlign: 'center' }}>Open Reports</Link>
                        </div>
                        <div style={{ padding: 'var(--space-3)', background: 'hsla(var(--bg-surface-elevated),0.5)', borderRadius: 'var(--radius-md)', border: '1px solid var(--border-subtle)' }}>
                            <h5 style={{ fontSize: '0.875rem', marginBottom: '0.4rem' }}>Explore AI Matching</h5>
                            <p style={{ color: 'hsl(var(--text-secondary))', fontSize: '0.8rem', marginBottom: '0.75rem' }}>Let AI rank internships by your skills</p>
                            <Link to="/opportunities" className="btn btn-ghost" style={{ width: '100%', fontSize: '0.82rem', padding: '0.5rem', border: '1px solid var(--border-subtle)', textAlign: 'center' }}>Browse &amp; Match</Link>
                        </div>
                    </div>
                </section>
            </div>
        </div>
    );
}

function FallbackDashboard({ name }) {
    return (
        <div>
            <h2 style={{ marginBottom: '0.5rem' }}>Welcome, {name}</h2>
            <p style={{ color: 'hsl(var(--text-secondary))', marginBottom: '2rem' }}>Backend is initializing. Your data will appear once your profile is set up.</p>
            <div style={{ display: 'flex', gap: '1rem', flexWrap: 'wrap' }}>
                <Link to="/opportunities" className="btn btn-primary">Browse Opportunities</Link>
                <Link to="/profile" className="btn btn-secondary">Complete Your Profile</Link>
            </div>
        </div>
    );
}
