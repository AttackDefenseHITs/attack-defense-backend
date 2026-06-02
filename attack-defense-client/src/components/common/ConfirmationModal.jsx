import React, { useState, useEffect } from 'react';
import { Modal, Button, Typography, Space, Progress } from 'antd';
import { ExclamationCircleOutlined, ClockCircleOutlined } from '@ant-design/icons';
import { useTranslation } from 'react-i18next';
import { motion } from 'framer-motion';

const { Text, Title } = Typography;

const ConfirmationModal = ({ visible, actionLabel, onConfirm, onCancel, theme = 'light', themeStyles = {} }) => {
  const { t } = useTranslation();
  const [confirmCountdown, setConfirmCountdown] = useState(5);
  const [confirmLoading, setConfirmLoading] = useState(false);
  const [countdownTimer, setCountdownTimer] = useState(null);

  useEffect(() => {
    if (visible) {
      setConfirmCountdown(5);

      const timer = setInterval(() => {
        setConfirmCountdown((prev) => {
          if (prev === 1) {
            clearInterval(timer);
          }
          return prev - 1;
        });
      }, 1000);

      setCountdownTimer(timer);
      return () => clearInterval(timer);
    }
  }, [visible]);

  const handleConfirm = async () => {
    setConfirmLoading(true);
    try {
      await onConfirm();
    } finally {
      setConfirmLoading(false);
    }
  };

  const handleCancel = () => {
    if (countdownTimer) {
      clearInterval(countdownTimer);
    }
    onCancel();
  };

  return (
    <Modal
      open={visible}
      title={
        <Space align="center">
          <ExclamationCircleOutlined style={{ color: themeStyles?.warning || '#faad14', fontSize: '18px' }} />
          <Title level={5} style={{ margin: 0, color: themeStyles?.commonText || 'inherit' }}>
            {t("confirm_action")}
          </Title>
        </Space>
      }
      onCancel={handleCancel}
      width={400}
      centered
      maskClosable={false}
      className={theme === 'dark' ? 'dark-modal' : ''}
      style={{ 
        borderRadius: '8px',
        overflow: 'hidden'
      }}
      bodyStyle={{ 
        padding: '16px', 
        backgroundColor: themeStyles?.contentBackground || '#fff' 
      }}
      headerStyle={{ 
        padding: '12px 16px',
        backgroundColor: themeStyles?.headerBackground || '#fafafa',
        borderBottom: `1px solid ${themeStyles?.border || '#f0f0f0'}`
      }}
      footer={[
        <Button 
          key="cancel" 
          onClick={handleCancel} 
          disabled={confirmLoading}
          style={{
            borderRadius: '4px',
            height: '30px',
            color: themeStyles?.secondaryText || 'inherit',
            borderColor: themeStyles?.border || 'inherit'
          }}
        >
          {t("cancel")}
        </Button>,
        <Button
          key="confirm"
          type="primary"
          danger
          onClick={handleConfirm}
          disabled={confirmCountdown > 0 || confirmLoading}
          loading={confirmLoading}
          style={{
            borderRadius: '4px',
            height: '30px',
            backgroundColor: confirmCountdown > 0 ? 'transparent' : (themeStyles?.primaryButton || '#1890ff'),
            borderColor: confirmCountdown > 0 ? (themeStyles?.border || '#d9d9d9') : (themeStyles?.primaryButton || '#1890ff'),
            color: confirmCountdown > 0 ? (themeStyles?.disabledText || '#d9d9d9') : (themeStyles?.primaryButtonText || '#fff')
          }}
        >
          {confirmCountdown > 0
            ? t("confirm_with_countdown", { count: confirmCountdown })
            : t("confirm")}
        </Button>,
      ]}
    >
      <motion.div
        initial={{ opacity: 0, y: 10 }}
        animate={{ opacity: 1, y: 0 }}
        transition={{ duration: 0.3 }}
      >
        <Space direction="vertical" size="small" style={{ width: '100%' }}>
          <Text style={{ color: themeStyles?.commonText || 'inherit', fontSize: '14px' }}>
            {t("confirm_action_text", { action: actionLabel })}
          </Text>
          
          {confirmCountdown > 0 && (
            <Space align="center" style={{ marginTop: '12px' }}>
              <ClockCircleOutlined style={{ color: themeStyles?.icon || '#1890ff', fontSize: '14px' }} />
              <Text style={{ color: themeStyles?.secondaryText || '#8c8c8c', fontSize: '13px' }}>
                {t("confirm_available_in", { count: confirmCountdown })}
              </Text>
              <Progress 
                percent={((5 - confirmCountdown) / 5) * 100} 
                size="small" 
                showInfo={false}
                strokeColor={themeStyles?.primaryButton || '#1890ff'}
                style={{ width: '80px' }}
              />
            </Space>
          )}
        </Space>
      </motion.div>
    </Modal>
  );
};

export default ConfirmationModal;
