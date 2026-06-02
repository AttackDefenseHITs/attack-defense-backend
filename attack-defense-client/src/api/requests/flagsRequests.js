import { message } from 'antd';
import { api } from '../axiosInstance';
import { handleRequest } from './handleRequest';

// Получение всех флагов
export const axiosGetAllFlags = async (page = 0, size = 10, search = '', isActive = null) => {
  const params = {
    page,
    size,
    search
  };

  if (isActive !== null && isActive !== undefined) {
    params.isActive = isActive;
  }

  return handleRequest(() => api.get('/admin/flags', { params }), {
    onError: () => message.error('Ошибка при получении списка флагов'),
    rethrow: true,
  });
};
// Переключение статуса флага
export const axiosToggleFlagStatus = async (flagId) => {
  return handleRequest(() => api.put(`/admin/flags/${flagId}`), {
    onError: () => message.error('Ошибка при обновлении статуса флага'),
    rethrow: true,
  });
};

// Удаление флага
export const axiosDeleteFlag = async (flagId) => {
  return handleRequest(() => api.delete(`/admin/flags/${flagId}`), {
    onError: () => message.error('Ошибка при удалении флага'),
    rethrow: true,
  });
}; 
