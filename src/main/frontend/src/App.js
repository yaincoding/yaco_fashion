import './App.css';
import { BrowserRouter, Route, Routes } from 'react-router-dom';
import { Layout } from 'antd';

import GoodsSearchView from './route/goods/GoodsSearchView';
import WordDictionaryView from './route/admin/WordDictionaryView';
import SynonymDictionaryView from './route/admin/SynonymDictionaryView';
import AppNavigation from './component/AppNavigation';
import LoginView from './route/login/LoginView';
import Main from './route/Main';

const { Content } = Layout;

const App = () => (
	<BrowserRouter>
		<Routes>
			<Route path="/login" element={<LoginView />} />
			<Route path="/*" element={<Main />} />
		</Routes>
	</BrowserRouter>
);

export default App;
