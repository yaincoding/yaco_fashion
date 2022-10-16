import './App.css';
import { BrowserRouter, Route, Routes } from 'react-router-dom';

import UserContextProvider from './context/auth/UserContextProvider';
import Login from './route/login/Login';
import Logout from './route/login/Logout';
import Main from './route/Main';

const App = () => (
	<BrowserRouter>
		<UserContextProvider>
			<Routes>
				<Route path="/login" element={<Login />} />
				<Route path="/logout" element={<Logout />} />
				<Route path="/*" element={<Main />} />
			</Routes>
		</UserContextProvider>
	</BrowserRouter>
);

export default App;
