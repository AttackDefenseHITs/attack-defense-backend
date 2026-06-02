import { api } from "../axiosInstance.js";
import { API_URLS } from "../../constants/apiUrls.js";
import { showError } from "../../utils/messageHandler";
import { message } from 'antd';

export const axiosSendFlagRequest = async (flagValue) => {
    try {
        const response = await api.post(`${API_URLS.SEND_FLAG}`, { flagValue });
        message.success("Флаг успешно захвачен!");
        return response;
    } catch (error) {
        if (error.response) {
            const { status, data } = error.response;
            switch (status) {
                case 400:
                    showError(data || "Неправильное значение флага");
                    break;
                case 403:
                    showError(data || "Флаг больше не активен");
                    break;
                case 410:
                    showError(data || "Вы не можете отправить флаг своей команды");
                    break;
                default:
                    throw error;
            }
        } else {
            throw error;
        }
    }
};
