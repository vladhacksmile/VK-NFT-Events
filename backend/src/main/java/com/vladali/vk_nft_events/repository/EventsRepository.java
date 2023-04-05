package com.vladali.vk_nft_events.repository;

import com.vladali.vk_nft_events.model.Events;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface EventsRepository extends JpaRepository<Events, Long> {
    List<Events> findAllByGroupId(Long id);
}
