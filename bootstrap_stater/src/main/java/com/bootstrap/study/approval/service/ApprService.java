package com.bootstrap.study.approval.service;

import com.bootstrap.study.approval.repository.ApprRepository;
import org.springframework.stereotype.Service;

@Service
public class ApprService {
    private final ApprRepository apprRepository;

    public ApprService(ApprRepository apprRepository) {
        this.apprRepository = apprRepository;
    }
}
