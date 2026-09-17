import React, { useEffect, useState } from 'react';
import { Bell, CheckCheck, Trash2, Loader, BellOff } from 'lucide-react';
import api from '../lib/api';

export default function NotificationsPage() {
    const [notifications, setNotifications] = useState([]);
    const [loading, setLoading] = useState(true);
    const [error, setError] = useState('');
    const [unreadOnly, setUnreadOnly] = useState(false);
    const [marking, setMarking] = useState(false);

    const fetch = (unreadOnly) => {
        setLoading(true);
        api.get(`/api/v1/notifications?unreadOnly=${unreadOnly}&size=50`)
            .then(res => setNotifications(res.data?.content || []))
            .catch(err => setError(err.message))
            .finally(() => setLoading(false));
    };

    useEffect(() => { fetch(unreadOnly); }, [unreadOnly]);

    const handleMarkRead = async (id) => {
        try {
            const res = await api.patch(`/api/v1/notifications/${id}/read`);
            setNotifications(prev => prev.map(n => n.id === id ? res.data : n));
        } catch (e) { alert(e.message); }
    };

    const handleMarkAll = async () => {
        setMarking(true);
        try {
            await api.patch('/api/v1/notifications/read-all');
            setNotifications(prev => prev.map(n => ({ ...n, read: true })));
        } catch (e) { alert(e.message); }
        finally { setMarking(false); }
    };

    const handleDelete = async (id) => {
        try {
            await api.delete(`/api/v1/notifications/${id}`);
            setNotifications(prev => prev.filter(n => n.id !== id));
        } catch (e) { alert(e.message); }
    };

    const unreadCount = notifications.filter(n => !n.read).length;

    return (
        <div className="container animate-fade-in" style={{ padding: 'var(--space-6) var(--space-4)', marginTop: '2rem', maxWidth: '800px' }}>
            <div className="flex-between" style={{ marginBottom: 'var(--space-6)', flexWrap: 'wrap', gap: '1rem' }}>
                <div>
                    <h2>Notifications</h2>
                    <p style={{ color: 'hsl(var(--text-secondary))' }}>{unreadCount} unread notifications</p>
                </div>
                <div style={{ display: 'flex', gap: '0.75rem' }}>
                    <button onClick={() => setUnreadOnly(!unreadOnly)} className={`btn ${unreadOnly ? 'btn-primary' : 'btn-secondary'}`} style={{ fontSize: '0.875rem' }}>
                        {unreadOnly ? 'Show All' : 'Unread Only'}
                    </button>
                    <button onClick={handleMarkAll} disabled={marking || unreadCount === 0} className="btn btn-secondary" style={{ display: 'flex', alignItems: 'center', gap: '0.5rem', fontSize: '0.875rem' }}>
                        {marking ? <Loader size={14} style={{ animation: 'spin 1s linear infinite' }} /> : <CheckCheck size={14} />}
                        Mark All Read
                    </button>
                </div>
            </div>

            {loading ? (
                <div style={{ display: 'flex', alignItems: 'center', justifyContent: 'center', padding: '4rem', gap: '1rem' }}>
                    <Loader size={32} color="hsl(var(--primary-accent))" style={{ animation: 'spin 1s linear infinite' }} />
                </div>
            ) : error ? (
                <div style={{ background: 'hsla(var(--danger),0.1)', border: '1px solid hsla(var(--danger),0.3)', borderRadius: 'var(--radius-md)', padding: '1.5rem', color: 'hsl(var(--danger))' }}>
                    Could not load notifications: {error}
                </div>
            ) : notifications.length === 0 ? (
                <div className="card" style={{ textAlign: 'center', padding: '4rem 2rem' }}>
                    <BellOff size={48} color="hsl(var(--text-muted))" style={{ margin: '0 auto 1rem' }} />
                    <h3 style={{ marginBottom: '0.5rem' }}>No notifications</h3>
                    <p style={{ color: 'hsl(var(--text-secondary))' }}>{unreadOnly ? 'No unread notifications.' : "You're all caught up!"}</p>
                </div>
            ) : (
                <div style={{ display: 'flex', flexDirection: 'column', gap: '0.5rem' }}>
                    {notifications.map(n => (
                        <div key={n.id} className="card" style={{ display: 'flex', alignItems: 'center', gap: '1rem', padding: '1rem 1.25rem', background: n.read ? 'transparent' : 'hsla(var(--primary-accent),0.06)', borderColor: n.read ? 'var(--border-subtle)' : 'hsla(var(--primary-accent),0.25)' }}>
                            <div style={{ background: n.read ? 'hsla(var(--text-muted),0.1)' : 'hsla(var(--primary-accent),0.15)', padding: '0.65rem', borderRadius: 'var(--radius-full)', flexShrink: 0 }}>
                                <Bell size={18} color={n.read ? 'hsl(var(--text-muted))' : 'hsl(var(--primary-accent))'} />
                            </div>
                            <div style={{ flex: 1, minWidth: 0 }}>
                                <p style={{ fontWeight: n.read ? 400 : 600, marginBottom: '0.2rem' }}>{n.title || n.message}</p>
                                {n.message && n.title && <p style={{ color: 'hsl(var(--text-secondary))', fontSize: '0.875rem' }}>{n.message}</p>}
                                <p style={{ color: 'hsl(var(--text-muted))', fontSize: '0.78rem', marginTop: '0.25rem' }}>{n.createdAt ? new Date(n.createdAt).toLocaleString() : ''}</p>
                            </div>
                            <div style={{ display: 'flex', gap: '0.5rem', flexShrink: 0 }}>
                                {!n.read && (
                                    <button onClick={() => handleMarkRead(n.id)} className="btn btn-ghost" style={{ padding: '0.4rem', borderRadius: 'var(--radius-sm)' }} title="Mark as read">
                                        <CheckCheck size={16} color="hsl(var(--primary-accent))" />
                                    </button>
                                )}
                                <button onClick={() => handleDelete(n.id)} className="btn btn-ghost" style={{ padding: '0.4rem', borderRadius: 'var(--radius-sm)' }} title="Delete">
                                    <Trash2 size={16} color="hsl(var(--danger))" />
                                </button>
                            </div>
                        </div>
                    ))}
                </div>
            )}
            <style>{`@keyframes spin { to { transform: rotate(360deg); } }`}</style>
        </div>
    );
}
