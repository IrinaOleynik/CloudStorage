package com.diplom.cloudstorage.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class FileResponse {
    private String filename;
    private Long size;

    public FileResponse(String filename, long size) {
        this.filename = filename;
        this.size = size;
    }
}
