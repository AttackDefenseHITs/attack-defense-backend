import React, { useEffect, useMemo, useState } from "react";
import { Alert } from "antd";
import { useTranslation } from "react-i18next";

export default function RulesBanner({ rules, themeStyles, theme }) {
    const { t } = useTranslation();
    const [closed, setClosed] = useState(false);

    const text = useMemo(() => {
        if (typeof rules !== "string") return "";
        return rules.trim();
    }, [rules]);

    useEffect(() => {
        setClosed(false);
    }, [text]);

    if (!text || closed) return null;

    // чем меньше число — тем прозрачнее
    const glassBg =
        theme === "dark"
            ? "rgba(0, 0, 0, 0.35)"
            : "rgba(255, 255, 255, 0.45)";

    return (
        <div
            style={{
                width: "100%",
                borderRadius: 12,
                border: `1px solid ${themeStyles.cardBorder}`,
                background: glassBg,
                backdropFilter: "blur(10px)",
                WebkitBackdropFilter: "blur(10px)",
                boxShadow: themeStyles.cardShadow,
                overflow: "hidden",
            }}
        >
            <Alert
                closable
                onClose={() => setClosed(true)}
                showIcon={false}
                message={
                    <div
                        style={{
                            textAlign: "center",
                            color: themeStyles.commonText,
                            fontWeight: 700,
                            fontSize: 14,
                            marginBottom: 6,
                        }}
                    >
                        {t("announcement") || "Сообщение"}
                    </div>
                }
                description={
                    <div
                        style={{
                            textAlign: "center",
                            color: themeStyles.secondaryText,
                            whiteSpace: "pre-wrap",
                            lineHeight: 1.5,
                        }}
                    >
                        {text}
                    </div>
                }
                style={{
                    background: "transparent", // важно: сам Alert прозрачный
                    border: "none",
                    padding: "12px 16px",
                }}
            />
        </div>
    );
}
