package com.fastcampus.loan.service;

import com.fastcampus.loan.domain.AcceptTerms;
import com.fastcampus.loan.domain.Application;
import com.fastcampus.loan.domain.BaseEntity;
import com.fastcampus.loan.domain.Terms;
import com.fastcampus.loan.dto.ApplicationDTO;
import com.fastcampus.loan.exception.BaseException;
import com.fastcampus.loan.repository.AcceptTermsRepository;
import com.fastcampus.loan.repository.ApplicationRepository;
import com.fastcampus.loan.repository.TermsRepository;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentMatchers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Sort;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class ApplicationServiceTest {

    @InjectMocks
    ApplicationServiceImpl applicationService;

    @Mock
    private ApplicationRepository applicationRepository;

    @Mock
    private TermsRepository termsRepository;

    @Mock
    AcceptTermsRepository acceptTermsRepository;

    @Spy
    private ModelMapper modelMapper;

    @Test
    void 새로운_대출신청이_오면_신청된_엔티티_반환(){
        Application entity = Application.builder()
                .name("Member kim")
                .cellPhone("010-1111-2222")
                .email("mail@abd.cd")
                .hopeAmount(BigDecimal.valueOf(5000000))
                .build();

        ApplicationDTO.Request request = ApplicationDTO.Request.builder()
                        .name("Member kim")
                        .cellPhone("010-1111-2222")
                        .email("mail@abd.cd")
                        .hopeAmount(BigDecimal.valueOf(5000000))
                        .build();

        when(applicationRepository.save(ArgumentMatchers.any(Application.class))).thenReturn(entity);
        ApplicationDTO.Response actual = applicationService.create(request);

        Assertions.assertThat(actual.getHopeAmount()).isSameAs(entity.getHopeAmount());
        Assertions.assertThat(actual.getName()).isSameAs(entity.getName());
    }

    @Test
    void 존재하는_대출신청id_요청시_존재하는_엔티티_반환(){
        Long findId = 1L;
        Application entity = Application.builder()
                .applicationId(1L)
                .build();

        when(applicationRepository.findById(findId)).thenReturn(Optional.of(entity));
        ApplicationDTO.Response actual = applicationService.get(findId);

        Assertions.assertThat(actual.getApplicationId()).isSameAs(entity.getApplicationId());
    }

    @Test
    void 존재하는_엔티티에_대한_업데이트요청시_해당엔티티_업데이트된_응답리턴(){

        Long findId = 1L;

        Application entity = Application.builder()
                .applicationId(1L)
                .hopeAmount(BigDecimal.valueOf(50000000))
                .build();

        ApplicationDTO.Request request = ApplicationDTO.Request.builder()
                .hopeAmount(BigDecimal.valueOf(5000000))
                .build();

        when(applicationRepository.save(ArgumentMatchers.any(Application.class))).thenReturn(entity);
        when(applicationRepository.findById(findId)).thenReturn(Optional.of(entity));

        ApplicationDTO.Response actual = applicationService.update(findId, request);

        Assertions.assertThat(actual.getApplicationId()).isSameAs(findId);
        Assertions.assertThat(actual.getHopeAmount()).isSameAs(request.getHopeAmount());

    }


    @Test
    void 존재하는엔티티_삭제요청시_엔티티삭제(){
        Long targetId = 1L;

        Application entity = Application.builder()
                .applicationId(1L)
                .build();


        when(applicationRepository.save(ArgumentMatchers.any(Application.class))).thenReturn(entity);
        when(applicationRepository.findById(targetId)).thenReturn(Optional.of(entity));

        applicationService.delete(targetId);

        Assertions.assertThat(entity.getIsDeleted()).isSameAs(true);
    }

    @Test
    void 신청정보의_약관동의요청시_약관추가(){
        Terms terms1 = Terms.builder()
                .termsId(1L)
                .name("약관1")
                .termsDetailUrl("test")
                .build();

        Terms terms2 = Terms.builder()
                .termsId(2L)
                .name("약관2")
                .termsDetailUrl("test")
                .build();

        // 고객이 동의한 약관
        List<Long> acceptTerms = Arrays.asList(1L, 2L);

        ApplicationDTO.AcceptTermsDTO request = ApplicationDTO.AcceptTermsDTO.builder()
                .acceptTermsIds(acceptTerms)
                .build();

        // 대출신정정보 1번
        Long findId = 1L;


        when(applicationRepository.findById(findId)).thenReturn(Optional.ofNullable(Application.builder().build()));
        when(termsRepository.findAll(Sort.by(Sort.Direction.ASC,"termsId"))).thenReturn(Arrays.asList(terms1, terms2));
        when(acceptTermsRepository.save(ArgumentMatchers.any(AcceptTerms.class))).thenReturn(AcceptTerms.builder().build());

        Boolean actual = applicationService.acceptTerms(findId, request);

        Assertions.assertThat(actual).isTrue();

    }


    @Test
    void 모든약관동의_하지않으면_예외발생(){
        Terms terms1 = Terms.builder()
                .termsId(1L)
                .name("약관1")
                .termsDetailUrl("test")
                .build();

        Terms terms2 = Terms.builder()
                .termsId(2L)
                .name("약관2")
                .termsDetailUrl("test")
                .build();

        // 고객이 동의한 약관
        List<Long> acceptTerms = Arrays.asList(1L);

        ApplicationDTO.AcceptTermsDTO request = ApplicationDTO.AcceptTermsDTO.builder()
                .acceptTermsIds(acceptTerms)
                .build();

        // 대출신정정보 1번
        Long findId = 1L;


        when(applicationRepository.findById(findId)).thenReturn(Optional.ofNullable(Application.builder().build()));
        when(termsRepository.findAll(Sort.by(Sort.Direction.ASC,"termsId"))).thenReturn(Arrays.asList(terms1, terms2));

        org.junit.jupiter.api.Assertions.assertThrows(BaseException.class, () ->  applicationService.acceptTerms(findId, request));

    }

    @Test
    void 존재하지않는_약관_동의하면_예외발생(){
        Terms terms1 = Terms.builder()
                .termsId(1L)
                .name("약관1")
                .termsDetailUrl("test")
                .build();

        Terms terms2 = Terms.builder()
                .termsId(2L)
                .name("약관2")
                .termsDetailUrl("test")
                .build();

        // 고객이 동의한 약관
        List<Long> acceptTerms = Arrays.asList(1L,3L);

        ApplicationDTO.AcceptTermsDTO request = ApplicationDTO.AcceptTermsDTO.builder()
                .acceptTermsIds(acceptTerms)
                .build();

        // 대출신정정보 1번
        Long findId = 1L;


        when(applicationRepository.findById(findId)).thenReturn(Optional.ofNullable(Application.builder().build()));
        when(termsRepository.findAll(Sort.by(Sort.Direction.ASC,"termsId"))).thenReturn(Arrays.asList(terms1, terms2));

        org.junit.jupiter.api.Assertions.assertThrows(BaseException.class, () ->  applicationService.acceptTerms(findId, request));

    }
}
