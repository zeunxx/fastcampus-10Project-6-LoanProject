package com.fastcampus.loan.service;


import com.fastcampus.loan.domain.AcceptTerms;
import com.fastcampus.loan.domain.Application;
import com.fastcampus.loan.domain.Terms;
import com.fastcampus.loan.dto.ApplicationDTO;
import com.fastcampus.loan.exception.BaseException;
import com.fastcampus.loan.exception.ResultType;
import com.fastcampus.loan.repository.AcceptTermsRepository;
import com.fastcampus.loan.repository.ApplicationRepository;
import com.fastcampus.loan.repository.TermsRepository;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import static java.time.LocalDateTime.now;

@Service
@RequiredArgsConstructor
public class ApplicationServiceImpl implements ApplicationService {

    private final ApplicationRepository applicationRepository;
    private final TermsRepository termsRepository;
    private final AcceptTermsRepository acceptTermsRepository;

    private final ModelMapper modelMapper;

    @Override
    public ApplicationDTO.Response create(ApplicationDTO.Request request) {

        Application applicaiton = modelMapper.map(request, Application.class);
        applicaiton.setAppliedAt(now());
        Application applied = applicationRepository.save(applicaiton);

        return modelMapper.map(applied, ApplicationDTO.Response.class);
    }

    @Override
    public ApplicationDTO.Response get(Long applicationId) {
        Application application = applicationRepository.findById(applicationId).orElseThrow(() -> new BaseException(ResultType.SYSTEM_ERROR));

        return modelMapper.map(application, ApplicationDTO.Response.class);
    }

    @Override
    public ApplicationDTO.Response update(Long applicationId, ApplicationDTO.Request request) {
        Application application = applicationRepository.findById(applicationId).orElseThrow(() -> new BaseException(ResultType.SYSTEM_ERROR));

        application.setName(request.getName());
        application.setCellPhone(request.getCellPhone());
        application.setEmail(request.getEmail());
        application.setHopeAmount(request.getHopeAmount());

        applicationRepository.save(application);

        return modelMapper.map(application, ApplicationDTO.Response.class);
    }

    @Override
    public void delete(Long applicationId) {
        Application application = applicationRepository.findById(applicationId).orElseThrow(() -> new BaseException(ResultType.SYSTEM_ERROR));

        application.setIsDeleted(true);
        applicationRepository.save(application);
    }

    @Override
    public Boolean acceptTerms(Long applicationId, ApplicationDTO.AcceptTermsDTO request) {
        /**
         * validation
         */

        // 대출 신청 정보가 존재하는지 확인
        applicationRepository.findById(applicationId).orElseThrow(()-> new BaseException(ResultType.SYSTEM_ERROR));

        List<Terms> termsList = termsRepository.findAll(Sort.by(Sort.Direction.ASC,"termsId"));
        // 약관이 1개 이상 존재하는지 확인
        if(termsList.isEmpty()){
            throw new BaseException(ResultType.SYSTEM_ERROR);
        }

        List<Long> acceptTermsIds = request.getAcceptTermsIds();
        // 동의한 약관 수 != 저장된 약관 수
        if(termsList.size()!=acceptTermsIds.size()){
            throw new BaseException(ResultType.SYSTEM_ERROR);
        }

        List<Long> termsIds = termsList.stream().map(Terms::getTermsId).toList();
        Collections.sort(acceptTermsIds); // 정렬

        // 고객이 동의한 약관이 우리가 가지고 있는 약관인지 확인
        if (!termsIds.containsAll(acceptTermsIds)){
            throw new BaseException(ResultType.SYSTEM_ERROR);
        }

        /**
         * 약관 동의 도메인 생성
         */
        for(Long termsId:acceptTermsIds){
            AcceptTerms accepted = AcceptTerms.builder()
                    .termsId(termsId) // 해당 신청에 동의한 약관
                    .applicationId(applicationId) // 신청정보
                    .build();


            acceptTermsRepository.save(accepted);
        }

        return true;

    }
}
