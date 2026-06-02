import { message } from 'antd';
import { api } from '../axiosInstance';
import { handleRequest } from './handleRequest';

// Получение всех пользователей
export const axiosGetAllUsers = async () => {
  return handleRequest(() => api.get('/admin/users'), {
    onError: () => message.error('Ошибка при получении списка пользователей'),
    rethrow: true,
  });
};

// Изменение роли пользователя
export const axiosSetUserRole = async (userId, role) => {
  return handleRequest(() => api.post(`/admin/users/${userId}`, { role }), {
    onError: () => message.error('Ошибка при изменении роли пользователя'),
    rethrow: true,
  });
}; 
