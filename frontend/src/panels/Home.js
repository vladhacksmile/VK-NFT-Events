import React, { useState, useEffect } from 'react';
import PropTypes from 'prop-types';
import axios from 'axios';
import {
	Panel,
	PanelHeader,
	Header,
	Button,
	Group,
	Cell,
	Div,
	Avatar,
	Tabbar,
	TabbarItem,
	Badge,
	Counter,
	Banner,
	CardScroll,
	Card,
	Title,
	Gradient,
	Text,
	PopoutWrapper,
	Tappable,
	View,
	PanelHeaderBack,
	SegmentedControl,
	Search,
	FixedLayout,
	Headline,
	Paragraph,
	Footnote,
	Alert, Snackbar, ModalCard,
	ModalRoot,
	ModalPage,
	ModalPageHeader, CellButton,
	Footer, FormLayout, FormItem, Input, Link, Checkbox, Textarea, File, ScreenSpinner
} from '@vkontakte/vkui';
import {
	Icon28UserCircleOutline,
	Icon28NewsfeedOutline,
	Icon28MessageOutline,
	Icon16Done,
	Icon56MoneyTransferOutline, Icon24Document
} from '@vkontakte/icons';
import { Icon28UserOutline } from '@vkontakte/icons';
import { Icon28Users3Outline } from '@vkontakte/icons';
import qr from '@vkontakte/vk-qr';
import bridge from '@vkontakte/vk-bridge';

function gen_password(len) {
	var password = "";
	var symbols = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789!№;%:?*()_+=";
	for (var i = 0; i < len; i++){
		password += symbols.charAt(Math.floor(Math.random() * symbols.length));
	}
	return password;
}

const Home = ({ id, go, fetchedUser, changePopout, changeModal, setModalPages, fetchedToken, fetchedGroups}) => {
	const [text, setText] = useState('one');
	const [QRData, setQRData] = useState('no data');
	const [QRImage, setQRImage] = useState(null);
	const [eventsContent, setEventsContent] = useState('Актуальные');
	const [isNFTBuy, setIsNFTBuy] = useState(false);
	const [search, setSearch] = React.useState(null);
	const [name, setName] = React.useState(null);
	const [city, setCity] = React.useState(null);
	const [address, setAddress] = React.useState(null);
	const [eventDate, setEventDate] = React.useState(null);
	const [description, setDescription] = React.useState(null);
	const [groups, setGroups] = React.useState(null);
	const [snackbar, setSnackbar] = React.useState(null);
	const [groupId, setGroupId] = React.useState(null);
	const [actualEvents, setActualEvents] = React.useState(null);
	const [formFile, setFormFile] = React.useState(null);

	const [eventName, setEventName] = React.useState(null);
	const [eventId, setEventId] = React.useState(null);
	const [eventImage, setEventImage] = React.useState(null);
	const [eventCity, setEventCity] = React.useState(null);
	const [eventAddress, setEventAddress] = React.useState(null);
	const [eventEventDate, setEventEventDate] = React.useState(null);
	const [eventDescription, setEventDescription] = React.useState(null);

	function addEvent(event) {
		event.preventDefault()
		changePopout(<ScreenSpinner state="loading" />);
		let body = {
			creatorId: fetchedUser.id,
			groupId: groupId,
			file: formFile[0],
			wallet_address: "0xF456716695ACFb7B9b57d5Ecfb1eB65b61685464",
			name: name,
			city: city,
			address: address,
			time: eventDate,
			description: description
		};

		axios.post('https://f1-hack.ru:8080/api/admin/createContract', body, {
			headers: {
				"Access-Control-Allow-Origin": "*",
				"Content-type": "multipart/form-data"
			}
		}) .then(function (response) {
			getEvents(groupId).then(r => setText('events'))
			changePopout(<ScreenSpinner state="done" aria-label="Успешно" />);
			setTimeout(closePopout, 1000);
			console.log(response)
		})
			.catch(function (error) {
				changePopout(<ScreenSpinner state="error" aria-label="Произошла ошибка" />);
				setTimeout(closePopout, 1000);
				console.log(error);
			});
	}

	function buyTicket(event) {
		changePopout(<ScreenSpinner state="loading" />);
		let body = {
			user_id: fetchedUser.id,
			event_id: groupId,
			wallet_address: "0xF456716695ACFb7B9b57d5Ecfb1eB65b61685464",
		};

		axios.post('https://f1-hack.ru:8080/api/admin/createNft', body, {
			headers: {
				"Content-type": "multipart/form-data"
			}
		}) .then(function (response) {
			getEvents(groupId).then(r => setText('events'))
			changePopout(<ScreenSpinner state="done" aria-label="Успешно" />);
			setTimeout(closePopout, 1000);
			console.log(response)
		})
			.catch(function (error) {
				changePopout(<ScreenSpinner state="error" aria-label="Произошла ошибка" />);
				setTimeout(closePopout, 1000);
				console.log(error);
			});
	}

	const getEvents = async (grid) => {
		const events = await axios.get(`https://f1-hack.ru:8080/api/admin/group/${grid}`);
		if(events.data) {
			for (let i = 0; i < events.data.length; i++) {
				const jsonReader = await axios.get(events.data[i].dataUri);
				events.data[i].image = (jsonReader.data.image).replace("ipfs://", "https://ipfs.io/ipfs/")
			}
		}
		setActualEvents(events.data);
	}

	const openEvent = async (e) => {
		setEventName(e.name);
		setEventCity(e.city);
		setEventAddress(e.address);
		setEventEventDate(e.time);
		setEventDescription(e.description);
		setText('event')
		setActualEvents(events.data)
		setEventImage(e.image)
		setEventId(e.id)
	}

	const events = () => {
		setText('three');
	};
	const onChangeSearch = async (e) => {
		setSearch(e.target.value)
		const groupsFetched = await bridge.send('VKWebAppCallAPIMethod', {
			method: 'groups.search',
			params: {
				q: search,
				access_token: fetchedToken,
				v: '5.131'
			}
		});

		setGroups(groupsFetched.response.items)
	}

	const changeSelectedType = (value) => {
		if(value === "actual") {
			setEventsContent('Контент актуальных')
		} else {
			setEventsContent('Контент прошедших')
		}
	};

	const openNotifyNFTBuy = () => {
		if (snackbar) return;
		setSnackbar(
			<Snackbar
				onClose={() => setSnackbar(null)}
				action="Рассказать об этом друзьям"
				// onActionClick={() => setText('Добавляем метку.')}
				before={
					<Avatar size={24} style={{ background: 'var(--vkui--color_background_accent)' }}>
						<Icon16Done fill="#fff" width={14} height={14} />
					</Avatar>
				}
			>
				Поздравляем! Вы приобрели NFT-билет!
			</Snackbar>,
		);
	};

	const createNFTEvent = () => {
		setText('createEvent')
	}

	const closePopout = () => {
		changePopout(null);
	};

	const buyNFT = (eventId) => {
		changePopout(<ScreenSpinner state="loading" />);
		let body = {
			userId: fetchedUser.id,
			eventId: eventId,
			wallet_address: "0xF456716695ACFb7B9b57d5Ecfb1eB65b61685464",
		};

		axios.post('https://f1-hack.ru:8080/api/admin/createNft', body).then(function (response) {
			openNotifyNFTBuy();
			setIsNFTBuy(true);
			changePopout(<ScreenSpinner state="done" aria-label="Успешно" />);
			setTimeout(closePopout, 1000);
			console.log(response)
		})
			.catch(function (error) {
				changePopout(<ScreenSpinner state="error" aria-label="Произошла ошибка" />);
				setTimeout(closePopout, 1000);
				console.log(error);
			});
	};

	const delay = ms => new Promise(
		resolve => setTimeout(resolve, ms)
	);
	const openBuyNFT = (eventId) => {
		changePopout(
			<Alert
				actions={[
					{
						title: 'Отмена',
						autoClose: true,
						mode: 'cancel',
					},
					{
						title: 'Купить',
						autoClose: true,
						mode: 'destructive',
						action: () => buyNFT(eventId),
					},
				]}
				actionsLayout="horizontal"
				onClose={closePopout}
				header="Покупка NFT"
				text="Вы уверены, что хотите купить билет на это мероприятие?"
			/>,
		);
	};

	const validateToken = async (tmp) => {
		let body = {
			token: tmp,
		};

		const result = await axios.post('https://f1-hack.ru:8080/api/admin/validate', body);
		changeModal(null);
		setModalPages(<ModalPage
			id="qrScanner"
			onClose={() => changeModal(null)}
			header={<ModalPageHeader>Результат сканирования</ModalPageHeader>}
		>
			<Div style={styles} dangerouslySetInnerHTML={result.data}/>
		</ModalPage>);
		changeModal('qrScanner');
	}
	const openEvents = (e) => {
		getEvents(e.id).then(r => {
			setText('events')
			setGroupId(e.id)
		})
	}
	const readEventQR = () => {
		bridge.send('VKWebAppOpenCodeReader')
			.then((data) => {
				if (data.code_data) {
					// Результат сканирования получен
					validateToken(data.code_data);
				}
			})
			.catch((error) => {
				// Ошибка
				changeModal(null);
				setModalPages(<ModalPage
					id="qrScanner"
					onClose={() => changeModal(null)}
					header={<ModalPageHeader>Произошла ошибка</ModalPageHeader>}
				>
					<Div style={styles}> {error.error_data.error_reason} </Div>
				</ModalPage>);

				changeModal('qrScanner');
			});
	};

	const styles = {
		margin: 0,
		display: 'flex',
		flexDirection: 'column',
		alignItems: 'center',
		justifyContent: 'center',
		textAlign: 'center',
		padding: 32,
	};

	const makeEventQR = (token) => {
		let options = {};

		options.isShowLogo = true;
		options.isShowBackground = true;
		options.backgroundColor = "#bbebf0";
		return {__html: qr.createQR(token.data.token, 256, "qr-code-class", options)};
	}

	const showQRAlert = async (grid) => {
		changeModal(null);
		const token = await axios.get(`https://f1-hack.ru:8080/api/admin/generate/${grid}`);
		console.log(token.data.token)
		setModalPages(<ModalPage
			id="nftqr"
			onClose={() => changeModal(null)}
			header={<ModalPageHeader>Покажите QR код организаторам</ModalPageHeader>}
		>
			<Div style={styles} dangerouslySetInnerHTML={makeEventQR(token)}/>
			<Div style={styles}><Button onClick={() => showQRAlert(grid)}>Обновить</Button></Div>
		</ModalPage>);

		changeModal('nftqr');
	};

	return (
		<Div>
			<View activePanel={text}>
				<Panel id="one">
					<PanelHeader separator={false}>Главная</PanelHeader>
					<Group header={<Header mode="secondary">Мои ближайшие мероприятия</Header>}>
						<CardScroll size="l">
							<Card>
								<Banner
									before={
										<img src="https://sun9-63.userapi.com/yOEQYPHrNHjZEoanbqPb65HPl5iojmiLgLzfGA/W3geVMMt8TI.jpg" style={{ width: 96, height: 96 }} alt="Logo"/>
									}
									header="Баста в Ледовом"
									subheader="Большой концерт"
									actions={<Button onClick={() => setText('event')}>Подробнее</Button>}
								/>
							</Card>
							<Card>
								<Banner
									before={
										<img src="https://sun9-63.userapi.com/yOEQYPHrNHjZEoanbqPb65HPl5iojmiLgLzfGA/W3geVMMt8TI.jpg" style={{ width: 96, height: 96 }} alt="Logo"/>
									}
									header="Баста в Ледовом"
									subheader="Большой концерт"
									actions={<Button onClick={() => setText('event')}>Подробнее</Button>}
								/>
							</Card>
						</CardScroll>
					</Group>
					<Group header={<Header mode="secondary">Поиск мероприятий</Header>}>
						<Search value={search} onChange={onChangeSearch} after={null} />
						{groups && groups.map(group => <Tappable onClick={() => openEvents(group)}>
							<Banner
								before={
									<img src={group.photo_100} style={{ width: 96, height: 96 }} alt="Logo"/>
								}
								header={group.name}
								subheader={"Музыкальный исполнитель"}
								actions={<Button onClick={() => openEvents(group)}>Перейти к мероприятиям</Button>}
							/>
						</Tappable>)
						}
						{!groups && <Footer>Ничего не найдено</Footer>}
						{/*<Tappable>*/}
						{/*	<Banner*/}
						{/*		before={*/}
						{/*			<img src="https://sun9-63.userapi.com/yOEQYPHrNHjZEoanbqPb65HPl5iojmiLgLzfGA/W3geVMMt8TI.jpg" style={{ width: 96, height: 96 }} alt="Logo"/>*/}
						{/*		}*/}
						{/*		header={"Баста"}*/}
						{/*		subheader={"Музыкальный исполнитель"}*/}
						{/*		actions={<Button onClick={() => setText('events')}>Перейти к мероприятиям</Button>}*/}
						{/*	/>*/}
						{/*</Tappable>*/}
					</Group>
				</Panel>
				<Panel id="two">
					<PanelHeader>Профиль</PanelHeader>
					{fetchedUser &&
						<Group header={<Header mode="secondary">Мой профиль</Header>}>
							<Gradient mode="tint" to="top" style={styles}>
								{fetchedUser.photo_200 ? <Avatar src={fetchedUser.photo_200}/> : null}
								<Title style={{ marginBottom: 8, marginTop: 20 }} level="2" weight="2">
									{`${fetchedUser.first_name} ${fetchedUser.last_name}`}
								</Title>
								<Headline level="2" style={{ marginBottom: 8, marginTop: 20 }}>Всего посещено мероприятий: {groups ? groups.length : 0}</Headline>
								<Button>История покупок NFT-билетов</Button>
							</Gradient>
						</Group>
					}
					<Group header={<Header mode="secondary">Недавние посещенные мероприятия</Header>}>
						<Search/>
						<Tappable>
							<Banner
								before={
									<img src="https://sun9-63.userapi.com/yOEQYPHrNHjZEoanbqPb65HPl5iojmiLgLzfGA/W3geVMMt8TI.jpg" style={{ width: 96, height: 96 }} alt="Logo"/>
								}
								header={"Баста"}
								subheader={"Музыкальный исполнитель"}
								actions={<Button onClick={() => setText('events')}>Перейти к мероприятиям</Button>}
							/>
						</Tappable>
						<Tappable>
							<Banner
								before={
									<img src="https://sun9-53.userapi.com/impg/9PTgVd5NEPeKOAW20JJIhZ7UpOlL1Hpn15BbNQ/q1M6840Tvjc.jpg?size=1728x2160&quality=95&sign=ed478d952733b745411bbe74aee962d8&type=album" style={{ width: 96, height: 96 }} alt="Logo"/>
								}
								header={"Клава Кока"}
								subheader={"Музыкальный исполнитель"}
								actions={<Button onClick={() => setText('events')}>Перейти к мероприятиям</Button>}
							/>
						</Tappable>
					</Group>
				</Panel>
				<Panel id="three">
					<PanelHeader>Мероприятия</PanelHeader>
					<Group header={<Header mode="secondary">Мои ближайшие мероприятия</Header>}>
						<CardScroll size="l">
							<Card>
								<Banner
									before={
										<img src="https://sun9-63.userapi.com/yOEQYPHrNHjZEoanbqPb65HPl5iojmiLgLzfGA/W3geVMMt8TI.jpg" style={{ width: 96, height: 96 }} alt="Logo"/>
									}
									header="Баста в Ледовом"
									subheader="Большой концерт"
									actions={<Button onClick={() => setText('event')}>Подробнее</Button>}
								/>
							</Card>
							<Card>
								<Banner
									before={
										<img src="https://sun9-63.userapi.com/yOEQYPHrNHjZEoanbqPb65HPl5iojmiLgLzfGA/W3geVMMt8TI.jpg" style={{ width: 96, height: 96 }} alt="Logo"/>
									}
									header="Баста в Ледовом"
									subheader="Большой концерт"
									actions={<Button onClick={() => setText('event')}>Подробнее</Button>}
								/>
							</Card>
						</CardScroll>
					</Group>
					<Group header={<Header mode="secondary">Мои мероприятия</Header>}>
						<Search />
						<Tappable>
							<Banner
								before={
									<img src="https://sun9-63.userapi.com/yOEQYPHrNHjZEoanbqPb65HPl5iojmiLgLzfGA/W3geVMMt8TI.jpg" style={{ width: 96, height: 96 }} alt="Logo"/>
								}
								header={"Баста"}
								subheader={"Музыкальный исполнитель"}
								actions={<Button onClick={() => setText('events')}>Перейти к мероприятиям</Button>}
							/>
						</Tappable>
						<Tappable>
							<Banner
								before={
									<img src="https://sun9-53.userapi.com/impg/9PTgVd5NEPeKOAW20JJIhZ7UpOlL1Hpn15BbNQ/q1M6840Tvjc.jpg?size=1728x2160&quality=95&sign=ed478d952733b745411bbe74aee962d8&type=album" style={{ width: 96, height: 96 }} alt="Logo"/>
								}
								header={"Клава Кока"}
								subheader={"Музыкальный исполнитель"}
								actions={<Button onClick={() => setText('events')}>Перейти к мероприятиям</Button>}
							/>
						</Tappable>
					</Group>
				</Panel>
				<Panel id="events">
					<PanelHeader separator={false}
								 before={<PanelHeaderBack onClick={() => setText('one')} />}>Мероприятия</PanelHeader>
					<Group>
						<Div style={styles}>
							<Button onClick={createNFTEvent}>Создать NFT мероприятие</Button>
						</Div>
						<SegmentedControl
							size="m"
							name="type"
							options={[
								{
									label: 'Актуальные',
									value: 'actual',
								},
								{
									label: 'Прошедшие',
									value: 'past',
								},
							]}
							onChange={(value) => changeSelectedType(value)}
						/>
						{actualEvents && actualEvents.map(event => <Tappable onClick={() => setText('event')}>
							<Banner
								before={
									<img src={event.image} style={{ width: 96, height: 96 }} alt="Logo"/>
								}
								header={event.name}
								subheader={event.description}
								actions={<Button onClick={() => openEvent(event)}>Подробнее</Button>}
							/>
						</Tappable>)
						}
					</Group>
				</Panel>
				<Panel id="createEvent">
					<PanelHeader separator={false}
								 before={<PanelHeaderBack onClick={() => setText('events')} />}>Создание NFT-мероприятия</PanelHeader>
					<Group>
						<FormLayout onSubmit={(event) => addEvent(event)}>
							<FormItem
								top="Название"
								status={name ? 'valid' : 'error'}
								bottom={
									name ? 'Хорошее название для этого мероприятия!' : 'Пожалуйста, введите название мероприятия'
								}
							>
								<Input type="text" name="name" value={name} onChange={(event) => setName(event.target.value)} />
							</FormItem>
							<FormItem top="Дата проведения и время">
								<Input onChange={(event) => setEventDate(event.target.value)}/>
							</FormItem>
							<FormItem top="Город">
								<Input onChange={(event) => setCity(event.target.value)}/>
							</FormItem>
							<FormItem top="Адрес">
								<Input onChange={(event) => setAddress(event.target.value)}/>
							</FormItem>
							<FormItem top="Описание мероприятия">
								<Textarea onChange={(event) => setDescription(event.target.value)}/>
							</FormItem>
							<FormItem top="Загрузите картинку мероприятия">
								<File name="file" before={<Icon24Document role="presentation" />} size="l" mode="secondary" onChange={(event) => setFormFile(event.target.files)}/>
							</FormItem>
							<Checkbox>
								Я прочитал и согласен со всем <Link>правилами</Link>
							</Checkbox>
							<FormItem>
								{/*alert(`{"name":"${name}", "description":"${description}"}`)*/}
								{/*<Button size="l" onClick={() => addEvent()} stretched>*/}
							</FormItem>
							<Button type="submit" size="l" stretched>
								Создать NFT-мероприятие
							</Button>
						</FormLayout>
					</Group>
				</Panel>
				<Panel id="event">
					<PanelHeader separator={false}
								 before={<PanelHeaderBack onClick={() => setText('events')} />}>{eventName}</PanelHeader>
					<Group>
						<Gradient mode="tint" to="top">
							<Div style={styles}>
								<Avatar size={96} src={eventImage}/>
								{/*<Title style={{ marginBottom: 8, marginTop: 20 }} level="2" weight="2">*/}
								{/*	Алексей Мазелюк*/}
								{/*</Title>*/}
								{/*<Text*/}
								{/*	style={{*/}
								{/*		marginBottom: 24,*/}
								{/*		color: 'var(--vkui--color_text_secondary)',*/}
								{/*	}}*/}
								{/*>*/}
								{/*	Учащийся*/}
								{/*</Text>*/}
								{/*<Button size="m">*/}
								{/*	Редактировать*/}
								{/*</Button>*/}
							</Div>
							<Div style={{ padding: 20 }}>
								<Title level="3">Дата и время проведения: {eventEventDate}</Title>
								<Title level="3">Город: {eventCity}</Title>
								<Footnote>Адрес: {eventAddress}</Footnote>
							</Div>
							<Div style={{ padding: 20 }}>
								<Paragraph>
									Описание мероприятия: {eventDescription}
								</Paragraph>
							</Div>
							{isNFTBuy ? <Div style={styles}><Button onClick={() => showQRAlert(eventId)}>Показать QR</Button></Div> :  <Div style={styles}><Button onClick={() => openBuyNFT(eventId)}>Приобрести NFT</Button></Div> }
							<Button onClick={readEventQR}>Открыть QR-сканнер</Button>
						</Gradient>
					</Group>
					{snackbar}
				</Panel>
			</View>
			<FixedLayout filled vertical="bottom">
				<Tabbar>
					<TabbarItem selected={text === 'one'} onClick={() => setText('one')} text="Главная">
						<Icon28UserOutline />
					</TabbarItem>
					<TabbarItem selected={text === 'two'} onClick={() => setText('two')} text="Профиль">
						<Icon28UserCircleOutline />
					</TabbarItem>
					<TabbarItem selected={text === 'three'} onClick={() => events()} text="Мероприятия">
						<Icon28Users3Outline />
					</TabbarItem>
				</Tabbar>
			</FixedLayout>
		</Div>
	);
};

Home.propTypes = {
	id: PropTypes.string.isRequired,
	go: PropTypes.func.isRequired,
	changePopout: PropTypes.func.isRequired,
	setModalPages: PropTypes.func.isRequired,
	changeModal: PropTypes.func.isRequired,
	fetchedUser: PropTypes.shape({
		photo_200: PropTypes.string,
		first_name: PropTypes.string,
		last_name: PropTypes.string,
		city: PropTypes.shape({
			title: PropTypes.string,
		}),
	}),
};

export default Home;