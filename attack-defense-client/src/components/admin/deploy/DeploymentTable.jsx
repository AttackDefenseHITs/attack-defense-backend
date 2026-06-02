import React, { useEffect, useState, useContext } from "react";
import { Table, Button, message, Typography, Space, Tooltip } from "antd";
import { RocketOutlined, SyncOutlined, InfoCircleOutlined, ReloadOutlined } from "@ant-design/icons";
import { motion, AnimatePresence } from "framer-motion";
import { 
  axiosGetDeploymentData, 
  axiosDeployAllServices, 
  axiosDeploySpecificService, 
  axiosGetDeployPossibility 
} from "../../../api/requests/deployRequests";
import WhiteCardWithLabel from "../../common/WhiteCardWithLabel";
import { generateDeployColumns } from "./generateDeployColumns";
import ConfirmationModal from "../../common/ConfirmationModal";
import { useWebSocket } from "../../../context/DeployWebSocketContext";
import { useTranslation } from "react-i18next";
import ThemeContext from "../../../context/ThemeContext";

const { Text } = Typography;

const DeploymentTable = () => {
  const [data, setData] = useState([]);
  const [loading, setLoading] = useState(true);
  const [deploying, setDeploying] = useState(false);
  const [canDeploy, setCanDeploy] = useState(true);
  const [isModalVisible, setIsModalVisible] = useState(false);
  const [refreshing, setRefreshing] = useState(false);

  const { registerEventHandler, unregisterEventHandler } = useWebSocket();
  const { t } = useTranslation();
  const { theme, themeConfig } = useContext(ThemeContext);
  const themeStyles = themeConfig[theme];

  useEffect(() => {
    fetchDeploymentData();
    checkDeployPossibility();

    const handleWebSocketMessage = (message) => {
      updateTableData(message.deploymentData);
    };

    registerEventHandler("DEPLOYMENT_UPDATE", handleWebSocketMessage);
    return () => {
      unregisterEventHandler("DEPLOYMENT_UPDATE", handleWebSocketMessage);
    };
  }, []);

  const fetchDeploymentData = async () => {
    try {
      setLoading(true);
      const response = await axiosGetDeploymentData();
      setData(response?.data?.deploymentData || []);
    } catch (error) {
      console.error("Ошибка получения данных:", error);
    } finally {
      setLoading(false);
    }
  };

  const refreshData = async () => {
    try {
      setRefreshing(true);
      await fetchDeploymentData();
      await checkDeployPossibility();
      message.success(t("data_refreshed"));
    } catch (error) {
      console.error("Ошибка обновления данных:", error);
    } finally {
      setRefreshing(false);
    }
  };

  const checkDeployPossibility = async () => {
    try {
      const response = await axiosGetDeployPossibility();
      setCanDeploy(response?.data?.canDeploy || false);
    } catch (error) {
      console.error("Ошибка проверки доступности деплоя:", error);
      setCanDeploy(false);
    }
  };

  const deployAllServices = async () => {
    try {
      if (!canDeploy) {
        message.error(t("deploy_not_available_message"));
        return;
      }

      setDeploying(true);
      await axiosDeployAllServices();
      checkDeployPossibility();
      message.success(t("deploy_started"));
    } catch (error) {
      message.error(t("deploy_error"));
    } finally {
      setDeploying(false);
    }
  };

  const deploySpecificService = async (serviceId, virtualMachineId) => {
    try {
      if (!canDeploy) {
        message.error(t("deploy_not_available_message"));
        return;
      }

      await axiosDeploySpecificService(serviceId, virtualMachineId);
      message.success(t("service_deploy_started"));
    } catch (error) {
      console.error("Ошибка деплоя сервиса:", error);
      message.error(t("service_deploy_error"));
    }
  };

  const updateTableData = (updates) => {
    setData((prevData) => {
      const updatedData = [...prevData];

      updates.forEach((update) => {
        const index = updatedData.findIndex(
          (item) =>
            item.virtualMachine.id === update.virtualMachine.id &&
            item.vulnerableService.id === update.vulnerableService.id
        );

        if (index !== -1) {
          updatedData[index] = { ...updatedData[index], ...update };
        }
      });

      return updatedData;
    });
  };

  const transformData = (rawData) => {
    const virtualMachines = {};
    const services = {};

    rawData.forEach((item) => {
      const vm = item.virtualMachine;
      const service = item.vulnerableService;

      if (!virtualMachines[vm.id]) {
        virtualMachines[vm.id] = {
          key: vm.id,
          teamName: vm.teamName,
          ipAddress: vm.ipAddress,
        };
      }

      if (!services[service.id]) {
        services[service.id] = service;
      }

      virtualMachines[vm.id][service.id] = {
        deploymentStatus: item.deploymentStatus,
        updatedAt: item.updatedAt,
        message: item.message
      };
    });

    return {
      tableData: Object.values(virtualMachines),
      columns: generateDeployColumns(
        Object.values(services),
        deploySpecificService,
        t,
        themeStyles
      ),
    };
  };

  const { tableData, columns } = transformData(data);

  return (
    <motion.div
      initial={{ opacity: 0, y: 20 }}
      animate={{ opacity: 1, y: 0 }}
      transition={{ duration: 0.3 }}
    >

      <WhiteCardWithLabel
        title={t("deployment_settings")}
        textColor={themeStyles.commonText}
        backgroundColor={themeStyles.window}
        button={
          <Space>
            <Button
              icon={<ReloadOutlined />}
              onClick={refreshData}
              loading={refreshing}
              style={{
                borderRadius: '4px'
              }}
            />
            
            <Tooltip title={!canDeploy ? t("deploy_not_available") : ""}>
              <Button
                type="primary"
                danger
                loading={deploying}
                onClick={() => setIsModalVisible(true)}
                disabled={!canDeploy}
                icon={<RocketOutlined />}
                style={{
                  backgroundColor: themeStyles.primaryButton,
                  color: themeStyles.primaryButtonText,
                  borderColor: themeStyles.primaryButton,
                  borderRadius: '4px',
                }}
              >
                {t("deploy_all")}
              </Button>
            </Tooltip>
          </Space>
        }
      >
        <AnimatePresence>
          <motion.div
            initial={{ opacity: 0, height: 0 }}
            animate={{ opacity: 1, height: 'auto' }}
            exit={{ opacity: 0, height: 0 }}
            transition={{ duration: 0.2 }}
          >
            <Space 
              align="center" 
              style={{ 
                padding: '6px 12px', 
                backgroundColor: canDeploy ? themeStyles.successBackground : themeStyles.warningBackground,
                borderRadius: '4px',
                marginBottom: '12px',
                width: '100%',
              }}
            >
              <InfoCircleOutlined style={{ 
                color: canDeploy ? themeStyles.success : themeStyles.warning,
                fontSize: '14px'
              }} />
              <Text style={{ 
                color: themeStyles.commonText, 
                fontSize: '13px',
                margin: 0
              }}>
                {canDeploy 
                  ? t("deploy_available") 
                  : t("deploy_in_progress")}
              </Text>
              {!canDeploy && <SyncOutlined spin style={{ color: themeStyles.warning }} />}
            </Space>
          </motion.div>
        </AnimatePresence>

        <Table
          columns={columns}
          dataSource={tableData}
          loading={loading}
          bordered
          pagination={false}
          scroll={{ x: 'max-content' }}
          className={`custom-table ${theme === "dark" ? "dark-theme" : ""}`}
          size="small"
          style={{
            borderRadius: '6px',
            overflow: 'hidden',
          }}
        />
      </WhiteCardWithLabel>

      <ConfirmationModal
        visible={isModalVisible}
        actionLabel={t("deploy_all")}
        onConfirm={async () => {
          await deployAllServices();
          setIsModalVisible(false);
        }}
        onCancel={() => setIsModalVisible(false)}
        theme={theme}
        themeStyles={themeStyles}
      />
    </motion.div>
  );
};

export default DeploymentTable;
