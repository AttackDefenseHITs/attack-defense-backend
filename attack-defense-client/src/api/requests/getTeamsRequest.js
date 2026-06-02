import {api} from "../axiosInstance.js";
import {API_URLS} from "../../constants/apiUrls.js";
import { handleRequest } from "./handleRequest.js";

export const axiosGetAllTeams = async() => {
    return handleRequest(() => api.get(API_URLS.TEAM));
}

export const axiosGetAllTeamMembers = async() => {
    return handleRequest(() => api.get(API_URLS.TEAM_MEMBERS_RATING));
}

export const axiosGetMyTeamStats = async() => {
    return handleRequest(() => api.get(`${API_URLS.TEAM}/my/stats`), {
        onError: () => {},
    });
}
