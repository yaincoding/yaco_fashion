import React from 'react';

import { List } from 'antd';
import Layout from 'antd/lib/layout/layout';

import { Link } from 'react-router-dom';
import styled from 'styled-components';
import GoodsCard from './GoodsCard';

const GoodsContainer = styled.div`
	&:hover {
		background-color: #0000ff10;
	}
`;

const GoodsList = ({ goodsList }) => {
	return (
		<List
			size="large"
			dataSource={goodsList}
			renderItem={(goods) => (
				<GoodsContainer>
					<Link
						key={goods.sno}
						to={`/goods/${goods.sno}`}
					>
						<GoodsCard goods={goods} />
					</Link>
				</GoodsContainer>
			)}
		/>
	);
};

export default GoodsList;
