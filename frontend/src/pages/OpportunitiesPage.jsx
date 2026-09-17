import React, { useState, useEffect } from 'react';
import { Search, MapPin, Clock, Building, Loader, Sparkles, CheckCircle2, X } from 'lucide-react';
import api from '../lib/api';

export default function OpportunitiesPage() {
    const [opportunities, setOpportunities] = useState([]);
    const [loading, setLoading] = useState(true);
    const [error, setError] = useState('');
    const [searchTerm, setSearchTerm] = useState('');
    const [aiMatches, setAiMatches] = useState(null);
    const [aiLoading, setAiLoading] = useState(false);
    const [aiError, setAiError] = useState('');
    const [applying, setApplying] = useState(null);
    const [applied, setApplied] = useState({});

    useEffect(() => {
        api.get('/api/v1/opportunities?size=30')
            .then(res => setOpportunities(res.data?.content || []))
            .catch(err => setError(err.message))
            .finally(() => setLoading(false));
    }, []);

    const handleAiMatch = async () => {
        setAiLoading(true);
        setAiError('');
        try {
            const res = await api.get('/api/ai/match-internships');
            setAiMatches(res.data);
        } catch (e) {
            setAiError(e.message);
        } finally {
            setAiLoading(false);
        }
    };

    const handleApply = async (opportunityId) => {
        setApplying(opportunityId);
        try {
            await api.post('/api/v1/applications', { opportunityId, coverLetter: '' });
            setApplied(prev => ({ ...prev, [opportunityId]: true }));
        } catch (e) {
            alert('Could not apply: ' + e.message);
        } finally {
            setApplying(null);
        }
    };

    const filtered = opportunities.filter(op =>
        !searchTerm || op.title?.toLowerCase().includes(searchTerm.toLowerCase()) ||
        op.organization?.toLowerCase().includes(searchTerm.toLowerCase())
    );

    const getMatchScore = (id) => {
        if (!aiMatches?.matches) return null;
        const m = aiMatches.matches.find(m => m.opportunityId === id);
        return m?.score ?? null;
    };

    return (
        <div className="container animate-fade-in" style={{ padding: 'var(--space-6) var(--space-4)', marginTop: '2rem' }}>
            <div style={{ textAlign: 'center', maxWidth: '640px', margin: '0 auto var(--space-6)' }}>
                <h2 style={{ marginBottom: '0.5rem' }}>Available Opportunities</h2>
                <p style={{ color: 'hsl(var(--text-secondary))', marginBottom: '1.5rem' }}>
                    Browse and apply for premium internships verified by your institution.
                </p>
                <div style={{ position: 'relative' }}>
                    <Search size={20} style={{ position: 'absolute', left: '1rem', top: '50%', transform: 'translateY(-50%)', color: 'hsl(var(--text-muted))' }} />
                    <input type="text" placeholder="Search roles or companies…" value={searchTerm} onChange={e => setSearchTerm(e.target.value)}
                        style={{ width: '100%', padding: '0.875rem 1rem 0.875rem 3rem', borderRadius: 'var(--radius-full)', border: '1px solid var(--border-subtle)', background: 'hsla(var(--bg-surface-elevated),0.7)', color: 'hsl(var(--text-primary))', fontSize: '1rem', outline: 'none', boxSizing: 'border-box' }} />
                </div>
            </div>

            {/* AI Match Banner */}
            <div className="card" style={{ marginBottom: 'var(--space-6)', display: 'flex', alignItems: 'center', justifyContent: 'space-between', flexWrap: 'wrap', gap: '1rem', background: 'hsla(var(--primary-accent),0.06)', borderColor: 'hsla(var(--primary-accent),0.2)' }}>
                <div className="flex-center" style={{ gap: '1rem', justifyContent: 'flex-start' }}>
                    <div style={{ background: 'hsla(var(--primary-accent),0.15)', padding: '0.75rem', borderRadius: 'var(--radius-md)' }}>
                        <Sparkles color="hsl(var(--primary-accent))" size={24} />
                    </div>
                    <div>
                        <h4 style={{ fontSize: '1rem', marginBottom: '0.25rem' }}>AI Internship Matching</h4>
                        <p style={{ color: 'hsl(var(--text-secondary))', fontSize: '0.875rem' }}>Let AI rank these opportunities based on your skills and profile.</p>
                        {aiError && <p style={{ color: 'hsl(var(--danger))', fontSize: '0.8rem', marginTop: '0.25rem' }}>{aiError}</p>}
                        {aiMatches && <p style={{ color: 'hsl(var(--success))', fontSize: '0.8rem', marginTop: '0.25rem' }}>✓ {aiMatches.summary || 'Matches computed. Scores shown on cards.'}</p>}
                    </div>
                </div>
                <button onClick={handleAiMatch} disabled={aiLoading} className="btn btn-primary" style={{ display: 'flex', alignItems: 'center', gap: '0.5rem' }}>
                    {aiLoading ? <Loader size={18} style={{ animation: 'spin 1s linear infinite' }} /> : <Sparkles size={18} />}
                    {aiLoading ? 'Matching…' : 'Run AI Match'}
                </button>
            </div>

            {loading ? (
                <div style={{ display: 'flex', alignItems: 'center', justifyContent: 'center', padding: '4rem', gap: '1rem' }}>
                    <Loader size={32} color="hsl(var(--primary-accent))" style={{ animation: 'spin 1s linear infinite' }} />
                    <p style={{ color: 'hsl(var(--text-secondary))' }}>Loading opportunities…</p>
                </div>
            ) : error ? (
                <div style={{ background: 'hsla(var(--danger),0.1)', border: '1px solid hsla(var(--danger),0.3)', borderRadius: 'var(--radius-md)', padding: '1.5rem', textAlign: 'center', color: 'hsl(var(--danger))' }}>
                    Could not load opportunities: {error}
                </div>
            ) : (
                <div style={{ display: 'grid', gridTemplateColumns: 'repeat(auto-fill, minmax(320px, 1fr))', gap: 'var(--space-5)' }}>
                    {(filtered.length ? filtered : FALLBACK_OPPS).map((op, idx) => {
                        const score = getMatchScore(op.id);
                        const isApplied = applied[op.id];
                        return (
                            <div key={op.id || idx} className="card card-hover" style={{ display: 'flex', flexDirection: 'column', position: 'relative' }}>
                                {score !== null && (
                                    <div style={{ position: 'absolute', top: '1rem', right: '1rem', background: score >= 70 ? 'hsla(var(--success),0.15)' : 'hsla(var(--warning),0.15)', border: `1px solid hsla(${score >= 70 ? 'var(--success)' : 'var(--warning)'},0.3)`, borderRadius: 'var(--radius-full)', padding: '0.2rem 0.6rem', fontSize: '0.75rem', fontWeight: 700, color: `hsl(${score >= 70 ? 'var(--success)' : 'var(--warning)'})` }}>
                                        {score}% match
                                    </div>
                                )}
                                <div style={{ display: 'flex', gap: '0.5rem', flexWrap: 'wrap', marginBottom: '0.75rem' }}>
                                    {(op.requiredSkills || []).slice(0, 3).map(s => (
                                        <span key={s} className="badge badge-info" style={{ fontSize: '0.65rem' }}>{s}</span>
                                    ))}
                                </div>
                                <h3 style={{ fontSize: '1.15rem', marginBottom: '0.25rem', paddingRight: score !== null ? '5rem' : 0 }}>{op.title}</h3>
                                <div className="flex-center" style={{ justifyContent: 'flex-start', gap: '0.5rem', color: 'hsl(var(--primary-accent))', marginBottom: 'var(--space-3)', fontWeight: 500 }}>
                                    <Building size={15} /> <span style={{ fontSize: '0.9rem' }}>{op.organization}</span>
                                </div>
                                <div style={{ display: 'grid', gridTemplateColumns: '1fr 1fr', gap: '0.5rem', marginBottom: 'var(--space-3)', flex: 1 }}>
                                    <div className="flex-center" style={{ justifyContent: 'flex-start', gap: '0.4rem', color: 'hsl(var(--text-secondary))', fontSize: '0.82rem' }}>
                                        <MapPin size={14} /> {op.location || 'Remote'}
                                    </div>
                                    <div className="flex-center" style={{ justifyContent: 'flex-start', gap: '0.4rem', color: 'hsl(var(--text-secondary))', fontSize: '0.82rem' }}>
                                        <Clock size={14} /> {op.duration || 'Flexible'}
                                    </div>
                                </div>
                                {op.description && (
                                    <p style={{ color: 'hsl(var(--text-secondary))', fontSize: '0.82rem', marginBottom: 'var(--space-4)', display: '-webkit-box', WebkitLineClamp: 2, WebkitBoxOrient: 'vertical', overflow: 'hidden' }}>
                                        {op.description}
                                    </p>
                                )}
                                <button
                                    onClick={() => !isApplied && handleApply(op.id)}
                                    disabled={!!applying || isApplied}
                                    className={`btn ${isApplied ? 'btn-secondary' : 'btn-primary'}`}
                                    style={{ width: '100%', justifyContent: 'center', gap: '0.5rem' }}>
                                    {applying === op.id ? <Loader size={16} style={{ animation: 'spin 1s linear infinite' }} /> :
                                        isApplied ? <><CheckCircle2 size={16} />Applied!</> : 'Apply Now'}
                                </button>
                            </div>
                        );
                    })}
                </div>
            )}
            <style>{`@keyframes spin { to { transform: rotate(360deg); } }`}</style>
        </div>
    );
}

const FALLBACK_OPPS = [
    { id: '1', title: 'Full Stack Engineering Intern', organization: 'TechNova', location: 'Remote', duration: '6 Months', requiredSkills: ['React', 'Node.js', 'PostgreSQL'], description: 'Build scalable web applications with a modern tech stack in an agile environment.' },
    { id: '2', title: 'Data Science Intern', organization: 'Quantium Labs', location: 'Remote', duration: '4 Months', requiredSkills: ['Python', 'TensorFlow', 'SQL'], description: 'Analyze large datasets and build machine learning models for real-world predictions.' },
    { id: '3', title: 'DevOps & Cloud Intern', organization: 'CloudWorks', location: 'Hybrid', duration: '6 Months', requiredSkills: ['AWS', 'Docker', 'CI/CD'], description: 'Design and manage cloud infrastructure, pipelines, and deployment automation.' },
];
