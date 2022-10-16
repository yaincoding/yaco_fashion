import axios from 'axios';
import { useContext, useEffect, useState } from 'react';
import { useNavigate } from 'react-router-dom';
import GoogleLogin from './component/GoogleLogin';
import { UserContext } from '../../context/auth/UserContextProvider';

const Login = () => {
	const { user, contextDispatch } = useContext(UserContext);
	const navigate = useNavigate();

	const onGoogleSignIn = async (res) => {
		const { clientId, credential } = res;
		await axios({
			url: '/api/login',
			method: 'post',
			data: {
				client_id: clientId,
				credential: credential,
			},
		})
			.then((response) => {
				const {
					name,
					picture,
					access_token,
					refresh_token,
				} = response.data;
				contextDispatch({
					type: 'LOGIN',
					value: {
						name,
						picture,
					},
				});
				window.sessionStorage.setItem(
					'user',
					JSON.stringify({
						name: name,
						picture: picture,
					})
				);
				window.sessionStorage.setItem(
					'access_token',
					access_token
				);

				window.sessionStorage.setItem(
					'refresh_token',
					refresh_token
				);

				if (access_token !== null && name !== null) {
					navigate('/search');
				}
			})
			.catch((error) => {
				console.error(error);
			});
	};

	useEffect(() => {
		const isLogin =
			user.name !== null &&
			window.sessionStorage.getItem('access_token') !== null;

		if (isLogin) {
			navigate('/search');
		}
	}, []);

	return (
		<div
			style={{
				display: 'flex',
				width: '100wh',
				height: '100vh',
				justifyContent: 'center',
				alignItems: 'center',
			}}
		>
			<GoogleLogin
				onGoogleSignIn={onGoogleSignIn}
				text="로그인"
			/>
		</div>
	);
};

export default Login;
