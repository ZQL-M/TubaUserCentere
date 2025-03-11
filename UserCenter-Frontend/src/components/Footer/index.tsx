import { GithubOutlined } from '@ant-design/icons';
import { DefaultFooter } from '@ant-design/pro-components';
import React from 'react';

const Footer: React.FC = () => {
  const defaultMessage = 'Tuba出品，当然是精品！';
  const currentYear = new Date().getFullYear();
  return (
    <DefaultFooter
      style={{
        background: 'none',
      }}
      copyright={`${currentYear} ${defaultMessage}`}
      links={[
        {
          key: 'gitee',
          title: 'Gitee',
          href: 'https://gitee.com/tub-8/tuba-user-center',
          blankTarget: true,
        },
        {
          key: 'github',
          title: <> <GithubOutlined /> tuba Github </>,
          href: 'https://github.com/ZQL-M/TubaUserCentere',
          blankTarget: true,
        },
        {
          key: 'Tuab user center',
          title: "图八用户中心",
          // todo: 后期部署修改为自己的链接
          href: 'http://192.168.73.1:8000',
          blankTarget: true,
        },
      ]}
    />
  );
};

export default Footer;
