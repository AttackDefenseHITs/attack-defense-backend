import { Menu } from 'antd';
import { useNavigate, Link } from 'react-router-dom';
import { ROUTES } from '../../constants/routes';
import { useUser } from '../../context/UserContext';
import { useTranslation } from 'react-i18next';

const AccountMenu = () => {
  const { logout } = useUser();
  const navigate = useNavigate();
  const { t } = useTranslation();

  const handleLogout = () => {
    logout();
    navigate(ROUTES.LOGIN, { replace: true });
  };

  return (
    <Menu>
      <Menu.Item key="1">
        <Link to="/profile">{t('profile')}</Link>
      </Menu.Item>
      <Menu.Item key="2" onClick={handleLogout}>
        {t('logout')}
      </Menu.Item>
    </Menu>
  );
};

export default AccountMenu;
