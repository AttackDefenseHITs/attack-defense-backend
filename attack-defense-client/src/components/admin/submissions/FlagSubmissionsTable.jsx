import React, { useEffect, useState } from 'react';
import { Table, Tag, Typography, Tooltip } from 'antd';
import { CheckCircleOutlined, CloseCircleOutlined, FlagOutlined } from '@ant-design/icons';
import { axiosGetFlagSubmissions } from '../../../api/requests/getFlagSubmissionsRequest';
import { useTranslation } from 'react-i18next';
import ThemeContext from '../../../context/ThemeContext';
import { useContext } from 'react';
import { motion } from 'framer-motion';
import WhiteCardWithLabel from '../../common/WhiteCardWithLabel';

const { Text } = Typography;

const FlagSubmissionsTable = () => {
  const [loading, setLoading] = useState(false);
  const [data, setData] = useState([]);
  const [page, setPage] = useState(1);
  const [pageSize, setPageSize] = useState(10);
  const [total, setTotal] = useState(0);

  const { t } = useTranslation();
  const { theme, themeConfig } = useContext(ThemeContext);
  const themeStyles = themeConfig[theme];

  const fetchData = async (currentPage = 1, size = 10) => {
    setLoading(true);
    try {
      const response = await axiosGetFlagSubmissions(currentPage - 1, size);
      const { content, totalElements } = response.data;

      setData(content);
      setTotal(totalElements);
    } catch (error) {
      console.error('Error fetching flag submissions:', error);
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    fetchData(page, pageSize);
  }, [page, pageSize]);

  const handleTableChange = (pagination) => {
    setPage(pagination.current);
    setPageSize(pagination.pageSize);
  };

  const columns = [
    {
      title: t('submitted_flag'),
      dataIndex: 'submittedFlag',
      key: 'submittedFlag',
      render: (text) => (
        <Text style={{ color: themeStyles.commonText }}>{text}</Text>
      ),
    },
    {
      title: t('service'),
      dataIndex: 'serviceName',
      key: 'serviceName',
      render: (text) => (
          <Text style={{ color: themeStyles.commonText }}>
            {text ?? "—"}
          </Text>
      ),
    },
    {
      title: t('name'),
      dataIndex: 'name',
      key: 'name',
      render: (text) => (
        <Text style={{ color: themeStyles.commonText }}>{text}</Text>
      ),
    },
    {
      title: t('submission_time'),
      dataIndex: 'submissionTime',
      key: 'submissionTime',
      render: (text) => (
        <Text style={{ color: themeStyles.commonText }}>
          {new Date(text).toLocaleString()}
        </Text>
      ),
    },
    {
      title: t('status'),
      dataIndex: 'isCorrect',
      key: 'isCorrect',
      render: (isCorrect, record) => (
      <Tooltip title={record.result}>
        <Tag
          color={isCorrect ? 'success' : 'error'}
          icon={isCorrect ? <CheckCircleOutlined /> : <CloseCircleOutlined />}
          style={{
            padding: '4px 8px',
            borderRadius: '4px',
            display: 'flex',
            alignItems: 'center',
            gap: '4px',
            cursor: 'pointer', 
          }}
        >
          {isCorrect ? t('correct') : t('incorrect')}
        </Tag>
      </Tooltip>
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
        title={t('submissions')}
        textColor={themeStyles.commonText}
        backgroundColor={themeStyles.window}
        icon={<FlagOutlined />}
      >
        <Table
          className={`modern-table ${theme === "dark" ? "dark-theme" : ""}`}
          columns={columns}
          dataSource={data}
          rowKey={(record) => `${record.submittedFlag}-${record.submissionTime}`}
          loading={loading}
          pagination={{
            current: page,
            total: total,
            pageSize: pageSize,
            showSizeChanger: true,
            pageSizeOptions: ['10', '20', '50'],
            onChange: (newPage, newSize) => {
              setPage(newPage);
              setPageSize(newSize);
            },
          }}
          onChange={handleTableChange}
          style={{
            backgroundColor: themeStyles.window,
            borderRadius: '8px',
            overflow: 'hidden',
          }}
        />
      </WhiteCardWithLabel>
    </motion.div>
  );
};

export default FlagSubmissionsTable;
