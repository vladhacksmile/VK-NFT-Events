package com.vladali.vk_nft_events.model.repository;

import com.vladali.vk_nft_events.model.UsersEvents;
import com.vladali.vk_nft_events.model.UsersEventsKey;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface UserEventsRepository extends JpaRepository<UsersEvents, UsersEventsKey> {

    List<UsersEvents> getUsersEventsByUserId(Long id);
}
