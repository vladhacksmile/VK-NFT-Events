package com.vladali.vk_nft_events.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.springframework.web.multipart.MultipartFile;

@AllArgsConstructor
@Setter
@Getter
public class EventDTO {
    private String name;
    private String description;
    private String time;
    private String address;
    private String city;
    private String wallet_address;
    private MultipartFile file;
    private Long creatorId;
    private Long groupId;

    @Override
    public String toString() {
        return "EventDTO{" +
                "name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", time='" + time + '\'' +
                ", address='" + address + '\'' +
                ", city='" + city + '\'' +
                ", wallet_address='" + wallet_address + '\'' +
                ", file=" + file +
                ", creatorId=" + creatorId +
                ", groupId=" + groupId +
                '}';
    }
}
