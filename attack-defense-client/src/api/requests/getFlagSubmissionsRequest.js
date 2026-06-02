import { message } from 'antd';
import { api } from '../axiosInstance';
import { handleRequest } from './handleRequest';

export const axiosGetFlagSubmissions = async (page = 0, size = 10) => {
  const params = {
    page,
    size,
  };

  return handleRequest(() => api.get('/dashboard/submissions', { params }), {
    onError: () => message.error('Ошибка при получении списка флагов'),
    rethrow: true,
  });
};
