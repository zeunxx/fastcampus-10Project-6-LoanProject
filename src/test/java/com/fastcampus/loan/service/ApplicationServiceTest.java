package com.fastcampus.loan.service;

import com.fastcampus.loan.domain.Application;
import com.fastcampus.loan.dto.ApplicationDTO;
import com.fastcampus.loan.repository.ApplicationRepository;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentMatchers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;

import java.math.BigDecimal;
import java.util.Optional;

import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class ApplicationServiceTest {

    @InjectMocks
    ApplicationServiceImpl applicationService;

    @Mock
    private ApplicationRepository applicationRepository;

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

        when(applicationRepository.findById(1L)).thenReturn(Optional.of(entity));
        ApplicationDTO.Response actual = applicationService.get(findId);

        Assertions.assertThat(actual.getApplicationId()).isSameAs(entity.getApplicationId());
    }
}
