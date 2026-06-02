import React, { useEffect, useMemo, useState } from "react";
import {
    Button,
    Divider,
    Input,
    InputNumber,
    List,
    Popconfirm,
    Space,
    Switch,
    Typography,
    message,
} from "antd";
import { DeleteOutlined, PlusOutlined, ReloadOutlined } from "@ant-design/icons";
import {
    axiosGetAdminHintsByService,
    axiosCreateAdminHint,
    axiosSetAdminHintEnabled,
    axiosDeleteAdminHint,
} from "../../../api/requests/hintsAdminRequests";

const { Text, Paragraph } = Typography;

const toPercent = (value) => {
    if (value === null || value === undefined) return 0;
    const num = Number(value);
    if (Number.isNaN(num)) return 0;
    if (num <= 1) return Math.round(num * 100 * 100) / 100;
    return num;
};

const HintsSection = ({ serviceId, themeStyles, t }) => {
    const [loading, setLoading] = useState(false);
    const [hints, setHints] = useState([]);

    const [text, setText] = useState("");
    const [multiplierPercent, setMultiplierPercent] = useState(0);

    // id подсказки, которую раскрыли (если нужно несколько — сделаем Set)
    const [expandedId, setExpandedId] = useState(null);

    const load = async () => {
        setLoading(true);
        try {
            const res = await axiosGetAdminHintsByService(serviceId);
            const data = Array.isArray(res.data) ? res.data : [];
            data.sort((a, b) => (a.level ?? 0) - (b.level ?? 0));
            setHints(data);
        } catch (e) {
            console.error(e);
            message.error(t?.("hints_load_error") ?? "Не удалось загрузить подсказки");
        } finally {
            setLoading(false);
        }
    };

    useEffect(() => {
        if (!serviceId) return;
        load();
    }, [serviceId]);

    const handleCreate = async () => {
        if (!text.trim()) {
            message.warning(t?.("fill_all_fields") ?? "Заполни текст подсказки");
            return;
        }

        try {
            await axiosCreateAdminHint({
                serviceId,
                text: text.trim(),
                multiplier: Number(multiplierPercent ?? 0),
            });

            message.success(t?.("hint_created") ?? "Подсказка создана");
            setText("");
            setMultiplierPercent(0);
            await load();
        } catch (e) {
            message.error(t?.("hint_create_error") ?? "Не удалось создать подсказку");
        }
    };

    const handleToggle = async (hintTemplateId, enabled) => {
        try {
            await axiosSetAdminHintEnabled(hintTemplateId, enabled);
            message.success(t?.("saved") ?? "Сохранено");
            await load();
        } catch (e) {
            message.error(t?.("hint_update_error") ?? "Не удалось обновить подсказку");
        }
    };

    const handleDelete = async (hintTemplateId) => {
        try {
            await axiosDeleteAdminHint(hintTemplateId);
            message.success(t?.("deleted") ?? "Удалено");
            await load();
        } catch (e) {
            message.error(t?.("hint_delete_error") ?? "Не удалось удалить подсказку");
        }
    };

    const controlsRow = useMemo(
        () => (
            <Space wrap style={{ width: "100%", justifyContent: "space-between" }}>
                <Space wrap>
                    <InputNumber
                        value={multiplierPercent}
                        onChange={setMultiplierPercent}
                        min={0}
                        max={10000}
                        addonAfter="%"
                        style={{
                            width: 160,
                            backgroundColor: themeStyles.inputBackground,
                            color: themeStyles.inputText,
                            borderColor: themeStyles.border,
                        }}
                    />
                    <Button
                        type="primary"
                        icon={<PlusOutlined />}
                        onClick={handleCreate}
                        style={{
                            backgroundColor: themeStyles.primaryButton,
                            borderColor: themeStyles.primaryButton,
                            color: themeStyles.primaryButtonText,
                        }}
                    >
                        {t?.("add") ?? "Добавить"}
                    </Button>
                </Space>

                <Button icon={<ReloadOutlined />} onClick={load}>
                    {t?.("refresh") ?? "Обновить"}
                </Button>
            </Space>
        ),
        [multiplierPercent, themeStyles, t]
    );

    return (
        <div style={{ padding: 12 }}>
            <Space direction="vertical" size="middle" style={{ width: "100%" }}>
                {/* Большое поле ввода */}
                <Input.TextArea
                    value={text}
                    onChange={(e) => setText(e.target.value)}
                    placeholder={t?.("hint_text") ?? "Текст подсказки"}
                    autoSize={{ minRows: 3, maxRows: 8 }}
                    style={{
                        width: "100%",
                        backgroundColor: themeStyles.inputBackground,
                        color: themeStyles.inputText,
                        borderColor: themeStyles.border,
                    }}
                />

                {controlsRow}

                <Divider style={{ margin: "8px 0", borderColor: themeStyles.border }} />

                <List
                    loading={loading}
                    locale={{ emptyText: t?.("no_hints") ?? "Подсказок пока нет" }}
                    dataSource={hints}
                    renderItem={(hint) => {
                        const isExpanded = expandedId === hint.id;
                        const isLong = (hint.text?.length ?? 0) > 160;

                        return (
                            <List.Item
                                style={{
                                    border: `1px solid ${themeStyles.border}`,
                                    borderRadius: 8,
                                    padding: 12,
                                    marginBottom: 10,
                                    background: themeStyles.window,
                                }}
                                actions={[
                                    <Space key="enabled" size="small">
                                        <Switch
                                            checked={!!hint.enabled}
                                            onChange={(checked) => handleToggle(hint.id, checked)}
                                        />
                                    </Space>,
                                    <Popconfirm
                                        key="delete"
                                        title={t?.("confirm_delete") ?? "Удалить подсказку?"}
                                        okText={t?.("delete") ?? "Удалить"}
                                        cancelText={t?.("cancel") ?? "Отмена"}
                                        onConfirm={() => handleDelete(hint.id)}
                                    >
                                        <Button danger type="text" icon={<DeleteOutlined />}>
                                            {t?.("delete") ?? "Удалить"}
                                        </Button>
                                    </Popconfirm>,
                                ]}
                            >
                                <List.Item.Meta
                                    title={
                                        <Space wrap>
                                            <Text strong style={{ color: themeStyles.commonText }}>
                                                {t?.("hint") ?? "Подсказка"} №{hint.level}
                                            </Text>
                                            <Text style={{ color: themeStyles.lightText }}>
                                                {t?.("multiplier") ?? "Множитель"}: {toPercent(hint.multiplier)}%
                                            </Text>
                                        </Space>
                                    }
                                    description={
                                        <div>
                                            <Paragraph
                                                style={{ color: themeStyles.commonText, marginBottom: 6 }}
                                                ellipsis={
                                                    !isExpanded
                                                        ? { rows: 2, expandable: false }
                                                        : false
                                                }
                                            >
                                                {hint.text}
                                            </Paragraph>

                                            {isLong && (
                                                <Button
                                                    type="link"
                                                    size="small"
                                                    style={{ padding: 0, height: "auto", color: themeStyles.link }}
                                                    onClick={() => setExpandedId(isExpanded ? null : hint.id)}
                                                >
                                                    {isExpanded
                                                        ? t?.("expand") ?? "Показать полностью"
                                                        : t?.("collapse") ?? "Свернуть"}
                                                </Button>
                                            )}
                                        </div>
                                    }
                                />
                            </List.Item>
                        );
                    }}
                />
            </Space>
        </div>
    );
};

export default HintsSection;