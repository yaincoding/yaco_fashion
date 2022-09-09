import React, { useState } from 'react';

import { Layout, Input, Pagination } from 'antd';
import axios from 'axios';
import GoodsList from '../../component/goods/GoodsList';

const { Search } = Input;

const GoodsSearchView = () => {
	const [goodsList, setGoodsList] = useState([]);
	const [count, setCount] = useState(0);
	const [query, setQuery] = useState(null);

	const searchGoods = async (query, page) => {
		console.log(`query=${query}`);
		setQuery(query);
		await axios({
			method: 'get',
			url: '/api/goods/search',
			params: { query: query, page: page },
		})
			.then((res) => {
				console.log('검색결과 log', res.data);
				setCount(res.data.count);
				setGoodsList(res.data.docs);
			})
			.catch((err) => {
				console.error(err);
			});
	};

	const onSearch = (query) => {
		setQuery(query);
		searchGoods(query, 1);
	};

	const onPageChange = (page) => {
		searchGoods(query, page);
	};

	return (
		<Layout className="container" style={layoutStyle}>
			<Search
				placeholder="검색어를 입력하세요"
				enterButton="검색"
				allowClear
				size="large"
				onSearch={onSearch}
				style={{
					width: '50%',
					maxWidth: '600px',
					marginTop: '50px',
					marginBottom: '50px',
				}}
			/>
			<GoodsList goodsList={goodsList} />
			<Pagination
				defaultCurrent={1}
				total={count}
				onChange={onPageChange}
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
