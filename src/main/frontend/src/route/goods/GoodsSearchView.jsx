import React, { useState } from 'react';
import { useSearchParams, useLocation } from 'react-router-dom';

import { Layout, Input, Pagination, AutoComplete } from 'antd';
import axios from 'axios';
import GoodsList from '../../component/goods/GoodsList';
import { useEffect } from 'react';

const GoodsSearchView = () => {
	const [goodsList, setGoodsList] = useState([]);
	const [count, setCount] = useState(0);
	const [suggests, setSuggests] = useState([]);

	let [searchParams, setSearchParams] = useSearchParams();

	const query = searchParams.get('query');
	const page = searchParams.get('page') || 1;

	console.log(query, page);

	const fetchGoods = async () => {
		await axios({
			method: 'get',
			url: '/api/goods/search',
			params: { query: query, page: page },
		})
			.then((response) => {
				setCount(response.data.count);
				setGoodsList(response.data.docs);
			})
			.catch((error) => {
				console.error(error);
			});
	};

	const goSearchPage = (query, page) => {
		setSearchParams({
			query: query,
			page: page,
		});
	};

	const onTyping = async (query) => {
		await axios({
			method: 'get',
			url: 'api/auto_complete',
			params: { query: query, size: 5 },
		})
			.then((response) => {
				setSuggests(response.data.keywords);
			})
			.catch((error) => {
				console.error(error);
			});
	};

	const renderSuggests = () => {
		return (
			suggests &&
			suggests.map((suggest) => ({
				value: suggest,
				label: (
					<div
						style={{
							display: 'flex',
							justifyContent:
								'space-between',
						}}
					>
						<span>{suggest}</span>
					</div>
				),
			}))
		);
	};

	useEffect(() => {
		if (typeof query === 'string' && query.length > 0) {
			fetchGoods();
		}
	}, [query, page, suggests]);

	return (
		<Layout className="container" style={layoutStyle}>
			<AutoComplete
				style={{
					width: '50%',
					maxWidth: '600px',
					marginTop: '50px',
					marginBottom: '50px',
				}}
				options={renderSuggests()}
				onSelect={(value) => {
					goSearchPage(value, 1);
				}}
				defaultActiveFirstOption={false}
				onSearch={onTyping}
			>
				<Input.Search
					placeholder="검색어를 입력하세요"
					enterButton
					defaultValue={query}
					size="large"
					onSearch={(value, event) => {
						event.preventDefault();
						goSearchPage(value, page);
					}}
					style={{
						width: '100%',
					}}
				/>
			</AutoComplete>
			<GoodsList goodsList={goodsList} />
			<Pagination
				defaultCurrent={page}
				total={count}
				onChange={() => {
					goSearchPage(query, page);
				}}
			/>
		</Layout>
	);
};

const layoutStyle = {
	display: 'flex',
	flexDirection: 'column',
	width: '70%',
	minWidth: '300px',
	maxWidth: '1024px',
	height: '100%',
	minHeight: '600px',
	overflow: 'auto',
	background: '#fff',
	fontSize: '15px',
	alignItems: 'center',
};

export default GoodsSearchView;
