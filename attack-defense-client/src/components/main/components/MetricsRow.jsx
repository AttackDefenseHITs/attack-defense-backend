import React, { useMemo } from "react";
import { Card, Col, Row, Skeleton, Statistic } from "antd";
import { TeamOutlined, RocketOutlined, FlagOutlined, FieldTimeOutlined } from "@ant-design/icons";
import { useTranslation } from "react-i18next";

export default function MetricsRow({ themeStyles, loading, metrics, isFlagsEnabled }) {
    const { t } = useTranslation();
    const palette = useMemo(() => {
        const items = [
            {
                key: "teams",
                title: t("teams_count_metric"),
                value: metrics.teamsCount,
                icon: <TeamOutlined />,
                bg: "linear-gradient(135deg, rgba(99, 179, 237, 0.22), rgba(147, 197, 253, 0.10))",
                border: "rgba(99, 179, 237, 0.30)",
                iconBg: "rgba(99, 179, 237, 0.20)",
            },
            {
                key: "services",
                title: t("services_count_metric"),
                value: metrics.servicesCount,
                icon: <RocketOutlined />,
                bg: "linear-gradient(135deg, rgba(52, 211, 153, 0.22), rgba(167, 243, 208, 0.10))",
                border: "rgba(52, 211, 153, 0.30)",
                iconBg: "rgba(52, 211, 153, 0.20)",
            },
            {
                key: "flags",
                title: t("flags_submitted_metric"),
                value: metrics.flagsSubmitted,
                icon: <FlagOutlined />,
                bg: "linear-gradient(135deg, rgba(167, 139, 250, 0.22), rgba(221, 214, 254, 0.10))",
                border: "rgba(167, 139, 250, 0.30)",
                iconBg: "rgba(167, 139, 250, 0.20)",
            },
            {
                key: "round",
                title: t("round"),
                value: `${metrics.currentRound} / ${metrics.totalRounds}`,
                icon: <FieldTimeOutlined />,
                bg: "linear-gradient(135deg, rgba(251, 191, 36, 0.22), rgba(254, 243, 199, 0.10))",
                border: "rgba(251, 191, 36, 0.30)",
                iconBg: "rgba(251, 191, 36, 0.20)",
            },
        ];

        return isFlagsEnabled ? items : items.filter((i) => i.key !== "flags");
    }, [metrics, isFlagsEnabled, t]);

    const lgSpan = 24 / palette.length;

    const baseCardStyle = {
        borderRadius: 14,
        overflow: "hidden",
        height: "100%",
        boxShadow: themeStyles.cardShadow,
        border: `1px solid ${themeStyles.cardBorder}`,
        transition: "transform 160ms ease, box-shadow 160ms ease",
    };

    const statValueStyle = {
        color: themeStyles.commonText,
        fontWeight: 700,
    };

    const titleStyle = {
        color: themeStyles.commonText,
        fontSize: 14,
        fontWeight: 600,
        letterSpacing: 0.2,
        lineHeight: 1.2,
    };

    return (
        <Row gutter={[16, 16]} style={{ marginTop: 40, marginBottom: 16 }}>
            {palette.map((it) => (
                <Col key={it.key} xs={24} sm={12} lg={lgSpan}>
                    <Card
                        className="metric-card"
                        style={{
                            ...baseCardStyle,
                            background: it.bg,
                            border: `1px solid ${it.border}`,
                        }}
                        bodyStyle={{ padding: 16 }}
                    >
                        {loading ? (
                            <Skeleton active paragraph={false} />
                        ) : (
                            <div style={{ display: "flex", alignItems: "center", gap: 12 }}>
                                <div
                                    style={{
                                        width: 40,
                                        height: 40,
                                        borderRadius: 12,
                                        display: "grid",
                                        placeItems: "center",
                                        background: it.iconBg,
                                        border: `1px solid ${it.border}`,
                                        color: themeStyles.commonText,
                                        flex: "0 0 auto",
                                    }}
                                >
                                    {it.icon}
                                </div>

                                <div style={{ minWidth: 0, flex: 1 }}>
                                    <Statistic
                                        title={<span style={titleStyle}>{it.title}</span>}
                                        value={it.value}
                                        valueStyle={statValueStyle}
                                    />
                                </div>
                            </div>
                        )}
                    </Card>
                </Col>
            ))}

            <style>{`
                .metric-card:hover {
                  transform: translateY(-2px);
                  box-shadow: 0 10px 30px rgba(0,0,0,0.08);
                }
            `}</style>
        </Row>
    );
}
