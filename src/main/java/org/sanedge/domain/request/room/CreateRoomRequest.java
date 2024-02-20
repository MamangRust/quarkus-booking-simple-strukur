package org.sanedge.domain.request.room;

import org.jboss.resteasy.plugins.providers.multipart.MultipartFormDataInput;

import lombok.Data;

@Data
public class CreateRoomRequest {
    private String roomName;
    private Integer roomCapacity;
    private MultipartFormDataInput file;
}
