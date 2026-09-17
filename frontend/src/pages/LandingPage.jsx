import React from 'react';
import { ArrowRight, Briefcase, FileText, CheckCircle, ShieldCheck } from 'lucide-react';
import { Link } from 'react-router-dom';

export default function LandingPage() {
    return (
        <div className="animate-fade-in">
            {/* Hero Section */}
            <section style={{
                padding: 'var(--space-12) 0',
                textAlign: 'center',
                background: 'radial-gradient(circle at top, hsla(var(--primary-accent), 0.15) 0%, transparent 70%)'
            }}>
                <div className="container">
                    <div className="badge badge-info" style={{ marginBottom: 'var(--space-4)' }}>
                        Welcome to InternTrack 2.0
                    </div>
                    <h1 style={{ marginBottom: 'var(--space-4)', maxWidth: '800px', margin: '0 auto var(--space-4)' }}>
                        Bridge the Gap Between <span className="text-gradient">Campus and Career</span>
                    </h1>
                    <p style={{
                        color: 'hsl(var(--text-secondary))',
                        fontSize: '1.125rem',
                        maxWidth: '600px',
                        margin: '0 auto var(--space-6)'
                    }}>
                        The premier intelligent internship management platform designed for students pursuing top-tier placements, and academic institutions demanding rigorous oversight.
                    </p>
                    <div className="flex-center" style={{ gap: '1rem' }}>
                        <Link to="/opportunities" className="btn btn-primary" style={{ padding: '0.75rem 1.5rem' }}>
                            Explore Opportunities <ArrowRight size={18} />
                        </Link>
                        <Link to="/dashboard" className="btn btn-secondary" style={{ padding: '0.75rem 1.5rem' }}>
                            Access Dashboard
                        </Link>
                    </div>
                </div>
            </section>

            {/* Features Outline */}
            <section style={{ padding: 'var(--space-12) 0', background: 'hsla(var(--bg-surface-elevated), 0.3)' }}>
                <div className="container">
                    <div style={{ textAlign: 'center', marginBottom: 'var(--space-8)' }}>
                        <h2 style={{ marginBottom: 'var(--space-2)' }}>Intelligent Internship Lifecycles</h2>
                        <p style={{ color: 'hsl(var(--text-muted))' }}>Streamlined tools from application tracking to final compliance reports.</p>
                    </div>

                    <div style={{
                        display: 'grid',
                        gridTemplateColumns: 'repeat(auto-fit, minmax(250px, 1fr))',
                        gap: 'var(--space-6)'
                    }}>
                        <div className="card card-hover stagger-1 text-center" style={{ textAlign: 'center' }}>
                            <div className="flex-center" style={{ marginBottom: 'var(--space-4)' }}>
                                <div style={{ background: 'hsla(var(--secondary-accent), 0.1)', padding: '1rem', borderRadius: 'var(--radius-full)' }}>
                                    <Briefcase color="hsl(var(--secondary-accent))" size={32} />
                                </div>
                            </div>
                            <h3 style={{ fontSize: '1.25rem', marginBottom: 'var(--space-2)' }}>Premium Opportunities</h3>
                            <p style={{ color: 'hsl(var(--text-secondary))', fontSize: '0.9rem' }}>
                                Access verified internships from leading technology partners and scale your technical skillset globally.
                            </p>
                        </div>

                        <div className="card card-hover stagger-2 text-center" style={{ textAlign: 'center' }}>
                            <div className="flex-center" style={{ marginBottom: 'var(--space-4)' }}>
                                <div style={{ background: 'hsla(var(--primary-accent), 0.1)', padding: '1rem', borderRadius: 'var(--radius-full)' }}>
                                    <FileText color="hsl(var(--primary-accent))" size={32} />
                                </div>
                            </div>
                            <h3 style={{ fontSize: '1.25rem', marginBottom: 'var(--space-2)' }}>Weekly Analytics</h3>
                            <p style={{ color: 'hsl(var(--text-secondary))', fontSize: '0.9rem' }}>
                                Automated weekly reporting and AI-powered document intelligence that tracks progress seamlessly without overhead.
                            </p>
                        </div>

                        <div className="card card-hover stagger-3 text-center" style={{ textAlign: 'center' }}>
                            <div className="flex-center" style={{ marginBottom: 'var(--space-4)' }}>
                                <div style={{ background: 'hsla(var(--success), 0.1)', padding: '1rem', borderRadius: 'var(--radius-full)' }}>
                                    <ShieldCheck color="hsl(var(--success))" size={32} />
                                </div>
                            </div>
                            <h3 style={{ fontSize: '1.25rem', marginBottom: 'var(--space-2)' }}>Institutional Compliance</h3>
                            <p style={{ color: 'hsl(var(--text-secondary))', fontSize: '0.9rem' }}>
                                Robust faculty approval workflows and compliance verification built to satisfy rigorous academic requirements.
                            </p>
                        </div>
                    </div>
                </div>
            </section>

            {/* CTA Section */}
            <section style={{ padding: 'var(--space-12) 0' }}>
                <div className="container flex-center">
                    <div className="card text-center" style={{ maxWidth: '800px', width: '100%', borderColor: 'hsla(var(--secondary-accent), 0.2)', padding: '3rem 2rem' }}>
                        <h2 style={{ marginBottom: 'var(--space-4)' }}>Ready to accelerate your career?</h2>
                        <p style={{ color: 'hsl(var(--text-secondary))', marginBottom: 'var(--space-6)', maxWidth: '500px', margin: '0 auto var(--space-6)' }}>
                            Join thousands of students and academic institutions managing their internship lifecycle through InternTrack.
                        </p>
                        <Link to="/opportunities" className="btn btn-primary" style={{ padding: '1rem 2rem', fontSize: '1rem' }}>
                            Get Started Today
                        </Link>
                    </div>
                </div>
            </section>
        </div>
    );
}
