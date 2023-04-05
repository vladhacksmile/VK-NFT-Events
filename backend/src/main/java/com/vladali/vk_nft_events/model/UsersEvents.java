package com.vladali.vk_nft_events.model;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;

@Getter
@Setter
@Entity
public class UsersEvents {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    private Long userId;

    private Long eventId;

    private String walletAddress;

    private Long tokenId;


}
