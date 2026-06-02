import { api } from "../axiosInstance.js";
import { API_URLS } from "../../constants/apiUrls.js";
import { handleRequest } from "./handleRequest.js";

/**
 * Получить текущий режим соревнования:
 * GET /api/competition/mode
 */
export const axiosGetCompetitionMode = async () => {
    return handleRequest(() => api.get(`${API_URLS.COMPETITION}/mode`), {
        errorMessage: "Ошибка при получении режима соревнования:",
        rethrow: true,
    });
};

/**
 * Установить новый режим соревнования:
 * POST /api/competition/mode
 * body: { competitionMode: "ATTACK_DEFENSE" }
 */
export const axiosSetCompetitionMode = async (competitionMode) => {
    return handleRequest(
        () => api.post(
            `${API_URLS.COMPETITION}/mode`,
            { competitionMode }
        ),
        {
            errorMessage: "Ошибка при установке режима соревнования:",
            rethrow: true,
        }
    );
};
