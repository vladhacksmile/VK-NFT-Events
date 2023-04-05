package com.vladali.vk_nft_events.service;

import com.vladali.vk_nft_events.dto.EventDTO;
import com.vladali.vk_nft_events.model.Events;
import com.vladali.vk_nft_events.model.Nfts;
import com.vladali.vk_nft_events.repository.EventsRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class EventsService {
    @Autowired
    EventsRepository eventsRepository;


    public void saveEvent(Events event){
        eventsRepository.save(event);
    }

    public Events getEventById(Long id) {
        Optional<Events> optionalEvents = eventsRepository.findById(id);
        if(optionalEvents.isPresent()) {
            return optionalEvents.get();
        } else {
            return null;
        }
    }
    public Events add(EventDTO eventRequest, Nfts nft) {
        Events events = new Events(
                eventRequest.getName(),
                eventRequest.getDescription(),
                eventRequest.getTime(),
                eventRequest.getCity(),
                eventRequest.getAddress(),
                eventRequest.getCreatorId(),
                eventRequest.getGroupId(),
                eventRequest.getWallet_address(),
                nft.getSmartContracts(),
                nft.getDataUri(),
                nft.getTokenId()
        );
        eventsRepository.save(events);
        return events;
    }

    public List<Events> getGroupEvents(Long id) {
        System.out.println(id);
        List<Events> list =  eventsRepository.findAllByGroupId(id);
        for (Events e : list ){
            String uri = e.getDataUri().replaceAll("ipfs://","https://ipfs.io/ipfs/");
            e.setDataUri(uri);
        }
        return list;
    }

}
