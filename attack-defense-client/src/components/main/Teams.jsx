import React, { useContext, useState } from "react";
import { Helmet } from "react-helmet";
import { useNavigate } from "react-router-dom";
import { Button, Card, Col, Divider, Row, Modal } from "antd";
import { SwapOutlined } from "@ant-design/icons";

import TeamsTable from "./TeamsTable";
import TeamsDashboard from "./TeamsDashboard.jsx";
import EventBanner from "./EventBanner.jsx";
import TeamMembersTable from "../team_details/TeamMembersTable.jsx";
import RulesBanner from "./components/RulesBanner.jsx";

import MetricsRow from "./components/MetricsRow.jsx";
import EventsFeedCard from "./components/EventsFeedCard.jsx";
import MyTeamStatusCard from "./components/MyTeamStatusCard.jsx";

import { useWebSocket } from "../../context/EventWebSocketContext";
import { useTranslation } from "react-i18next";
import ThemeContext from "../../context/ThemeContext";
import { useTeamsPageData } from "./hooks/useTeamsPageData.js";

export default function Teams() {
  const navigate = useNavigate();
  const { t } = useTranslation();
  const { theme, themeConfig } = useContext(ThemeContext);
  const themeStyles = themeConfig[theme];

  const { registerEventHandler, unregisterEventHandler } = useWebSocket();
  const {
    teams,
    members,
    dashboardData,
    competitionSettings,
    metrics,
    events,
    myTeam,
    loading,
    fetchMembers,
  } = useTeamsPageData({ registerEventHandler, unregisterEventHandler });

  const [showMembers, setShowMembers] = useState(false);
  const [chartModalOpen, setChartModalOpen] = useState(false);

  const toggleView = () => {
    setShowMembers((v) => !v);
    if (!showMembers) fetchMembers();
  };

  const scrollToTeamsTable = () => {
    window.scrollTo({ top: document.body.scrollHeight, behavior: "smooth" });
  };

  const handleRowClick = (record) => navigate(`/teams/${record.key}`);

  const cardStyle = {
    backgroundColor: themeStyles.cardBackground,
    border: `1px solid ${themeStyles.cardBorder}`,
    boxShadow: themeStyles.cardShadow,
    borderRadius: 12,
    overflow: "hidden",
  };

  return (
      <>
        <Helmet>
          <title>Главная - AD</title>
        </Helmet>

        <EventBanner
            title={competitionSettings.name || "Attack-Defense"}
            startTime={competitionSettings.startDate}
            endTime={competitionSettings.endDate}
            status={competitionSettings.status}
            currentRound={competitionSettings.currentRound}
            scrollToTeams={scrollToTeamsTable}
            loading={loading.settings || loading.teams}
        />

          <RulesBanner
              rules={competitionSettings.rules}
              themeStyles={themeStyles}
              theme={theme}
          />

        <div className="content-container">
          <MetricsRow
              themeStyles={themeStyles}
              loading={loading.settings || loading.teams}
              metrics={metrics}
              isFlagsEnabled={competitionSettings.competitionMode === "ATTACK_DEFENSE"}
          />

          <Row gutter={[16, 16]} style={{ marginBottom: 16 }}>
            {/* LEFT: график */}
            <Col xs={24} lg={16}>
              <Card style={cardStyle} bodyStyle={{ padding: 24 }}>
                <TeamsDashboard
                    chartData={dashboardData}
                    onExpand={() => setChartModalOpen(true)}
                />
              </Card>
            </Col>

            <Col xs={24} lg={8}>
              <div
                  style={{
                    height: "100%",
                    display: "flex",
                    flexDirection: "column",
                    gap: 16,
                  }}
              >
                <MyTeamStatusCard
                    themeStyles={themeStyles}
                    loading={loading.myTeam || loading.settings || loading.teams}
                    myTeam={myTeam}
                    onTeamClick={(team) => navigate(`/teams/${team.id}`)}
                />
              </div>
            </Col>
          </Row>

          {/* MODAL: увеличенный график */}
          <Modal
              open={chartModalOpen}
              onCancel={() => setChartModalOpen(false)}
              footer={null}
              width="90vw"
              centered
              styles={{
                  content: { background: themeStyles.cardBackground },
                  header: { background: themeStyles.cardBackground },
                  body: { background: themeStyles.cardBackground },
              }}
              destroyOnClose
          >
              <TeamsDashboard chartData={dashboardData} hideExpand />
          </Modal>

            {/*<div style={{ marginTop: 8, marginBottom: 20 }}>*/}
            {/*    <EventsFeedCard*/}
            {/*        themeStyles={themeStyles}*/}
            {/*        loading={loading.events}*/}
            {/*        events={events}*/}
            {/*        onViewAll={() => navigate("/events")}*/}
            {/*    />*/}
            {/*</div>*/}

            <Divider
                style={{
                    margin: "0 0 16px 0",
                    borderColor: themeStyles.cardBorder,
                    opacity: 0.6,
                }}
            />

          <div
              style={{
                display: "flex",
                alignItems: "center",
                justifyContent: "space-between",
              }}
          >
            <div style={{ display: "flex", alignItems: "center", gap: 12 }}>
              <h2
                  className="section-title"
                  style={{ color: themeStyles.commonText, margin: 0 }}
              >
                {showMembers ? t("members_rating") : t("teams")}
              </h2>

              <Button
                  type="text"
                  icon={<SwapOutlined />}
                  onClick={toggleView}
                  style={{
                    color: themeStyles.commonText,
                    transform: showMembers ? "rotate(90deg)" : "rotate(0)",
                  }}
              />
            </div>
          </div>

          <Divider
              style={{
                margin: "12px 0 16px 0",
                borderColor: themeStyles.cardBorder,
              }}
          />

          <Card style={cardStyle} bodyStyle={{ padding: 0 }}>
            {showMembers ? (
                <TeamMembersTable
                    members={members}
                    loading={loading.members}
                    themeStyles={themeStyles}
                    pageSize={10}
                />
            ) : (
                <TeamsTable
                    data={teams}
                    loading={loading.teams}
                    onRowClick={handleRowClick}
                />
            )}
          </Card>
        </div>
      </>
  );
}