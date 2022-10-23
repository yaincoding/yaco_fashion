import React, { useEffect, useState } from 'react';
import { Input, PageHeader, Table, Select } from 'antd';
import { useSearchParams } from 'react-router-dom';

import axios from 'axios';

const { Search } = Input;
const { Option } = Select;

const INDEX_ANALYZER = 'index_analyzer';
const SEARCH_ANALYZER = 'search_analyzer';

const WordDictionary = () => {
	const [searchParams, setSearchParams] = useSearchParams();
	const [tokens, setTokens] = useState(null);
	const [analyzer, setAnalyzer] = useState(INDEX_ANALYZER);

	const analyze = (query, analyzer) => {
		axios({
			method: 'get',
			url: '/api/analyze',
			params: {
				query: query,
				analyzer: analyzer,
			},
		})
			.then(function (response) {
				console.log(response.data);
				setTokens(response.data.tokens);
			})
			.catch(function (error) {
				console.error(error);
			});
	};

	useEffect(() => {
		const query = searchParams.get('query');
		const analyzer = searchParams.get('analyzer');
		if (typeof query === 'string' && query.length > 0) {
			analyze(query, analyzer);
		}
	}, [searchParams]);

	const columns = [
		{
			title: 'token',
			dataIndex: 'token',
		},
		{
			title: 'type',
			dataIndex: 'type',
		},
		{
			title: 'position',
			dataIndex: 'position',
		},
		{
			title: 'start_offset',
			dataIndex: 'start_offset',
		},
		{
			title: 'end_offset',
			dataIndex: 'end_offset',
		},
	];

	return (
		<>
			<div
				className="container"
				style={{ width: '100%', padding: '10px' }}
			>
				<PageHeader
					className="site-page-header"
					title="형태소 분석"
				/>
			</div>
			<div style={searchBoxContainerStyle}>
				<Search
					enterButton="검색"
					size="large"
					onSearch={(value) => {
						setSearchParams({
							query: value,
							analyzer: analyzer,
						});
					}}
				/>
				<Select
					defaultValue={analyzer}
					size="large"
					onChange={(value) => {
						setAnalyzer(value);
					}}
				>
					<Option value={INDEX_ANALYZER}>
						색인분석기
					</Option>
					<Option value={SEARCH_ANALYZER}>
						검색분석기
					</Option>
				</Select>
			</div>
			<div className="container" style={tableContainerStyle}>
				<Table columns={columns} dataSource={tokens} />
			</div>
		</>
	);
};

const searchBoxContainerStyle = {
	display: 'flex',
	flexDirection: 'row',
	width: '40%',
	maxWidth: '600px',
	marginTop: '50px',
	marginBottom: '50px',
};

const tableContainerStyle = {
	width: '90%',
	padding: '10px',
};

export default WordDictionary;
