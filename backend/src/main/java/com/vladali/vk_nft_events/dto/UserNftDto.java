package com.vladali.vk_nft_events.dto;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Data
@Getter
@Setter
public class UserNftDto {
    private Long userId;
    private String wallet_address;
    private Long eventId;

    @Override
    public String toString() {
        return "UserNftDto{" +
                "wallet_address='" + wallet_address + '\'' +
                ", eventId=" + eventId +
                '}';
    }
}
