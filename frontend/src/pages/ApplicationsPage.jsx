import React, { useEffect, useState } from 'react';
import { Link } from 'react-router-dom';
import { Clock, CheckCircle2, User, ChevronRight, Loader, Plus, FileText } from 'lucide-react';
import api from '../lib/api';

const STATUS_STYLES = {
    DRAFT: { label: 'Draft', bg: 'hsla(220,60%,60%,0.1)', color: 'hsl(220,60%,70%)' },
    SUBMITTED: { label: 'Submitted', bg: 'hsla(var(--warning),0.1)', color: 'hsl(var(--warning))' },
    UNDER_REVIEW: { label: 'Under Review', bg: 'hsla(35,90%,55%,0.1)', color: 'hsl(35,90%,65%)' },
    APPROVED: { label: 'Approved', bg: 'hsla(var(--success),0.1)', color: 'hsl(var(--success))' },
    ACCEPTED: { label: 'Accepted', bg: 'hsla(var(--success),0.1)', color: 'hsl(var(--success))' },
    REJECTED: { label: 'Rejected', bg: 'hsla(var(--danger),0.1)', color: 'hsl(var(--danger))' },
};

export default function ApplicationsPage() {
    const [applications, setApplications] = useState([]);
    const [loading, setLoading] = useState(true);
    const [error, setError] = useState('');

    useEffect(() => {
        api.get('/api/v1/applications/me')
            .then(res => setApplications(res.data?.content || []))
            .catch(err => setError(err.message))
            .finally(() => setLoading(false));
    }, []);

    const handleSubmit = async (id) => {
        try {
            const res = await api.post(`/api/v1/applications/${id}/submit`, { coverLetter: '' });
            setApplications(prev => prev.map(a => a.id === id ? res.data : a));
        } catch (e) {
            alert('Could not submit: ' + e.message);
        }
    };

    return (
        <div className="container animate-fade-in" style={{ padding: 'var(--space-6) var(--space-4)', marginTop: '2rem' }}>
            <div className="flex-between" style={{ marginBottom: 'var(--space-6)', flexWrap: 'wrap', gap: '1rem' }}>
                <div>
                    <h2>My Applications</h2>
                    <p style={{ color: 'hsl(var(--text-secondary))' }}>Track your internship applications and faculty approvals.</p>
                </div>
                <Link to="/opportunities" className="btn btn-primary" style={{ display: 'flex', alignItems: 'center', gap: '0.5rem' }}>
                    <Plus size={18} /> Apply to Internship
                </Link>
            </div>

            {loading ? (
                <div style={{ display: 'flex', alignItems: 'center', justifyContent: 'center', padding: '4rem', gap: '1rem' }}>
                    <Loader size={32} color="hsl(var(--primary-accent))" style={{ animation: 'spin 1s linear infinite' }} />
                    <p style={{ color: 'hsl(var(--text-secondary))' }}>Loading your applications…</p>
                </div>
            ) : error ? (
                <div style={{ background: 'hsla(var(--danger),0.1)', border: '1px solid hsla(var(--danger),0.3)', borderRadius: 'var(--radius-md)', padding: '1.5rem', color: 'hsl(var(--danger))' }}>
                    Could not load applications: {error}
                </div>
            ) : applications.length === 0 ? (
                <div className="card" style={{ textAlign: 'center', padding: '4rem 2rem' }}>
                    <FileText size={48} color="hsl(var(--text-muted))" style={{ margin: '0 auto 1rem' }} />
                    <h3 style={{ marginBottom: '0.5rem' }}>No applications yet</h3>
                    <p style={{ color: 'hsl(var(--text-secondary))', marginBottom: '1.5rem' }}>Start by browsing and applying to internship opportunities.</p>
                    <Link to="/opportunities" className="btn btn-primary">Browse Opportunities</Link>
                </div>
            ) : (
                <div className="card" style={{ padding: 0, overflow: 'hidden' }}>
                    <div style={{ overflowX: 'auto' }}>
                        <table style={{ width: '100%', borderCollapse: 'collapse', textAlign: 'left' }}>
                            <thead>
                                <tr style={{ background: 'hsla(var(--bg-surface-elevated),0.5)', borderBottom: '1px solid var(--border-strong)' }}>
                                    {['Role & Organization', 'Status', 'Faculty', 'Applied On', 'Actions'].map(h => (
                                        <th key={h} style={{ padding: '1rem var(--space-4)', fontWeight: 600, color: 'hsl(var(--text-secondary))', fontSize: '0.85rem' }}>{h}</th>
                                    ))}
                                </tr>
                            </thead>
                            <tbody>
                                {applications.map((app, i) => {
                                    const style = STATUS_STYLES[app.status] || STATUS_STYLES.DRAFT;
                                    return (
                                        <tr key={app.id}
                                            style={{ borderBottom: i !== applications.length - 1 ? '1px solid var(--border-subtle)' : 'none' }}
                                            onMouseEnter={e => e.currentTarget.style.background = 'hsla(var(--bg-surface-elevated),0.3)'}
                                            onMouseLeave={e => e.currentTarget.style.background = 'transparent'}>
                                            <td style={{ padding: '1rem var(--space-4)' }}>
                                                <p style={{ fontWeight: 500 }}>{app.opportunityTitle || 'Internship Position'}</p>
                                                <p style={{ color: 'hsl(var(--primary-accent))', fontSize: '0.82rem' }}>{app.organizationName || '—'}</p>
                                            </td>
                                            <td style={{ padding: '1rem var(--space-4)' }}>
                                                <span style={{ background: style.bg, color: style.color, padding: '0.25rem 0.75rem', borderRadius: 'var(--radius-full)', fontSize: '0.78rem', fontWeight: 600 }}>{style.label}</span>
                                            </td>
                                            <td style={{ padding: '1rem var(--space-4)' }}>
                                                <div className="flex-center" style={{ justifyContent: 'flex-start', gap: '0.5rem', color: 'hsl(var(--text-secondary))', fontSize: '0.85rem' }}>
                                                    <User size={14} /> {app.facultyName || 'Pending Assignment'}
                                                </div>
                                            </td>
                                            <td style={{ padding: '1rem var(--space-4)', color: 'hsl(var(--text-secondary))', fontSize: '0.85rem' }}>
                                                <div className="flex-center" style={{ justifyContent: 'flex-start', gap: '0.4rem' }}>
                                                    <Clock size={14} /> {app.appliedAt ? new Date(app.appliedAt).toLocaleDateString() : '—'}
                                                </div>
                                            </td>
                                            <td style={{ padding: '1rem var(--space-4)' }}>
                                                {app.status === 'DRAFT' && (
                                                    <button onClick={() => handleSubmit(app.id)} className="btn btn-primary" style={{ padding: '0.4rem 1rem', fontSize: '0.8rem' }}>
                                                        Submit
                                                    </button>
                                                )}
                                                {app.status === 'ACCEPTED' && (
                                                    <Link to="/reports" className="btn btn-secondary" style={{ padding: '0.4rem 1rem', fontSize: '0.8rem' }}>
                                                        Weekly Reports
                                                    </Link>
                                                )}
                                            </td>
                                        </tr>
                                    );
                                })}
                            </tbody>
                        </table>
                    </div>
                </div>
            )}
            <style>{`@keyframes spin { to { transform: rotate(360deg); } }`}</style>
        </div>
    );
}
