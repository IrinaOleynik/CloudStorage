package com.diplom.cloudstorage.entites;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Data
@Entity
@Table(name = "files")
public class File {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "filename", nullable = false)
    private String filename;

    @Column(name = "content_type", nullable = false)
    private String contentType;

    @Column(name = "size", nullable = false)
    private long size;

    @Lob
    @Column(name = "data", nullable = false)
    private byte[] data;

    @Column(name = "owner", nullable = false)
    private String owner;

    public File(String contentType, byte[] data, String filename, String owner, long size) {
        this.filename = filename;
        this.contentType = contentType;
        this.size = size;
        this.data = data;
        this.owner = owner;
    }
}
