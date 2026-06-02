import React, { useState, useContext } from 'react';
import { Button, Collapse, Empty, message, Spin, Input, InputNumber, Space, Tooltip, Typography, Tabs } from 'antd';
import {
  CheckOutlined,
  CloseOutlined,
  DeleteOutlined,
  CodeOutlined,
  EditOutlined,
  LinkOutlined,
  NumberOutlined,
} from '@ant-design/icons';
import { motion } from 'framer-motion';
import WhiteCardWithLabel from '../../common/WhiteCardWithLabel';
import { axiosDeleteService, axiosUpdateService } from '../../../api/requests/servicesRequests';
import { axiosGetCheckerForService, axiosPostCheckerForService } from '../../../api/requests/checkerRequests';
import ScriptSection from './ScriptSection';
import HintsSection from './HintsSection';
import { useTranslation } from 'react-i18next';
import ThemeContext from '../../../context/ThemeContext';

const { Text } = Typography;

const ServicesManagement = ({ services, setServices }) => {
  const [serviceScripts, setServiceScripts] = useState({});
  const [checkersCache, setCheckersCache] = useState({});
  const [loadingCheckers, setLoadingCheckers] = useState({});
  const [searchText, setSearchText] = useState('');
  const [editingPortServiceId, setEditingPortServiceId] = useState(null);
  const [editingPort, setEditingPort] = useState(null);
  const [savingPortServiceId, setSavingPortServiceId] = useState(null);

  const { theme, themeConfig } = useContext(ThemeContext);
  const themeStyles = themeConfig[theme];
  const { t } = useTranslation();

  const handleDeleteService = async (serviceId) => {
    try {
      await axiosDeleteService(serviceId);
      message.success(t('service_deleted'));
      setServices(services.filter((service) => service.id !== serviceId));
    } catch (error) {
      message.error(t('service_delete_error'));
      console.error('Error:', error);
    }
  };

  const fetchCheckerCode = async (serviceId) => {
    if (checkersCache[serviceId]) return;

    setLoadingCheckers((prev) => ({ ...prev, [serviceId]: true }));
    try {
      const response = await axiosGetCheckerForService(serviceId);
      setCheckersCache((prev) => ({ ...prev, [serviceId]: response.data }));
      setServiceScripts((prev) => ({ ...prev, [serviceId]: response.data }));
    } catch (error) {
      message.error(t('checker_load_error'));
      console.error('Error loading checker:', error);
    } finally {
      setLoadingCheckers((prev) => ({ ...prev, [serviceId]: false }));
    }
  };

  const handlePanelChange = (key) => {
    if (Array.isArray(key) && key.length > 0) {
      // как и раньше: при открытии панели подгружаем чекер
      fetchCheckerCode(key[0]);
    }
  };

  const handleSaveScript = async (serviceId) => {
    const scriptText = serviceScripts[serviceId];
    if (!scriptText) {
      message.warning(t('enter_script_before_save'));
      return;
    }
    await axiosPostCheckerForService(serviceId, scriptText);
  };

  const handleSearch = (value) => setSearchText(value.toLowerCase());

  const startPortEdit = (event, service) => {
    event.stopPropagation();
    setEditingPortServiceId(service.id);
    setEditingPort(service.port);
  };

  const cancelPortEdit = (event) => {
    event.stopPropagation();
    setEditingPortServiceId(null);
    setEditingPort(null);
  };

  const handlePortChange = (value) => setEditingPort(value);

  const savePort = async (event, serviceId) => {
    event.stopPropagation();

    if (!Number.isInteger(editingPort) || editingPort < 1 || editingPort > 65535) {
      message.warning(t('port_range_error'));
      return;
    }

    setSavingPortServiceId(serviceId);
    try {
      const response = await axiosUpdateService(serviceId, { port: editingPort });
      if (!response) {
        throw new Error('Service update request failed');
      }

      const updatedService = response?.data;

      setServices(services.map((service) => (
        service.id === serviceId
          ? { ...service, ...(updatedService || {}), port: editingPort }
          : service
      )));
      message.success(t('service_updated'));
      setEditingPortServiceId(null);
      setEditingPort(null);
    } catch (error) {
      message.error(t('service_update_error'));
      console.error('Error updating service port:', error);
    } finally {
      setSavingPortServiceId(null);
    }
  };

  const filteredServices = services.filter((service) =>
      service.name.toLowerCase().includes(searchText)
  );
  const hasServices = services.length > 0;

  return (
      <motion.div initial={{ opacity: 0, y: 20 }} animate={{ opacity: 1, y: 0 }} transition={{ duration: 0.5 }}>
        <WhiteCardWithLabel
            title={t('services_management')}
            textColor={themeStyles.commonText}
            backgroundColor={themeStyles.window}
        >
          <Space direction="vertical" size="large" style={{ width: '100%' }}>
            {hasServices && (
                <Input.Search
                    placeholder={t('search_service')}
                    value={searchText}
                    onChange={(e) => handleSearch(e.target.value)}
                    allowClear
                    style={{
                      marginBottom: 16,
                      backgroundColor: themeStyles.inputBackground,
                      color: themeStyles.inputText,
                      borderColor: themeStyles.border,
                      borderRadius: '6px',
                    }}
                />
            )}

            {hasServices ? (
                <Collapse
                    accordion
                    onChange={handlePanelChange}
                    style={{ backgroundColor: 'transparent', border: 'none' }}
                    items={filteredServices.map((service) => ({
                  key: service.id,
                  label: (
                      <Space direction="vertical" size="small">
                        <Text strong style={{ color: themeStyles.commonText, fontSize: '16px' }}>
                          <CodeOutlined style={{ marginRight: '8px' }} />
                          {service.name}
                        </Text>
                        <Space direction="vertical">
                          <Text style={{ color: themeStyles.commonText }}>
                            <LinkOutlined style={{ marginRight: '4px' }} />
                            <a
                                href={service.gitRepositoryUrl}
                                target="_blank"
                                rel="noopener noreferrer"
                                style={{ color: themeStyles.link }}
                            >
                              {t('go_to_repo')}
                            </a>
                          </Text>
                          <Text style={{ color: themeStyles.commonText }}>
                            <NumberOutlined style={{ marginRight: '4px' }} />
                            {t('port')}:{' '}
                            {editingPortServiceId === service.id ? (
                                <Space size={4} onClick={(event) => event.stopPropagation()}>
                                  <InputNumber
                                      min={1}
                                      max={65535}
                                      value={editingPort}
                                      onChange={handlePortChange}
                                      controls={false}
                                      autoFocus
                                      style={{
                                        width: 110,
                                        backgroundColor: themeStyles.inputBackground,
                                        color: themeStyles.inputText,
                                        borderColor: themeStyles.border,
                                      }}
                                  />
                                  <Tooltip title={t('save')}>
                                    <Button
                                        type="text"
                                        size="small"
                                        icon={<CheckOutlined />}
                                        loading={savingPortServiceId === service.id}
                                        onClick={(event) => savePort(event, service.id)}
                                        style={{
                                          color: themeStyles.success || '#52c41a',
                                          width: 28,
                                          height: 28,
                                          padding: 0,
                                        }}
                                    />
                                  </Tooltip>
                                  <Tooltip title={t('cancel')}>
                                    <Button
                                        type="text"
                                        size="small"
                                        icon={<CloseOutlined />}
                                        disabled={savingPortServiceId === service.id}
                                        onClick={cancelPortEdit}
                                        style={{
                                          color: themeStyles.error || '#ff4d4f',
                                          width: 28,
                                          height: 28,
                                          padding: 0,
                                        }}
                                    />
                                  </Tooltip>
                                </Space>
                            ) : (
                                <Space size={4}>
                                  <span>{service.port}</span>
                                  <Tooltip title={t('edit')}>
                                    <Button
                                        type="text"
                                        size="small"
                                        icon={<EditOutlined />}
                                        onClick={(event) => startPortEdit(event, service)}
                                        style={{
                                          color: themeStyles.link,
                                          width: 28,
                                          height: 28,
                                          padding: 0,
                                        }}
                                    />
                                  </Tooltip>
                                </Space>
                            )}
                          </Text>
                        </Space>
                      </Space>
                  ),
                  style: {
                    marginBottom: '16px',
                    border: `1px solid ${themeStyles.border}`,
                    borderRadius: '8px',
                    overflow: 'hidden',
                  },
                  extra: (
                      <Button
                          type="text"
                          danger
                          icon={<DeleteOutlined />}
                          onClick={(e) => {
                            e.stopPropagation();
                            handleDeleteService(service.id);
                          }}
                          style={{ display: 'flex', alignItems: 'center', gap: '4px' }}
                      >
                        {t('delete')}
                      </Button>
                  ),
                  children: (
                      <motion.div
                          initial={{ opacity: 0 }}
                          animate={{ opacity: 1 }}
                          transition={{ duration: 0.3 }}
                          style={{ backgroundColor: themeStyles.window, borderRadius: '8px' }}
                      >
                        <Tabs
                            defaultActiveKey="script"
                            items={[
                              {
                                key: 'script',
                                label: t('script') ?? 'Скрипт',
                                children: loadingCheckers[service.id] ? (
                                    <div style={{ textAlign: 'center', padding: '20px' }}>
                                      <Spin size="large" />
                                    </div>
                                ) : (
                                    <Space direction="vertical" size="large" style={{ width: '100%' }}>
                                      <ScriptSection
                                          serviceId={service.id}
                                          handleSaveScript={handleSaveScript}
                                          serviceScripts={serviceScripts}
                                          setServiceScripts={setServiceScripts}
                                          theme={theme}
                                          themeStyles={themeStyles}
                                          t={t}
                                          backgroundColor={themeStyles.inputBackground}
                                          buttonColor={themeStyles.primaryButton}
                                          buttonText={themeStyles.primaryButtonText}
                                      />
                                    </Space>
                                ),
                              },
                              {
                                key: 'hints',
                                label: t('hints') ?? 'Подсказки',
                                children: (
                                    <HintsSection
                                        serviceId={service.id}
                                        themeStyles={themeStyles}
                                        t={t}
                                    />
                                ),
                              },
                            ]}
                        />
                      </motion.div>
                  )
                }))}
                />
            ) : (
                <Empty
                    image={Empty.PRESENTED_IMAGE_SIMPLE}
                    description={
                      <Text style={{ color: themeStyles.commonText }}>
                        {t('services_empty_sync_repository')}
                      </Text>
                    }
                    style={{
                      padding: '40px 16px',
                      border: `1px dashed ${themeStyles.border}`,
                      borderRadius: '8px',
                      backgroundColor: themeStyles.contentBackground || 'transparent',
                    }}
                />
            )}
          </Space>
        </WhiteCardWithLabel>
      </motion.div>
  );
};

export default ServicesManagement;
