import React, { useState } from 'react';
import { Link, useNavigate } from 'react-router-dom';
import { Rocket, Mail, Lock, Eye, EyeOff, Loader } from 'lucide-react';
import { supabase } from '../lib/supabaseClient';

export default function LoginPage() {
    const [email, setEmail] = useState('');
    const [password, setPassword] = useState('');
    const [showPassword, setShowPassword] = useState(false);
    const [loading, setLoading] = useState(false);
    const [error, setError] = useState('');
    const navigate = useNavigate();

    const handleLogin = async (e) => {
        e.preventDefault();
        setLoading(true);
        setError('');
        const { error } = await supabase.auth.signInWithPassword({ email, password });
        if (error) {
            setError(error.message);
        } else {
            navigate('/dashboard');
        }
        setLoading(false);
    };

    const handleGoogleLogin = async () => {
        await supabase.auth.signInWithOAuth({
            provider: 'google',
            options: { redirectTo: `${window.location.origin}/dashboard` },
        });
    };

    return (
        <div style={{
            minHeight: '100vh', display: 'flex', alignItems: 'center', justifyContent: 'center',
            background: 'radial-gradient(ellipse at 60% 30%, hsla(var(--primary-accent),0.15) 0%, transparent 60%)',
            padding: '1rem'
        }}>
            <div className="glass-panel animate-fade-in" style={{ width: '100%', maxWidth: '440px', padding: '2.5rem', borderRadius: 'var(--radius-xl)' }}>
                {/* Logo */}
                <div className="flex-center" style={{ marginBottom: '2rem', gap: '0.5rem' }}>
                    <div style={{ background: 'hsla(var(--primary-accent),0.15)', padding: '0.75rem', borderRadius: 'var(--radius-md)' }}>
                        <Rocket color="hsl(var(--primary-accent))" size={28} />
                    </div>
                    <span style={{ fontFamily: 'var(--font-display)', fontWeight: 700, fontSize: '1.5rem' }}>
                        Intern<span className="text-gradient">Track</span>
                    </span>
                </div>

                <h2 style={{ textAlign: 'center', marginBottom: '0.5rem', fontSize: '1.75rem' }}>Welcome back</h2>
                <p style={{ textAlign: 'center', color: 'hsl(var(--text-secondary))', marginBottom: '2rem', fontSize: '0.9rem' }}>
                    Sign in to manage your internship journey
                </p>

                {error && (
                    <div style={{ background: 'hsla(var(--danger),0.12)', border: '1px solid hsla(var(--danger),0.3)', borderRadius: 'var(--radius-md)', padding: '0.75rem 1rem', marginBottom: '1.5rem', color: 'hsl(var(--danger))', fontSize: '0.875rem' }}>
                        {error}
                    </div>
                )}

                <form onSubmit={handleLogin} style={{ display: 'flex', flexDirection: 'column', gap: '1rem' }}>
                    <div>
                        <label style={{ display: 'block', marginBottom: '0.5rem', fontSize: '0.875rem', color: 'hsl(var(--text-secondary))' }}>Email</label>
                        <div style={{ position: 'relative' }}>
                            <Mail size={18} style={{ position: 'absolute', left: '1rem', top: '50%', transform: 'translateY(-50%)', color: 'hsl(var(--text-muted))' }} />
                            <input
                                type="email" required value={email} onChange={e => setEmail(e.target.value)}
                                placeholder="you@university.edu"
                                style={{ width: '100%', padding: '0.875rem 1rem 0.875rem 2.75rem', borderRadius: 'var(--radius-md)', border: '1px solid var(--border-subtle)', background: 'hsla(var(--bg-surface-elevated),0.6)', color: 'hsl(var(--text-primary))', fontSize: '1rem', outline: 'none', boxSizing: 'border-box' }}
                            />
                        </div>
                    </div>

                    <div>
                        <label style={{ display: 'block', marginBottom: '0.5rem', fontSize: '0.875rem', color: 'hsl(var(--text-secondary))' }}>Password</label>
                        <div style={{ position: 'relative' }}>
                            <Lock size={18} style={{ position: 'absolute', left: '1rem', top: '50%', transform: 'translateY(-50%)', color: 'hsl(var(--text-muted))' }} />
                            <input
                                type={showPassword ? 'text' : 'password'} required value={password} onChange={e => setPassword(e.target.value)}
                                placeholder="••••••••"
                                style={{ width: '100%', padding: '0.875rem 3rem 0.875rem 2.75rem', borderRadius: 'var(--radius-md)', border: '1px solid var(--border-subtle)', background: 'hsla(var(--bg-surface-elevated),0.6)', color: 'hsl(var(--text-primary))', fontSize: '1rem', outline: 'none', boxSizing: 'border-box' }}
                            />
                            <button type="button" onClick={() => setShowPassword(!showPassword)}
                                style={{ position: 'absolute', right: '1rem', top: '50%', transform: 'translateY(-50%)', background: 'none', border: 'none', cursor: 'pointer', color: 'hsl(var(--text-muted))', padding: 0 }}>
                                {showPassword ? <EyeOff size={18} /> : <Eye size={18} />}
                            </button>
                        </div>
                    </div>

                    <button type="submit" className="btn btn-primary" disabled={loading}
                        style={{ width: '100%', padding: '0.875rem', fontSize: '1rem', marginTop: '0.5rem', justifyContent: 'center' }}>
                        {loading ? <Loader size={20} style={{ animation: 'spin 1s linear infinite' }} /> : 'Sign In'}
                    </button>
                </form>

                <div style={{ display: 'flex', alignItems: 'center', gap: '1rem', margin: '1.5rem 0' }}>
                    <div style={{ flex: 1, height: '1px', background: 'var(--border-subtle)' }}></div>
                    <span style={{ color: 'hsl(var(--text-muted))', fontSize: '0.8rem' }}>or continue with</span>
                    <div style={{ flex: 1, height: '1px', background: 'var(--border-subtle)' }}></div>
                </div>

                <button onClick={handleGoogleLogin} className="btn btn-secondary" style={{ width: '100%', padding: '0.875rem', fontSize: '1rem', justifyContent: 'center', gap: '0.5rem' }}>
                    <svg width="20" height="20" viewBox="0 0 24 24"><path fill="#4285F4" d="M22.56 12.25c0-.78-.07-1.53-.2-2.25H12v4.26h5.92c-.26 1.37-1.04 2.53-2.21 3.31v2.77h3.57c2.08-1.92 3.28-4.74 3.28-8.09z" /><path fill="#34A853" d="M12 23c2.97 0 5.46-.98 7.28-2.66l-3.57-2.77c-.98.66-2.23 1.06-3.71 1.06-2.86 0-5.29-1.93-6.16-4.53H2.18v2.84C3.99 20.53 7.7 23 12 23z" /><path fill="#FBBC05" d="M5.84 14.09c-.22-.66-.35-1.36-.35-2.09s.13-1.43.35-2.09V7.07H2.18C1.43 8.55 1 10.22 1 12s.43 3.45 1.18 4.93l2.85-2.22.81-.62z" /><path fill="#EA4335" d="M12 5.38c1.62 0 3.06.56 4.21 1.64l3.15-3.15C17.45 2.09 14.97 1 12 1 7.7 1 3.99 3.47 2.18 7.07l3.66 2.84c.87-2.6 3.3-4.53 6.16-4.53z" /></svg>
                    Google
                </button>

                <p style={{ textAlign: 'center', marginTop: '1.5rem', color: 'hsl(var(--text-secondary))', fontSize: '0.875rem' }}>
                    Don't have an account?{' '}
                    <Link to="/signup" style={{ color: 'hsl(var(--primary-accent))', fontWeight: 600 }}>Sign Up</Link>
                </p>
            </div>

            <style>{`@keyframes spin { to { transform: rotate(360deg); } }`}</style>
        </div>
    );
}
