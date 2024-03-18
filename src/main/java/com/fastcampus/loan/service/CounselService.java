package com.fastcampus.loan.service;


import com.fastcampus.loan.dto.CounselDTO.Request;
import com.fastcampus.loan.dto.CounselDTO.Response;

public interface CounselService {

    // 상담 등록 기능
    Response create(Request request);

    Response get(Long counselId);
}
