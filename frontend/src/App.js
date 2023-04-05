import React, { useState, useEffect } from 'react';
import bridge from '@vkontakte/vk-bridge';
import {
	View, ScreenSpinner, AdaptivityProvider, AppRoot, ConfigProvider, SplitLayout, SplitCol,
	Panel,
	PanelHeader,
	Header,
	Group,
	SimpleCell, ModalRoot, ModalPage, ModalPageHeader, Button
} from '@vkontakte/vkui';
import '@vkontakte/vkui/dist/vkui.css';

import Home from './panels/Home';
import Persik from './panels/Persik';

const App = () => {
	const [scheme, setScheme] = useState('bright_light')
	const [activePanel, setActivePanel] = useState('home');
	const [fetchedUser, setUser] = useState(null);
	const [fetchedToken, setToken] = useState(null);
	const [fetchedGroups, setGroups] = useState(null);
	const [popout, setPopout] = useState(<ScreenSpinner size='large' />);
	const [modal, setModal] = useState(null);
	const [modalPages, setModalPages] = useState(null);

	useEffect(() => {
		bridge.subscribe(({ detail: { type, data }}) => {
			if (type === 'VKWebAppUpdateConfig') {
				setScheme(data.scheme)
			}
		});

		async function fetchData() {
			const user = await bridge.send('VKWebAppGetUserInfo');
			const token = await bridge.send('VKWebAppGetAuthToken', {
				app_id: 51558227,
				scope: 'friends,status,groups'
			});
			const groups = await bridge.send('VKWebAppCallAPIMethod', {
				method: 'groups.search',
				params: {
					q: "Концерт",
					access_token: token.access_token,
					v: '5.131'
				}});
			setToken(token.access_token);
			setGroups(groups.response.items);
			setUser(user);
			setPopout(null);
			console.log(token)
			console.log(groups)
		}
		fetchData();
	}, []);

	const modals = (
		<ModalRoot activeModal={modal}>
			{modalPages}
		</ModalRoot>
	);
	const go = e => {
		setActivePanel(e.currentTarget.dataset.to);
	};

	const changePopout = e => {
		setPopout(e)
	};

	const changeModal = e => {
		setModal(e);
	};

	return (
		<ConfigProvider scheme={scheme}>
			<AdaptivityProvider>
				<AppRoot>
					<SplitLayout popout={popout} modal={modals}>
						<SplitCol>
							<View activePanel={activePanel}>
								<Home id='home' fetchedUser={fetchedUser} go={go} changePopout={changePopout} changeModal={changeModal} setModalPages={setModalPages} fetchedToken={fetchedToken} fetchedGroups={fetchedGroups}/>
								<Persik id='persik' go={go} />
							</View>
						</SplitCol>
					</SplitLayout>
				</AppRoot>
			</AdaptivityProvider>
		</ConfigProvider>
	);
}

export default App;
