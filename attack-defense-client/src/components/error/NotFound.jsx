import React from 'react';
import { Button, Typography, Space } from 'antd';
import { useNavigate } from 'react-router-dom';
import { HomeOutlined } from '@ant-design/icons';
import { motion } from 'framer-motion';
import { useTranslation } from 'react-i18next';
import { useContext } from 'react';
import ThemeContext from '../../context/ThemeContext';

const { Title, Text } = Typography;

const NotFound = () => {
  const navigate = useNavigate();
  const { t } = useTranslation();
  const { theme, themeConfig } = useContext(ThemeContext);
  const themeStyles = themeConfig[theme];

  return (
    <motion.div
      initial={{ opacity: 0, y: 20 }}
      animate={{ opacity: 1, y: 0 }}
      transition={{ duration: 0.5 }}
      className="login-page"
      style={{
        display: 'flex',
        justifyContent: 'center',
        alignItems: 'center',
        background: themeStyles.bannerGradient,
        padding: '20px',
      }}
    >
      <Space direction="vertical" align="center" size="large">
        <motion.div
          initial={{ scale: 0 }}
          animate={{ scale: 1 }}
          transition={{ duration: 0.5, delay: 0.2 }}
        >
          <Title
            level={1}
            style={{
              fontSize: '120px',
              margin: 0,
              color: themeStyles.commonText,
              textAlign: 'center',
              fontWeight: 'bold',
              textShadow: '2px 2px 4px rgba(0,0,0,0.2)'
            }}
          >
            404
          </Title>
        </motion.div>

        <motion.div
          initial={{ opacity: 0 }}
          animate={{ opacity: 1 }}
          transition={{ duration: 0.5, delay: 0.4 }}
        >
          <Title
            level={2}
            style={{
              margin: 0,
              color: themeStyles.commonText,
              textAlign: 'center'
            }}
          >
            {t('page_not_found')}
          </Title>
        </motion.div>

        <motion.div
          initial={{ opacity: 0 }}
          animate={{ opacity: 1 }}
          transition={{ duration: 0.5, delay: 0.6 }}
        >
          <Text
            style={{
              fontSize: '16px',
              color: themeStyles.commonText,
              textAlign: 'center',
              display: 'block'
            }}
          >
            {t('page_not_found_description')}
          </Text>
        </motion.div>

        <motion.div
          initial={{ opacity: 0, y: 20 }}
          animate={{ opacity: 1, y: 0 }}
          transition={{ duration: 0.5, delay: 0.8 }}
        >
          <Button
            type="primary"
            size="large"
            icon={<HomeOutlined />}
            onClick={() => navigate('/')}
            style={{
              backgroundColor: themeStyles.primaryButton,
              borderColor: themeStyles.primaryButton,
              color: themeStyles.primaryButtonText,
              height: '40px',
              padding: '0 24px',
              display: 'flex',
              alignItems: 'center',
              gap: '8px',
              fontSize: '16px'
            }}
          >
            {t('back_to_home')}
          </Button>
        </motion.div>
      </Space>
    </motion.div>
  );
};

export default NotFound; 