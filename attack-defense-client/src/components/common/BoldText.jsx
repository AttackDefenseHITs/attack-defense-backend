const BoldText = ({ text, color }) => (
  <p
    style={{
      color: color || '#1890ff',
      fontWeight: 'bold',
      fontSize: '16px',
      margin: 0,
      display: 'flex',
      justifyContent: 'center',
      alignItems: 'center'
    }}
  >
    {text}
  </p>
);

export default BoldText;