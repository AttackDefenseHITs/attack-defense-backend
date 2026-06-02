import { Divider, Spin } from 'antd';

const WhiteCardWithLabel = ({ title, loading, button, children, backgroundColor, textColor }) => (
  <div
    style={{
      padding: '20px',
      backgroundColor: backgroundColor || '#fff',
      borderRadius: '8px',
      boxShadow: '0 0 10px rgba(0, 0, 0, 0.1)',
    }}
  >
    <div
      style={{
        display: 'flex',
        justifyContent: 'space-between',
        alignItems: 'center',
        marginBottom: '20px',
      }}
    >
      <h2 style={{ margin: 0, color: textColor || '#000' }}>{title}</h2>
      {button}
    </div>

    <Divider style={{ margin: '10px 0' }} />

    {loading ? (
      <div
        style={{
          display: 'flex',
          justifyContent: 'center',
          alignItems: 'center',
          height: '200px',
        }}
      >
        <Spin size="large" tip="Загрузка данных..." />
      </div>
    ) : (
      children
    )}
  </div>
);

export default WhiteCardWithLabel;

  

