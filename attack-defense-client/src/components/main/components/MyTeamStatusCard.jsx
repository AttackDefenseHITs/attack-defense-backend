import React, { useMemo } from "react";
import { Card, Divider, Progress, Skeleton, Tooltip } from "antd";
import { useTranslation } from "react-i18next";

export default function MyTeamStatusCard({ themeStyles, loading, myTeam, onTeamClick }) {
    const { t } = useTranslation();
    const cardStyle = {
        backgroundColor: themeStyles.cardBackground,
        border: `1px solid ${themeStyles.cardBorder}`,
        boxShadow: themeStyles.cardShadow,
        borderRadius: 14,
        overflow: "hidden",
    };

    const isTeamSelected = !!(myTeam && (myTeam.id || myTeam.name));

    const services = myTeam?.services ?? [];
    const onlineCount = useMemo(() => services.filter((s) => s.online).length, [services]);
    const totalCount = services.length;

    const avgUptime = useMemo(() => {
        if (!services.length) return 0;
        const vals = services.map((s) => Number(s.uptimePct ?? 0));
        return Math.round(vals.reduce((a, b) => a + b, 0) / vals.length);
    }, [services]);

    const isOnline = !!myTeam?.online;

    const headerBg =
        themeStyles.cardHeaderBackground ||
        (isOnline
            ? "linear-gradient(135deg, rgba(34,197,94,0.12), rgba(34,197,94,0.02))"
            : "linear-gradient(135deg, rgba(239,68,68,0.12), rgba(239,68,68,0.02))");

    const kpiCard = (title, value, hint) => (
        <div
            style={{
                padding: 12,
                borderRadius: 12,
                background: themeStyles.kpiBackground || (themeStyles.background ?? "rgba(0,0,0,0.02)"),
                border: `1px solid ${themeStyles.cardBorder}`,
            }}
            title={hint}
        >
            <div style={{ color: themeStyles.commonText, fontSize: 12, marginBottom: 6 }}>{title}</div>
            <div style={{ color: themeStyles.commonText, fontSize: 18, fontWeight: 700, lineHeight: 1.1 }}>
                {value}
            </div>
        </div>
    );

    const Header = ({ title, subtitle, rightLabel = t("services_count_metric"), rightValue = "—", bg }) => {
        const clickable = !!(onTeamClick && isTeamSelected && !loading);

        return (
            <div
                style={{
                    padding: "14px 16px",
                    background: bg,
                    borderBottom: `1px solid ${themeStyles.cardBorder}`,
                    display: "flex",
                    alignItems: "flex-start",
                    justifyContent: "space-between",
                    gap: 12,
                }}
            >
                <div style={{ minWidth: 0 }}>
                    <div
                        onClick={clickable ? () => onTeamClick(myTeam) : undefined}
                        onKeyDown={
                            clickable
                                ? (e) => {
                                    if (e.key === "Enter" || e.key === " ") onTeamClick(myTeam);
                                }
                                : undefined
                        }
                        role={clickable ? "button" : undefined}
                        tabIndex={clickable ? 0 : undefined}
                        style={{
                            color: themeStyles.commonText,
                            fontWeight: 600,
                            fontSize: 16,
                            cursor: clickable ? "pointer" : "default",
                            whiteSpace: "nowrap",
                            overflow: "hidden",
                            textOverflow: "ellipsis",
                        }}
                        title={clickable ? t("go_to_team_page") : undefined}
                    >
                        {title}
                    </div>

                    <div style={{ color: themeStyles.secondaryText, fontSize: 12, marginTop: 4 }}>{subtitle}</div>
                </div>

                <div style={{ textAlign: "right" }}>
                    <div style={{ color: themeStyles.commonText, fontSize: 12, marginBottom: 4 }}>{rightLabel}</div>
                    <div style={{ color: themeStyles.commonText, fontWeight: 500, fontSize: 16 }}>{rightValue}</div>
                </div>
            </div>
        );
    };

    const KPIGrid = ({ hints, stack, uptime }) => (
        <div
            style={{
                display: "grid",
                gridTemplateColumns: "repeat(3, minmax(0, 1fr))",
                gap: 10,
                marginBottom: 14,
            }}
        >
            {kpiCard(t("hints"), hints, t("hints_bought_hint"))}
            {kpiCard(t("tod_stack"), stack, t("tod_stack_hint"))}
            {kpiCard(t("avg_uptime"), uptime, t("avg_uptime_hint"))}
        </div>
    );

    const ServicesList = () => {
        if (!totalCount) {
            return <div style={{ color: themeStyles.commonText }}>{t("no_service_data")}</div>;
        }

        return (
            <div style={{ display: "flex", flexDirection: "column", gap: 10 }}>
                {services.map((svc) => {
                    const pct = Number(svc.uptimePct ?? 0);
                    return (
                        <div
                            key={svc.name}
                            style={{
                                padding: "10px 12px",
                                borderRadius: 12,
                                border: `1px solid ${themeStyles.cardBorder}`,
                                background: themeStyles.serviceRowBackground || "transparent",
                                display: "flex",
                                alignItems: "center",
                                justifyContent: "space-between",
                                gap: 12,
                            }}
                        >
                            <div style={{ display: "flex", alignItems: "center", gap: 10, minWidth: 0 }}>
                            <span
                                style={{
                                    width: 10,
                                    height: 10,
                                    borderRadius: 999,
                                    background: svc.online ? "#22c55e" : "#ef4444",
                                    boxShadow: svc.online ? "0 0 0 4px rgba(34,197,94,0.12)" : "0 0 0 4px rgba(239,68,68,0.12)",
                                    flex: "0 0 auto",
                                }}
                            />
                                <div style={{ minWidth: 0 }}>
                                    <div
                                        style={{
                                            color: themeStyles.commonText,
                                            fontWeight: 700,
                                            lineHeight: 1.1,
                                            whiteSpace: "nowrap",
                                            overflow: "hidden",
                                            textOverflow: "ellipsis",
                                            maxWidth: 170,
                                        }}
                                        title={svc.name}
                                    >
                                        {svc.name}
                                    </div>
                                    <div style={{ color: themeStyles.commonText, fontSize: 12, marginTop: 2 }}>
                                        {svc.online ? t("status_up") : t("status_down")}
                                    </div>
                                </div>
                            </div>

                            <Tooltip title={`${t("uptime")}: ${pct}%`}>
                                <div style={{ width: 160 }}>
                                    <Progress percent={pct} size="small" showInfo={false} />
                                </div>
                            </Tooltip>
                        </div>
                    );
                })}
            </div>
        );
    };

    const isPlaceholder = !loading && !isTeamSelected;

    const headerTitle = myTeam?.name ? myTeam.name : t("current_team");
    const headerSubtitle = isPlaceholder
        ? t("team_not_selected_status")
        : t(myTeam?.online ? "all_services_up" : "not_all_services_available");

    const rightValue = isPlaceholder ? "—" : totalCount ? `${onlineCount}/${totalCount}` : "—";
    const headerBackground = isPlaceholder ? themeStyles.cardHeaderBackground || "rgba(0,0,0,0.02)" : headerBg;

    const kpiHints = isPlaceholder ? "—" : myTeam?.hintsBought ?? 0;
    const kpiStack = isPlaceholder ? "—" : myTeam?.stack ?? t("none");
    const kpiUptime = isPlaceholder ? "—" : `${avgUptime}%`;

    return (
        <Card style={cardStyle} bodyStyle={{ padding: 0 }}>
            <Header title={headerTitle} subtitle={headerSubtitle} rightValue={rightValue} bg={headerBackground} />

            <div style={{ padding: 16 }}>
                {loading ? (
                    <Skeleton active />
                ) : (
                    <>
                        <KPIGrid hints={kpiHints} stack={kpiStack} uptime={kpiUptime} />

                        <Divider style={{ margin: "12px 0", borderColor: themeStyles.cardBorder }} />

                        <div style={{ display: "flex", alignItems: "center", justifyContent: "space-between", marginBottom: 10 }}>
                            <div style={{ color: themeStyles.secondaryText, fontSize: 12 }}>{t("services_count_metric")}</div>
                            {!isPlaceholder && !!totalCount && (
                                <div style={{ color: themeStyles.secondaryText, fontSize: 12 }}>{t("updates_realtime")}</div>
                            )}
                        </div>

                        {isPlaceholder ? <div style={{ color: themeStyles.secondaryText }}>{t("no_service_data")}</div> : <ServicesList />}
                    </>
                )}
            </div>
        </Card>
    );
}
