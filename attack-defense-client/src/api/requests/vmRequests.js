import { api } from "../axiosInstance.js";
import { API_URLS } from "../../constants/apiUrls.js";
import { handleRequest } from "./handleRequest.js";

export const axiosGetAllVms = async () => {
    return handleRequest(() => api.get(API_URLS.VM), {
        errorMessage: 'Ошибка при загрузке виртуальных машин:',
    });
};

export const axiosCreateVm = async (vmData) => {
    return handleRequest(() => api.post(API_URLS.VM, vmData), {
        errorMessage: 'Ошибка при создании виртуальной машины:',
    });
};

export const axiosDeleteVm = async (vmId) => {
    return handleRequest(() => api.delete(`${API_URLS.VM}/${vmId}`), {
        errorMessage: 'Ошибка при удалении виртуальной машины:',
    });
};

export const axiosGetVmDetails = async (vmId) => {
    return handleRequest(() => api.get(`${API_URLS.VM}/${vmId}`), {
        errorMessage: 'Ошибка при получении данных виртуальной машины:',
    });
};

export const axiosUpdateVm = async (vmId, vmData) => {
    return handleRequest(() => api.put(`${API_URLS.VM}/${vmId}`, vmData), {
        errorMessage: 'Ошибка при обновлении виртуальной машины:',
    });
};
