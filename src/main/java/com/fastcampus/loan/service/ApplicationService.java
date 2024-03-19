package com.fastcampus.loan.service;

import com.fastcampus.loan.dto.ApplicationDTO;

public interface ApplicationService {

    ApplicationDTO.Response create(ApplicationDTO.Request request);
    ApplicationDTO.Response get(Long applicationId);


}
