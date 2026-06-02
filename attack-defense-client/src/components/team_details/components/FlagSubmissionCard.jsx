import React, { useState } from 'react';
import { Input, Button } from 'antd';
import { SendOutlined } from '@ant-design/icons';
import { useTranslation } from 'react-i18next';

const FlagSubmissionCard = ({
  handleFlagSubmit,
  submitting,
  textColor = '#000',
  backgroundColor = '#fff',
  inputBackground = '#f5f5f5',
  buttonColor = '#1890ff',
  buttonTextColor = '#fff',
}) => {
  const { t } = useTranslation();
  const [flag, setFlag] = useState('');

  const handleSubmit = async () => {
    await handleFlagSubmit(flag);
    setFlag('');
  };

  return (
    <div className="fade-in">
      <Input
        placeholder={t("enter_flag")}
        value={flag}
        onChange={(e) => setFlag(e.target.value)}
        onPressEnter={handleSubmit}
        size="large"
        style={{
          marginBottom: '16px',
          backgroundColor: inputBackground,
          color: textColor,
          border: `1px solid ${textColor}30`,
          borderRadius: '8px',
          height: '45px',
        }}
      />
      <Button
        type="primary"
        block
        size="large"
        onClick={handleSubmit}
        loading={submitting}
        disabled={!flag.trim()}
        icon={<SendOutlined />}
        style={{
          backgroundColor: buttonColor,
          borderColor: buttonColor,
          color: buttonTextColor,
          height: '45px',
          borderRadius: '8px',
          fontSize: '16px',
          fontWeight: '500',
        }}
      >
        {t("submit_flag")}
      </Button>
    </div>
  );
};

export default FlagSubmissionCard;