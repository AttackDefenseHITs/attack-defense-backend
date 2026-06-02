import { Space, Typography } from 'antd';
import { UserOutlined } from '@ant-design/icons';
import EditableField from '../../common/EditableField';

const { Title } = Typography;

export const ProfileInfoBlock = ({
                                     profile,
                                     themeStyles,
                                     editingField,
                                     updatedValue,
                                     setUpdatedValue,
                                     startEditing,
                                     saveField,
                                     cancelEditing,
                                     t
                                 }) => (
    <div style={{ backgroundColor: themeStyles.contentBackground, padding: '16px', borderRadius: '8px' }}>
        <Title level={5} style={{ color: themeStyles.commonText }}>
            <UserOutlined /> {t('personal_information')}
        </Title>

        <Space direction="vertical" size={12} style={{ width: '100%' }}>
            <EditableField
                label={t('login_name')}
                value={editingField === 'login' ? updatedValue : profile.login}
                field="login"
                textColor={ themeStyles.commonText }
                isEditing={editingField === 'login'}
                onEdit={(f) => startEditing(f, profile[f])}
                onSave={saveField}
                onCancel={cancelEditing}
                onChange={setUpdatedValue}
            />

            <EditableField
                label={t('name')}
                value={editingField === 'name' ? updatedValue : profile.name}
                field="name"
                textColor={ themeStyles.commonText }
                isEditing={editingField === 'name'}
                onEdit={(f) => startEditing(f, profile[f])}
                onSave={saveField}
                onCancel={cancelEditing}
                onChange={setUpdatedValue}
            />
        </Space>
    </div>
);
