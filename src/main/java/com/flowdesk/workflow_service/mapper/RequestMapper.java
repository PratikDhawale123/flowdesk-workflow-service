package com.flowdesk.workflow_service.mapper;

import com.flowdesk.workflow_service.dto.CreateRequestDto;
import com.flowdesk.workflow_service.entity.Request;
import com.flowdesk.workflow_service.entity.RequestStatus;
import com.flowdesk.workflow_service.entity.User;

import java.time.LocalDateTime;

public class RequestMapper{
    public static Request toEntity(CreateRequestDto dto, User requester){
        Request req = new Request();
        req.setTitle(dto.getTitle());
        req.setDescription(dto.getDescription());
        req.setAmount(dto.getAmount());
        req.setQuotationUrl(dto.getQuotationUrl());
        req.setRequester(requester);

        req.setStatus(RequestStatus.PENDING);
        req.setCreatedAt(LocalDateTime.now());

        return req;
    }
}
