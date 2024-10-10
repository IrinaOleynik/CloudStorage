package com.diplom.cloudstorage.repositories;

import com.diplom.cloudstorage.entites.File;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface FileRepository extends JpaRepository<File, Long> {
    @Query(value = "select f from File f where f.owner = :owner")
    Optional<List<File>> findAllByOwner(@Param("owner") String owner);

    void removeByFilenameAndOwner(String filename, String owner);

    File findByFilenameAndOwner(String filename, String owner);

    @Modifying
    @Query("update File f set f.filename = :newName where f.filename = :filename and f.owner = :owner")
    void renameFile(@Param("filename") String filename, @Param("newName") String newFilename, @Param("owner") String owner);

}
