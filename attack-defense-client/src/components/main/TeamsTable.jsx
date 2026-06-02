import React, { useState, useContext } from 'react';
import { Table, Typography, Tooltip, Badge } from 'antd';
import { TrophyOutlined, TeamOutlined, GlobalOutlined, UserOutlined, StarOutlined } from '@ant-design/icons';
import { useTranslation } from "react-i18next";
import ThemeContext from '../../context/ThemeContext';
import themeConfig from '../../configuration/themeConfig';
import '../team_details/styles/TeamsTable.css';

const TeamsTable = ({ data, loading, onRowClick }) => {
  const { t } = useTranslation();
  const { theme } = useContext(ThemeContext);
  const themeStyles = themeConfig[theme];

  const [sorter, setSorter] = useState({
    columnKey: 'place',
    order: 'ascend',
  });

  const handleTableChange = (pagination, filters, sorter) => {
    setSorter({
      columnKey: sorter.field,
      order: sorter.order,
    });
  };

  const columns = [
    {
      title: (
        <div style={{ display: 'flex', alignItems: 'center', gap: '8px' }}>
          <TrophyOutlined style={{ color: themeStyles.highlight }} />
          <span>{t('place')}</span>
        </div>
      ),
      dataIndex: 'place',
      key: 'place',
      width: '10%',
      sorter: (a, b) => a.place - b.place,
      sortOrder: sorter.columnKey === 'place' && sorter.order,
      render: (place) => {
        return (
          <div style={{
              display: 'flex',
              alignItems: 'center',
              gap: '8px'
          }}>
           {place <= 3 ? (
               <TrophyOutlined
                   style={{
                       fontSize: '20px',
                       color: place === 1 ? '#FFD700' : place === 2 ? '#C0C0C0' : '#CD7F32'
                   }}
               />
           ) : (
               <StarOutlined style={{ fontSize: '18px', color: themeStyles.lightText }} />
           )}
               <span style={{
                   fontSize: '18px',
                   fontWeight: '600',
                   color: place <= 3 ? themeStyles.primaryButton : themeStyles.commonText
               }}>
                {place}
              </span>
          </div>
        );
      },
    },
    {
      title: (
        <div style={{ display: 'flex', alignItems: 'center', gap: '8px' }}>
          <TeamOutlined style={{ color: themeStyles.text }} />
          <span>{t('team')}</span>
        </div>
      ),
      dataIndex: 'name',
      key: 'name',
      width: '35%',
      sorter: (a, b) => a.name.localeCompare(b.name),
      sortOrder: sorter.columnKey === 'name' && sorter.order,
      render: (text, record) => (
        <div className="team-name-cell" style={{ 
          fontWeight: '600', 
          fontSize: '15px',
          display: 'flex',
          alignItems: 'center',
          gap: '8px'
        }}>
          {record.isMyTeam && (
            <Badge 
              status="processing" 
              color={themeStyles.primaryButton}
              style={{ marginRight: '4px' }}
            />
          )}
          <span>{text}</span>
          {record.isMyTeam && (
            <Tooltip title={t('your_team')} className="custom-tooltip">
              <span className="custom-badge" style={{ 
                backgroundColor: theme === 'dark' ? 'rgba(64, 150, 255, 0.15)' : 'rgba(22, 119, 255, 0.1)',
                color: themeStyles.text,
                fontSize: '12px',
                padding: '2px 8px',
                borderRadius: '10px'
              }}>
                {t('your_team')}
              </span>
            </Tooltip>
          )}
        </div>
      ),
    },
    {
      title: (
        <div style={{ display: 'flex', alignItems: 'center', gap: '8px' }}>
          <GlobalOutlined style={{ color: themeStyles.text }} />
          <span>{t('ip_address')}</span>
        </div>
      ),
      dataIndex: 'ip',
      key: 'ip',
      width: '20%',
      render: (text) => (
        <Typography.Text
          copyable={{
            text,
            tooltips: [t('copy_tooltip'), t('copied')],
          }}
          style={{
            cursor: "pointer",
            color: themeStyles.text,
            fontFamily: 'monospace',
            fontSize: '14px',
            padding: '4px 8px',
            backgroundColor: theme === 'dark' ? 'rgba(255, 255, 255, 0.05)' : 'rgba(0, 0, 0, 0.02)',
            borderRadius: '4px',
            display: 'inline-block'
          }}
        >
          {text || t('no_data')}
        </Typography.Text>
      ),
    },
    {
      title: (
        <div style={{ display: 'flex', alignItems: 'center', gap: '8px' }}>
          <UserOutlined style={{ color: themeStyles.text }} />
          <span>{t('members')}</span>
        </div>
      ),
      dataIndex: 'members',
      key: 'members',
      width: '15%',
      render: (text) => (
        <span className="members-cell" style={{ 
          fontWeight: '500',
          padding: '2px 8px',
          backgroundColor: theme === 'dark' ? 'rgba(255, 255, 255, 0.05)' : 'rgba(0, 0, 0, 0.02)',
          borderRadius: '4px',
          display: 'inline-block'
        }}>
          {text}
        </span>
      ),
    },
    {
      title: (
        <div style={{ display: 'flex', alignItems: 'center', gap: '8px' }}>
          <StarOutlined style={{ color: themeStyles.highlight }} />
          <span>{t('score')}</span>
        </div>
      ),
      dataIndex: 'points',
      key: 'points',
      width: '15%',
      sorter: (a, b) => a.points - b.points,
      render: (text) => (
        <span
          style={{
            fontSize: "16px",
            fontWeight: "bold",
            color: themeStyles.highlight,
            padding: '4px 12px',
            backgroundColor: theme === 'dark' ? 'rgba(255, 169, 64, 0.1)' : 'rgba(250, 140, 22, 0.05)',
            borderRadius: '16px',
            display: 'inline-block'
          }}
        >
          {text}
        </span>
      ),
    },
  ];

  return (
    <Table
      className={`custom-table ${theme === "dark" ? "dark-theme" : ""}`}
      columns={columns}
      dataSource={data}
      pagination={{ 
        pageSize: 10
      }}
      style={{ width: '100%' }}
      loading={loading}
      onRow={(record) => ({
        onClick: () => onRowClick(record),
        style: { 
          cursor: 'pointer',
          transition: 'background-color 0.3s',
          backgroundColor: record.isMyTeam 
            ? (theme === 'dark' ? 'rgba(64, 150, 255, 0.1)' : 'rgba(22, 119, 255, 0.05)')
            : 'transparent'
        }
      })}
      onChange={handleTableChange}
      rowClassName={(record) => (record.isMyTeam ? 'highlight-row' : '')}
      defaultSortOrder="ascend"
    />
  );
};

export default TeamsTable;
