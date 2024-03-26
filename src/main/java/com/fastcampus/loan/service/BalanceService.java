package com.fastcampus.loan.service;

import com.fastcampus.loan.dto.BalanceDTO;
import com.fastcampus.loan.dto.RepaymentDTO;

public interface BalanceService {

    BalanceDTO.Response create(Long applicationId, BalanceDTO.Request request);
    BalanceDTO.Response update(Long applicationId, BalanceDTO.UpdateRequest request);

    BalanceDTO.Response repaymentUpdate(Long applicationId, BalanceDTO.RepaymentRequest request);
}
