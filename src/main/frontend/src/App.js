import './App.css';
import { BrowserRouter, Route, Routes } from 'react-router-dom';
import { Layout } from 'antd';

import GoodsSearchView from './route/GoodsSearchView';

const { Content } = Layout;

const App = () => (
	<BrowserRouter>
		<Layout style={{ height: '100vh' }}>
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
					</Routes>
				</Content>
			</Layout>
		</Layout>
	</BrowserRouter>
);

export default App;
