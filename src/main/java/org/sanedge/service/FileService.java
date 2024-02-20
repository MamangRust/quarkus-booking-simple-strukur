package org.sanedge.service;

import org.jboss.resteasy.plugins.providers.multipart.InputPart;

public interface FileService {
    String createFileImage(InputPart file, String filepath);

    void deleteFileImage(String filepath);
}
