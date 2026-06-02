import { Space, Tag, Typography } from 'antd';
import { LockOutlined } from '@ant-design/icons';

const { Title } = Typography;

export const ProfileAccessBlock = ({roleColor, roleLabel, themeStyles, t }) => (
    <div style={{ backgroundColor: themeStyles.contentBackground, padding: '16px', borderRadius: '8px' }}>
        <Title level={5} style={{ color: themeStyles.commonText }}>
            <LockOutlined /> {t('access_information')}
        </Title>

        <Space direction="vertical" size={12} style={{ width: '100%' }}>
            <div style={{ display: 'flex', justifyContent: 'space-between' }}>
                <span style={{ color: themeStyles.commonText}}>
                    {t('role')}
                </span>
                <Tag color={roleColor} style={{ padding: '2px 10px', borderRadius: '12px' }}>
                    {roleLabel}
                </Tag>
            </div>
        </Space>
    </div>
);
