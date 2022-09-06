import React from 'react';

import { List, Image, Space, Divider } from 'antd';
import { LikeOutlined, MessageOutlined, StarOutlined } from '@ant-design/icons';

const { Item } = List;

const IconText = ({ icon, name, text }) => (
	<Space>
		{React.createElement(icon)}
		{<p style={{ margin: 0, fontSize: '10px' }}>{name}</p>}
		{<p style={{ margin: 0, fontSize: '10px' }}>{text}</p>}
	</Space>
);

const GoodsCard = ({ goods }) => {
	return (
		<Item key={goods.id}>
			<div style={itemContainerStyle}>
				<div>
					<Image
						src={goods.imageUrl}
						width="180px"
						height="180px"
						preview={false}
					/>
				</div>
				<div style={goodsInfoContainerStyle}>
					<b style={titleStyle}>{goods.title}</b>
					<p style={metaDataStyle}>
						{`${goods.price} 원`}
					</p>
					<div
						style={{
							display: 'inline-block',
							position: 'absolute',
							bottom: 0,
						}}
					>
						<IconText
							icon={StarOutlined}
							name="클릭"
							text={goods.clickCount}
						/>
						<Divider type="vertical" />
						<IconText
							icon={LikeOutlined}
							name="찜"
							text={goods.likeCount}
						/>
						<Divider type="vertical" />
						<IconText
							icon={MessageOutlined}
							name="판매량"
							text={goods.sellCount}
						/>
					</div>
				</div>
			</div>
		</Item>
	);
};

const itemContainerStyle = {
	display: 'flex',
	flexDirection: 'row',
};

const goodsInfoContainerStyle = {
	display: 'flex',
	flexDirection: 'column',
	margin: '0 0 0 30px',
	maxWidth: '500px',
	position: 'relative',
};

const titleStyle = {
	fontFamily: 'notosans_bold',
	fontSize: '14px',
	margin: '0 0 10px 0',
	minWidth: '400px',
};

const metaDataStyle = {
	fontFamily: 'notosans_medium',
	fontSize: '12px',
	color: '#606060',
	margin: '0 0 10px 0',
};

export default GoodsCard;
