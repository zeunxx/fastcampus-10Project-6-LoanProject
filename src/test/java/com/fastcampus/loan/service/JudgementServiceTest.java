package com.fastcampus.loan.service;

import com.fastcampus.loan.domain.Application;
import com.fastcampus.loan.domain.Judgement;
import com.fastcampus.loan.dto.ApplicationDTO;
import com.fastcampus.loan.dto.JudgementDTO;
import com.fastcampus.loan.repository.ApplicationRepository;
import com.fastcampus.loan.repository.JudgementRepository;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;

import java.math.BigDecimal;
import java.util.Optional;

import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class JudgementServiceTest {

    @InjectMocks
    private JudgementServiceImpl judgementService;

    @Mock
    private JudgementRepository judgementRepository;

    @Mock
    private ApplicationRepository applicationRepository;

    @Spy
    private ModelMapper modelMapper;

    @Test
    void 심사요청시_새로운_심사객체반환(){

        Judgement judgement = Judgement.builder()
                .applicationId(1L)
                .name("member kim")
                .approvalAmount(BigDecimal.valueOf(5000000))
                .build();

        JudgementDTO.Request request = JudgementDTO.Request.builder()
                .applicationId(1L)
                .name("member kim")
                .approvalAmount(BigDecimal.valueOf(5000000))
                .build();

        when(applicationRepository.findById(1L)).thenReturn(Optional.ofNullable(Application.builder().build()));
        when(judgementRepository.save(ArgumentMatchers.any(Judgement.class))).thenReturn(judgement);

        JudgementDTO.Response actual = judgementService.create(request);

        Assertions.assertThat(actual.getName()).isSameAs(judgement.getName());
        Assertions.assertThat(actual.getApplicationId()).isSameAs(judgement.getApplicationId());
        Assertions.assertThat(actual.getApprovalAmount()).isSameAs(judgement.getApprovalAmount());

    }

    @Test
    void 심사객체id가_존재하는_해당객체반환(){

        Long findId = 1L;
        Judgement judgement = Judgement.builder()
                .judgementId(1L)
                .build();
        when(judgementRepository.findById(findId)).thenReturn(Optional.ofNullable(judgement));

        JudgementDTO.Response actual = judgementService.get(findId);

        Assertions.assertThat(actual.getJudgementId()).isSameAs(findId);
    }

    @Test
    void 존재하는_대출신청id로요청시_심사객체반환(){

        Long findId = 1L;

        Judgement judgement = Judgement.builder()
                .judgementId(1L)
                .build();

        Application application = Application.builder()
                .applicationId(1L)
                .build();



        when(applicationRepository.findById(findId)).thenReturn(Optional.ofNullable(application));
        when(judgementRepository.findByApplicationId(findId)).thenReturn(Optional.ofNullable(judgement));

        JudgementDTO.Response actual = judgementService.getJudgementOfApplicationId(findId);

        Assertions.assertThat(actual.getJudgementId()).isSameAs(findId);
    }

    @Test
    void 존재하는심사객체_수정요청시_수정된객체반환(){

        Long findId = 1L;

        Judgement judgement = Judgement.builder()
                .judgementId(1L)
                .name("member kim")
                .approvalAmount(BigDecimal.valueOf(1000000))
                .build();

        JudgementDTO.Request request = JudgementDTO.Request.builder()
                .name("member lee")
                .approvalAmount(BigDecimal.valueOf(5000000))
                .build();

        when(judgementRepository.findById(findId)).thenReturn(Optional.ofNullable(judgement));
        when(judgementRepository.save(ArgumentMatchers.any(Judgement.class))).thenReturn(judgement);

        JudgementDTO.Response actual = judgementService.update(findId, request);

        Assertions.assertThat(actual.getJudgementId()).isSameAs(findId);
        Assertions.assertThat(actual.getName()).isSameAs(request.getName());
        Assertions.assertThat(actual.getApprovalAmount()).isSameAs(request.getApprovalAmount());
    }

    @Test
    void 심사객체삭제요청시_객체삭제(){

        Long findId = 1L;

        Judgement judgement = Judgement.builder()
                .judgementId(findId)
                .build();

        when(judgementRepository.findById(findId)).thenReturn(Optional.ofNullable(judgement));
        when(judgementRepository.save(ArgumentMatchers.any(Judgement.class))).thenReturn(judgement);

        judgementService.delete(findId);
        Assertions.assertThat(judgement.getIsDeleted()).isTrue();
    }

    @Test
    void 승인금액부여_확정요청시_변경된신청객체_반환(){

        Judgement judgement = Judgement.builder()
                .name("member kim")
                .applicationId(1L)
                .approvalAmount(BigDecimal.valueOf(5000000))
                .build();

        Application application = Application.builder()
                .applicationId(1L)
                .approvalAmount(BigDecimal.valueOf(5000000))
                .build();

        when(judgementRepository.findById(1L)).thenReturn(Optional.ofNullable(judgement));
        when(applicationRepository.findById(1L)).thenReturn(Optional.ofNullable(application));
        when(applicationRepository.save(ArgumentMatchers.any(Application.class))).thenReturn(application);

        ApplicationDTO.GrantAmount actual = judgementService.grant(1L);

        Assertions.assertThat(actual.getApplicationId()).isSameAs(1L);
        Assertions.assertThat(actual.getApprovalAmount()).isSameAs(judgement.getApprovalAmount());
    }

}
