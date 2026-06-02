import React, { useState } from 'react';
import { Space, Typography, Alert, Button, Tooltip } from 'antd';
import { EyeOutlined, EyeInvisibleOutlined, CopyOutlined } from '@ant-design/icons';
import { useTranslation } from 'react-i18next';

const {Text } = Typography;

const VirtualMachineInfo = ({ 
  virtualMachine,
  textColor = '#000' 
}) => {
  const [showPassword, setShowPassword] = useState(false);
  const { t } = useTranslation();

  return (
    <div className="fade-in">
      {virtualMachine ? (
        <Space direction="vertical" size="middle" style={{ width: '100%' }}>
          <div className="info-row">
            <Text strong style={{ color: textColor, fontSize: '16px' }}>{t('ip_address')}</Text>
            <div className="info-value">
              <Text style={{ color: textColor }}>{virtualMachine.ipAddress}</Text>
              <Tooltip title={t('copy_tooltip')}>
                <Button
                  icon={<CopyOutlined />}
                  size="small"
                  type="text"
                  onClick={() => {
                    navigator.clipboard.writeText(virtualMachine.ipAddress);
                  }}
                  style={{ color: textColor }}
                />
              </Tooltip>
            </div>
          </div>
          
          <div className="info-row">
            <Text strong style={{ color: textColor, fontSize: '16px' }}>{t('username')}</Text>
            <div className="info-value">
              <Text style={{ color: textColor }}>{virtualMachine.username}</Text>
              <Tooltip title={t('copy_tooltip')}>
                <Button
                  icon={<CopyOutlined />}
                  size="small"
                  type="text"
                  onClick={() => {
                    navigator.clipboard.writeText(virtualMachine.username);
                  }}
                  style={{ color: textColor }}
                />
              </Tooltip>
            </div>
          </div>
          
          <div className="info-row">
            <Text strong style={{ color: textColor, fontSize: '16px' }}>{t('password')}</Text>
            <div className="info-value">
              <Text style={{ color: textColor }}>
                {showPassword ? virtualMachine.password : '••••••••'}
              </Text>
              <Tooltip title={showPassword ? t('hide_password') : t('show_password')}>
                <Button
                  icon={showPassword ? <EyeInvisibleOutlined /> : <EyeOutlined />}
                  size="small"
                  type="text"
                  onClick={() => setShowPassword(!showPassword)}
                  style={{ color: textColor, marginRight: '4px' }}
                />
              </Tooltip>
              {showPassword && (
                <Tooltip title={t('copy_tooltip')}>
                  <Button
                    icon={<CopyOutlined />}
                    size="small"
                    type="text"
                    onClick={() => {
                      navigator.clipboard.writeText(virtualMachine.password);
                    }}
                    style={{ color: textColor }}
                  />
                </Tooltip>
              )}
            </div>
          </div>
        </Space>
      ) : (
        <Alert
          message={t('vm_data_unavailable')}
          description={t('vm_data_unavailable_desc')}
          type="info"
          showIcon
          style={{ 
            marginTop: '10px', 
            color: textColor, 
            border: `1px solid ${textColor}20`,
            borderRadius: '8px'
          }}
        />
      )}
    </div>
  );
};

export default VirtualMachineInfo;