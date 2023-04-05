package com.vladali.vk_nft_events.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.util.Set;

@NoArgsConstructor
@Getter
@Setter
@Entity
@Table(name = "events")
public class Events {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    private String description;
    private String time;
    private String city;
    private String address;
    private Long creatorId;
    private Long groupId;

    private String walletAddress;

    @Column(name = "contract")
    private String smartContracts;

    private String dataUri;

    private Long tokenId;
//    @OneToMany(targetEntity = UsersEvents.class, mappedBy = "events", cascade = CascadeType.ALL, fetch = FetchType.EAGER)
//    @JsonIgnore
//    private Set<UsersEvents> usersEvents;

    public Events(String name, String description, String time, String city, String address, Long creatorId, Long groupId, String walletAddress, String smartContracts, String dataUri, Long tokenId) {
        this.name = name;
        this.description = description;
        this.time = time;
        this.city = city;
        this.address = address;
        this.creatorId = creatorId;
        this.groupId = groupId;
        this.walletAddress = walletAddress;
        this.smartContracts = smartContracts;
        this.dataUri = dataUri;
        this.tokenId = tokenId;
    }

    @Override
    public String toString() {
        return "Events{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", time='" + time + '\'' +
                ", city='" + city + '\'' +
                ", address='" + address + '\'' +
                ", creatorId=" + creatorId +
                ", groupId=" + groupId +
                ", walletAddress='" + walletAddress + '\'' +
                ", smartContracts='" + smartContracts + '\'' +
                ", dataUri='" + dataUri + '\'' +
                ", tokenId=" + tokenId +
                '}';
    }
}
