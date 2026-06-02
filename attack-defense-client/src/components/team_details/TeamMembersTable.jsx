import React from 'react';
import { Table, Tooltip, Badge } from 'antd';
import { TrophyOutlined, UserOutlined, StarOutlined } from '@ant-design/icons';
import { useTranslation } from 'react-i18next';
import './styles/TeamsTable.css';

const TeamMembersTable = ({ members, themeStyles, pageSize }) => {
  const { t } = useTranslation();

  const sortedMembers = [...members]
    .sort((a, b) => (b.points || 0) - (a.points || 0))
    .map((member, index) => ({
      ...member,
      place: index + 1,
    }));

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
      width: '15%',
      align: 'center',
      sorter: (a, b) => a.place - b.place,
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
          <UserOutlined style={{ color: themeStyles.text }} />
          <span>{t('name')}</span>
        </div>
      ),
      dataIndex: 'name',
      key: 'name',
      width: '55%',
      render: (text, record) => (
        <div style={{
          fontWeight: '600',
          fontSize: '15px',
          display: 'flex',
          alignItems: 'center',
          gap: '8px'
        }}>
          {record.isCurrentUser && (
            <Tooltip title={t('you')}>
              <Badge
                status="processing"
                color={themeStyles.primaryButton}
                style={{ marginRight: '4px' }}
              />
            </Tooltip>
          )}
          <span>{text}</span>
          {record.isCurrentUser && (
            <Tooltip title={t('you')} className="custom-tooltip">
              <span className="custom-badge" style={{
                backgroundColor: themeStyles.name === 'dark' ? 'rgba(64, 150, 255, 0.15)' : 'rgba(22, 119, 255, 0.1)',
                color: themeStyles.text,
                fontSize: '12px',
                padding: '2px 8px',
                borderRadius: '10px'
              }}>
                {t('you')}
              </span>
            </Tooltip>
          )}
        </div>
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
      width: '30%',
      align: 'center',
      sorter: (a, b) => a.points - b.points,
      defaultSortOrder: 'descend',
      render: (points) => (
        <span
          style={{
            fontSize: "16px",
            fontWeight: "bold",
            color: themeStyles.highlight,
            padding: '4px 12px',
            backgroundColor: themeStyles.name === 'dark' ? 'rgba(255, 169, 64, 0.1)' : 'rgba(250, 140, 22, 0.05)',
            borderRadius: '16px',
            display: 'inline-block'
          }}
        >
          {points}
        </span>
      ),
    },
  ];

  return (
    <Table
      className={`custom-table ${themeStyles.name === "dark" ? "dark-theme" : ""}`}
      columns={columns}
      dataSource={sortedMembers}
      rowKey="id"
      pagination={{
        pageSize: pageSize
      }}
      style={{
        width: '100%',
      }}
      locale={{ emptyText: t("no_members") }}
      defaultSortOrder="ascend"
    />
  );
};

export default TeamMembersTable;