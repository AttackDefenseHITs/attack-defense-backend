import React from "react";
import { Card, Col, Form, InputNumber, Row, Switch, Typography } from "antd";
import { useTranslation } from "react-i18next";
import { buildInputStyle, buildNumberStyle } from "./formStyles";

const { Text } = Typography;
const formItemStyle = { marginBottom: 12 };

const AttackBotSettings = ({ formData, handleChange, textColor, backgroundColor, theme, themeStyles }) => {
    const { t } = useTranslation();
    const inputStyle = buildInputStyle({ theme, textColor, backgroundColor });
    const numberStyle = buildNumberStyle(inputStyle);

    if (formData.competitionMode !== "ATTACK_DEFENSE") {
        return null;
    }

    const { attackBotSettings } = formData;

    return (
        <Card style={{ marginBottom: 20, borderRadius: 10, background: themeStyles.window }} bodyStyle={{ padding: 16 }}>
            <Row justify="space-between" align="middle">
                <Text style={{ fontSize: 15, fontWeight: 600 }}>{t("attack_bot")}</Text>
                <Switch
                    checked={attackBotSettings.enabled}
                    onChange={(v) => handleChange("attackBotSettings.enabled", v)}
                    style={{ transform: "scale(1.4)" }}
                />
            </Row>


            {attackBotSettings.enabled && (
                <>
                    <Row gutter={12} style={{ marginTop: 12 }}>
                        <Col span={12}>
                            <Form.Item label={t("attack_probability")} style={formItemStyle}>
                                <InputNumber
                                    className="custom-number"
                                    min={0}
                                    max={1}
                                    step={0.05}
                                    value={attackBotSettings.attackProbability}
                                    onChange={(v) => handleChange("attackBotSettings.attackProbability", v)}
                                    style={numberStyle}
                                    controls={false}
                                />
                            </Form.Item>
                        </Col>
                        <Col span={12}>
                            <Form.Item label={t("max_targets")} style={formItemStyle}>
                                <InputNumber
                                    className="custom-number"
                                    min={1}
                                    max={20}
                                    value={attackBotSettings.maxTargets}
                                    onChange={(v) => handleChange("attackBotSettings.maxTargets", v)}
                                    style={numberStyle}
                                    controls={false}
                                />
                            </Form.Item>
                        </Col>
                    </Row>


                    <Row gutter={12}>
                        <Col span={12}>
                            <Form.Item label={t("cooldown_rounds")} style={formItemStyle}>
                                <InputNumber
                                    className="custom-number"
                                    min={0}
                                    max={50}
                                    value={attackBotSettings.cooldownRounds}
                                    onChange={(v) => handleChange("attackBotSettings.cooldownRounds", v)}
                                    style={numberStyle}
                                    controls={false}
                                />
                            </Form.Item>
                        </Col>
                        <Col span={12}>
                            <Form.Item label={t("attack_interval_seconds")} style={formItemStyle}>
                                <InputNumber
                                    className="custom-number"
                                    min={5}
                                    max={300}
                                    value={attackBotSettings.roundIntervalSeconds}
                                    onChange={(v) => handleChange("attackBotSettings.roundIntervalSeconds", v)}
                                    style={numberStyle}
                                    controls={false}
                                />
                            </Form.Item>
                        </Col>
                    </Row>


                    <Row gutter={12}>
                        <Col span={12}>
                            <Form.Item label={t("score_weight")} style={formItemStyle}>
                                <InputNumber
                                    className="custom-number"
                                    min={0}
                                    max={5}
                                    step={0.1}
                                    value={attackBotSettings.priorityScoreWeight}
                                    onChange={(v) => handleChange("attackBotSettings.priorityScoreWeight", v)}
                                    style={numberStyle}
                                    controls={false}
                                />
                            </Form.Item>
                        </Col>
                        <Col span={12}>
                            <Form.Item label={t("sla_weight")} style={formItemStyle}>
                                <InputNumber
                                    className="custom-number"
                                    min={0}
                                    max={5}
                                    step={0.1}
                                    value={attackBotSettings.prioritySlaWeight}
                                    onChange={(v) => handleChange("attackBotSettings.prioritySlaWeight", v)}
                                    style={numberStyle}
                                    controls={false}
                                />
                            </Form.Item>
                        </Col>
                    </Row>
                </>
            )}
        </Card>
    );
};

export default AttackBotSettings;