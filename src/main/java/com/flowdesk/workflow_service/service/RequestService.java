package com.flowdesk.workflow_service.service;


import com.flowdesk.workflow_service.dto.CreateRequestDto;
import com.flowdesk.workflow_service.entity.Request;
import com.flowdesk.workflow_service.entity.User;
import com.flowdesk.workflow_service.mapper.RequestMapper;
import com.flowdesk.workflow_service.repository.RequestRepository;
import com.flowdesk.workflow_service.repository.UserRepository;
import org.springframework.stereotype.Service;

@Service
public class RequestService {

    private final RequestRepository reqRepo;
    private final UserRepository userRepo;

    public RequestService(RequestRepository reqRepo , UserRepository userRepo){
        this.reqRepo = reqRepo;
        this.userRepo = userRepo;
    }

    public Request createRequest(CreateRequestDto dto , Long requesterId){
       User requester = userRepo.findById(requesterId)
               .orElseThrow(()-> new RuntimeException("User not found with ID : "+ requesterId));

       Request request = RequestMapper.toEntity(dto,requester);

       return reqRepo.save(request);
    }
}
