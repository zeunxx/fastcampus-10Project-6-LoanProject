package com.fastcampus.loan.service;

import com.fastcampus.loan.domain.Counsel;
import com.fastcampus.loan.dto.CounselDTO;
import com.fastcampus.loan.repository.CounselRepository;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

import static com.fastcampus.loan.dto.CounselDTO.*;

@Service
@RequiredArgsConstructor
public class CounselServiceImpl implements CounselService {

    private final ModelMapper modelMapper;
    private final CounselRepository counselRepository;

    @Override
    public Response create(Request request) {
        Counsel counsel = modelMapper.map(request, Counsel.class); // request dto -> counsel entity로 맵핑
        counsel.setAppliedAt(LocalDateTime.now()); // 신청일자 등록

        Counsel createdCounsel = counselRepository.save(counsel);// db에 저장

        return  modelMapper.map(createdCounsel, Response.class);
    }
}
