package com.vladali.vk_nft_events.model;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;

import java.time.LocalDateTime;

@Getter
@Setter
@Entity
@Table(name = "smart_contracts")
public class SmartContracts {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "event_id", referencedColumnName = "id")
    private Events events;

    @Column(name = "creation_time", columnDefinition = "TIMESTAMP")
    private LocalDateTime creationTime;
    private String contract;





}
