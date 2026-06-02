import React, { useState, useEffect, useContext, useMemo } from 'react';
import { Table, Button, Switch, Input, Space, Typography, Tooltip, Popconfirm } from 'antd';
import { DeleteOutlined, FlagOutlined } from '@ant-design/icons';
import { motion } from 'framer-motion';
import WhiteCardWithLabel from '../../common/WhiteCardWithLabel';
import { axiosGetAllFlags, axiosToggleFlagStatus, axiosDeleteFlag } from '../../../api/requests/flagsRequests';
import { useTranslation } from 'react-i18next';
import ThemeContext from '../../../context/ThemeContext';
import { debounce } from 'lodash';

const { Text } = Typography;

const FlagsManagement = () => {
  const [flags, setFlags] = useState([]);
  const [loading, setLoading] = useState(false);
  const [searchText, setSearchText] = useState('');
  const [statusFilter, setStatusFilter] = useState(null);
  const [page, setPage] = useState(1);
  const [pageSize, setPageSize] = useState(10);
  const [total, setTotal] = useState(0);
  
  const { theme, themeConfig } = useContext(ThemeContext);
  const themeStyles = themeConfig[theme];
  
  const { t } = useTranslation();
  
  const fetchFlags = async (currentPage = 1, size = 10, search = '', isActive = null) => {
    setLoading(true);
    try {
      const response = await axiosGetAllFlags(currentPage - 1, size, search, isActive);
      setFlags(response.data.content);
      setTotal(response.data.totalElements);
    } catch (error) {
      console.error('Ошибка при загрузке флагов:', error);
    } finally {
      setLoading(false);
    }
  };

  const handleSearch = useMemo(
    () => debounce((value) => {
      setPage(1);
      setSearchText(value);
    }, 500),
    []
  );
  
  useEffect(() => {
    fetchFlags(page, pageSize, searchText, statusFilter);
  }, [page, pageSize, searchText, statusFilter]);

  useEffect(() => () => handleSearch.cancel(), [handleSearch]);

  const handleTableChange = (pagination, filters) => {
    const nextStatusFilter = filters.isActive?.length ? filters.isActive[0] : null;

    setPage(nextStatusFilter === statusFilter ? pagination.current : 1);
    setPageSize(pagination.pageSize);
    setStatusFilter(nextStatusFilter);
  };

  const handleStatusToggle = async (flagId) => {
    try {
      const response = await axiosToggleFlagStatus(flagId);
      const updatedFlag = response.data;
      setFlags(
        flags.map(flag => 
          flag.id === flagId ? updatedFlag : flag
        )
      );
    } catch (error) {
      console.error('Ошибка при обновлении статуса флага:', error);
    }
  };

  const handleDeleteFlag = async (flagId) => {
    try {
      await axiosDeleteFlag(flagId);
      setFlags(flags.filter(flag => flag.id !== flagId));
    } catch (error) {
      console.error('Ошибка при удалении флага:', error);
    }
  };

  const columns = [
    {
      title: t('team'),
      dataIndex: 'teamName',
      key: 'teamName',
      width: '20%',
      render: (text) => <Text style={{ color: themeStyles.commonText }}>{text}</Text>,
      sorter: (a, b) => a.teamName.localeCompare(b.teamName),
    },
    {
      title: t('service'),
      dataIndex: 'serviceName',
      key: 'serviceName',
      width: '20%',
      render: (text) => <Text style={{ color: themeStyles.commonText }}>{text}</Text>,
      sorter: (a, b) => a.serviceName.localeCompare(b.serviceName),
    },
    {
      title: t('flag_value'),
      dataIndex: 'value',
      key: 'value',
      width: '35%',
      render: (text) => (
        <Tooltip title={text} placement="topLeft">
          <Text 
            style={{ 
              color: themeStyles.commonText,
              maxWidth: '100%',
              overflow: 'hidden',
              textOverflow: 'ellipsis',
              whiteSpace: 'nowrap',
              display: 'block'
            }}
          >
            {text}
          </Text>
        </Tooltip>
      ),
      ellipsis: true,
    },
    {
      title: t('status'),
      dataIndex: 'isActive',
      key: 'isActive',
      width: '10%',
      render: (isActive, record) => (
        <Switch
          checked={isActive}
          onChange={() => handleStatusToggle(record.id)}
          style={{ 
            backgroundColor: isActive ? themeStyles.successColor : themeStyles.disabledColor
          }}
        />
      ),
      filters: [
        { text: t('active'), value: true },
        { text: t('inactive'), value: false },
      ],
      filteredValue: statusFilter === null ? null : [statusFilter],
    },
    {
      title: t('actions'),
      key: 'actions',
      width: '15%',
      render: (_, record) => (
        <Popconfirm
          title={t('confirm_delete_flag')}
          onConfirm={() => handleDeleteFlag(record.id)}
          okText={t('yes')}
          cancelText={t('no')}
        >
          <Button 
            type="text" 
            danger 
            icon={<DeleteOutlined />}
            style={{
              display: 'flex',
              alignItems: 'center',
              gap: '4px',
            }}
          >
            {t('delete')}
          </Button>
        </Popconfirm>
      ),
    },
  ];

  return (
    <motion.div
      initial={{ opacity: 0, y: 20 }}
      animate={{ opacity: 1, y: 0 }}
      transition={{ duration: 0.5 }}
    >
      <WhiteCardWithLabel
        title={t('flags_management')}
        textColor={themeStyles.commonText}
        backgroundColor={themeStyles.window}
        icon={<FlagOutlined />}
      >
        <Space direction="vertical" size="large" style={{ width: '100%' }}>
          <Input.Search
            placeholder={t('search_flag')}
            allowClear
            onChange={(e) => handleSearch(e.target.value)}
            style={{
              marginBottom: 16,
              backgroundColor: themeStyles.inputBackground,
              color: themeStyles.inputText,
              borderColor: themeStyles.border,
              borderRadius: '6px',
            }}
          />
          
          <Table
            className={`modern-table ${theme === "dark" ? "dark-theme" : ""}`}
            columns={columns}
            dataSource={flags}
            rowKey="id"
            loading={loading}
            pagination={{
              current: page,
              total: total,
              pageSize: pageSize,
              showSizeChanger: true,
              pageSizeOptions: ['10', '20', '50'],
            }}
            onChange={handleTableChange}
            style={{
              backgroundColor: themeStyles.tableBackground,
              borderRadius: '8px',
              overflow: 'hidden',
            }}
          />
        </Space>
      </WhiteCardWithLabel>
    </motion.div>
  );
};

export default FlagsManagement;
