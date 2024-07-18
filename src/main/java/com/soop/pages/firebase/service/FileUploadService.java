package com.soop.pages.firebase.service;

import org.springframework.stereotype.Service;
import com.google.cloud.storage.Blob;
import com.google.cloud.storage.BlobId;
import com.google.cloud.storage.BlobInfo;
import com.google.cloud.storage.Storage;
import com.google.firebase.cloud.StorageClient;
import org.springframework.web.multipart.MultipartFile;
import java.io.IOException;
import java.util.UUID;

@Service
public class FileUploadService {

    public String uploadFile(MultipartFile file) throws IOException {
        String fileName = UUID.randomUUID().toString() + "_" + file.getOriginalFilename();
        BlobId blobId = BlobId.of("soop-profile-pic.appspot.com", "profile_pictures/" + fileName);
        BlobInfo blobInfo = BlobInfo.newBuilder(blobId)
                .setContentType(file.getContentType())
                .build();

        Storage storage = StorageClient.getInstance().bucket().getStorage();
        Blob blob = storage.create(blobInfo, file.getInputStream());

        return blob.getMediaLink();
    }
}
