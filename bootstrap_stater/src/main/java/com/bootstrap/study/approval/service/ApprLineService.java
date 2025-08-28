package com.bootstrap.study.approval.service;

import com.bootstrap.study.approval.entity.Appr;
import com.bootstrap.study.approval.entity.ApprLine;
import com.bootstrap.study.approval.repository.ApprLineRepository;
import com.bootstrap.study.approval.repository.ApprRepository;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ApprLineService {

	@Autowired
	private ApprLineRepository apprLineRepository;
    
    public ApprLineService(ApprLineRepository apprLineRepository) {
        this.apprLineRepository = apprLineRepository;
    }

	public void registApprLine(Appr appr, String[] empIds) throws IOException{
		
		List<ApprLine> apprLines = new ArrayList<ApprLine>();
		
		int index = 1;
		
		for(String empId : empIds) {
			ApprLine line = new ApprLine();
			line.setApprId(empId);
			line.setStepNo(index);
			line.setAppr(appr);
			
			apprLines.add(line);
			
			index++;
		}
		
		apprLineRepository.saveAll(apprLines);
	}
}
