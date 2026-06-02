import { Link } from 'react-router-dom';
import { Typography } from 'antd';
import logoIcon from '../../resources/hits2.svg';
import { ROUTES } from '../../constants/routes';

const { Title } = Typography;

const Logo = ({ isAuthenticated }) => (
  isAuthenticated ? (
    <Link to={ROUTES.ROOT} style={{ textDecoration: 'none', display: 'flex', alignItems: 'center' }}>
      <img src={logoIcon} alt="Logo" style={{ width: 48, height: 48, marginRight: 8 }} />
      <Title level={2} style={{ margin: 0, color: '#1890ff' }}>
        HITs
      </Title>
    </Link>
  ) : (
    <div style={{ display: 'flex', alignItems: 'center' }}>
      <img src={logoIcon} alt="Logo" style={{ width: 48, height: 48, marginRight: 8 }} />
      <Title level={2} style={{ margin: 0, color: '#1890ff' }}>
        HITs
      </Title>
    </div>
  )
);

export default Logo;
