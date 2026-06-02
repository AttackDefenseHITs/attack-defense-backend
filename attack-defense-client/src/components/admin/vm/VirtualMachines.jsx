import React, { useState, useContext } from 'react';
import { Button, Modal, List, Typography, Form, Input, Space, Avatar, Card } from 'antd';
import { DeleteOutlined, EditOutlined, PlusOutlined, TeamOutlined, UserOutlined, GlobalOutlined } from '@ant-design/icons';
import { motion, AnimatePresence } from 'framer-motion';
import VirtualMachineModal from './VirtualMachineModal';
import { axiosCreateVm, axiosDeleteVm, axiosUpdateVm } from '../../../api/requests/vmRequests';
import WhiteCardWithLabel from '../../common/WhiteCardWithLabel';
import ThemeContext from "../../../context/ThemeContext";
import { useTranslation } from 'react-i18next';

const { Text } = Typography;

const VirtualMachines = ({ vms, setVms, teams }) => {
  const { t } = useTranslation();
  const [isModalVisible, setIsModalVisible] = useState(false);
  const [form] = Form.useForm();
  const [selectedVm, setSelectedVm] = useState(null);
  const [modalType, setModalType] = useState('create');
  const [searchText, setSearchText] = useState('');

  const { theme, themeConfig } = useContext(ThemeContext);
  const themeStyles = themeConfig[theme];

  const handleAddVm = async (values) => {
    try {
      const response = await axiosCreateVm(values);
      setVms([...vms, response.data]);
      closeModal();
    } catch (error) {
      console.error('Error creating VM:', error);
    }
  };

  const handleEditVm = (vm) => {
    setSelectedVm(vm);
    form.setFieldsValue({
      ipAddress: vm.ipAddress,
      username: vm.username,
      teamId: vm.teamId,
      password: vm.password,
    });
    openModal('edit');
  };

  const handleUpdateVm = async (values) => {
    try {
      await axiosUpdateVm(selectedVm.id, values);
      setVms(vms.map((vm) => (vm.id === selectedVm.id ? { ...vm, ...values } : vm)));
      closeModal();
    } catch (error) {
      console.error('Error updating VM:', error);
    }
  };

  const handleDeleteVm = async (vmId) => {
    try {
      await axiosDeleteVm(vmId);
      setVms(vms.filter((vm) => vm.id !== vmId));
    } catch (error) {
      console.error('Error deleting VM:', error);
    }
  };

  const openModal = (type) => {
    setModalType(type);
    setIsModalVisible(true);
  };

  const closeModal = () => {
    setIsModalVisible(false);
    setSelectedVm(null);
    form.setFieldsValue({
      ipAddress: '',
      username: '',
      password: '',
    });
  };

  const handleSearch = (value) => {
    setSearchText(value.toLowerCase());
  };

  const filteredVms = vms.filter((vm) =>
    vm.teamName.toLowerCase().includes(searchText) || vm.ipAddress.includes(searchText)
  );

  const sortedVms = [...filteredVms].sort((a, b) =>
    a.teamName.localeCompare(b.teamName, undefined, { sensitivity: 'base' })
  );

  const getRandomColor = (teamName) => {
    const colors = [
      '#1890ff', '#52c41a', '#faad14', '#f5222d', '#722ed1',
      '#13c2c2', '#eb2f96', '#fa8c16', '#a0d911', '#fadb14'
    ];
    
    const hash = teamName.split('').reduce((acc, char) => acc + char.charCodeAt(0), 0);
    return colors[hash % colors.length];
  };

  return (
    <motion.div
      initial={{ opacity: 0, y: 20 }}
      animate={{ opacity: 1, y: 0 }}
      transition={{ duration: 0.5 }}
    >
      <WhiteCardWithLabel
        title={t('vm_settings')}
        loading={false}
        textColor={themeStyles.commonText}
        backgroundColor={themeStyles.window}
        button={
          <Button
            type="primary"
            icon={<PlusOutlined />}
            onClick={() => openModal('create')}
            style={{
              backgroundColor: themeStyles.primaryButton,
              color: themeStyles.primaryButtonText,
              borderColor: themeStyles.primaryButton,
              borderRadius: '4px',
              height: '32px',
              display: 'flex',
              alignItems: 'center',
              gap: '6px',
              padding: '0 12px'
            }}
            size="small"
          >
            {t('add_vm')}
          </Button>
        }
      >
        <Space direction="vertical" size="large" style={{ width: '100%' }}>
          <Input.Search
            placeholder={t('teams_search')}
            value={searchText}
            onChange={(e) => handleSearch(e.target.value)}
            allowClear
            style={{
              marginBottom: 12,
              backgroundColor: themeStyles.inputBackground,
              color: themeStyles.inputText,
              borderColor: themeStyles.border,
              borderRadius: '4px',
              height: '32px'
            }}
          />

          <AnimatePresence>
            <List
              bordered={false}
              itemLayout="horizontal"
              dataSource={sortedVms}
              style={{
                color: themeStyles.commonText,
                borderColor: themeStyles.border
              }}
              renderItem={(vm) => (
                <motion.div
                  key={vm.id}
                  initial={{ opacity: 0, y: 10 }}
                  animate={{ opacity: 1, y: 0 }}
                  exit={{ opacity: 0, y: -10 }}
                  transition={{ duration: 0.3 }}
                >
                  <Card
                    style={{
                      color: themeStyles.commonText,
                      backgroundColor: 'transparent',
                      border: `1px solid ${themeStyles.border}`,
                      borderRadius: '6px',
                      marginBottom: '8px',
                      padding: '10px 12px',
                      boxShadow: 'none',
                      transition: 'border-color 0.2s ease'
                    }}
                    bodyStyle={{ padding: '8px' }}
                  >
                    <div style={{ 
                      display: 'flex', 
                      alignItems: 'center', 
                      justifyContent: 'space-between',
                      gap: '16px'
                    }}>
                      <div style={{ 
                        display: 'flex', 
                        alignItems: 'center', 
                        gap: '24px',
                        flex: 1
                      }}>
                        <div style={{ 
                          display: 'flex', 
                          alignItems: 'center', 
                          gap: '8px',
                          minWidth: '200px'
                        }}>
                          <Avatar
                            style={{
                              backgroundColor: getRandomColor(vm.teamName),
                              color: '#fff',
                              display: 'flex',
                              alignItems: 'center',
                              justifyContent: 'center'
                            }}
                            icon={<TeamOutlined />}
                            size="small"
                          />
                          <Text style={{ 
                            color: themeStyles.commonText,
                            fontWeight: 500
                          }}>
                            {vm.teamName}
                          </Text>
                        </div>

                        <div style={{ 
                          display: 'flex', 
                          alignItems: 'center',
                          gap: '24px',
                          color: themeStyles.commonText,
                          fontSize: '13px'
                        }}>
                          <div style={{ display: 'flex', alignItems: 'center', gap: '6px' }}>
                            <GlobalOutlined style={{ fontSize: '14px' }} />
                            <Text style={{ color: themeStyles.commonText }}>{vm.ipAddress}</Text>
                          </div>
                          <div style={{ display: 'flex', alignItems: 'center', gap: '6px' }}>
                            <UserOutlined style={{ fontSize: '14px' }} />
                            <Text style={{ color: themeStyles.commonText }}>{vm.username}</Text>
                          </div>
                        </div>
                      </div>

                      <div style={{ display: 'flex', gap: '8px' }}>
                        <Button
                          type="text"
                          icon={<EditOutlined style={{ fontSize: '14px' }} />}
                          onClick={() => handleEditVm(vm)}
                          style={{ 
                            color: themeStyles.text,
                            height: '28px',
                            padding: '4px 8px',
                            display: 'flex',
                            alignItems: 'center',
                            gap: '4px'
                          }}
                          size="small"
                        >
                          {t('edit')}
                        </Button>
                        <Button
                          type="text"
                          danger
                          icon={<DeleteOutlined style={{ fontSize: '14px' }} />}
                          onClick={() => handleDeleteVm(vm.id)}
                          style={{ 
                            height: '28px',
                            padding: '4px 8px',
                            display: 'flex',
                            alignItems: 'center',
                            gap: '4px'
                          }}
                          size="small"
                        >
                          {t('delete')}
                        </Button>
                      </div>
                    </div>
                  </Card>
                </motion.div>
              )}
              locale={{ emptyText: t('no_vms') }}
              pagination={{
                pageSize: 5,
                className: `custom-pagination ${theme === "dark" ? "custom-pagination-dark" : "custom-pagination-light"}`,
              }}
            />
          </AnimatePresence>
        </Space>

        <Modal
          title={modalType === 'create' ? t('create_vm') : t('edit_vm')}
          open={isModalVisible}
          onCancel={closeModal}
          onOk={() => form.submit()}
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
          <VirtualMachineModal
            form={form}
            teams={teams}
            onFinish={modalType === 'create' ? handleAddVm : handleUpdateVm}
            initialValues={modalType === 'edit' ? selectedVm : null}
            theme={theme}
            themeStyles={themeStyles}
            t={t}
          />
        </Modal>
      </WhiteCardWithLabel>
    </motion.div>
  );
};

export default VirtualMachines;
