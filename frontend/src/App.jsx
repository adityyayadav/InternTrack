import React from 'react';
import { BrowserRouter, Routes, Route, Navigate, Outlet } from 'react-router-dom';
import { AuthProvider, useAuth } from './context/AuthContext';
import Layout from './components/Layout';
import LandingPage from './pages/LandingPage';
import LoginPage from './pages/LoginPage';
import SignupPage from './pages/SignupPage';
import DashboardPage from './pages/DashboardPage';
import OpportunitiesPage from './pages/OpportunitiesPage';
import ApplicationsPage from './pages/ApplicationsPage';
import WeeklyReportsPage from './pages/WeeklyReportsPage';
import NotificationsPage from './pages/NotificationsPage';
import ProfilePage from './pages/ProfilePage';
import { Loader } from 'lucide-react';

function ProtectedRoute() {
  const { session, loading } = useAuth();
  if (loading) {
    return (
      <div style={{ minHeight: '100vh', display: 'flex', alignItems: 'center', justifyContent: 'center' }}>
        <Loader size={40} color="hsl(var(--primary-accent))" style={{ animation: 'spin 1s linear infinite' }} />
        <style>{`@keyframes spin { to { transform: rotate(360deg); } }`}</style>
      </div>
    );
  }
  return session ? <Outlet /> : <Navigate to="/login" replace />;
}

function GuestRoute() {
  const { session, loading } = useAuth();
  if (loading) return null;
  return session ? <Navigate to="/dashboard" replace /> : <Outlet />;
}

function AppRoutes() {
  return (
    <Routes>
      {/* Public landing page with nav */}
      <Route path="/" element={<Layout />}>
        <Route index element={<LandingPage />} />
      </Route>

      {/* Guest-only auth pages (no nav) */}
      <Route element={<GuestRoute />}>
        <Route path="/login" element={<LoginPage />} />
        <Route path="/signup" element={<SignupPage />} />
      </Route>

      {/* Protected app pages with nav */}
      <Route element={<ProtectedRoute />}>
        <Route element={<Layout />}>
          <Route path="/dashboard" element={<DashboardPage />} />
          <Route path="/opportunities" element={<OpportunitiesPage />} />
          <Route path="/applications" element={<ApplicationsPage />} />
          <Route path="/reports" element={<WeeklyReportsPage />} />
          <Route path="/notifications" element={<NotificationsPage />} />
          <Route path="/profile" element={<ProfilePage />} />
        </Route>
      </Route>

      <Route path="*" element={<Navigate to="/" replace />} />
    </Routes>
  );
}

export default function App() {
  return (
    <AuthProvider>
      <BrowserRouter>
        <AppRoutes />
      </BrowserRouter>
    </AuthProvider>
  );
}
