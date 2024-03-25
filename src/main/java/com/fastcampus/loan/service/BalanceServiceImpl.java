package com.fastcampus.loan.service;

import com.fastcampus.loan.domain.Balance;
import com.fastcampus.loan.dto.BalanceDTO;
import com.fastcampus.loan.exception.BaseException;
import com.fastcampus.loan.exception.ResultType;
import com.fastcampus.loan.repository.BalanceRepository;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Service
@RequiredArgsConstructor
public class BalanceServiceImpl implements BalanceService{

    private final BalanceRepository balanceRepository;
    private final ModelMapper modelMapper;
    @Override
    public BalanceDTO.Response create(Long applicationId, BalanceDTO.Request request) {
        //이미 존재하는 잔고 테이블 있는지 확인


        Balance balance = modelMapper.map(request, Balance.class);
        BigDecimal entryAmount = request.getEntryAmount();

        balance.setApplicationId(applicationId);
        balance.setBalance(entryAmount);

        // 이미 존재하는 balance의 경우 덮어쓰기(id, 삭제여부, 생성/수정일시 제외)
        balanceRepository.findByApplicationId(applicationId).ifPresent(b ->{
            balance.setBalanceId(b.getBalanceId());
            balance.setIsDeleted(b.getIsDeleted());
            balance.setCreatedAt(b.getCreatedAt());
            balance.setUpdatedAt(b.getUpdatedAt());
        });

        Balance saved = balanceRepository.save(balance);
        return modelMapper.map(saved, BalanceDTO.Response.class);

    }

    @Override
    public BalanceDTO.Response update(Long applicationId, BalanceDTO.UpdateRequest request) {
        // balance 존재유무
        Balance balance = balanceRepository.findByApplicationId(applicationId).orElseThrow(() -> {
            throw new BaseException(ResultType.SYSTEM_ERROR);
        });

        BigDecimal beforeEntryAmount = request.getBeforeEntryAmount();
        BigDecimal afterEntryAmount = request.getAfterEntryAmount();
        BigDecimal updatedBalance = balance.getBalance();

        //as-is -> to-be: balance -잘못 집행한 금액 +새로 집행한 금액
         updatedBalance = updatedBalance.subtract(beforeEntryAmount).add(afterEntryAmount);
         balance.setBalance(updatedBalance);

         balanceRepository.save(balance);

        return modelMapper.map(balance, BalanceDTO.Response.class);
    }
}
