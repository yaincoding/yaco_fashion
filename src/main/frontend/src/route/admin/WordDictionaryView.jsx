import React, { useEffect, useState } from 'react';
import { Button, Input, Form, PageHeader, Space, Switch, Table } from 'antd';
import axios from 'axios';
import { formatTimeStr } from 'antd/lib/statistic/utils';

const { Search } = Input;

const WordDictionary = () => {
	const [query, setQuery] = useState(null);
	const [totalCount, setTotalCount] = useState(0);
	const [wordList, setWordList] = useState([]);
	const [page, setPage] = useState(1);

	const [form] = Form.useForm();

	const perPage = 10;

	const fetchWordList = (page) => {
		axios({
			method: 'get',
			url: '/api/es_word/list',
			params: { page: page - 1, size: perPage },
		})
			.then(function (response) {
				setWordList(response.data.list);
				setTotalCount(response.data.totalCount);
			})
			.catch(function (error) {
				console.error(error);
			});
	};

	useEffect(() => {
		fetchWordList(page);
	}, [query]);

	const searchWordList = (query, page) => {
		axios({
			method: 'get',
			url: '/api/es_word/search',
			params: {
				query: query,
				page: page - 1,
				size: perPage,
			},
		})
			.then(function (response) {
				setWordList(response.data.list);
				setTotalCount(response.data.totalCount);
			})
			.catch(function (error) {
				console.error(error);
			});
	};

	const save = ({ word, expression, active }) => {
		if (typeof word !== 'string' || word.length == 0) {
			alert('단어 입력은 필수입니다!');
			return;
		}

		axios({
			method: 'post',
			url: '/api/es_word/save',
			data: {
				word: word,
				expression: expression,
				active: active,
			},
		})
			.then((response) => {
				console.log(response);
				window.location.reload();
			})
			.catch((error) => {
				console.error(error);
			});
	};

	const update = ({ id, word, expression, active }) => {
		axios({
			method: 'put',
			url: `/api/es_word/update/${id}`,
			data: {
				word: word,
				expression: expression,
				active: active,
			},
		})
			.then((response) => {
				console.log(response);
				if (
					typeof query === 'string' &&
					query.length > 0
				) {
					searchWordList(query, page);
				} else {
					fetchWordList(page);
				}
			})
			.catch((error) => {
				console.error(error);
			});
	};

	const remove = (id) => {
		axios({
			method: 'delete',
			url: `/api/es_word/${id}`,
		})
			.then(() => {
				alert('삭제되었습니다.');
				if (
					typeof query === 'string' &&
					query.length > 0
				) {
					searchWordList(query, page);
				} else {
					fetchWordList(page);
				}
			})
			.catch((error) => {
				console.error(error);
				alert(error);
			});
	};

	const handleTableChange = (pagination) => {
		if (typeof query === 'string' && query.length > 0) {
			searchWordList(query, pagination.current);
		} else {
			fetchWordList(pagination.current);
		}
		setPage(pagination.current);
	};

	const formatDateTime = (dateTimeString) => {
		const formatDigit = (digit) => {
			if (digit < 10) {
				digit = `0${digit}`;
			}

			return digit;
		};

		const dateTime = new Date(dateTimeString);
		const year = formatDigit(dateTime.getFullYear());
		const month = formatDigit(dateTime.getMonth());
		const day = formatDigit(dateTime.getDay());
		const hours = formatDigit(dateTime.getHours());
		const minutes = formatDigit(dateTime.getMinutes());
		const seconds = formatDigit(dateTime.getSeconds());

		return `${year}-${month}-${day} ${hours}:${minutes}:${seconds}`;
	};

	const columns = [
		{
			title: 'ID',
			dataIndex: 'id',
			key: 'id',
		},
		{
			title: '단어',
			dataIndex: 'word',
			key: 'word',
			width: '10%',
		},
		{
			title: '표현',
			dataIndex: 'expression',
			key: 'expression',
		},
		{
			title: '생성',
			dataIndex: 'createdAt',
			key: 'createdAt',
			render: (createdAt) => formatDateTime(createdAt),
		},
		{
			title: '수정',
			dataIndex: 'updatedAt',
			key: 'updatedAt',
			render: (updatedAt) => formatDateTime(updatedAt),
		},
		{
			title: 'ON/OFF',
			dataIndex: 'active',
			key: 'active',
			render: (active) => <Switch defaultChecked={active} />,
			onCell: (record) => {
				return {
					onClick: (e) => {
						update({
							id: record.id,
							active: !record.active,
						});
					},
				};
			},
		},
		{
			title: '삭제',
			key: 'action',
			render: (_, record) => (
				<Space size="middle">
					<Button
						onClick={() => {
							remove(record.id);
						}}
					>
						삭제
					</Button>
				</Space>
			),
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
					title="단어 사전"
				/>
			</div>
			<div>
				<Form
					layout="inline"
					form={form}
					initialValues={{
						layout: 'inline',
					}}
					onFinish={(values) => {
						save({
							word: values.word,
							expression: values.expression,
							active: true,
						});
					}}
				>
					<Form.Item
						label={<b>단어</b>}
						name="word"
						rules={[{ required: true }]}
						style={{ marginRight: '20px' }}
					>
						<Input size="large" />
					</Form.Item>
					<Form.Item
						label={<b>표현식</b>}
						name="expression"
						rules={[{ required: false }]}
					>
						<Input size="large" />
					</Form.Item>

					<Form.Item>
						<Button
							type="primary"
							htmlType="submit"
							size="large"
						>
							<b>등록</b>
						</Button>
					</Form.Item>
				</Form>
			</div>
			<div style={searchBoxContainerStyle}>
				<Search
					enterButton="검색"
					size="large"
					onSearch={(value) => {
						searchWordList(value, page);
						setQuery(value);
					}}
				/>
			</div>
			<div className="container" style={tableContainerStyle}>
				<Table
					columns={columns}
					rowKey={(record) => record.id}
					pagination={{
						current: page,
						pageSize: perPage,
						total: totalCount,
					}}
					dataSource={wordList}
					onChange={handleTableChange}
				/>
			</div>
		</>
	);
};

const searchBoxContainerStyle = {
	width: '20%',
	maxWidth: '600px',
	marginTop: '50px',
	marginBottom: '50px',
};

const tableContainerStyle = {
	width: '90%',
	padding: '10px',
};

export default WordDictionary;
