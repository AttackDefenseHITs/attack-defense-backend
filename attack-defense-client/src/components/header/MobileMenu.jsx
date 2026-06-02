import { Drawer, Menu } from 'antd';
import { Link } from 'react-router-dom';
import AccountMenu from './AccountMenu';
import { useTranslation } from 'react-i18next';

const MobileMenu = ({ isVisible, toggleDrawer, isAuthenticated, isAdmin }) => {
  const { t } = useTranslation();

  return (
    <Drawer
      title={t('menu')}
      placement="right"
      onClose={toggleDrawer}
      visible={isVisible}
    >
      <Menu mode="vertical">
        {isAuthenticated && (
          <Menu.Item key="1">
            <Link to="/checkers">{t('team_status')}</Link>
          </Menu.Item>
        )}

        {/*{isAuthenticated && (*/}
        {/*  <Menu.Item key="3">*/}
        {/*    <Link to="/learning-materials">{t('learning_materials')}</Link>*/}
        {/*  </Menu.Item>*/}
        {/*)}*/}

        {isAuthenticated && isAdmin && (
          <Menu.Item key="2">
            <Link to="/admin">{t('admin_panel')}</Link>
          </Menu.Item>
        )}
        {isAuthenticated && <AccountMenu />}
      </Menu>
    </Drawer>
  );
};

export default MobileMenu;
