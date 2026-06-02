import React, { useContext, useEffect, useMemo } from "react";
import { Card, Typography, Button, Menu } from "antd";
import { BookOutlined, PlusOutlined } from "@ant-design/icons";
import { useTranslation } from "react-i18next";
import { useNavigate } from "react-router-dom";
import { Helmet } from "react-helmet";

import ThemeContext from "../../context/ThemeContext";
import { useUser } from "../../context/UserContext";

import { useLearningMaterials } from "./hooks/useLearningMaterials";
import {
    renderEditStrategy,
    renderViewStrategy,
    renderEmptyStrategy,
} from "./strategies/learningMaterialsStrategies";

import "react-quill/dist/quill.snow.css";
import "./LearningMaterials.css";

const { Title } = Typography;

const LearningMaterials = () => {
    const { isAuthenticated, roles } = useUser();
    const isAdmin = roles.includes("ADMIN");

    const navigate = useNavigate();
    const { t } = useTranslation();

    const { theme, themeConfig } = useContext(ThemeContext);
    const themeStyles = themeConfig[theme];

    const {
        selectedPage,
        selectedPageId,
        setSelectedPageId,
        isEditMode,
        setIsEditMode,
        visiblePages,
        updateSelectedPage,
        handleAddPage,
        handleDeleteSelectedPage,
        toggleSelectedPageVisibility,
    } = useLearningMaterials({ isAdmin, t });

    useEffect(() => {
        const hasTokens = !!localStorage.getItem("accessToken") || !!localStorage.getItem("refreshToken");
        if (!isAuthenticated && !hasTokens) navigate("/");
    }, [isAuthenticated, navigate]);

    const quillModules = useMemo(
        () => ({
            toolbar: {
                container: [
                    [{ header: [1, 2, 3, false] }],
                    ["bold", "italic", "underline", "strike"],
                    [{ list: "ordered" }, { list: "bullet" }],
                    ["link", "image"],
                    [{ "code-block": true }, { code: true }],
                    ["clean"],
                ],
                handlers: {
                    image: function imageHandler() {
                        const input = document.createElement("input");
                        input.setAttribute("type", "file");
                        input.setAttribute("accept", "image/*");
                        input.click();

                        input.onchange = () => {
                            const file = input.files?.[0];
                            if (!file) return;

                            const reader = new FileReader();
                            reader.onload = () => {
                                const quill = this.quill;
                                const range = quill.getSelection(true);
                                const index = range ? range.index : quill.getLength();
                                quill.insertEmbed(index, "image", String(reader.result));
                                quill.setSelection(index + 1, 0);
                            };
                            reader.readAsDataURL(file);
                        };
                    },
                },
            },
            clipboard: {
                matchVisual: false,
            },
        }),
        []
    );

    const leftMenuItems = visiblePages.map((p) => ({
        key: p.id,
        label: (
            <div
                style={{
                    display: "flex",
                    justifyContent: "space-between",
                    alignItems: "center",
                    gap: 8,
                    width: "100%",
                }}
            >
        <span
            style={{
                overflow: "hidden",
                textOverflow: "ellipsis",
                whiteSpace: "nowrap",
            }}
        >
          {p.title}
        </span>

                {isAdmin && (
                    <span
                        style={{
                            fontSize: 12,
                            opacity: 0.75,
                            flexShrink: 0,
                        }}
                    >
            {p.visibleToUsers ? "Открыта" : "Скрыта"}
          </span>
                )}
            </div>
        ),
    }));

    const renderRightContent = () => {
        if (!selectedPage) {
            return renderEmptyStrategy(themeStyles);
        }

        if (isAdmin && isEditMode) {
            return renderEditStrategy({
                selectedPage,
                themeStyles,
                updateSelectedPage,
                toggleSelectedPageVisibility,
                handleDeleteSelectedPage,
                setIsEditMode,
                quillModules,
                t,
            });
        }

        return renderViewStrategy({
            selectedPage,
            themeStyles,
            isAdmin,
            setIsEditMode,
            toggleSelectedPageVisibility,
            handleDeleteSelectedPage,
        });
    };

    return (
        <>
            <Helmet>
                <title>{t("learning_materials")}</title>
            </Helmet>

            <div
                className="login-page fade-in learning-materials-page"
                style={{
                    padding: "40px",
                    background: themeStyles.bannerGradient,
                    minHeight: "calc(100vh - 64px)",
                    display: "flex",
                    flexDirection: "column",
                    boxSizing: "border-box",
                }}
            >
                <div
                    style={{
                        maxWidth: "1400px",
                        margin: "0 auto",
                        flex: 1,
                        width: "100%",
                        display: "flex",
                        flexDirection: "column",
                        minHeight: 0,
                    }}
                >
                    <Card
                        className="custom-card slide-up full-height-learning-card"
                        bodyStyle={{
                            flex: 1,
                            display: "flex",
                            flexDirection: "column",
                            minHeight: 0,
                        }}
                        style={{
                            borderRadius: "16px",
                            backgroundColor: themeStyles.cardBackground,
                            border: `1px solid ${themeStyles.cardBorder}`,
                            boxShadow: themeStyles.cardShadow,
                            flex: 1,
                            display: "flex",
                            flexDirection: "column",
                            minHeight: 0,
                        }}
                    >
                        <div
                            style={{
                                width: "100%",
                                flex: 1,
                                display: "flex",
                                flexDirection: "column",
                                minHeight: 0,
                                gap: 24,
                            }}
                        >
                            <div
                                style={{
                                    display: "flex",
                                    alignItems: "center",
                                    gap: "12px",
                                    flexShrink: 0,
                                }}
                            >
                                <BookOutlined style={{ fontSize: "24px", color: themeStyles.primaryButton }} />
                                <Title level={2} style={{ margin: 0, color: themeStyles.commonText }}>
                                    {t("learning_materials")}
                                </Title>
                            </div>

                            <div
                                style={{
                                    display: "flex",
                                    gap: 16,
                                    alignItems: "stretch",
                                    flex: 1,
                                    minHeight: 0,
                                }}
                            >
                                <div
                                    style={{
                                        width: 340,
                                        minWidth: 340,
                                        minHeight: 0,
                                        alignSelf: "stretch",
                                        background: themeStyles.window,
                                        border: `1px solid ${themeStyles.border}`,
                                        borderRadius: 12,
                                        overflow: "hidden",
                                        display: "flex",
                                        flexDirection: "column",
                                    }}
                                >
                                    <div
                                        style={{
                                            padding: "12px 14px",
                                            borderBottom: `1px solid ${themeStyles.border}`,
                                            display: "flex",
                                            alignItems: "center",
                                            justifyContent: "space-between",
                                            gap: 12,
                                            flexShrink: 0,
                                        }}
                                    >
                                        <span style={{ color: themeStyles.commonText, fontWeight: 600 }}>Разделы</span>

                                        {isAdmin && (
                                            <Button
                                                type="primary"
                                                icon={<PlusOutlined />}
                                                onClick={handleAddPage}
                                                style={{
                                                    borderRadius: 10,
                                                    height: 32,
                                                    boxShadow: "none",
                                                }}
                                            >
                                                Добавить
                                            </Button>
                                        )}
                                    </div>

                                    <div
                                        style={{
                                            padding: 8,
                                            overflowY: "auto",
                                            flex: 1,
                                            minHeight: 0,
                                        }}
                                    >
                                        <Menu
                                            mode="inline"
                                            selectedKeys={selectedPageId ? [selectedPageId] : []}
                                            items={leftMenuItems}
                                            onClick={(e) => setSelectedPageId(e.key)}
                                            style={{
                                                background: "transparent",
                                                borderRight: "none",
                                            }}
                                        />
                                    </div>
                                </div>

                                <div
                                    style={{
                                        flex: 1,
                                        minWidth: 0,
                                        minHeight: 0,
                                        alignSelf: "stretch",
                                        background: themeStyles.window,
                                        border: `1px solid ${themeStyles.border}`,
                                        borderRadius: 12,
                                        padding: 16,
                                        display: "flex",
                                        flexDirection: "column",
                                    }}
                                >
                                    {renderRightContent()}
                                </div>
                            </div>
                        </div>
                    </Card>
                </div>
            </div>
        </>
    );
};

export default LearningMaterials;