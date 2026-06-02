import { message } from "antd";
import { ERROR_MESSAGES } from "../constants/errorMessages";

export const showError = (text) => {
    message.open({
        type: 'error',
        content: text,
        duration: 2
    });
}

export const showSuccess = (text) => {
    message.open({
        type: 'success',
        content: text,
        duration: 2
    });
}

export const showLoginFailed = () => {
    showError(ERROR_MESSAGES.LOGIN_FAILED);
}

export const showUnauthorizedError = () => {
    showError(ERROR_MESSAGES.UNAUTHORIZED);
}

export const showElementNotFound = () => {
    showError(ERROR_MESSAGES.ELEMENT_NOT_FOUND);
}

export const showRegistrationFailed = () => {
    showError(ERROR_MESSAGES.REGISTRATION_FAILED);
}