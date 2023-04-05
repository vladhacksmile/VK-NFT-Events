package com.vladali.vk_nft_events.model;

import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Embeddable;

import java.io.Serializable;

@Embeddable
@Data
public class UsersEventsKey implements Serializable {
    @Column(name = "user_id")
    private Long userId;

    @Column(name = "event_id")
    private Long eventId;

}
