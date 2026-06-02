import { API_URLS } from "../../constants/apiUrls";
import { api } from "../axiosInstance";
import { showLoginFailed } from "../../utils/messageHandler";
import { saveTokensAndNavigate, handleApiError } from "../../utils/authUtils";

export const axiosLogin = async (login, password, navigate, loginHook) => {
    const data = { login, password };

    try {
        const token = await api.post(API_URLS.LOGIN, data);
        const roles = Array.isArray(token.data.role) ? token.data.role : [token.data.role];

        loginHook(roles);
        saveTokensAndNavigate(token.data, navigate);
    } catch (error) {
        handleApiError(error, "Ошибка входа!", showLoginFailed);
    }
};