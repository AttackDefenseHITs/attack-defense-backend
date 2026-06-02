import React, { useState, useEffect, useContext, useCallback } from "react";
import { Table, Card, Typography, Space, Modal, List, Tag, Button, Skeleton, message } from "antd";
import { DatabaseOutlined, SyncOutlined, BulbOutlined, LockOutlined, ShoppingCartOutlined } from "@ant-design/icons";
import { useTranslation } from "react-i18next";
import { axiosGetAllStatuses, axiosPostStartCheckerForService } from "../../api/requests/serviceStatusesRequests";
import { axiosGetHintsByServiceId, axiosBuyHint } from "../../api/requests/hintsRequests";
import { CheckerWebSocketContext } from "../../context/CheckerWebSocketContext";
import { useUser } from "../../context/UserContext";
import { getColumns } from "./helpers/teamsTableColumns";
import { updateServiceStatuses } from "./helpers/updateServiceStatuses";
import ThemeContext from "../../context/ThemeContext";
import "./styles/checkerStyles.css";

const { Title } = Typography;

const Checker = () => {
  const { t } = useTranslation();
  const [tableData, setTableData] = useState([]);
  const [loading, setLoading] = useState(true);
  const { roles } = useUser();
  const isAdmin = roles.includes("ADMIN");

  const { registerEventHandler, unregisterEventHandler } = useContext(CheckerWebSocketContext);
  const { theme, themeConfig } = useContext(ThemeContext);
  const themeStyles = themeConfig[theme];

  const [hintsOpen, setHintsOpen] = useState(false);
  const [hintsLoading, setHintsLoading] = useState(false);
  const [hints, setHints] = useState([]);
  const [selectedService, setSelectedService] = useState({ id: null, name: "" });
  const [expandedHintId, setExpandedHintId] = useState(null);

  const loadHints = useCallback(
      async (serviceId) => {
        if (!serviceId) return;
        setHintsLoading(true);
        try {
          const res = await axiosGetHintsByServiceId(serviceId);
          const list = Array.isArray(res?.data?.hints) ? res.data.hints : [];
          setHints(list);
        } catch (e) {
          message.error("Не удалось загрузить подсказки");
          setHints([]);
        } finally {
          setHintsLoading(false);
        }
      },
      []
  );

  const openHintsModal = useCallback(
      async (serviceId, serviceName) => {
        setSelectedService({ id: serviceId, name: serviceName });
        setHintsOpen(true);
        await loadHints(serviceId);
      },
      [loadHints]
  );

  const handleBuyHint = useCallback(
      async (templateId) => {
        try {
          await axiosBuyHint(templateId);
          message.success("Подсказка куплена");
          await loadHints(selectedService.id);
        } catch (e) {
          message.error("Не удалось купить подсказку");
        }
      },
      [loadHints, selectedService.id]
  );

  useEffect(() => {
    const fetchData = async () => {
      try {
        const response = await axiosGetAllStatuses();
        setTableData(response.data.serviceStatuses);
      } catch (error) {
        console.error("Ошибка при загрузке данных:", error);
      } finally {
        setLoading(false);
      }
    };

    fetchData();
  }, [t]);

  // ---- WS updates ----
  useEffect(() => {
    const handleWebSocketMessage = (message) => {
      if (message?.eventType === "CHECKER") {
        setTableData((prevData) => updateServiceStatuses(prevData, message.message.serviceStatuses));
      }
    };

    registerEventHandler("CHECKER", handleWebSocketMessage);
    return () => unregisterEventHandler("CHECKER", handleWebSocketMessage);
  }, [registerEventHandler, unregisterEventHandler]);

  const handleRestartChecker = async (teamId, serviceName) => {
    await axiosPostStartCheckerForService(teamId, serviceName, ["check", "put", "get", "get_flags"]);
  };

  const getColumnsWithActions = () =>
      getColumns(tableData, isAdmin, handleRestartChecker, t, themeStyles, openHintsModal);

  return (
      <div
          className="checker-page fade-in"
          style={{
            padding: "40px",
            background: themeStyles.bannerGradient,
          }}
      >
        <div style={{ maxWidth: "1400px", margin: "0 auto" }}>
          <Card
              className="custom-card slide-up"
              style={{
                borderRadius: "16px",
                backgroundColor: themeStyles.cardBackground,
                border: `1px solid ${themeStyles.cardBorder}`,
                boxShadow: themeStyles.cardShadow,
              }}
          >
            <Space direction="vertical" size={24} style={{ width: "100%" }}>
              <div style={{ display: "flex", alignItems: "center", gap: "12px" }}>
                <DatabaseOutlined style={{ fontSize: "24px", color: themeStyles.primaryButton }} />
                <Title level={2} style={{ margin: 0, color: themeStyles.commonText }}>
                  {t("service_status")}
                </Title>
                {loading && (
                    <SyncOutlined
                        spin
                        style={{ fontSize: "24px", color: themeStyles.primaryButton, marginLeft: "auto" }}
                    />
                )}
              </div>

              <Table
                  className={`modern-table ${theme === "dark" ? "dark-theme" : ""}`}
                  columns={getColumnsWithActions()}
                  dataSource={tableData}
                  loading={loading}
                  pagination={{
                    pageSize: 10,
                    style: { marginTop: "24px", padding: "12px 0" },
                  }}
                  rowKey={(record) => record.key}
                  style={{ borderRadius: "12px", overflow: "hidden" }}
                  scroll={{ x: "max-content" }}
              />
            </Space>
          </Card>
        </div>

        <Modal
            open={hintsOpen}
            onCancel={() => setHintsOpen(false)}
            footer={null}
            centered
            destroyOnClose
            title={
              <Space>
                <BulbOutlined style={{ color: themeStyles.highlight }} />
                <span style={{ color: themeStyles.commonText }}>
              Подсказки — {selectedService.name || "Сервис"}
            </span>
              </Space>
            }
            styles={{
                content: { background: themeStyles.cardBackground },
                header: { background: themeStyles.cardBackground },
                body: { background: themeStyles.cardBackground },
            }}
        >
          {hintsLoading ? (
              <Skeleton active />
          ) : (
              <List
                  dataSource={hints}
                  locale={{
                    emptyText: <span style={{ color: themeStyles.secondaryText }}>Подсказок нет</span>,
                  }}
                  renderItem={(h) => {
                    const purchased = !!h.purchased;
                    return (
                        <List.Item
                            style={{
                              border: `1px solid ${themeStyles.cardBorder}`,
                              borderRadius: 12,
                              padding: 12,
                              marginBottom: 10,
                              background: themeStyles.window,
                            }}
                            actions={[
                              purchased ? (
                                  <Tag color="green">Куплено</Tag>
                              ) : (
                                  <Button
                                      type="primary"
                                      icon={<ShoppingCartOutlined />}
                                      onClick={() => handleBuyHint(h.id)}
                                  >
                                    Купить
                                  </Button>
                              ),
                            ]}
                        >
                          <List.Item.Meta
                              title={
                                <Space>
                                    <span style={{ color: themeStyles.commonText, fontWeight: 600 }}>
                                      Подсказка {h.level}
                                    </span>
                                    <Tag color="blue">
                                        {`${Math.round((h.multiplier ?? 0) * 100)}%`}
                                    </Tag>
                                </Space>
                              }
                              description={
                                  purchased ? (
                                      <div>
                                          <Typography.Paragraph
                                              style={{
                                                  color: themeStyles.commonText,
                                                  marginBottom: 6,
                                                  whiteSpace: "pre-wrap",
                                              }}
                                              ellipsis={
                                                  expandedHintId !== h.id
                                                      ? { rows: 3, expandable: false }
                                                      : false
                                              }
                                          >
                                              {h.text}
                                          </Typography.Paragraph>

                                          {h.text?.length > 180 && (
                                              <Button
                                                  type="link"
                                                  size="small"
                                                  style={{
                                                      padding: 0,
                                                      height: "auto",
                                                      color: themeStyles.link,
                                                  }}
                                                  onClick={() =>
                                                      setExpandedHintId(expandedHintId === h.id ? null : h.id)
                                                  }
                                              >
                                                  {expandedHintId === h.id
                                                      ? t?.("expand") ?? "Показать полностью"
                                                      : t?.("collapse") ?? "Свернуть"}
                                              </Button>
                                          )}
                                      </div>
                                  ) : (
                                      <Space style={{ color: themeStyles.secondaryText }}>
                                          <LockOutlined />
                                          <span>Купите подсказку, чтобы увидеть текст</span>
                                      </Space>
                                  )
                              }
                          />
                        </List.Item>
                    );
                  }}
              />
          )}
        </Modal>
      </div>
  );
};

export default Checker;