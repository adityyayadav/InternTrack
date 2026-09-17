import React, { useEffect, useState } from 'react';
import { Plus, FileText, Loader, Sparkles, CheckCircle2, Send } from 'lucide-react';
import api from '../lib/api';

const STATUS_STYLES = {
    DRAFT: { label: 'Draft', bg: 'hsla(220,60%,60%,0.1)', color: 'hsl(220,60%,70%)' },
    SUBMITTED: { label: 'Submitted', bg: 'hsla(var(--success),0.1)', color: 'hsl(var(--success))' },
    REVIEWED: { label: 'Reviewed', bg: 'hsla(var(--warning),0.1)', color: 'hsl(var(--warning))' },
};

export default function WeeklyReportsPage() {
    const [reports, setReports] = useState([]);
    const [loading, setLoading] = useState(true);
    const [error, setError] = useState('');
    const [showForm, setShowForm] = useState(false);
    const [form, setForm] = useState({ weekNumber: '', tasksDone: '', challenges: '', plansNextWeek: '' });
    const [saving, setSaving] = useState(false);
    const [summarizing, setSummarizing] = useState(null);
    const [summaries, setSummaries] = useState({});

    useEffect(() => {
        api.get('/api/v1/weekly-reports/me')
            .then(res => setReports(res.data?.content || []))
            .catch(err => setError(err.message))
            .finally(() => setLoading(false));
    }, []);

    const handleCreate = async (e) => {
        e.preventDefault();
        setSaving(true);
        try {
            const res = await api.post('/api/v1/weekly-reports', {
                weekNumber: parseInt(form.weekNumber),
                tasksDone: form.tasksDone,
                challenges: form.challenges,
                plansNextWeek: form.plansNextWeek,
            });
            setReports(prev => [res.data, ...prev]);
            setShowForm(false);
            setForm({ weekNumber: '', tasksDone: '', challenges: '', plansNextWeek: '' });
        } catch (e) {
            alert('Error creating report: ' + e.message);
        } finally {
            setSaving(false);
        }
    };

    const handleSubmitReport = async (id) => {
        try {
            const res = await api.post(`/api/v1/weekly-reports/${id}/submit`);
            setReports(prev => prev.map(r => r.id === id ? res.data : r));
        } catch (e) {
            alert('Error submitting: ' + e.message);
        }
    };

    const handleAiSummarize = async (id) => {
        setSummarizing(id);
        try {
            const res = await api.get(`/api/ai/summarize-weekly-report/${id}`);
            setSummaries(prev => ({ ...prev, [id]: res.data }));
        } catch (e) {
            setSummaries(prev => ({ ...prev, [id]: { error: e.message } }));
        } finally {
            setSummarizing(null);
        }
    };

    return (
        <div className="container animate-fade-in" style={{ padding: 'var(--space-6) var(--space-4)', marginTop: '2rem' }}>
            <div className="flex-between" style={{ marginBottom: 'var(--space-6)', flexWrap: 'wrap', gap: '1rem' }}>
                <div>
                    <h2>Weekly Reports</h2>
                    <p style={{ color: 'hsl(var(--text-secondary))' }}>Log your weekly internship progress for faculty review.</p>
                </div>
                <button className="btn btn-primary" onClick={() => setShowForm(!showForm)} style={{ display: 'flex', alignItems: 'center', gap: '0.5rem' }}>
                    <Plus size={18} /> {showForm ? 'Cancel' : 'New Report'}
                </button>
            </div>

            {/* Create Form */}
            {showForm && (
                <div className="card animate-fade-in" style={{ marginBottom: 'var(--space-6)', borderColor: 'hsla(var(--primary-accent),0.3)' }}>
                    <h3 style={{ marginBottom: 'var(--space-4)' }}>Create Weekly Report</h3>
                    <form onSubmit={handleCreate} style={{ display: 'grid', gridTemplateColumns: '1fr', gap: '1rem' }}>
                        <div>
                            <label style={{ display: 'block', marginBottom: '0.375rem', fontSize: '0.875rem', color: 'hsl(var(--text-secondary))' }}>Week Number</label>
                            <input type="number" min="1" required value={form.weekNumber} onChange={e => setForm(p => ({ ...p, weekNumber: e.target.value }))}
                                placeholder="e.g. 4"
                                style={{ width: '100%', padding: '0.75rem', borderRadius: 'var(--radius-md)', border: '1px solid var(--border-subtle)', background: 'hsla(var(--bg-surface-elevated),0.6)', color: 'hsl(var(--text-primary))', fontSize: '1rem', outline: 'none', boxSizing: 'border-box' }} />
                        </div>
                        {[
                            { field: 'tasksDone', label: 'Tasks Completed This Week', placeholder: 'Describe what you accomplished…' },
                            { field: 'challenges', label: 'Challenges Faced', placeholder: 'Any blockers or difficulties?' },
                            { field: 'plansNextWeek', label: 'Plans for Next Week', placeholder: 'What will you work on next?' },
                        ].map(({ field, label, placeholder }) => (
                            <div key={field}>
                                <label style={{ display: 'block', marginBottom: '0.375rem', fontSize: '0.875rem', color: 'hsl(var(--text-secondary))' }}>{label}</label>
                                <textarea rows={3} required value={form[field]} onChange={e => setForm(p => ({ ...p, [field]: e.target.value }))}
                                    placeholder={placeholder}
                                    style={{ width: '100%', padding: '0.75rem', borderRadius: 'var(--radius-md)', border: '1px solid var(--border-subtle)', background: 'hsla(var(--bg-surface-elevated),0.6)', color: 'hsl(var(--text-primary))', fontSize: '0.9rem', outline: 'none', resize: 'vertical', boxSizing: 'border-box' }} />
                            </div>
                        ))}
                        <div style={{ display: 'flex', gap: '1rem' }}>
                            <button type="submit" disabled={saving} className="btn btn-primary" style={{ display: 'flex', alignItems: 'center', gap: '0.5rem' }}>
                                {saving ? <Loader size={16} style={{ animation: 'spin 1s linear infinite' }} /> : null}
                                Save as Draft
                            </button>
                        </div>
                    </form>
                </div>
            )}

            {loading ? (
                <div style={{ display: 'flex', alignItems: 'center', justifyContent: 'center', padding: '4rem', gap: '1rem' }}>
                    <Loader size={32} color="hsl(var(--primary-accent))" style={{ animation: 'spin 1s linear infinite' }} />
                    <p style={{ color: 'hsl(var(--text-secondary))' }}>Loading reports…</p>
                </div>
            ) : error ? (
                <div style={{ background: 'hsla(var(--danger),0.1)', border: '1px solid hsla(var(--danger),0.3)', borderRadius: 'var(--radius-md)', padding: '1.5rem', color: 'hsl(var(--danger))' }}>
                    Could not load reports: {error}
                </div>
            ) : reports.length === 0 ? (
                <div className="card" style={{ textAlign: 'center', padding: '4rem 2rem' }}>
                    <FileText size={48} color="hsl(var(--text-muted))" style={{ margin: '0 auto 1rem' }} />
                    <h3 style={{ marginBottom: '0.5rem' }}>No reports yet</h3>
                    <p style={{ color: 'hsl(var(--text-secondary))' }}>Create your first weekly internship report above.</p>
                </div>
            ) : (
                <div style={{ display: 'flex', flexDirection: 'column', gap: 'var(--space-4)' }}>
                    {reports.map(report => {
                        const s = STATUS_STYLES[report.status] || STATUS_STYLES.DRAFT;
                        const summary = summaries[report.id];
                        return (
                            <div key={report.id} className="card">
                                <div className="flex-between" style={{ marginBottom: '1rem', flexWrap: 'wrap', gap: '0.5rem' }}>
                                    <div>
                                        <h3 style={{ fontSize: '1.1rem', marginBottom: '0.25rem' }}>Week {report.weekNumber} Report</h3>
                                        <p style={{ color: 'hsl(var(--text-muted))', fontSize: '0.8rem' }}>{report.createdAt ? new Date(report.createdAt).toLocaleDateString() : ''}</p>
                                    </div>
                                    <div style={{ display: 'flex', gap: '0.75rem', alignItems: 'center' }}>
                                        <span style={{ background: s.bg, color: s.color, padding: '0.25rem 0.75rem', borderRadius: 'var(--radius-full)', fontSize: '0.78rem', fontWeight: 600 }}>{s.label}</span>
                                        {report.status === 'DRAFT' && (
                                            <button onClick={() => handleSubmitReport(report.id)} className="btn btn-primary" style={{ padding: '0.375rem 1rem', fontSize: '0.8rem', display: 'flex', alignItems: 'center', gap: '0.4rem' }}>
                                                <Send size={14} /> Submit
                                            </button>
                                        )}
                                        <button onClick={() => handleAiSummarize(report.id)} disabled={summarizing === report.id} className="btn btn-secondary" style={{ padding: '0.375rem 1rem', fontSize: '0.8rem', display: 'flex', alignItems: 'center', gap: '0.4rem' }}>
                                            {summarizing === report.id ? <Loader size={14} style={{ animation: 'spin 1s linear infinite' }} /> : <Sparkles size={14} />}
                                            AI Summary
                                        </button>
                                    </div>
                                </div>

                                {/* Report Content Preview */}
                                <div style={{ display: 'grid', gridTemplateColumns: 'repeat(auto-fit, minmax(220px, 1fr))', gap: '1rem', marginBottom: summary ? '1rem' : 0 }}>
                                    {[['Tasks Done', report.tasksDone], ['Challenges', report.challenges], ['Next Week', report.plansNextWeek]].map(([label, val]) => (
                                        val && (
                                            <div key={label} style={{ background: 'hsla(var(--bg-surface-elevated),0.4)', padding: '0.75rem', borderRadius: 'var(--radius-md)', border: '1px solid var(--border-subtle)' }}>
                                                <p style={{ fontSize: '0.75rem', color: 'hsl(var(--text-muted))', marginBottom: '0.25rem', textTransform: 'uppercase', letterSpacing: '0.05em' }}>{label}</p>
                                                <p style={{ fontSize: '0.875rem', color: 'hsl(var(--text-secondary))', display: '-webkit-box', WebkitLineClamp: 2, WebkitBoxOrient: 'vertical', overflow: 'hidden' }}>{val}</p>
                                            </div>
                                        )
                                    ))}
                                </div>

                                {/* AI Summary Panel */}
                                {summary && (
                                    <div style={{ marginTop: '1rem', background: 'hsla(var(--primary-accent),0.06)', border: '1px solid hsla(var(--primary-accent),0.2)', borderRadius: 'var(--radius-md)', padding: '1rem' }}>
                                        <div className="flex-center" style={{ justifyContent: 'flex-start', gap: '0.5rem', marginBottom: '0.75rem' }}>
                                            <Sparkles size={16} color="hsl(var(--primary-accent))" />
                                            <h4 style={{ color: 'hsl(var(--primary-accent))', fontSize: '0.9rem' }}>AI Summary</h4>
                                        </div>
                                        {summary.error ? (
                                            <p style={{ color: 'hsl(var(--danger))', fontSize: '0.875rem' }}>Error: {summary.error}</p>
                                        ) : (
                                            <>
                                                {summary.summary && <p style={{ color: 'hsl(var(--text-secondary))', fontSize: '0.875rem', marginBottom: '0.5rem' }}>{summary.summary}</p>}
                                                {summary.blockers?.length > 0 && (
                                                    <div>
                                                        <p style={{ color: 'hsl(var(--warning))', fontSize: '0.8rem', fontWeight: 600 }}>Blockers: {summary.blockers.join(', ')}</p>
                                                    </div>
                                                )}
                                                {summary.actionItems?.length > 0 && (
                                                    <div>
                                                        <p style={{ color: 'hsl(var(--success))', fontSize: '0.8rem', fontWeight: 600 }}>Action Items: {summary.actionItems.join(', ')}</p>
                                                    </div>
                                                )}
                                            </>
                                        )}
                                    </div>
                                )}
                            </div>
                        );
                    })}
                </div>
            )}
            <style>{`@keyframes spin { to { transform: rotate(360deg); } }`}</style>
        </div>
    );
}
