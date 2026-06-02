import { api } from "../axiosInstance.js";
import { API_URLS } from "../../constants/apiUrls.js";
import { handleRequest } from "./handleRequest.js";

export const axiosGetRole = async () => {
    return handleRequest(() => api.get(API_URLS.ROLE));
};
