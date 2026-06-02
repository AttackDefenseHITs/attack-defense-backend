import React, { useContext } from 'react';
import { Form, Input, Select, Space, Typography } from 'antd';
import { CloudServerOutlined, TeamOutlined, UserOutlined, LockOutlined, GlobalOutlined } from '@ant-design/icons';
import { motion } from 'framer-motion';
import ThemeContext from "../../../context/ThemeContext";
import { getFormItemStyle, getInputStyle, getLabelStyle } from '../../styles/modalStyles';

const { Option } = Select;
const { Text } = Typography;

const VirtualMachineModal = ({ form, teams, onFinish, initialValues, t }) => {
  const { theme, themeConfig } = useContext(ThemeContext);
  const themeStyles = themeConfig[theme];

  const formItemStyle = getFormItemStyle(theme);
  const inputStyle = getInputStyle(theme, themeStyles);
  const labelStyle = getLabelStyle(themeStyles);

  return (
    <motion.div
      initial={{ opacity: 0 }}
      animate={{ opacity: 1 }}
      transition={{ duration: 0.3 }}
    >
      <Space direction="vertical" size="middle" style={{ width: '100%' }}>
        <Form
          form={form}
          layout="vertical"
          onFinish={onFinish}
          initialValues={initialValues}
          style={{ color: themeStyles.commonText }}
          size="small"
        >
          <Form.Item
            style={formItemStyle}
            label={
              <Space>
                <GlobalOutlined style={{ fontSize: '14px' }} />
                <Text style={labelStyle}>{t('ip')}</Text>
              </Space>
            }
            name="ipAddress"
            rules={[
              { required: true, message: t('ip_required') },
              {
                pattern: /^\d{1,3}\.\d{1,3}\.\d{1,3}\.\d{1,3}$/,
                message: t('ip_invalid'),
              },
            ]}
          >
            <Input
              placeholder={t('enter_ip')}
              style={inputStyle}
              prefix={
                <CloudServerOutlined
                  style={{ color: themeStyles.icon || '#1677ff', marginRight: '8px', fontSize: '14px' }}
                />
              }
            />
          </Form.Item>

          <Form.Item
            style={formItemStyle}
            label={
              <Space>
                <UserOutlined style={{ fontSize: '14px' }} />
                <Text style={labelStyle}>{t('username')}</Text>
              </Space>
            }
            name="username"
            rules={[{ required: true, message: t('username_required') }]}
          >
            <Input
              placeholder={t('enter_username')}
              style={inputStyle}
              prefix={
                <UserOutlined
                  style={{ color: themeStyles.icon || '#1677ff', marginRight: '8px', fontSize: '14px' }}
                />
              }
            />
          </Form.Item>

          <Form.Item
            style={formItemStyle}
            label={
              <Space>
                <LockOutlined style={{ fontSize: '14px' }} />
                <Text style={labelStyle}>{t('password')}</Text>
              </Space>
            }
            name="password"
            rules={[{ required: true, message: t('password_required') }]}
          >
            <Input.Password
              placeholder={t('enter_password')}
              style={inputStyle}
              prefix={
                <LockOutlined
                  style={{ color: themeStyles.icon || '#1677ff', marginRight: '8px', fontSize: '14px' }}
                />
              }
            />
          </Form.Item>

          <Form.Item
            style={formItemStyle}
            label={
              <Space>
                <TeamOutlined style={{ fontSize: '14px' }} />
                <Text style={labelStyle}>{t('team')}</Text>
              </Space>
            }
            name="teamId"
            rules={[{ required: true, message: t('team_required') }]}
          >
            <Select
              placeholder={t('select_team')}
              style={{ width: '100%', ...inputStyle }}
              dropdownStyle={{
                borderRadius: '4px',
              }}
              optionLabelProp="label"
              size="small"
            >
              {teams.map((team) => (
                <Option key={team.id} value={team.id} label={team.name}>
                  <Space>
                    <TeamOutlined style={{ color: themeStyles.icon || '#1677ff', fontSize: '14px' }} />
                    <span style={{ color: themeStyles.commonText }}>{team.name}</span>
                  </Space>
                </Option>
              ))}
            </Select>
          </Form.Item>
        </Form>
      </Space>
    </motion.div>
  );
};

export default VirtualMachineModal;
