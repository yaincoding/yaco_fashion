import { Layout, Button } from 'antd';
import googleBtnImage from './images/btn_google_signin_dark_normal_web.png';

import axios from 'axios';

const iconImage = <img src={googleBtnImage} alt="구글 로그인" />;

const LoginView = () => {
	return (
		<Layout
			style={{
				width: '100wh',
				height: '100vh',
				display: 'flex',
				justifyContent: 'center',
				alignItems: 'center',
			}}
		>
			<Button
				onClick={() => {
					axios({
						method: 'get',
						url: '/oauth2/authorization/google',
					})
						.then((response) => {
							console.log(
								response.data
							);
						})
						.erorr((error) => {
							console.error(error);
						});
				}}
				icon={iconImage}
			></Button>
		</Layout>
	);
};

export default LoginView;
