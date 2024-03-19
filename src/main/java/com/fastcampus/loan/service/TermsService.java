package com.fastcampus.loan.service;

import com.fastcampus.loan.dto.TermsDTO;

public interface TermsService {

    TermsDTO.Response create(TermsDTO.Request request);
}
