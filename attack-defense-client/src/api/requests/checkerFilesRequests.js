import { api } from "../axiosInstance.js";
import { API_URLS } from "../../constants/apiUrls.js";
import { handleRequest } from "./handleRequest.js";

/**
 * Получить корневое дерево файлов чекера:
 * GET /api/checkers/{serviceId}/files
 */
export const axiosGetCheckerFilesRoot = async (serviceId) => {
    return handleRequest(() => api.get(`${API_URLS.CHECKERS}/${serviceId}/files`), {
        errorMessage: "Ошибка при загрузке корневого дерева файлов чекера:",
        rethrow: true,
    });
};

/**
 * Получить дерево внутри директории:
 * GET /api/checkers/{serviceId}/files?path=...
 */
export const axiosGetCheckerFilesByPath = async (serviceId, path) => {
    return handleRequest(
        () => api.get(`${API_URLS.CHECKERS}/${serviceId}/files`, {
            params: { path }
        }),
        {
            errorMessage: "Ошибка при загрузке содержимого директории чекера:",
            rethrow: true,
        }
    );
};

/**
 * Получить содержимое файла:
 * GET /api/checkers/{serviceId}/file?path=...
 */
export const axiosGetCheckerFileContent = async (serviceId, path) => {
    return handleRequest(
        () => api.get(`${API_URLS.CHECKERS}/${serviceId}/file`, {
            params: { path }
        }),
        {
            errorMessage: "Ошибка при загрузке файла чекера:",
            rethrow: true,
        }
    );
};
