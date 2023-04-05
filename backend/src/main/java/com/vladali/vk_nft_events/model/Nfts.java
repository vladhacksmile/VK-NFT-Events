package com.vladali.vk_nft_events.model;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.math.BigInteger;

@Getter
@Setter
@Entity
public class Nfts {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    private String smartContracts;

    @Column(name = "user_id")
    private Long userId;
    private Long groupId;

    @Column(name = "data_uri")
    private String dataUri;

    Long tokenId;

}
