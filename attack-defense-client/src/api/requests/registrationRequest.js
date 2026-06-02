import { API_URLS } from "../../constants/apiUrls";
import { api } from "../axiosInstance";
import { showRegistrationFailed } from "../../utils/messageHandler";
import { saveTokensAndNavigate, handleApiError } from "../../utils/authUtils";

export const axiosRegistration = async (login, name, password, confirmPassword, navigate, loginHook) => {
    const data = { login, name, password, confirmPassword };

    try {
        const token = await api.post(API_URLS.REGISTRATION, data);
        console.log("token", token);

        const roles = Array.isArray(token.data.role) ? token.data.role : [token.data.role];

        loginHook(roles);
        saveTokensAndNavigate(token.data, navigate);
    } catch (error) {
        handleApiError(error, "Ошибка регистрации!", showRegistrationFailed);
    }
};