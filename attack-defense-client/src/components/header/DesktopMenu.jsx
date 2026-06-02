import { Space, Button, Dropdown } from 'antd';
import { useNavigate } from 'react-router-dom';
import { UserOutlined, DashboardOutlined, TeamOutlined, BookOutlined } from '@ant-design/icons';
import AccountMenu from './AccountMenu';
import { useTranslation } from 'react-i18next';

const DesktopMenu = ({ isAuthenticated, isAdmin }) => {
  const { t } = useTranslation();
  const navigate = useNavigate();

  return (
    <div className="desktop-menu">
      <Space size="small">
        {isAuthenticated && (
          <Button 
            type="link" 
            icon={<TeamOutlined />} 
            onClick={() => navigate('/checkers')}
          >
            {t('team_status')}
          </Button>
        )}
        
        {/*{isAuthenticated && (*/}
        {/*  <Button*/}
        {/*    type="link"*/}
        {/*    icon={<BookOutlined />}*/}
        {/*    onClick={() => navigate('/learning-materials')}*/}
        {/*  >*/}
        {/*    {t('learning_materials')}*/}
        {/*  </Button>*/}
        {/*)}*/}

        {isAuthenticated && isAdmin && (
          <Button 
            type="link" 
            icon={<DashboardOutlined />} 
            onClick={() => navigate('/admin')}
          >
            {t('admin_panel')}
          </Button>
        )}
        
        {isAuthenticated && (
          <Dropdown overlay={<AccountMenu />} placement="bottomRight">
            <Button type="link" icon={<UserOutlined />}>
              {t('account')}
            </Button>
          </Dropdown>
        )}
      </Space>
      <style jsx>{`
        .desktop-menu {
          display: flex;
        }
        @media (max-width: 1000px) {
          .desktop-menu {
            display: none;
          }
        }
      `}</style>
    </div>
  );
};

export default DesktopMenu;
