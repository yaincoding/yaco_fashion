import { Route, Routes, useNavigate } from 'react-router-dom';
import { Layout } from 'antd';

import GoodsSearchView from './goods/GoodsSearchView';
import WordDictionaryView from './admin/WordDictionaryView';
import SynonymDictionaryView from './admin/SynonymDictionaryView';
import AppNavigation from '../component/AppNavigation';
import { useEffect } from 'react';
import axios from 'axios';

const { Content } = Layout;

const Main = () => {
	const navigate = useNavigate();

	useEffect(() => {
		const accessToken =
			window.sessionStorage.getItem('access_token');
		const user = sessionStorage.getItem('user');

		const isLogin = accessToken !== null && user !== null;

		if (!isLogin) {
			navigate('/login');
		}

		axios.interceptors.response.use(
			(response) => {
				return response;
			},
			(error) => {
				if (error.response.status == 401) {
					navigate('/logout');
				}
				return Promise.reject(error);
			}
		);

		axios.defaults.headers.common[
			'Authorization'
		] = `BEARER ${accessToken}`;
	}, []);

	return (
		<Layout style={{ height: '100vh' }}>
			<AppNavigation />
			<Layout
				style={{
					background: '#ffffff',
					padding: '0 24px 24px',
				}}
			>
				<Content
					className="site-layout-background"
					style={{
						display: 'flex',
						flexDirection: 'column',
						alignItems: 'center',
						padding: 24,
						margin: 0,
						minHeight: 280,
					}}
				>
					<Routes>
						<Route
							path="/"
							element={
								<GoodsSearchView />
							}
							exact
						/>
						<Route
							path="/search"
							element={
								<GoodsSearchView />
							}
							exact
						/>
						<Route
							path="/admin"
							element={
								<WordDictionaryView />
							}
							exact
						/>
						<Route
							path="/admin/dictionary"
							element={
								<WordDictionaryView />
							}
							exact
						/>
						<Route
							path="/admin/dictionary/word"
							element={
								<WordDictionaryView />
							}
							exact
						/>
						<Route
							path="/admin/dictionary/synonym"
							element={
								<SynonymDictionaryView />
							}
							exact
						/>
					</Routes>
				</Content>
			</Layout>
		</Layout>
	);
};

export default Main;
