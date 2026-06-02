import React from "react";
import { Button, Card, Col, Form, Input, Row, Tooltip, Typography, Space } from "antd";
import { ExportOutlined, FolderAddOutlined, ReloadOutlined } from "@ant-design/icons";
import { useTranslation } from "react-i18next";
import { buildInputStyle, buttonStyleBase } from "./formStyles";

const { Text } = Typography;

const RepositorySettings = ({
                                formData,
                                textColor,
                                backgroundColor,
                                theme,
                                themeStyles,
                                repoAction,
                                onRepoCreate,
                                onRepoSync,
                            }) => {
    const { t } = useTranslation();
    const inputStyle = buildInputStyle({ theme, textColor, backgroundColor });

    const creating = !!repoAction?.creating;
    const syncing = !!repoAction?.syncing;
    const busy = creating || syncing;

    return (
        <Card style={{ marginBottom: 10, borderRadius: 10, background: themeStyles.window }} bodyStyle={{ padding: 16 }}>
            <Text style={{ fontSize: 15, fontWeight: 600, color: themeStyles.commonText }}>
                {t("repository")}
            </Text>

            <Row align="middle" style={{ marginTop: 12 }} gutter={12}>
                <Col flex="auto">
                    <Form.Item style={{ marginBottom: 0 }}>
                        <Input
                            value={formData.repoUrl}
                            readOnly
                            style={{
                                ...inputStyle,
                                cursor: "not-allowed",
                                opacity: 0.9,
                                color: themeStyles.commonText,
                            }}
                        />
                    </Form.Item>
                </Col>

                <Col>
                    <Tooltip title={creating ? (t("repo_creating") || "Создаём...") : t("repo_create")}>
                        <Button
                            type="primary"
                            onClick={onRepoCreate}
                            loading={creating}
                            disabled={busy}
                            style={{
                                ...buttonStyleBase,
                                background: themeStyles.primaryButton,
                                borderColor: themeStyles.primaryButton,
                            }}
                            icon={!creating ? <FolderAddOutlined /> : undefined}
                        />
                    </Tooltip>
                </Col>

                <Col>
                    <Tooltip title={t("repo_open")}>
                        <Button
                            disabled={!formData.repoUrl || busy}
                            onClick={() => window.open(formData.repoUrl, "_blank")}
                            style={{
                                ...buttonStyleBase,
                                border: `1px solid ${themeStyles.border}`,
                                background: themeStyles.window,
                            }}
                            icon={<ExportOutlined />}
                        />
                    </Tooltip>
                </Col>

                <Col>
                    <Tooltip title={syncing ? (t("repo_syncing") || "Синхронизация...") : t("repo_sync")}>
                        <Button
                            disabled={!formData.repoUrl || busy}
                            onClick={onRepoSync}
                            loading={syncing}
                            style={{
                                ...buttonStyleBase,
                                border: `1px solid ${themeStyles.border}`,
                                background: themeStyles.window,
                            }}
                            icon={!syncing ? <ReloadOutlined /> : undefined}
                        />
                    </Tooltip>
                </Col>
            </Row>

            {busy && (
                <div style={{ marginTop: 10, color: themeStyles.secondaryText, fontSize: 12 }}>
                    {creating
                        ? (t("repo_creating_long") || "Создаём репозиторий. Это может занять до минуты…")
                        : (t("repo_syncing_long") || "Синхронизируем репозиторий. Подождите…")}
                </div>
            )}
        </Card>
    );
};

export default RepositorySettings;