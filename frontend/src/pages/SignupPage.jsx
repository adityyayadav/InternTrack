import React, { useState } from 'react';
import { Link, useNavigate } from 'react-router-dom';
import { Rocket, Mail, Lock, User, Eye, EyeOff, Loader } from 'lucide-react';
import { supabase } from '../lib/supabaseClient';

export default function SignupPage() {
    const [form, setForm] = useState({ name: '', email: '', password: '', role: 'STUDENT' });
    const [showPassword, setShowPassword] = useState(false);
    const [loading, setLoading] = useState(false);
    const [error, setError] = useState('');
    const [success, setSuccess] = useState(false);
    const navigate = useNavigate();

    const handleChange = (e) => setForm(prev => ({ ...prev, [e.target.name]: e.target.value }));

    const handleSignup = async (e) => {
        e.preventDefault();
        setLoading(true);
        setError('');

        const { error } = await supabase.auth.signUp({
            email: form.email,
            password: form.password,
            options: {
                data: { full_name: form.name, role: form.role },
            },
        });

        if (error) {
            setError(error.message);
        } else {
            setSuccess(true);
        }
        setLoading(false);
    };

    if (success) {
        return (
            <div style={{ minHeight: '100vh', display: 'flex', alignItems: 'center', justifyContent: 'center', padding: '1rem' }}>
                <div className="glass-panel animate-fade-in" style={{ maxWidth: '440px', width: '100%', padding: '2.5rem', borderRadius: 'var(--radius-xl)', textAlign: 'center' }}>
                    <div style={{ background: 'hsla(var(--success),0.1)', borderRadius: 'var(--radius-full)', width: '4rem', height: '4rem', display: 'flex', alignItems: 'center', justifyContent: 'center', margin: '0 auto 1.5rem' }}>
                        <Mail color="hsl(var(--success))" size={28} />
                    </div>
                    <h2 style={{ marginBottom: '0.5rem' }}>Check your email</h2>
                    <p style={{ color: 'hsl(var(--text-secondary))', marginBottom: '1.5rem' }}>
                        We sent a confirmation link to <strong>{form.email}</strong>. Click it to activate your account and then sign in.
                    </p>
                    <Link to="/login" className="btn btn-primary" style={{ display: 'inline-flex', padding: '0.875rem 2rem' }}>Go to Login</Link>
                </div>
            </div>
        );
    }

    return (
        <div style={{
            minHeight: '100vh', display: 'flex', alignItems: 'center', justifyContent: 'center',
            background: 'radial-gradient(ellipse at 40% 70%, hsla(var(--secondary-accent),0.12) 0%, transparent 60%)',
            padding: '1rem'
        }}>
            <div className="glass-panel animate-fade-in" style={{ width: '100%', maxWidth: '440px', padding: '2.5rem', borderRadius: 'var(--radius-xl)' }}>
                <div className="flex-center" style={{ marginBottom: '2rem', gap: '0.5rem' }}>
                    <div style={{ background: 'hsla(var(--primary-accent),0.15)', padding: '0.75rem', borderRadius: 'var(--radius-md)' }}>
                        <Rocket color="hsl(var(--primary-accent))" size={28} />
                    </div>
                    <span style={{ fontFamily: 'var(--font-display)', fontWeight: 700, fontSize: '1.5rem' }}>
                        Intern<span className="text-gradient">Track</span>
                    </span>
                </div>

                <h2 style={{ textAlign: 'center', marginBottom: '0.5rem', fontSize: '1.75rem' }}>Create account</h2>
                <p style={{ textAlign: 'center', color: 'hsl(var(--text-secondary))', marginBottom: '2rem', fontSize: '0.9rem' }}>
                    Join InternTrack and start your internship journey
                </p>

                {error && (
                    <div style={{ background: 'hsla(var(--danger),0.12)', border: '1px solid hsla(var(--danger),0.3)', borderRadius: 'var(--radius-md)', padding: '0.75rem 1rem', marginBottom: '1.5rem', color: 'hsl(var(--danger))', fontSize: '0.875rem' }}>
                        {error}
                    </div>
                )}

                <form onSubmit={handleSignup} style={{ display: 'flex', flexDirection: 'column', gap: '1rem' }}>
                    <div>
                        <label style={{ display: 'block', marginBottom: '0.5rem', fontSize: '0.875rem', color: 'hsl(var(--text-secondary))' }}>Full Name</label>
                        <div style={{ position: 'relative' }}>
                            <User size={18} style={{ position: 'absolute', left: '1rem', top: '50%', transform: 'translateY(-50%)', color: 'hsl(var(--text-muted))' }} />
                            <input name="name" type="text" required value={form.name} onChange={handleChange}
                                placeholder="Jane Smith"
                                style={{ width: '100%', padding: '0.875rem 1rem 0.875rem 2.75rem', borderRadius: 'var(--radius-md)', border: '1px solid var(--border-subtle)', background: 'hsla(var(--bg-surface-elevated),0.6)', color: 'hsl(var(--text-primary))', fontSize: '1rem', outline: 'none', boxSizing: 'border-box' }}
                            />
                        </div>
                    </div>

                    <div>
                        <label style={{ display: 'block', marginBottom: '0.5rem', fontSize: '0.875rem', color: 'hsl(var(--text-secondary))' }}>Email</label>
                        <div style={{ position: 'relative' }}>
                            <Mail size={18} style={{ position: 'absolute', left: '1rem', top: '50%', transform: 'translateY(-50%)', color: 'hsl(var(--text-muted))' }} />
                            <input name="email" type="email" required value={form.email} onChange={handleChange}
                                placeholder="you@university.edu"
                                style={{ width: '100%', padding: '0.875rem 1rem 0.875rem 2.75rem', borderRadius: 'var(--radius-md)', border: '1px solid var(--border-subtle)', background: 'hsla(var(--bg-surface-elevated),0.6)', color: 'hsl(var(--text-primary))', fontSize: '1rem', outline: 'none', boxSizing: 'border-box' }}
                            />
                        </div>
                    </div>

                    <div>
                        <label style={{ display: 'block', marginBottom: '0.5rem', fontSize: '0.875rem', color: 'hsl(var(--text-secondary))' }}>I am a</label>
                        <select name="role" value={form.role} onChange={handleChange}
                            style={{ width: '100%', padding: '0.875rem 1rem', borderRadius: 'var(--radius-md)', border: '1px solid var(--border-subtle)', background: 'hsla(var(--bg-surface-elevated),0.9)', color: 'hsl(var(--text-primary))', fontSize: '1rem', outline: 'none', cursor: 'pointer' }}>
                            <option value="STUDENT">Student</option>
                            <option value="FACULTY">Faculty / Supervisor</option>
                        </select>
                    </div>

                    <div>
                        <label style={{ display: 'block', marginBottom: '0.5rem', fontSize: '0.875rem', color: 'hsl(var(--text-secondary))' }}>Password</label>
                        <div style={{ position: 'relative' }}>
                            <Lock size={18} style={{ position: 'absolute', left: '1rem', top: '50%', transform: 'translateY(-50%)', color: 'hsl(var(--text-muted))' }} />
                            <input name="password" type={showPassword ? 'text' : 'password'} required minLength={8} value={form.password} onChange={handleChange}
                                placeholder="At least 8 characters"
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
                        {loading ? <Loader size={20} style={{ animation: 'spin 1s linear infinite' }} /> : 'Create Account'}
                    </button>
                </form>

                <p style={{ textAlign: 'center', marginTop: '1.5rem', color: 'hsl(var(--text-secondary))', fontSize: '0.875rem' }}>
                    Already have an account?{' '}
                    <Link to="/login" style={{ color: 'hsl(var(--primary-accent))', fontWeight: 600 }}>Sign In</Link>
                </p>
            </div>
            <style>{`@keyframes spin { to { transform: rotate(360deg); } }`}</style>
        </div>
    );
}
