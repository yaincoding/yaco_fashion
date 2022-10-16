import React, { useReducer } from 'react';

export const UserContext = React.createContext();

const initialState = JSON.parse(window.sessionStorage.getItem('user')) || {
	name: null,
	picture: null,
};

const reducer = (state, action) => {
	switch (action.type) {
		case 'LOGIN':
			return action.value;
		case 'LOGOUT':
			return initialState;
		default:
			throw new Error('일치하는 명령이 존재하지 않습니다.');
	}
};

const UserContextProvider = ({ children }) => {
	const [user, contextDispatch] = useReducer(reducer, initialState);

	return (
		<UserContext.Provider
			value={{
				user,
				contextDispatch,
			}}
		>
			{children}
		</UserContext.Provider>
	);
};

export default UserContextProvider;
