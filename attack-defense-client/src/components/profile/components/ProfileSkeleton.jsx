import { Card, Skeleton } from 'antd';

export const ProfileSkeleton = ({ themeStyles }) => (
    <div style={{ padding: '20px', maxWidth: '700px', margin: '0 auto' }}>
        <Card style={{ borderRadius: '12px', backgroundColor: themeStyles.window }}>
            <Skeleton active avatar paragraph={{ rows: 4 }} />
        </Card>
    </div>
);