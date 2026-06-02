import React from "react";
import { Alert, Button } from "antd";
import { ROUTES } from "../../../constants/routes";

const ErrorScreen = ({ themeStyles, t, navigate }) => (
    <div
        style={{
            padding: '20px',
            textAlign: 'center',
            background: themeStyles.background,
            minHeight: '100vh',
            display: 'flex',
            justifyContent: 'center',
            alignItems: 'center',
            flexDirection: 'column',
        }}
    >
        <Alert
            message={t("error")}
            description={t("team_load_error")}
            type="error"
            showIcon
            style={{
                backgroundColor: themeStyles.errorBackground,
                color: themeStyles.errorText,
                borderRadius: '8px',
                maxWidth: '500px',
            }}
        />

        <Button
            type="primary"
            style={{
                marginTop: '20px',
                backgroundColor: themeStyles.primaryButton,
                color: themeStyles.primaryButtonText,
                borderRadius: '8px',
                height: '40px',
                fontWeight: '500',
            }}
            onClick={() => navigate(ROUTES.ROOT)}
        >
            {t("back_to_teams")}
        </Button>
    </div>
);

export default ErrorScreen;
