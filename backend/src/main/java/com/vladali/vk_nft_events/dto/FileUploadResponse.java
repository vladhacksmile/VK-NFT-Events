package com.vladali.vk_nft_events.dto;

import lombok.Data;
import lombok.Setter;

@Data

public class FileUploadResponse {
    String fileName;
    long size;
    String filecode;
}
