package com.vladali.vk_nft_events.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;

@Getter
@Setter
@Entity
@Table(name = "usersEvents")
public class UsersEvents {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    private Long userId;

//    private Long eventId;

    private String walletAddress;

    private Long tokenId;

    @ManyToOne(fetch = FetchType.EAGER)
    private Events event;
}
