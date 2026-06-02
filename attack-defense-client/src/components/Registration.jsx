import React, { useEffect, useState, useContext } from 'react';
import { App, Card, Button, Form, Input, Typography, Space } from 'antd';
import { UserOutlined, LockOutlined, IdcardOutlined, ArrowRightOutlined } from '@ant-design/icons';
import { ERROR_MESSAGES } from '../constants/errorMessages';
import { axiosRegistration } from '../api/requests/registrationRequest';
import { useNavigate } from "react-router-dom";
import { useUser } from '../context/UserContext';
import LoadingScreen from './common/LoadingScreen';
import { useTranslation } from 'react-i18next';
import ThemeContext from '../context/ThemeContext';

const { Title, Text, Link } = Typography;

const Registration = () => {
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
      await axiosRegistration(values.login, values.name, values.password, values.confirmPassword, navigate, login);
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
      className="registration-page"
      style={{
        display: 'flex',
        justifyContent: 'center',
        alignItems: 'center',
        background: themeStyles.bannerGradient,
        padding: '20px',
      }}
    >
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
                {t('create_account')}
              </Title>
              <Text
                style={{
                  fontSize: '16px',
                  color: themeStyles.lightText,
                }}
              >
                {t('join_our_community')}
              </Text>
            </div>

            <Form
              name="register"
              onFinish={handleSubmit}
              onFinishFailed={onFinishFailed}
              autoComplete="off"
              layout="vertical"
              style={{ width: '100%' }}
              className="slide-up"
            >
              <Form.Item
                name="name"
                rules={[
                  { required: true, message: ERROR_MESSAGES.ENTER_USERNAME },
                  {
                    min: 1,
                    max: 50,
                    message: 'Имя должно содержать от 1 до 50 символов',
                  }
                ]}
              >
                <Input
                  size="large"
                  prefix={<UserOutlined style={{ color: themeStyles.lightText }} />}
                  placeholder={t('enter_username')}
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
                name="login"
                rules={[
                  { required: true, message: ERROR_MESSAGES.ENTER_LOGIN },
                  {
                    pattern: /^[a-zA-Z0-9._-]+$/,
                    message: 'Логин должен содержать только латинские буквы и цифры',
                  },
                  {
                    min: 3,
                    max: 20,
                    message: 'Логин должен содержать от 3 до 20 символов',
                  },
                ]}
              >
                <Input
                  size="large"
                  prefix={<IdcardOutlined style={{ color: themeStyles.lightText }} />}
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
                rules={[
                  { required: true, message: ERROR_MESSAGES.ENTER_PASSWORD },
                  {
                    min: 6,
                    max: 50,
                    message: 'Пароль должен содержать от 6 до 50 символов',
                  },
                  {
                    pattern: /^(?=.*[A-Za-z])(?=.*\d).+$/,
                    message: 'Пароль должен содержать латинские буквы и цифры',
                  },
                ]}
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

              <Form.Item
                name="confirmPassword"
                dependencies={['password']}
                rules={[
                  { required: true, message: ERROR_MESSAGES.ENTER_CONFIRM_PASSWORD },
                  ({ getFieldValue }) => ({
                    validator(_, value) {
                      if (!value || getFieldValue('password') === value) {
                        return Promise.resolve();
                      }
                      return Promise.reject(new Error(ERROR_MESSAGES.PASSWORDS_DO_NOT_MATCH));
                    },
                  }),
                ]}
              >
                <Input.Password
                  size="large"
                  prefix={<LockOutlined style={{ color: themeStyles.lightText }} />}
                  placeholder={t('enter_confirm_password')}
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
                  {t('create_account')}
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
                {t('already_have_account')}{' '}
                <Link
                  onClick={() => navigate('/login')}
                  style={{
                    color: themeStyles.primaryButton,
                    fontWeight: '500',
                    transition: 'color 0.3s',
                  }}
                >
                  {t('sign_in')}
                </Link>
              </Text>
            </div>
          </Space>
        </Card>
      </App>
    </div>
  );
};

export default Registration;
