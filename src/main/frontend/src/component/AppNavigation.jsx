import { BookOutlined, ToolOutlined } from '@ant-design/icons';
import { Layout, Menu } from 'antd';
import React from 'react';
import { Link } from 'react-router-dom';

const { Sider } = Layout;

const items = [
	{
		icon: ToolOutlined,
		title: '검색',
		children: [{ subTitle: '검색 결과', to: '/search' }],
	},
	{
		icon: BookOutlined,
		title: '사전 관리',
		children: [
			{ subTitle: '단어', to: '/admin/dictionary/word' },
			{ subTitle: '동의어', to: '/admin/dictionary/synonym' },
		],
	},
].map(({ icon, title, children }, index) => {
	const key = String(index + 1);
	return {
		key: `sub${key}`,
		icon: React.createElement(icon),
		label: <b>{title}</b>,
		children: children.map(({ subTitle, to }, idx) => {
			const subKey = String(`${key}_${idx + 1}`);
			return {
				key: subKey,
				label: <Link to={to}>{subTitle}</Link>,
			};
		}),
	};
});

const Navigation = () => {
	return (
		<Sider width={200} className="site-layout-background">
			<Menu
				mode="inline"
				defaultOpenKeys={['sub1', 'sub2']}
				defaultSelectedKeys={['1']}
				style={{
					height: '100%',
					borderRight: 0,
				}}
				items={items}
			/>
		</Sider>
	);
};

export default Navigation;
