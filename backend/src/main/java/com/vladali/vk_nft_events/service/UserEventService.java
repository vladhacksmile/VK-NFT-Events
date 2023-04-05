package com.vladali.vk_nft_events.service;

import com.vladali.vk_nft_events.dto.UserNftDto;
import com.vladali.vk_nft_events.model.UsersEvents;
import com.vladali.vk_nft_events.model.repository.EventsRepository;
import com.vladali.vk_nft_events.model.repository.UserEventsRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class UserEventService {
    @Autowired
    UserEventsRepository userEventsRepository;
    @Autowired
    EventsRepository eventsRepository;

    public List<UsersEvents> getEventsForUser(Long id) {
        return userEventsRepository.getUsersEventsByUserId(id);
    }

    public List<UsersEvents> getEventsFromGroupAndUser(Long userId, Long groupId) {
        return userEventsRepository.getUsersEventsByUserIdAndEventGroupId(userId, groupId);
    }

    public UsersEvents addUserEvent(UserNftDto userNftDto, Long tokenId){
        UsersEvents usersEvents = new UsersEvents();
        usersEvents.setUserId(userNftDto.getUserId());
        usersEvents.setEvent(eventsRepository.findById(userNftDto.getEventId()).get()); // TODO
        usersEvents.setWalletAddress(userNftDto.getWallet_address());
        usersEvents.setTokenId(tokenId);
        userEventsRepository.save(usersEvents);
        return usersEvents;
    }
}