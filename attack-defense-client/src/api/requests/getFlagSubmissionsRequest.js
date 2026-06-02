import { message } from 'antd';
import { api } from '../axiosInstance';
import { handleRequest } from './handleRequest';

export const axiosGetFlagSubmissions = async (page = 0, size = 10, search = '', isCorrect = null) => {
  const params = {
    page,
    size,
  };

  if (search) {
    params.search = search;
  }

  if (isCorrect !== null && isCorrect !== undefined) {
    params.isCorrect = isCorrect;
  }

  return handleRequest(() => api.get('/dashboard/submissions', { params }), {
    onError: () => message.error('Ошибка при получении списка флагов'),
    rethrow: true,
  });
};
