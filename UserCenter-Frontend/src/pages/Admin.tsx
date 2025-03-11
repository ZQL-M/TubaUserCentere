import { PageContainer } from '@ant-design/pro-components';
import { Outlet } from '@umijs/max';
import React from 'react';

const Admin: React.FC = () => {
  return (
    <PageContainer>
      <Outlet />
    </PageContainer>
  );
};

export default Admin;
