import React from "react";
import {
    Card,
    Col,
    DatePicker,
    Form,
    Input,
    InputNumber,
    Row,
    Typography,
} from "antd";
import { CalendarOutlined, FileTextOutlined } from "@ant-design/icons";
import { useTranslation } from "react-i18next";
import { buildInputStyle, buildNumberStyle } from "./formStyles";

const { Text } = Typography;
const formItemStyle = { marginBottom: 12 };

const MainSettings = ({
                          formData,
                          handleChange,
                          disableStartDate,
                          disableEndDate,
                          textColor,
                          backgroundColor,
                          theme,
                          themeStyles,
                      }) => {
    const { t } = useTranslation();


    const inputStyle = buildInputStyle({ theme, textColor, backgroundColor });
    const numberStyle = buildNumberStyle(inputStyle);


    return (
        <Card
            style={{ marginBottom: 10, borderRadius: 10, background: themeStyles.window }}
            bodyStyle={{ padding: 16 }}
        >
            <Form.Item label={<Text>{t("competition_name")}</Text>} style={formItemStyle}>
                <Input
                    value={formData.name}
                    onChange={(e) => handleChange("name", e.target.value)}
                    style={inputStyle}
                    prefix={<FileTextOutlined />}
                />
            </Form.Item>


            <Row gutter={12}>
                <Col span={12}>
                    <Form.Item label={t("start_date")} style={formItemStyle}>
                        <DatePicker
                            showTime
                            format="DD.MM.YYYY HH:mm"
                            value={formData.startDate}
                            onChange={(v) => handleChange("startDate", v)}
                            disabledDate={disableStartDate}
                            style={{ width: "100%", ...inputStyle }}
                            suffixIcon={<CalendarOutlined />}
                            inputReadOnly
                        />
                    </Form.Item>
                </Col>
                <Col span={12}>
                    <Form.Item label={t("end_date")} style={formItemStyle}>
                        <DatePicker
                            showTime
                            format="DD.MM.YYYY HH:mm"
                            value={formData.endDate}
                            onChange={(v) => handleChange("endDate", v)}
                            disabledDate={disableEndDate}
                            style={{ width: "100%", ...inputStyle }}
                            suffixIcon={<CalendarOutlined />}
                            inputReadOnly
                        />
                    </Form.Item>
                </Col>
            </Row>


            <Row gutter={12}>
                <Col span={12}>
                    <Form.Item label={t("total_rounds")} style={formItemStyle}>
                        <InputNumber
                            className="custom-number"
                            min={3}
                            max={1000}
                            value={formData.totalRounds}
                            onChange={(v) => handleChange("totalRounds", v)}
                            style={numberStyle}
                            controls={false}
                        />
                    </Form.Item>
                </Col>
                <Col span={12}>
                    <Form.Item label={t("round_duration")} style={formItemStyle}>
                        <InputNumber
                            className="custom-number"
                            min={1}
                            max={120}
                            value={formData.roundDurationMinutes}
                            onChange={(v) => handleChange("roundDurationMinutes", v)}
                            style={numberStyle}
                            controls={false}
                        />
                    </Form.Item>
                </Col>
            </Row>


            {formData.competitionMode === "ATTACK_DEFENSE" && (
                <Row gutter={12}>
                    <Col span={12}>
                        <Form.Item label={t("flag_send_cost")} style={formItemStyle}>
                            <InputNumber
                                className="custom-number"
                                min={0}
                                max={10000}
                                value={formData.flagSendCost}
                                onChange={(v) => handleChange("flagSendCost", v)}
                                style={numberStyle}
                                controls={false}
                            />
                        </Form.Item>
                    </Col>
                    <Col span={12}>
                        <Form.Item label={t("flag_lost_cost")} style={formItemStyle}>
                            <InputNumber
                                className="custom-number"
                                min={0}
                                max={10000}
                                value={formData.flagLostCost}
                                onChange={(v) => handleChange("flagLostCost", v)}
                                style={numberStyle}
                                controls={false}
                            />
                        </Form.Item>
                    </Col>
                </Row>
            )}


            <Form.Item label={t("rules")} style={formItemStyle}>
                <Input.TextArea
                    value={formData.rules}
                    onChange={(e) => handleChange("rules", e.target.value)}
                    rows={3}
                    style={{ ...inputStyle, minHeight: 60, resize: "vertical" }}
                />
            </Form.Item>
        </Card>
    );
};

export default MainSettings;