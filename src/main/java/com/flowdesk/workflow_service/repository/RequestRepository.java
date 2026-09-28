package com.flowdesk.workflow_service.repository;

import com.flowdesk.workflow_service.entity.Request;
import com.flowdesk.workflow_service.entity.RequestStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RequestRepository extends JpaRepository<Request,Long> {

    List<Request> findByRequesterId(Long requesterId);

    List<Request> findByStatus(RequestStatus status);

    List<Request> findByRequesterDepartmentIdAndStatus(Long departmentId, RequestStatus status);
}