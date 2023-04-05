package com.vladali.vk_nft_events.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.springframework.web.multipart.MultipartFile;

@AllArgsConstructor
@Setter
@Getter
public class EventResponse {
    private Long id;
    private Long creatorId;
    private Long groupId;
    private String name;
    private String city;
    private String address;
    private String eventDate;
    private String description;
    private String wallet_address;
    private String json_url;

    @Override
    public String toString() {
        return "EventResponse{" +
                "id=" + id +
                ", creatorId=" + creatorId +
                ", groupId=" + groupId +
                ", name='" + name + '\'' +
                ", city='" + city + '\'' +
                ", address='" + address + '\'' +
                ", eventDate='" + eventDate + '\'' +
                ", description='" + description + '\'' +
                ", wallet_address='" + wallet_address + '\'' +
                ", json_url='" + json_url + '\'' +
                '}';
    }
}