package com.fastcampus.loan.service;

import com.fastcampus.loan.domain.Application;
import com.fastcampus.loan.domain.Entry;
import com.fastcampus.loan.dto.BalanceDTO;
import com.fastcampus.loan.dto.EntryDTO;
import com.fastcampus.loan.exception.BaseException;
import com.fastcampus.loan.exception.ResultType;
import com.fastcampus.loan.repository.ApplicationRepository;
import com.fastcampus.loan.repository.EntryRepository;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class EntryServiceImpl implements EntryService{

    private final BalanceService balanceService;
    private final EntryRepository entryRepository;
    private final ApplicationRepository applicationRepository;
    private final ModelMapper modelMapper;

    @Override
    public EntryDTO.Response create(Long applicationId, EntryDTO.Request request) {
        // 계약 체결 된 신청 정보 있는지
        if(!isContractedApplication(applicationId)){
            throw new BaseException(ResultType.SYSTEM_ERROR);
        }

        // request -> entity -> save
        Entry entry = modelMapper.map(request, Entry.class);
        entry.setApplicationId(applicationId);
        entryRepository.save(entry);


        // 대출 잔고 관리
            balanceService.create(applicationId,
                    BalanceDTO.Request.builder()
                            .entryAmount(request.getEntryAmount())
                            .build());

        return modelMapper.map(entry, EntryDTO.Response.class);
    }

    @Override
    public EntryDTO.Response get(Long applicationId) {
        Optional<Entry> entry = entryRepository.findByApplicationId(applicationId);

        if(entry.isPresent()){
            return modelMapper.map(entry, EntryDTO.Response.class);
        }else{
            return null;
        }
    }



    @Override
    public EntryDTO.UpdateResponse update(Long entryId, EntryDTO.Request request) {
        // entry 존재유무 확인
        Entry entry = entryRepository.findById(entryId).orElseThrow(() -> {
            throw new BaseException(ResultType.SYSTEM_ERROR);
        });

        // before -> after
        BigDecimal beforeEntryAmount = entry.getEntryAmount();
        entry.setEntryAmount(request.getEntryAmount());

        entryRepository.save(entry);

        //balance update
        Long applicationId = entry.getApplicationId();
        balanceService.update(applicationId,BalanceDTO.UpdateRequest.builder()
                        .beforeEntryAmount(beforeEntryAmount)
                        .afterEntryAmount(request.getEntryAmount())
                        .build());
        // response
        return EntryDTO.UpdateResponse.builder()
                .entryId(entryId)
                .applicationId(applicationId)
                .beforeEntryAmount(beforeEntryAmount)
                .afterEntryAmount(request.getEntryAmount())
                .build();
    }

    @Override
    public void delete(Long entryId) {
        // entry 존재유무 확인
        Entry entry = entryRepository.findById(entryId).orElseThrow(() -> {
            throw new BaseException(ResultType.SYSTEM_ERROR);
        });

        // 삭제 처리
        entry.setIsDeleted(true);
        entryRepository.save(entry);

        // balance 잔고 0원으로 set
        BigDecimal beforeEntryAmount = entry.getEntryAmount();

        Long applicationId = entry.getApplicationId();
        balanceService.update(applicationId, BalanceDTO.UpdateRequest.builder()
                        .beforeEntryAmount(beforeEntryAmount)
                        .afterEntryAmount(BigDecimal.ZERO)
                        .build());
    }

    private boolean isContractedApplication(Long applicationId){
        Optional<Application> application = applicationRepository.findById(applicationId);

        if (application.isEmpty()){
            return false;
        }

        return application.get().getContractedAt()!=null;
    }
}
