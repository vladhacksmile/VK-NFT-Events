package com.vladali.vk_nft_events.repository;

import com.vladali.vk_nft_events.model.UsersEvents;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface UserEventsRepository extends JpaRepository<UsersEvents, Long> {

    List<UsersEvents> getUsersEventsByUserId(Long id);
    List<UsersEvents> getUsersEventsByUserIdAndEventGroupId(Long userId, Long groupId);
}
