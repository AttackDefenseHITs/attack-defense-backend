import React from "react";
import { Spin } from "antd";

const LoadingScreen = ({ themeStyles, t }) => {
    return (
        <div
            style={{
                display: 'flex',
                justifyContent: 'center',
                alignItems: 'center',
                height: '100vh',
                background: themeStyles.background,
            }}
        >
            <Spin tip={t("loading_team")} size="large" />
        </div>
    );
};

export default LoadingScreen;
