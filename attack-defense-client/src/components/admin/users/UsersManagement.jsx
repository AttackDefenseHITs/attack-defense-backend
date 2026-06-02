import React, { useState, useEffect, useContext } from 'react';
import { Table, Select, Input, Space, Typography, Tag, message } from 'antd';
import { UserOutlined } from '@ant-design/icons';
import { motion } from 'framer-motion';
import WhiteCardWithLabel from '../../common/WhiteCardWithLabel';
import { axiosGetAllUsers, axiosSetUserRole } from '../../../api/requests/usersRequests';
import { useTranslation } from 'react-i18next';
import ThemeContext from '../../../context/ThemeContext';

const { Text } = Typography;
const { Option } = Select;

const UsersManagement = () => {
  const [users, setUsers] = useState([]);
  const [loading, setLoading] = useState(false);
  const [searchText, setSearchText] = useState('');
  const [roleLoading, setRoleLoading] = useState({});
  
  const { theme, themeConfig } = useContext(ThemeContext);
  const themeStyles = themeConfig[theme];
  
  const { t } = useTranslation();
  
  const fetchUsers = async () => {
    setLoading(true);
    try {
      const response = await axiosGetAllUsers();
      setUsers(response.data);
    } catch (error) {
      console.error('Ошибка при загрузке пользователей:', error);
    } finally {
      setLoading(false);
    }
  };
  
  useEffect(() => {
    fetchUsers();
  }, []);
  
  const handleRoleChange = async (userId, newRole) => {
    setRoleLoading(prev => ({ ...prev, [userId]: true }));
    try {
      const response = await axiosSetUserRole(userId, newRole);
      const updatedUser = response.data;
      
      setUsers(
        users.map(user => 
          user.id === userId ? updatedUser : user
        )
      );
      
      message.success(t('role_changed_success'));
    } catch (error) {
      console.error('Ошибка при изменении роли пользователя:', error);
    } finally {
      setRoleLoading(prev => ({ ...prev, [userId]: false }));
    }
  };
  
  const handleSearch = (value) => {
    setSearchText(value.toLowerCase());
  };
  
  const filteredUsers = users.filter(user => 
    user.login.toLowerCase().includes(searchText) || 
    user.name.toLowerCase().includes(searchText) ||
    t(user.role.toLowerCase()).toLowerCase().includes(searchText)
  );
  
  const getRoleTag = (role) => {
    const isAdmin = role === 'ADMIN';
    return (
      <Tag
        color={isAdmin ? themeStyles.adminTagColor : themeStyles.userTagColor}
        style={{
          borderRadius: '4px',
          padding: '0 8px',
          fontSize: '12px',
          fontWeight: 'bold'
        }}
      >
        {t(role.toLowerCase())}
      </Tag>
    );
  };
  
  const columns = [
    {
      title: t('login_name'),
      dataIndex: 'login',
      key: 'login',
      width: '25%',
      render: (text) => <Text style={{ color: themeStyles.commonText }}>{text}</Text>,
      sorter: (a, b) => a.login.localeCompare(b.login),
    },
    {
      title: t('name'),
      dataIndex: 'name',
      key: 'name',
      width: '30%',
      render: (text) => <Text style={{ color: themeStyles.commonText }}>{text}</Text>,
      sorter: (a, b) => a.name.localeCompare(b.name),
    },
    {
      title: t('role'),
      dataIndex: 'role',
      key: 'role',
      width: '20%',
      render: (role) => getRoleTag(role),
      filters: [
        { text: t('admin'), value: 'ADMIN' },
        { text: t('user'), value: 'USER' },
      ],
      onFilter: (value, record) => record.role === value,
    },
    {
      title: t('actions'),
      key: 'actions',
      width: '25%',
      render: (_, record) => (
        <Select
          value={record.role}
          onChange={(value) => handleRoleChange(record.id, value)}
          loading={roleLoading[record.id]}
          style={{ 
            width: 240,
            backgroundColor: themeStyles.inputBackground,
            color: themeStyles.inputText,
          }}
          dropdownStyle={{
            backgroundColor: themeStyles.dropdownBackground,
          }}
        >
          <Option value="ADMIN">
            {t('admin')}
          </Option>
          <Option value="USER">
            {t('user')}
          </Option>
        </Select>
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
        title={t('users_management')}
        textColor={themeStyles.commonText}
        backgroundColor={themeStyles.window}
        icon={<UserOutlined />}
      >
        <Space direction="vertical" size="large" style={{ width: '100%' }}>
          <Input.Search
            placeholder={t('search_user')}
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
            dataSource={filteredUsers}
            rowKey="id"
            loading={loading}
            pagination={{
              defaultPageSize: 10,
              showSizeChanger: true,
              pageSizeOptions: ['10', '20', '50'],
            }}
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

export default UsersManagement; 