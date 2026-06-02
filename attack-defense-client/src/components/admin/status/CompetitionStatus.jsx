import React, { useContext, useState } from 'react';
import { Button, Space, Tag, Card, Typography, Divider } from 'antd';
import {
  PlayCircleOutlined,
  PauseCircleOutlined,
  CheckCircleOutlined,
  ReloadOutlined,
  WarningOutlined,
} from '@ant-design/icons';
import { motion } from 'framer-motion';
import { useTranslation } from 'react-i18next';

import { getStatusLabel, getActionLabel, getStatusColor } from '../../../utils/competitionMapper';
import ConfirmationModal from '../../common/ConfirmationModal';
import WhiteCardWithLabel from '../../common/WhiteCardWithLabel';
import ThemeContext from '../../../context/ThemeContext';
import { useCompetitionStatus } from './hooks/useCompetitionStatus';

const { Text } = Typography;

const statusIcons = {
  RUNNING: <PlayCircleOutlined style={{ color: '#52c41a' }} />,
  PAUSED: <PauseCircleOutlined style={{ color: '#faad14' }} />,
  FINISHED: <CheckCircleOutlined style={{ color: '#1890ff' }} />,
  UNKNOWN: <WarningOutlined style={{ color: '#ff4d4f' }} />,
};

const actionIcons = {
  START: <PlayCircleOutlined />,
  PAUSE: <PauseCircleOutlined />,
  FINISH: <CheckCircleOutlined />,
};

const CompetitionStatus = () => {
  const { t } = useTranslation();
  const { theme, themeConfig } = useContext(ThemeContext);
  const themeStyles = themeConfig[theme];

  const {
    status,
    availableActions,
    loading,
    changeStatus,
    restartCompetition,
  } = useCompetitionStatus(t);

  const [actionToConfirm, setActionToConfirm] = useState(null);
  const [restartConfirm, setRestartConfirm] = useState(false);

  return (
      <motion.div initial={{ opacity: 0, y: 20 }} animate={{ opacity: 1, y: 0 }}>
        <WhiteCardWithLabel
            title={t('competition_status_settings')}
            loading={loading}
            textColor={themeStyles.commonText}
            backgroundColor={themeStyles.window}
            button={
              <Button
                  danger
                  icon={<ReloadOutlined />}
                  onClick={() => setRestartConfirm(true)}
              >
                {t('restart')}
              </Button>
            }
        >
          {/* STATUS */}
          <Card bordered style={{ borderColor: themeStyles.border }}>
            <Space direction="vertical">
              <Text>{t('current_status')}:</Text>
              <Space>
                {statusIcons[status] || statusIcons.UNKNOWN}
                <Tag
                    color={getStatusColor(status)}
                    style={{ fontSize: '14px', padding: '2px 8px', margin: 0, borderRadius: '4px', height: '24px', lineHeight: '20px', }}
                >
                  {getStatusLabel(status, t)}
                </Tag>
              </Space>
            </Space>
          </Card>

          <Divider />

          {/* ACTIONS */}
          <Space direction="vertical" size={8}>
            <Text>{t('available_actions')}:</Text>

            <Space wrap>
              {availableActions.map((action) => (
                 <Button
                     key={action}
                     icon={actionIcons[action]}
                     onClick={() => setActionToConfirm(action)}
                 >
                     {getActionLabel(action, t)}
                 </Button>
              ))}
            </Space>
          </Space>
            {/* MODALS */}
          <ConfirmationModal
              visible={!!actionToConfirm}
              actionLabel={getActionLabel(actionToConfirm, t)}
              onConfirm={() => {
                changeStatus(actionToConfirm);
                setActionToConfirm(null);
              }}
              onCancel={() => setActionToConfirm(null)}
          />

          <ConfirmationModal
              visible={restartConfirm}
              actionLabel={t('restart')}
              onConfirm={() => {
                restartCompetition();
                setRestartConfirm(false);
              }}
              onCancel={() => setRestartConfirm(false)}
          />
        </WhiteCardWithLabel>
      </motion.div>
  );
};

export default CompetitionStatus;
