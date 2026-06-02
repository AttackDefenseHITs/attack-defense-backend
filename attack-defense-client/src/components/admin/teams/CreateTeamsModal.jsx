import React, { useState, useContext, useMemo } from 'react';
import { Modal, Form, Input, InputNumber, Button, message, Typography } from 'antd';
import ThemeContext from '../../../context/ThemeContext';
import { getFormItemStyle, getInputStyle, getLabelStyle } from '../../styles/modalStyles';

const { Text } = Typography;

const CreateTeamsModal = ({ visible, onCancel, onSubmit, t }) => {
  const [form] = Form.useForm();
  const [creationMode, setCreationMode] = useState('one');
  const { theme, themeConfig } = useContext(ThemeContext);
  const themeStyles = themeConfig[theme];

  const formItemStyle = useMemo(() => getFormItemStyle(theme), [theme]);
  const inputStyle = useMemo(() => getInputStyle(theme, themeStyles), [theme, themeStyles]);
  const labelStyle = useMemo(() => getLabelStyle(themeStyles), [themeStyles]);

  const handleSubmit = async (values) => {
    try {
      await onSubmit({ ...values, creationMode });
      form.resetFields();
    } catch (error) {
      message.error('Ошибка при создании команд');
    }
  };

  return (
    <Modal
      title={t('team_create')}
      open={visible}
      onCancel={onCancel}
      onOk={() => form.submit()}
      width={520}
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
      <Form form={form} layout="vertical" onFinish={handleSubmit} size="small">
        <Form.Item label={<Text style={labelStyle}>{t('choose_mode_create_team')}</Text>} name="creationMode" style={formItemStyle}>
          <Button.Group style={{ width: '100%', display: 'flex' }}>
            <Button
              type={creationMode === 'one' ? 'primary' : 'default'}
              onClick={() => setCreationMode('one')}
              style={{ flex: 1, height: 32 }}
              size="small"
            >
              {t('single_creation')}
            </Button>
            <Button
              type={creationMode === 'bulk' ? 'primary' : 'default'}
              onClick={() => setCreationMode('bulk')}
              style={{ flex: 1, height: 32 }}
              size="small"
            >
              {t('bulk_creation')}
            </Button>
          </Button.Group>
        </Form.Item>

        {creationMode === 'one' ? (
          <>
            <Form.Item label={<Text style={labelStyle}>{t('team_name')}</Text>} name="name" rules={[{ required: true, message: t('team_name_required') }]} style={formItemStyle}>
              <Input placeholder={t('team_name')} style={inputStyle} />
            </Form.Item>
            <Form.Item label={<Text style={labelStyle}>{t('max_members')}</Text>} name="maxMembers" rules={[{ required: true, message: t('max_members_required') }]} style={formItemStyle}>
              <InputNumber min={2} placeholder={t('max_members')} style={{ ...inputStyle, width: '100%' }} controls={false} />
            </Form.Item>
          </>
        ) : (
          <>
            <Form.Item label={<Text style={labelStyle}>{t('team_count')}</Text>} name="teamsCount" rules={[{ required: true, message: t('team_count_required') }]} style={formItemStyle}>
              <InputNumber min={1} placeholder={t('team_count')} style={{ ...inputStyle, width: '100%' }} controls={false} />
            </Form.Item>
            <Form.Item label={<Text style={labelStyle}>{t('max_members')}</Text>} name="maxMembers" rules={[{ required: true, message: t('max_members_required') }]} style={formItemStyle}>
              <InputNumber min={2} placeholder={t('max_members')} style={{ ...inputStyle, width: '100%' }} controls={false} />
            </Form.Item>
          </>
        )}
      </Form>
    </Modal>
  );
};

export default CreateTeamsModal;
