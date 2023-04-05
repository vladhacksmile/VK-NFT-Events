package com.vladali.vk_nft_events.service;

import com.vladali.vk_nft_events.model.SmartContracts;
import com.vladali.vk_nft_events.model.repository.SmartContractsRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class SmartContractService {

    @Autowired
    SmartContractsRepository contractsRepository;

    public SmartContracts getContractByContract(String contract) {
        return contractsRepository.findByContract(contract);
    }
    public SmartContracts getContractByEventId(Long id ){
        return contractsRepository.findByEventsId(id);
    }
}
