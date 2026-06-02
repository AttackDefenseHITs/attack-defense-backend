import React, { useMemo } from 'react';
import { Modal, Form, Input, InputNumber } from 'antd';
import { CodeOutlined, LinkOutlined } from '@ant-design/icons';
import { motion } from 'framer-motion';
import { getFormItemStyle, getInputStyle, getLabelStyle } from '../../styles/modalStyles';

const AddServiceModal = ({ visible, onCancel, onSubmit, newService, setNewService, theme, themeStyles, t }) => {
  const sharedFormItemStyle = useMemo(() => getFormItemStyle(theme), [theme]);
  const sharedInputStyle = useMemo(() => getInputStyle(theme, themeStyles), [theme, themeStyles]);
  const labelStyle = useMemo(() => getLabelStyle(themeStyles), [themeStyles]);

  return (
    <Modal
      title={t('add_service')}
      open={visible}
      onCancel={onCancel}
      onOk={() => onSubmit(newService)}
      okButtonProps={{
        style: {
          backgroundColor: themeStyles.primaryButton,
          borderColor: themeStyles.primaryButton,
          color: themeStyles.primaryButtonText,
        }
      }}
      styles={{
        content: { backgroundColor: themeStyles.window },
        header: {
          backgroundColor: themeStyles.window,
          borderBottom: `1px solid ${themeStyles.border}`,
          padding: '16px 24px',
        },
        body: { padding: 24 },
        footer: {
          backgroundColor: themeStyles.window,
          borderTop: `1px solid ${themeStyles.border}`,
          padding: '16px 24px',
        },
      }}
    >
      <motion.div initial={{ opacity: 0, y: 20 }} animate={{ opacity: 1, y: 0 }} transition={{ duration: 0.3 }}>
        <Form layout="vertical">
          <Form.Item label={<span style={labelStyle}>{t('service_name')}</span>} required style={sharedFormItemStyle}>
            <Input
              prefix={<CodeOutlined style={{ color: themeStyles.lightText }} />}
              value={newService.name}
              onChange={(e) => setNewService({ ...newService, name: e.target.value })}
              placeholder={t('enter_service_name')}
              style={sharedInputStyle}
            />
          </Form.Item>

          <Form.Item label={<span style={labelStyle}>{t('git_repository_url')}</span>} required style={sharedFormItemStyle}>
            <Input
              prefix={<LinkOutlined style={{ color: themeStyles.lightText }} />}
              value={newService.gitRepositoryUrl}
              onChange={(e) => setNewService({ ...newService, gitRepositoryUrl: e.target.value })}
              placeholder={t('enter_repository_url')}
              style={sharedInputStyle}
            />
          </Form.Item>

          <Form.Item label={<span style={labelStyle}>{t('port')}</span>} required style={sharedFormItemStyle}>
            <InputNumber
              min={1}
              max={65535}
              value={newService.port}
              onChange={(value) => setNewService({ ...newService, port: value })}
              style={{ ...sharedInputStyle, width: '100%' }}
            />
          </Form.Item>
        </Form>
      </motion.div>
    </Modal>
  );
};

export default AddServiceModal;
