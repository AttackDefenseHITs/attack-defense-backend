import { Avatar, Tag, Typography } from 'antd';
import { generateColorFromName, getInitials } from '../utils/profileUtils';

const { Title } = Typography;

export const ProfileHeader = ({ profile, roleColor, roleLabel, themeStyles }) => {
    const avatarColor = generateColorFromName(profile.name);
    const initials = getInitials(profile.name);

    return (
        <div style={{ textAlign: 'center', marginBottom: '24px' }}>
            <Avatar
                size={96}
                style={{
                    backgroundColor: avatarColor,
                    fontSize: '32px',
                    fontWeight: 'bold',
                    marginBottom: '16px'
                }}
            >
                {initials}
            </Avatar>

            <Title level={3} style={{ margin: '12px 0 4px', color: themeStyles.commonText }}>
                {profile.name}
            </Title>

            <Tag color={roleColor} style={{ padding: '2px 10px', borderRadius: '12px' }}>
                {roleLabel}
            </Tag>
        </div>
    );
};
