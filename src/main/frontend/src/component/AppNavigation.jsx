import { BookOutlined, ToolOutlined } from '@ant-design/icons';
import { Layout, Menu, Avatar, Comment, Button } from 'antd';
import React, { useContext } from 'react';
import { Link, useNavigate } from 'react-router-dom';
import { UserContext } from '../context/auth/UserContextProvider';

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
			{
				subTitle: '형태소 분석',
				to: '/admin/dictionary/analyze',
			},
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
	const { user } = useContext(UserContext);
	const navigate = useNavigate();

	return (
		<Sider width={200} className="site-layout-background">
			<div
				style={{
					display: 'flex',
					flexDirection: 'column',
					height: '10%',
					backgroundColor: '#ffffff',
					paddingBottom: '10px',
				}}
			>
				<Comment
					avatar={
						<Avatar
							src={user.picture}
							size="large"
						/>
					}
					author={user.name}
					style={{
						margin: '0 16px',
						backgroundColor: '#ffffff',
					}}
				/>
				<Button
					size="small"
					style={{
						margin: '0 16px',
						verticalAlign: 'middle',
						backgroundColor: '#efefef',
					}}
					onClick={() => {
						navigate('/logout');
					}}
				>
					로그아웃
				</Button>
			</div>
			<Menu
				mode="inline"
				defaultOpenKeys={['sub1', 'sub2']}
				defaultSelectedKeys={['1']}
				style={{
					height: '90%',
					borderRight: 0,
					paddingTop: '10px',
				}}
				items={items}
			/>
		</Sider>
	);
};

export default Navigation;
