import React from "react";
import { Button, Input, Popconfirm, message } from "antd";
import ReactQuill from "react-quill";

export const renderEditStrategy = ({
                                       selectedPage,
                                       themeStyles,
                                       updateSelectedPage,
                                       toggleSelectedPageVisibility,
                                       handleDeleteSelectedPage,
                                       setIsEditMode,
                                       quillModules,
                                       t,
                                   }) => {
    return (
        <>
            <div
                style={{
                    display: "flex",
                    justifyContent: "space-between",
                    alignItems: "center",
                    gap: 12,
                    marginBottom: 12,
                    flexShrink: 0,
                }}
            >
                <Input
                    value={selectedPage.title}
                    onChange={(e) => updateSelectedPage({ title: e.target.value })}
                    style={{
                        fontWeight: 600,
                        fontSize: 16,
                        borderRadius: 10,
                        background: themeStyles.inputBackground,
                        color: themeStyles.inputText,
                    }}
                />

                <Button onClick={() => setIsEditMode(false)} style={{ borderRadius: 10, flexShrink: 0 }}>
                    Отмена
                </Button>
            </div>

            <div
                style={{
                    display: "flex",
                    alignItems: "center",
                    justifyContent: "space-between",
                    gap: 12,
                    marginBottom: 12,
                    flexShrink: 0,
                    flexWrap: "wrap",
                }}
            >
                <div style={{ color: themeStyles.commonText, fontSize: 14 }}>
                    Доступ участникам: <strong>{selectedPage.visibleToUsers ? "открыт" : "скрыт"}</strong>
                </div>

                <Button onClick={toggleSelectedPageVisibility} style={{ borderRadius: 10 }}>
                    {selectedPage.visibleToUsers ? "Скрыть для участников" : "Открыть для участников"}
                </Button>
            </div>

            <div
                className="learning-materials-quill-wrapper"
                style={{
                    "--lm-window": themeStyles.window,
                    "--lm-border": themeStyles.border,
                    "--lm-common": themeStyles.commonText,
                    "--lm-pre-bg": themeStyles.inputBackground,
                    "--lm-pre-text": themeStyles.commonText,
                    "--lm-inline-code-bg": themeStyles.contentBackground,
                    flex: 1,
                    minHeight: 0,
                    display: "flex",
                    flexDirection: "column",
                }}
            >
                <ReactQuill
                    theme="snow"
                    value={selectedPage.content || ""}
                    onChange={(v) => updateSelectedPage({ content: v })}
                    modules={quillModules}
                    formats={[
                        "header",
                        "bold",
                        "italic",
                        "underline",
                        "strike",
                        "list",
                        "bullet",
                        "link",
                        "image",
                        "code",
                        "code-block",
                    ]}
                    style={{
                        flex: 1,
                        minHeight: 0,
                        display: "flex",
                        flexDirection: "column",
                    }}
                />
            </div>

            <div
                style={{
                    display: "flex",
                    justifyContent: "space-between",
                    gap: 8,
                    marginTop: 12,
                    flexShrink: 0,
                    flexWrap: "wrap",
                }}
            >
                <Popconfirm
                    title="Удалить страницу?"
                    description="Это действие нельзя отменить."
                    okText="Удалить"
                    cancelText="Отмена"
                    onConfirm={handleDeleteSelectedPage}
                >
                    <Button danger style={{ borderRadius: 10 }}>
                        Удалить страницу
                    </Button>
                </Popconfirm>

                <div style={{ display: "flex", gap: 8, flexWrap: "wrap" }}>
                    <Button onClick={() => setIsEditMode(false)} style={{ borderRadius: 10 }}>
                        Закрыть редактор
                    </Button>

                    <Button
                        type="primary"
                        onClick={() => {
                            message.success?.(t("success_save"));
                            setIsEditMode(false);
                        }}
                        style={{ borderRadius: 10 }}
                    >
                        {t("save_changes")}
                    </Button>
                </div>
            </div>
        </>
    );
};

export const renderViewStrategy = ({
                                       selectedPage,
                                       themeStyles,
                                       isAdmin,
                                       setIsEditMode,
                                       toggleSelectedPageVisibility,
                                       handleDeleteSelectedPage,
                                   }) => {
    return (
        <>
            <div
                style={{
                    display: "flex",
                    justifyContent: "space-between",
                    alignItems: "flex-start",
                    gap: 12,
                    paddingBottom: 8,
                    flexShrink: 0,
                    flexWrap: "wrap",
                }}
            >
                <div>
                    <h6
                        style={{
                            marginTop: 0,
                            marginBottom: 4,
                            color: themeStyles.commonText,
                            paddingBottom: 8,
                            fontSize: 16,
                        }}
                    >
                        {selectedPage.title}
                    </h6>

                    {isAdmin && (
                        <div style={{ fontSize: 13, color: themeStyles.secondaryText }}>
                            Страница {selectedPage.visibleToUsers ? "открыта" : "скрыта"} для участников
                        </div>
                    )}
                </div>

                {isAdmin && (
                    <div style={{ display: "flex", gap: 8, flexShrink: 0, flexWrap: "wrap" }}>
                        <Button onClick={toggleSelectedPageVisibility} style={{ borderRadius: 10 }}>
                            {selectedPage.visibleToUsers ? "Скрыть" : "Открыть"}
                        </Button>

                        <Button type="primary" onClick={() => setIsEditMode(true)} style={{ borderRadius: 10 }}>
                            Редактировать
                        </Button>

                        <Popconfirm
                            title="Удалить страницу?"
                            description="Это действие нельзя отменить."
                            okText="Удалить"
                            cancelText="Отмена"
                            onConfirm={handleDeleteSelectedPage}
                        >
                            <Button danger style={{ borderRadius: 10 }}>
                                Удалить
                            </Button>
                        </Popconfirm>
                    </div>
                )}
            </div>

            <div
                className="learning-materials-richtext"
                style={{
                    "--lm-pre-bg": themeStyles.inputBackground,
                    "--lm-pre-text": themeStyles.commonText,
                    "--lm-inline-code-bg": themeStyles.contentBackground,
                    "--lm-border": themeStyles.border,
                    "--lm-common": themeStyles.commonText,
                    flex: 1,
                    minHeight: 0,
                    overflowY: "auto",
                }}
                dangerouslySetInnerHTML={{ __html: selectedPage.content || "" }}
            />
        </>
    );
};

export const renderEmptyStrategy = (themeStyles) => {
    return <div style={{ color: themeStyles.secondaryText }}>Нет выбранной страницы.</div>;
};