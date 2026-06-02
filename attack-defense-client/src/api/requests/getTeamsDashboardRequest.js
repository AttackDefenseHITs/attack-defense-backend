import { api } from "../axiosInstance.js";
import { API_URLS } from "../../constants/apiUrls.js";
import { handleRequest } from "./handleRequest.js";

export const axiosGetTeamsDashboard = async (isCorrect) => {
    return handleRequest(async () => {
        const response = await api.get(API_URLS.TEAMS_DASHBOARD, {
            params: {
                isCorrect
            }
        });
        return response.data;
    }, {
        onError: (error) => console.log(error?.response?.data?.message),
    });
};
