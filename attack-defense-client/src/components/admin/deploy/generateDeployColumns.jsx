import React from "react";
import { Tag, Tooltip, Button, Space, Typography } from "antd";
import { ReloadOutlined, CloudServerOutlined } from '@ant-design/icons';

const { Text } = Typography;

export const generateDeployColumns = (services, deploySpecificService, t, themeStyles) => {
  const serviceColumns = services.map((service) => ({
    title: (
      <Tooltip title={service.gitRepositoryUrl}>
        <Text strong style={{ fontSize: '13px', color: themeStyles?.commonText }}>
          {service.name}
        </Text>
      </Tooltip>
    ),
    dataIndex: service.id,
    key: service.id,
    align: "center",
    width: 150,
    render: (deployment, record) =>
      deployment ? (
        <div
          style={{
            display: "flex",
            flexDirection: "column",
            justifyContent: "center",
            alignItems: "center",
            padding: "6px",
            borderRadius: "6px",
            background: themeStyles?.contentBackground || "#f5f5f5",
            position: "relative",
            transition: "all 0.2s ease",
            border: `1px solid ${themeStyles?.border || "#e8e8e8"}`,
          }}
        >
          <Space direction="vertical" size={2} align="center" style={{ width: '100%' }}>
          <Tooltip title={deployment.message || '—'}>
            <Tag
              color={
                deployment.deploymentStatus === "SUCCESS"
                  ? "green"
                  : deployment.deploymentStatus === "FAILURE"
                  ? "red"
                  : deployment.deploymentStatus === "IN_PROGRESS"
                  ? "blue"
                  : "orange"
              }
              style={{
                fontSize: "12px",
                padding: "0 6px",
                borderRadius: "4px",
                margin: 0,
                lineHeight: '20px',
                height: '20px',
                cursor: "default",
              }}
            >
              {deployment.deploymentStatus}
            </Tag>
          </Tooltip>

            <Text style={{ fontSize: "11px", color: themeStyles?.secondaryText || "#555", margin: 0 }}>
              {new Date(deployment.updatedAt).toLocaleString()}
            </Text>
          </Space>
          
          <Button
            type="text"
            size="small"
            icon={<ReloadOutlined />}
            style={{
              position: "absolute",
              top: "2px",
              right: "2px",
              padding: "2px",
              height: "auto",
              minWidth: "auto",
              color: themeStyles?.icon || "#1890ff",
              fontSize: "14px",
            }}
            onClick={(e) => {
              e.stopPropagation();
              deploySpecificService(service.id, record.key);
            }}
          />
        </div>
      ) : (
        <Text type="secondary">—</Text>
      ),
  }));

  return [
    {
      title: t('team_ip'),
      dataIndex: "teamName",
      key: "teamName",
      align: "center",
      width: 150,
      fixed: "left",
      sorter: (a, b) => a.teamName.localeCompare(b.teamName),
      defaultSortOrder: "ascend",
      render: (text, record) => (
        <Space direction="vertical" size={0} style={{ padding: '4px 0' }}>
          <Space align="center">
            <CloudServerOutlined style={{ color: themeStyles?.icon || "#1890ff", fontSize: '14px' }} />
            <Text strong style={{ fontSize: "13px", color: themeStyles?.commonText || "#000" }}>
              {record.teamName}
            </Text>
          </Space>
          <Text copyable style={{ fontSize: "12px", color: themeStyles?.secondaryText || "#555" }}>
            {record.ipAddress}
          </Text>
        </Space>
      ),
    },
    ...serviceColumns,
  ];
};
