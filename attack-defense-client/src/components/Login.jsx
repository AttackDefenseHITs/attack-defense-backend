import { Helmet } from "react-helmet";
import React, { useEffect, useState, useContext } from 'react';
import { App, Card, Button, Form, Input, Typography, Space } from 'antd';
import { UserOutlined, LockOutlined, ArrowRightOutlined } from '@ant-design/icons';
import { useNavigate } from "react-router-dom";
import { ERROR_MESSAGES } from '../constants/errorMessages';
import { axiosLogin } from '../api/requests/loginRequest';
import { useUser } from '../context/UserContext';
import LoadingScreen from './common/LoadingScreen';
import { useTranslation } from 'react-i18next';
import ThemeContext from '../context/ThemeContext';

const { Title, Text, Link } = Typography;

const Login = () => {
  const navigate = useNavigate();
  const [loading, setLoading] = useState(true);
  const [isSubmitting, setIsSubmitting] = useState(false);
  const { login } = useUser();
  const { t } = useTranslation();
  const { theme, themeConfig } = useContext(ThemeContext);
  const themeStyles = themeConfig[theme]; 

  useEffect(() => {
    const token = localStorage.getItem('accessToken');
    if (token) {
      navigate('/');
    } else {
      setLoading(false);
    }
  }, [navigate]);

  const handleSubmit = async (values) => {
    setIsSubmitting(true);
    try {
      await axiosLogin(values.login, values.password, navigate, login);
    } catch (error) {
    } finally {
      setIsSubmitting(false);
    }
  };

  const onFinishFailed = (errorInfo) => {
    console.error("Failed:", errorInfo);
  };

  if (loading) {
    return <LoadingScreen />;
  }

  return (
    <div
      className="login-page"
      style={{
        display: 'flex',
        justifyContent: 'center',
        alignItems: 'center',
        background: themeStyles.bannerGradient,
        padding: '20px',
      }}
    >
      <Helmet>
        <title>{t('login')} - AD</title>
      </Helmet>
      <App>
        <Card
          className="custom-card fade-in"
          style={{
            width: 420,
            maxWidth: '95vw',
            padding: '2rem',
            backgroundColor: themeStyles.cardBackground,
            border: `1px solid ${themeStyles.cardBorder}`,
            boxShadow: themeStyles.cardShadow,
            borderRadius: '16px',
          }}
        >
          <Space direction="vertical" size={24} style={{ width: '100%' }}>
            <div className="slide-up" style={{ textAlign: 'center' }}>
              <Title
                level={2}
                style={{ 
                  marginBottom: '8px',
                  color: themeStyles.commonText,
                  letterSpacing: '-0.5px'
                }}
              >
                {t('welcome_back')}
              </Title>
              <Text
                style={{
                  fontSize: '16px',
                  color: themeStyles.lightText,
                }}
              >
                {t('login_to_continue')}
              </Text>
            </div>

            <Form
              name="login"
              onFinish={handleSubmit}
              onFinishFailed={onFinishFailed}
              autoComplete="off"
              layout="vertical"
              style={{ width: '100%' }}
              className="slide-up"
            >
              <Form.Item
                name="login"
                rules={[{ required: true, message: ERROR_MESSAGES.ENTER_LOGIN }]}
              >
                <Input
                  size="large"
                  prefix={<UserOutlined style={{ color: themeStyles.lightText }} />}
                  placeholder={t('enter_login')}
                  style={{
                    backgroundColor: themeStyles.inputBackground,
                    color: themeStyles.inputText,
                    border: `1px solid ${themeStyles.cardBorder}`,
                    borderRadius: '8px',
                    height: '45px',
                  }}
                />
              </Form.Item>

              <Form.Item
                name="password"
                rules={[{ required: true, message: ERROR_MESSAGES.ENTER_PASSWORD }]}
              >
                <Input.Password
                  size="large"
                  prefix={<LockOutlined style={{ color: themeStyles.lightText }} />}
                  placeholder={t('enter_password')}
                  style={{
                    backgroundColor: themeStyles.inputBackground,
                    color: themeStyles.inputText,
                    border: `1px solid ${themeStyles.cardBorder}`,
                    borderRadius: '8px',
                    height: '45px',
                  }}
                />
              </Form.Item>

              <Form.Item style={{ marginBottom: '12px' }}>
                <Button
                  type="primary"
                  htmlType="submit"
                  block
                  size="large"
                  loading={isSubmitting}
                  className="custom-button"
                  style={{
                    backgroundColor: themeStyles.primaryButton,
                    borderColor: themeStyles.primaryButton,
                    color: themeStyles.primaryButtonText,
                    height: '45px',
                    borderRadius: '8px',
                    fontSize: '16px',
                    fontWeight: '500',
                  }}
                  icon={<ArrowRightOutlined />}
                >
                  {t('login')}
                </Button>
              </Form.Item>
            </Form>

            <div 
              className="slide-up"
              style={{ 
                textAlign: 'center',
                animation: 'slideUp 0.5s ease-out 0.4s both'
              }}
            >
              <Text
                style={{
                  color: themeStyles.lightText,
                }}
              >
                {t('dont_have_account')}{' '}
                <Link
                  onClick={() => navigate('/registration')}
                  style={{
                    color: themeStyles.primaryButton,
                    fontWeight: '500',
                    transition: 'color 0.3s',
                  }}
                >
                  {t('register')}
                </Link>
              </Text>
            </div>
          </Space>
        </Card>
      </App>
    </div>
  );
};

export default Login;
