import axios from 'axios';
import { API_URLS } from '../constants/apiUrls';
import { ROUTES } from '../constants/routes';

export const api = axios.create({
  baseURL: API_URLS.BASE,
  headers: {
    'Authorization': `Bearer ${localStorage.getItem('accessToken')}`
  }
});

api.interceptors.request.use(
  (config) => {
    const token = localStorage.getItem('accessToken');
    if (token) {
      config.headers['Authorization'] = `Bearer ${token}`;
    }
    return config;
  },
  (error) => {
    return Promise.reject(error);
  }
);

api.interceptors.response.use(
  (response) => response,
  async (error) => {
    if (error.response) {
      const isOnLoginOrRegistrationPage = window.location.pathname === ROUTES.LOGIN || window.location.pathname === ROUTES.REGISTRATION;
      if (error.response.status === 401 && !isOnLoginOrRegistrationPage) {
        const refreshToken = localStorage.getItem('refreshToken');
        if (refreshToken && !isOnLoginOrRegistrationPage) {
          try {
            const refreshResponse = await api.post(API_URLS.REFRESH_TOKEN, {
              refreshToken
            });

            const newTokens = refreshResponse.data;

            localStorage.setItem('accessToken', newTokens.accessToken);
            localStorage.setItem('refreshToken', newTokens.refreshToken);

            error.config.headers['Authorization'] = `Bearer ${newTokens.accessToken}`;
            return axios(error.config);
          } catch (refreshError) {
            console.error('Refresh token request failed:', refreshError);
            localStorage.removeItem('accessToken');
            localStorage.removeItem('refreshToken');
            window.location.href = ROUTES.LOGIN;
          }
        } else {
          localStorage.removeItem('accessToken');
          localStorage.removeItem('refreshToken');
          window.location.href = ROUTES.LOGIN;
        }
      }
    }

    return Promise.reject(error);
  }
);
