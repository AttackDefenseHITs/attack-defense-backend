import { Tag, Button, Space, Tooltip } from "antd";
import { Link } from "react-router-dom";
import {
    ReloadOutlined,
    TrophyOutlined,
    TeamOutlined,
    StarOutlined,
    BulbOutlined,
} from "@ant-design/icons";
import React from "react";

const getStatusColor = (status, themeStyles) => {
    switch (status) {
        case "OK":
            return { bg: "rgba(82, 196, 26, 0.15)", text: "#52c41a", border: "#b7eb8f" };
        case "CORRUPT":
            return { bg: "rgba(250, 173, 20, 0.15)", text: "#faad14", border: "#ffe58f" };
        case "MUMBLE":
            return { bg: "rgba(114, 46, 209, 0.15)", text: "#722ed1", border: "#d3adf7" };
        case "DOWN":
            return { bg: "rgba(245, 34, 45, 0.15)", text: "#f5222d", border: "#ffa39e" };
        case "CHECK_FAILED":
            return { bg: "rgba(250, 84, 28, 0.15)", text: "#fa541c", border: "#ffbb96" };
        default:
            return {
                bg: themeStyles.cardBackground,
                text: themeStyles.lightText,
                border: themeStyles.cardBorder,
            };
    }
};

export const getColumns = (
    tableData,
    isAdmin,
    handleRestartChecker,
    t,
    themeStyles,
    onOpenHints // ✅ new
) => {
    const uniqueServices = Array.from(
        new Set(tableData.flatMap((teamData) => Object.keys(teamData.services || {})))
    );

    // map serviceName -> serviceId (берем первый найденный)
    const serviceIdByName = {};
    tableData.forEach((teamData) => {
        Object.entries(teamData.services || {}).forEach(([name, svc]) => {
            if (!serviceIdByName[name] && svc?.serviceId) {
                serviceIdByName[name] = svc.serviceId;
            }
        });
    });

    const commonColumns = [
        {
            title: t("place"),
            dataIndex: ["team", "place"],
            key: "place",
            align: "center",
            width: 100,
            sorter: (a, b) => a.team.place - b.team.place,
            defaultSortOrder: "ascend",
            render: (place) => (
                <div
                    style={{
                        display: "flex",
                        alignItems: "center",
                        justifyContent: "center",
                        gap: "8px",
                    }}
                >
                    {place <= 3 ? (
                        <TrophyOutlined
                            style={{
                                fontSize: "20px",
                                color: place === 1 ? "#FFD700" : place === 2 ? "#C0C0C0" : "#CD7F32",
                            }}
                        />
                    ) : (
                        <StarOutlined style={{ fontSize: "18px", color: themeStyles.lightText }} />
                    )}
                    <span
                        style={{
                            fontSize: "18px",
                            fontWeight: "600",
                            color: place <= 3 ? themeStyles.primaryButton : themeStyles.commonText,
                        }}
                    >
            {place}
          </span>
                </div>
            ),
        },
        {
            title: t("team_ip"),
            dataIndex: "team",
            key: "teamNameWithIp",
            align: "center",
            render: (team) => (
                <Space direction="vertical" size={4} style={{ width: "100%" }}>
                    <div
                        style={{
                            display: "flex",
                            alignItems: "center",
                            justifyContent: "center",
                            gap: "8px",
                        }}
                    >
                        <TeamOutlined style={{ color: themeStyles.primaryButton }} />
                        <Link
                            to={`/teams/${team.id}`}
                            style={{
                                fontSize: "16px",
                                fontWeight: "500",
                                color: themeStyles.commonText,
                                textDecoration: "none",
                            }}
                        >
                            {team.name}
                        </Link>
                    </div>
                    <div
                        style={{
                            fontSize: "14px",
                            color: themeStyles.lightText,
                            fontFamily: "monospace",
                        }}
                    >
                        {team.ipAddress || t("no_data")}
                    </div>
                </Space>
            ),
        },
        {
            title: t("score"),
            dataIndex: ["team", "points"],
            key: "points",
            align: "center",
            width: 120,
            sorter: (a, b) => a.team.points - b.team.points,
            render: (points) => (
                <span
                    style={{
                        fontSize: "16px",
                        fontWeight: "bold",
                        color: themeStyles.highlight,
                        padding: "4px 12px",
                        backgroundColor:
                            themeStyles.name === "dark"
                                ? "rgba(255, 169, 64, 0.1)"
                                : "rgba(250, 140, 22, 0.05)",
                        borderRadius: "16px",
                        display: "inline-block",
                    }}
                >
          {points}
        </span>
            ),
        },
    ];

    const serviceColumns = uniqueServices.map((serviceName) => {
        const serviceId = serviceIdByName[serviceName];

        return {
            title: (
                <Space size={6}>
                    <span>{serviceName}</span>
                    <Tooltip title="Подсказки">
                        <Button
                            size="small"
                            type="text"
                            icon={<BulbOutlined style={{ color: themeStyles.highlight }} />}
                            disabled={!serviceId}
                            onClick={(e) => {
                                e.preventDefault();
                                e.stopPropagation();
                                onOpenHints?.(serviceId, serviceName);
                            }}
                        />
                    </Tooltip>
                </Space>
            ),
            dataIndex: ["services", serviceName],
            key: serviceName,
            align: "center",
            render: (service, record) => {
                if (service) {
                    const statusColors = getStatusColor(service.status, themeStyles);

                    return (
                        <div
                            style={{
                                backgroundColor: themeStyles.cardBackground,
                                borderRadius: "12px",
                                padding: "16px",
                                border: `1px solid ${themeStyles.cardBorder}`,
                                boxShadow: themeStyles.cardShadow,
                                position: "relative",
                            }}
                        >
                            <Space direction="vertical" size={12} style={{ width: "100%" }}>
                                <Tag
                                    style={{
                                        backgroundColor: statusColors.bg,
                                        color: statusColors.text,
                                        border: `1px solid ${statusColors.border}`,
                                        borderRadius: "8px",
                                        padding: "4px 12px",
                                        fontSize: "14px",
                                        fontWeight: "500",
                                        margin: "0 auto",
                                    }}
                                >
                                    {service.status}
                                </Tag>

                                <div
                                    style={{
                                        display: "flex",
                                        justifyContent: "space-between",
                                        gap: "12px",
                                        marginTop: "8px",
                                    }}
                                >
                                    <div
                                        style={{
                                            backgroundColor: themeStyles.inputBackground,
                                            borderRadius: "8px",
                                            padding: "8px 12px",
                                            border: `1px solid ${themeStyles.cardBorder}`,
                                            flex: 1,
                                        }}
                                    >
                                        <div style={{ fontSize: "13px", color: themeStyles.lightText, marginBottom: "4px" }}>
                                            SLA
                                        </div>
                                        <div style={{ fontSize: "15px", fontWeight: "500", color: themeStyles.commonText }}>
                                            {service.sla}
                                        </div>
                                    </div>

                                    {service.flagPoints && (
                                        <div
                                            style={{
                                                backgroundColor: themeStyles.inputBackground,
                                                borderRadius: "8px",
                                                padding: "8px 12px",
                                                border: `1px solid ${themeStyles.cardBorder}`,
                                                flex: 1,
                                            }}
                                        >
                                            <div style={{ fontSize: "13px", color: themeStyles.lightText, marginBottom: "4px" }}>
                                                Flag Points
                                            </div>
                                            <div style={{ fontSize: "15px", fontWeight: "500", color: themeStyles.commonText }}>
                                                +{service.flagPoints.plusPoints} / -{service.flagPoints.minusPoints}
                                            </div>
                                        </div>
                                    )}
                                </div>
                            </Space>

                            {isAdmin && (
                                <Button
                                    type="text"
                                    icon={<ReloadOutlined style={{ color: themeStyles.primaryButton }} />}
                                    onClick={() => handleRestartChecker(service.serviceId, record.team.id)}
                                    style={{
                                        position: "absolute",
                                        top: "8px",
                                        right: "8px",
                                        border: "none",
                                        background: "none",
                                        padding: "4px",
                                        borderRadius: "8px",
                                    }}
                                />
                            )}
                        </div>
                    );
                }

                return (
                    <div
                        style={{
                            backgroundColor: themeStyles.cardBackground,
                            borderRadius: "12px",
                            padding: "16px",
                            border: `1px solid ${themeStyles.cardBorder}`,
                            color: themeStyles.lightText,
                            fontSize: "14px",
                        }}
                    >
                        {t("no_service")}
                    </div>
                );
            },
        };
    });

    return [...commonColumns, ...serviceColumns];
};
