import axios from 'axios';
import { supabase } from './supabaseClient';

const api = axios.create({
    baseURL: import.meta.env.VITE_API_BASE_URL || 'http://localhost:8080',
    timeout: 30000,
});

// Auto-attach Bearer token from active Supabase session
api.interceptors.request.use(async (config) => {
    const { data: { session } } = await supabase.auth.getSession();
    if (session?.access_token) {
        config.headers.Authorization = `Bearer ${session.access_token}`;
    }
    return config;
});

// Centralized error handling
api.interceptors.response.use(
    (response) => response.data,
    (error) => {
        const message = error.response?.data?.message || error.message || 'An unexpected error occurred';
        return Promise.reject(new Error(message));
    }
);

export default api;
