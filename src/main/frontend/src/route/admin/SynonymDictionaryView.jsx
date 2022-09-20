import React, { useEffect, useState } from 'react';
import {
	Button,
	Input,
	Form,
	PageHeader,
	Space,
	Switch,
	Table,
	Checkbox,
} from 'antd';
import { CloudSyncOutlined } from '@ant-design/icons';
import axios from 'axios';

const { Search } = Input;

const SynonymDictionaryView = () => {
	const [query, setQuery] = useState(null);
	const [totalCount, setTotalCount] = useState(0);
	const [synonymList, setSynonymList] = useState([]);
	const [page, setPage] = useState(1);
	const [loading, setLoading] = useState(false);

	const [form] = Form.useForm();

	const perPage = 10;

	const fetchSynonymList = (page) => {
		axios({
			method: 'get',
			url: '/api/es_synonym/list',
			params: { page: page - 1, size: perPage },
		})
			.then(function (response) {
				setSynonymList(response.data.list);
				setTotalCount(response.data.totalCount);
			})
			.catch(function (error) {
				console.error(error);
			});
	};

	useEffect(() => {
		if (typeof query === 'string' && query.length > 0) {
			searchSynonymList(query, page);
		} else {
			fetchSynonymList(page);
		}
	}, [query, page]);

	const searchSynonymList = (query, page) => {
		axios({
			method: 'get',
			url: '/api/es_synonym/search',
			params: {
				query: query,
				page: page - 1,
				size: perPage,
			},
		})
			.then(function (response) {
				setSynonymList(response.data.list);
				setTotalCount(response.data.totalCount);
			})
			.catch(function (error) {
				console.error(error);
			});
	};

	const save = ({ word, synonym, active, bidirect }) => {
		if (typeof word !== 'string' || word.length === 0) {
			alert('단어 입력은 필수입니다!');
			return;
		}

		if (typeof synonym !== 'string' || synonym.length === 0) {
			alert('동의어 입력은 필수입니다!');
			return;
		}

		axios({
			method: 'post',
			url: '/api/es_synonym/save',
			data: {
				word: word,
				synonym: synonym,
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

		if (bidirect) {
			axios({
				method: 'post',
				url: '/api/es_synonym/save',
				data: {
					word: synonym,
					synonym: word,
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
		}
	};

	const update = ({ id, word, synonym, active }) => {
		axios({
			method: 'put',
			url: `/api/es_synonym/update/${id}`,
			data: {
				word: word,
				synonym: synonym,
				active: active,
			},
		})
			.then((response) => {
				console.log(response);
				if (
					typeof query === 'string' &&
					query.length > 0
				) {
					searchSynonymList(query, page);
				} else {
					fetchSynonymList(page);
				}
			})
			.catch((error) => {
				console.error(error);
			});
	};

	const remove = (id) => {
		axios({
			method: 'delete',
			url: `/api/es_synonym/${id}`,
		})
			.then(() => {
				alert('삭제되었습니다.');
				if (
					typeof query === 'string' &&
					query.length > 0
				) {
					searchSynonymList(query, page);
				} else {
					fetchSynonymList(page);
				}
			})
			.catch((error) => {
				console.error(error);
				alert(error);
			});
	};

	const apply = () => {
		setLoading(true);
		axios({
			method: 'get',
			url: '/api/es_synonym/apply',
		})
			.then((response) => {
				setLoading(false);
				alert('색인에 적용되었습니다.');
			})
			.catch((error) => {
				setLoading(false);
				alert('색인에 적용 실패!');
				console.log(error);
			});
	};

	const handleTableChange = (pagination) => {
		if (typeof query === 'string' && query.length > 0) {
			searchSynonymList(query, pagination.current);
		} else {
			fetchSynonymList(pagination.current);
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
			title: '동의어',
			dataIndex: 'synonym',
			key: 'synonym',
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
					title="동의어 사전"
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
							synonym: values.synonym,
							active: true,
							bidirect: values.bidirect,
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
						label={<b>동의어</b>}
						name="synonym"
						rules={[{ required: false }]}
					>
						<Input size="large" />
					</Form.Item>

					<Form.Item
						name="bidirect"
						valuePropName="checked"
						wrapperCol={{
							offset: 3,
							span: 24,
						}}
					>
						<Checkbox>양방향</Checkbox>
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
						searchSynonymList(value, page);
						setQuery(value);
					}}
				/>
			</div>
			<div
				style={{
					display: 'flex',
					justifyContent: 'flex-end',
					width: '90%',
					padding: '10px',
				}}
			>
				<Button
					type="primary"
					shape="round"
					icon={
						<CloudSyncOutlined
							style={{
								fontSize: '20px',
							}}
						/>
					}
					size="large"
					onClick={() => {
						apply();
					}}
					loading={loading}
				>
					색인에 적용
				</Button>
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
					dataSource={synonymList}
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

export default SynonymDictionaryView;
