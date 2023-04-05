package com.vladali.vk_nft_events.model.repository;

import com.vladali.vk_nft_events.model.SmartContracts;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface SmartContractsRepository extends JpaRepository<SmartContracts, String> {
    SmartContracts findByEventsId(Long id);
    SmartContracts findByContract(String contract);
}
