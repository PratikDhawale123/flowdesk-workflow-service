package com.flowdesk.workflow_service.service;

import com.flowdesk.workflow_service.dto.CreateRequestDto;
import com.flowdesk.workflow_service.entity.AuditLog;
import com.flowdesk.workflow_service.entity.Request;
import com.flowdesk.workflow_service.entity.RequestStatus;
import com.flowdesk.workflow_service.entity.User;
import com.flowdesk.workflow_service.mapper.RequestMapper;
import com.flowdesk.workflow_service.repository.AuditLogRepository;
import com.flowdesk.workflow_service.repository.RequestRepository;
import com.flowdesk.workflow_service.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class RequestService {

    private final RequestRepository reqRepo;
    private final UserRepository userRepo;
    private final AuditLogRepository auditRepo;

    public RequestService(RequestRepository reqRepo, UserRepository userRepo, AuditLogRepository auditRepo) {
        this.reqRepo = reqRepo;
        this.userRepo = userRepo;
        this.auditRepo = auditRepo;
    }

    @Transactional
    public Request createRequest(CreateRequestDto dto, Long requesterId) {
        User requester = userRepo.findById(requesterId)
                .orElseThrow(() -> new RuntimeException("User not found with ID: " + requesterId));

        Request request = RequestMapper.toEntity(dto, requester);

        Request savedRequest = reqRepo.save(request);
        AuditLog log = new AuditLog(savedRequest, requester, "REQUEST_CREATED", "Initial draft created");

        auditRepo.save(log);
        return savedRequest;
    }

    @Transactional
    public Request submitRequest(Long requestId, Long userId) {
        Request request = reqRepo.findById(requestId)
                .orElseThrow(() -> new RuntimeException("Request not found with ID: " + requestId));

        User actor = userRepo.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found with ID: " + userId));

        if (request.getStatus() != RequestStatus.DRAFT && request.getStatus() != RequestStatus.NEED_INFO) {
            throw new IllegalStateException("Only DRAFT or NEED_INFO requests can be submitted. Current status: " + request.getStatus());
        }

        if (!request.getRequester().getId().equals(userId)) {
            throw new IllegalArgumentException("You can only submit your own requests.");
        }

        request.setStatus(RequestStatus.PENDING);
        Request updatedRequest = reqRepo.save(request);

        AuditLog log = new AuditLog(updatedRequest, actor, "REQUEST_SUBMITTED", "Submitted for review");
        auditRepo.save(log);

        return updatedRequest;
    }

    @Transactional
    public Request approveRequest(Long requestId, Long approverId, String remark) {
        Request request = reqRepo.findById(requestId)
                .orElseThrow(() -> new RuntimeException("Request not found with ID: " + requestId));

        User approver = userRepo.findById(approverId)
                .orElseThrow(() -> new RuntimeException("User not found with ID: " + approverId));

        if (request.getStatus() != RequestStatus.PENDING) {
            throw new IllegalStateException("Only PENDING requests can be approved. Current status: " + request.getStatus());
        }

        if (request.getRequester().getId().equals(approverId)) {
            throw new IllegalArgumentException("Maker-Checker violation: You cannot approve your own request.");
        }

        request.setStatus(RequestStatus.APPROVED);
        Request updatedRequest = reqRepo.save(request);

        AuditLog log = new AuditLog(
                updatedRequest,
                approver,
                "REQUEST_APPROVED",
                (remark != null && !remark.isBlank()) ? remark : "Approved"
        );

        auditRepo.save(log);

        return updatedRequest;
    }

    @Transactional
    public Request rejectRequest(Long requestId, Long approverId, String remark) {
        Request request = reqRepo.findById(requestId)
                .orElseThrow(() -> new RuntimeException("Request not found with ID: " + requestId));

        User approver = userRepo.findById(approverId)
                .orElseThrow(() -> new RuntimeException("User not found with ID: " + approverId));

        if (request.getStatus() != RequestStatus.PENDING) {
            throw new IllegalStateException("Only PENDING requests can be rejected. Current status: " + request.getStatus());
        }

        if (request.getRequester().getId().equals(approverId)) {
            throw new IllegalArgumentException("Maker-Checker violation: You cannot reject your own request.");
        }

        request.setStatus(RequestStatus.REJECTED);
        Request updatedRequest = reqRepo.save(request);

        AuditLog log = new AuditLog(
                updatedRequest,
                approver,
                "REQUEST_REJECTED",
                (remark != null && !remark.isBlank()) ? remark : "Rejected"
        );

        auditRepo.save(log);

        return updatedRequest;
    }

    @Transactional
    public Request requestMoreInfo(Long requestId, Long reviewerId, String remark) {
        Request request = reqRepo.findById(requestId)
                .orElseThrow(() -> new RuntimeException("Request not found with ID: " + requestId));

        User reviewer = userRepo.findById(reviewerId)
                .orElseThrow(() -> new RuntimeException("User not found with ID: " + reviewerId));

        if (request.getStatus() != RequestStatus.PENDING) {
            throw new IllegalStateException("Only PENDING requests can be put into NEED_INFO. Current status: " + request.getStatus());
        }

        if (request.getRequester().getId().equals(reviewerId)) {
            throw new IllegalArgumentException("Maker-Checker violation: You cannot review your own request.");
        }

        if (remark == null || remark.isBlank()) {
            throw new IllegalArgumentException("Remarks are required when requesting more information.");
        }

        request.setStatus(RequestStatus.NEED_INFO);
        Request updatedRequest = reqRepo.save(request);

        AuditLog log = new AuditLog(updatedRequest, reviewer, "REQUEST_NEED_INFO", remark);
        auditRepo.save(log);

        return updatedRequest;
    }
}