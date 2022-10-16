import { useEffect } from 'react';
import { useNavigate } from 'react-router-dom';

const Logout = () => {
	const navigate = useNavigate();

	useEffect(() => {
		window.sessionStorage.removeItem('user');
		window.sessionStorage.removeItem('access_token');
		window.sessionStorage.removeItem('refresh_token');
		navigate('/login');
	});

	return <div></div>;
};

export default Logout;
