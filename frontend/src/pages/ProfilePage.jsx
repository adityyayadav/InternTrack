import React, { useEffect, useState } from 'react';
import { User, Mail, BookOpen, Plus, X, Save, Loader, Sparkles, FileText, ChevronDown, ChevronUp } from 'lucide-react';
import api from '../lib/api';
import { useAuth } from '../context/AuthContext';

export default function ProfilePage() {
    const { user } = useAuth();
    const [profile, setProfile] = useState(null);
    const [loading, setLoading] = useState(true);
    const [saving, setSaving] = useState(false);
    const [skillInput, setSkillInput] = useState('');
    const [resumeText, setResumeText] = useState('');
    const [aiResult, setAiResult] = useState(null);
    const [aiLoading, setAiLoading] = useState(false);
    const [aiError, setAiError] = useState('');
    const [aiOpen, setAiOpen] = useState(false);

    useEffect(() => {
        api.get('/api/v1/students/me/profile')
            .then(res => setProfile(res.data || { skills: [], education: '', interests: '', preferredDomains: '' }))
            .catch(() => setProfile({ skills: [], education: '', interests: '', preferredDomains: '' }))
            .finally(() => setLoading(false));
    }, []);

    const handleSave = async () => {
        setSaving(true);
        try {
            await api.put('/api/v1/students/me/profile', profile);
        } catch (e) {
            alert('Save failed: ' + e.message);
        } finally {
            setSaving(false);
        }
    };

    const addSkill = () => {
        const s = skillInput.trim();
        if (!s) return;
        const updated = [...(profile.skills || [])];
        if (!updated.includes(s)) updated.push(s);
        setProfile(p => ({ ...p, skills: updated }));
        setSkillInput('');
    };

    const removeSkill = (skill) => {
        setProfile(p => ({ ...p, skills: p.skills.filter(s => s !== skill) }));
    };

    const handleAiAnalyze = async () => {
        if (!resumeText.trim()) { setAiError('Please paste your resume text first.'); return; }
        setAiLoading(true);
        setAiError('');
        try {
            const res = await api.post('/api/ai/analyze-resume', { resumeText });
            setAiResult(res.data);
        } catch (e) {
            setAiError(e.message);
        } finally {
            setAiLoading(false);
        }
    };

    const applyExtractedSkills = () => {
        if (!aiResult?.skills) return;
        const merged = [...new Set([...(profile.skills || []), ...aiResult.skills])];
        setProfile(p => ({ ...p, skills: merged }));
    };

    if (loading) return (
        <div style={{ display: 'flex', alignItems: 'center', justifyContent: 'center', minHeight: '60vh', gap: '1rem' }}>
            <Loader size={32} color="hsl(var(--primary-accent))" style={{ animation: 'spin 1s linear infinite' }} />
            <style>{`@keyframes spin { to { transform: rotate(360deg); } }`}</style>
        </div>
    );

    return (
        <div className="container animate-fade-in" style={{ padding: 'var(--space-6) var(--space-4)', marginTop: '2rem', maxWidth: '900px' }}>
            <div className="flex-between" style={{ marginBottom: 'var(--space-6)', flexWrap: 'wrap', gap: '1rem' }}>
                <div>
                    <h2>My Profile</h2>
                    <p style={{ color: 'hsl(var(--text-secondary))' }}>Manage your skills, interests, and education for better internship matches.</p>
                </div>
                <button onClick={handleSave} disabled={saving} className="btn btn-primary" style={{ display: 'flex', alignItems: 'center', gap: '0.5rem' }}>
                    {saving ? <Loader size={16} style={{ animation: 'spin 1s linear infinite' }} /> : <Save size={16} />}
                    Save Profile
                </button>
            </div>

            <div style={{ display: 'grid', gridTemplateColumns: '1fr 2fr', gap: 'var(--space-6)', alignItems: 'start' }}>
                {/* Left Panel - Info */}
                <div style={{ display: 'flex', flexDirection: 'column', gap: 'var(--space-4)' }}>
                    <div className="card" style={{ textAlign: 'center' }}>
                        <div style={{ width: '80px', height: '80px', borderRadius: '50%', background: 'hsla(var(--primary-accent),0.15)', display: 'flex', alignItems: 'center', justifyContent: 'center', margin: '0 auto 1rem', border: '2px solid hsla(var(--primary-accent),0.3)' }}>
                            <User size={36} color="hsl(var(--primary-accent))" />
                        </div>
                        <h3 style={{ marginBottom: '0.25rem' }}>{user?.user_metadata?.full_name || 'Your Name'}</h3>
                        <div className="flex-center" style={{ gap: '0.4rem', color: 'hsl(var(--text-secondary))', fontSize: '0.85rem', marginBottom: '0.25rem' }}>
                            <Mail size={14} /> {user?.email}
                        </div>
                        <span className="badge badge-info" style={{ marginTop: '0.5rem' }}>
                            {user?.user_metadata?.role || 'STUDENT'}
                        </span>
                    </div>

                    {/* Education & Interests */}
                    <div className="card">
                        <h4 style={{ marginBottom: 'var(--space-3)', display: 'flex', alignItems: 'center', gap: '0.5rem' }}>
                            <BookOpen size={18} color="hsl(var(--primary-accent))" /> Education
                        </h4>
                        <textarea rows={3} value={profile?.education || ''} onChange={e => setProfile(p => ({ ...p, education: e.target.value }))}
                            placeholder="e.g. B.Sc. Computer Science, University of XYZ, 2024"
                            style={{ width: '100%', padding: '0.75rem', borderRadius: 'var(--radius-md)', border: '1px solid var(--border-subtle)', background: 'hsla(var(--bg-surface-elevated),0.6)', color: 'hsl(var(--text-primary))', fontSize: '0.875rem', resize: 'vertical', boxSizing: 'border-box', outline: 'none' }} />
                    </div>

                    <div className="card">
                        <h4 style={{ marginBottom: 'var(--space-3)' }}>Interests & Domain</h4>
                        <textarea rows={2} value={profile?.interests || ''} onChange={e => setProfile(p => ({ ...p, interests: e.target.value }))}
                            placeholder="e.g. Machine Learning, Web Development"
                            style={{ width: '100%', padding: '0.75rem', borderRadius: 'var(--radius-md)', border: '1px solid var(--border-subtle)', background: 'hsla(var(--bg-surface-elevated),0.6)', color: 'hsl(var(--text-primary))', fontSize: '0.875rem', resize: 'vertical', boxSizing: 'border-box', outline: 'none', marginBottom: '0.75rem' }} />
                        <textarea rows={2} value={profile?.preferredDomains || ''} onChange={e => setProfile(p => ({ ...p, preferredDomains: e.target.value }))}
                            placeholder="Preferred domains: e.g. FinTech, AI Research"
                            style={{ width: '100%', padding: '0.75rem', borderRadius: 'var(--radius-md)', border: '1px solid var(--border-subtle)', background: 'hsla(var(--bg-surface-elevated),0.6)', color: 'hsl(var(--text-primary))', fontSize: '0.875rem', resize: 'vertical', boxSizing: 'border-box', outline: 'none' }} />
                    </div>
                </div>

                {/* Right Panel - Skills + AI */}
                <div style={{ display: 'flex', flexDirection: 'column', gap: 'var(--space-4)' }}>
                    {/* Skills */}
                    <div className="card">
                        <h4 style={{ marginBottom: 'var(--space-3)' }}>Skills</h4>
                        <div style={{ display: 'flex', gap: '0.5rem', marginBottom: '1rem' }}>
                            <input value={skillInput} onChange={e => setSkillInput(e.target.value)}
                                onKeyDown={e => e.key === 'Enter' && (e.preventDefault(), addSkill())}
                                placeholder="Add a skill (press Enter)…"
                                style={{ flex: 1, padding: '0.6rem 0.875rem', borderRadius: 'var(--radius-md)', border: '1px solid var(--border-subtle)', background: 'hsla(var(--bg-surface-elevated),0.6)', color: 'hsl(var(--text-primary))', fontSize: '0.9rem', outline: 'none' }} />
                            <button onClick={addSkill} className="btn btn-primary" style={{ padding: '0.6rem 1rem', flexShrink: 0 }}>
                                <Plus size={18} />
                            </button>
                        </div>
                        <div style={{ display: 'flex', flexWrap: 'wrap', gap: '0.5rem' }}>
                            {(profile?.skills || []).length === 0 ? (
                                <p style={{ color: 'hsl(var(--text-muted))', fontSize: '0.875rem' }}>No skills added yet. Add some or use AI to extract them.</p>
                            ) : (
                                (profile.skills).map(skill => (
                                    <span key={skill} style={{ display: 'inline-flex', alignItems: 'center', gap: '0.375rem', background: 'hsla(var(--primary-accent),0.12)', border: '1px solid hsla(var(--primary-accent),0.25)', borderRadius: 'var(--radius-full)', padding: '0.3rem 0.75rem', fontSize: '0.85rem', color: 'hsl(var(--primary-accent))' }}>
                                        {skill}
                                        <button onClick={() => removeSkill(skill)} style={{ background: 'none', border: 'none', cursor: 'pointer', padding: 0, display: 'flex', color: 'inherit', opacity: 0.6 }}>
                                            <X size={14} />
                                        </button>
                                    </span>
                                ))
                            )}
                        </div>
                    </div>

                    {/* AI Resume Analyzer */}
                    <div className="card" style={{ borderColor: 'hsla(var(--primary-accent),0.2)' }}>
                        <button onClick={() => setAiOpen(!aiOpen)} style={{ width: '100%', background: 'none', border: 'none', cursor: 'pointer', display: 'flex', alignItems: 'center', justifyContent: 'space-between', padding: 0 }}>
                            <div className="flex-center" style={{ gap: '0.75rem' }}>
                                <div style={{ background: 'hsla(var(--primary-accent),0.15)', padding: '0.625rem', borderRadius: 'var(--radius-md)' }}>
                                    <Sparkles size={20} color="hsl(var(--primary-accent))" />
                                </div>
                                <div style={{ textAlign: 'left' }}>
                                    <h4 style={{ color: 'hsl(var(--primary-accent))', marginBottom: '0.1rem' }}>AI Resume Analyzer</h4>
                                    <p style={{ color: 'hsl(var(--text-secondary))', fontSize: '0.8rem' }}>Extract skills, education &amp; experience from your resume.</p>
                                </div>
                            </div>
                            {aiOpen ? <ChevronUp size={20} color="hsl(var(--text-muted))" /> : <ChevronDown size={20} color="hsl(var(--text-muted))" />}
                        </button>

                        {aiOpen && (
                            <div style={{ marginTop: '1.25rem' }}>
                                <label style={{ display: 'block', marginBottom: '0.5rem', fontSize: '0.875rem', color: 'hsl(var(--text-secondary))' }}>
                                    <FileText size={14} style={{ verticalAlign: 'middle', marginRight: '0.375rem' }} />
                                    Paste your resume text below
                                </label>
                                <textarea rows={8} value={resumeText} onChange={e => setResumeText(e.target.value)}
                                    placeholder="Paste the full text content of your resume here…"
                                    style={{ width: '100%', padding: '0.75rem', borderRadius: 'var(--radius-md)', border: '1px solid var(--border-subtle)', background: 'hsla(var(--bg-surface-elevated),0.6)', color: 'hsl(var(--text-primary))', fontSize: '0.875rem', resize: 'vertical', boxSizing: 'border-box', outline: 'none', marginBottom: '0.75rem' }} />
                                {aiError && <p style={{ color: 'hsl(var(--danger))', fontSize: '0.8rem', marginBottom: '0.75rem' }}>{aiError}</p>}
                                <div style={{ display: 'flex', gap: '0.75rem' }}>
                                    <button onClick={handleAiAnalyze} disabled={aiLoading} className="btn btn-primary" style={{ display: 'flex', alignItems: 'center', gap: '0.5rem' }}>
                                        {aiLoading ? <Loader size={16} style={{ animation: 'spin 1s linear infinite' }} /> : <Sparkles size={16} />}
                                        {aiLoading ? 'Analyzing…' : 'Analyze Resume'}
                                    </button>
                                    {aiResult?.skills?.length > 0 && (
                                        <button onClick={applyExtractedSkills} className="btn btn-secondary" style={{ display: 'flex', alignItems: 'center', gap: '0.5rem' }}>
                                            <Plus size={16} /> Apply Extracted Skills
                                        </button>
                                    )}
                                </div>

                                {aiResult && (
                                    <div style={{ marginTop: '1.25rem', padding: '1rem', background: 'hsla(var(--bg-surface-elevated),0.4)', borderRadius: 'var(--radius-md)', border: '1px solid var(--border-subtle)' }}>
                                        <h5 style={{ marginBottom: '0.75rem', color: 'hsl(var(--primary-accent))' }}>Extracted Information</h5>
                                        {aiResult.skills?.length > 0 && (
                                            <div style={{ marginBottom: '0.75rem' }}>
                                                <p style={{ fontSize: '0.8rem', color: 'hsl(var(--text-muted))', marginBottom: '0.375rem', textTransform: 'uppercase', letterSpacing: '0.05em' }}>Skills</p>
                                                <div style={{ display: 'flex', flexWrap: 'wrap', gap: '0.375rem' }}>
                                                    {aiResult.skills.map(s => <span key={s} className="badge badge-info" style={{ fontSize: '0.75rem' }}>{s}</span>)}
                                                </div>
                                            </div>
                                        )}
                                        {aiResult.education && (
                                            <div style={{ marginBottom: '0.75rem' }}>
                                                <p style={{ fontSize: '0.8rem', color: 'hsl(var(--text-muted))', marginBottom: '0.25rem', textTransform: 'uppercase', letterSpacing: '0.05em' }}>Education</p>
                                                <p style={{ fontSize: '0.875rem', color: 'hsl(var(--text-secondary))' }}>{aiResult.education}</p>
                                            </div>
                                        )}
                                        {aiResult.summary && (
                                            <div>
                                                <p style={{ fontSize: '0.8rem', color: 'hsl(var(--text-muted))', marginBottom: '0.25rem', textTransform: 'uppercase', letterSpacing: '0.05em' }}>Summary</p>
                                                <p style={{ fontSize: '0.875rem', color: 'hsl(var(--text-secondary))' }}>{aiResult.summary}</p>
                                            </div>
                                        )}
                                    </div>
                                )}
                            </div>
                        )}
                    </div>
                </div>
            </div>
            <style>{`@keyframes spin { to { transform: rotate(360deg); } }`}</style>
        </div>
    );
}
