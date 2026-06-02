import React from 'react';
import { Spin } from 'antd';

const LoadingScreen = () => {
  return (
    <div style={{ display: 'flex', justifyContent: 'center', alignItems: 'center' }}>
      <Spin size="large" tip="Загрузка..." />
    </div>
  );
};

export default LoadingScreen;
