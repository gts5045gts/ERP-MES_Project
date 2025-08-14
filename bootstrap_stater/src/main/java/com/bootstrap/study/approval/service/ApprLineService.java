package com.bootstrap.study.approval.service;

import com.bootstrap.study.approval.repository.ApprRepository;
import org.springframework.stereotype.Service;

@Service
public class ApprLineService {

    private ApprRepository apprRepository;
    public ApprLineService(ApprRepository apprRepository) {
        this.apprRepository = apprRepository;
    }
}
