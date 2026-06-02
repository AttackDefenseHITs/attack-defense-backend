import { Card } from 'antd';

export const ProfileContainer = ({ children, themeStyles }) => (
    <Card
        style={{
            borderRadius: '12px',
            overflow: 'hidden',
            backgroundColor: themeStyles.window,
            boxShadow: '0 4px 12px rgba(0, 0, 0, 0.08)',
            border: `1px solid ${themeStyles.border}`
        }}
    >
        {children}
    </Card>
);
