package com.fastcampus.loan.service;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.Mockito.when;

import com.fastcampus.loan.domain.BaseEntity;
import com.fastcampus.loan.domain.Counsel;
import com.fastcampus.loan.dto.CounselDTO;
import com.fastcampus.loan.exception.BaseException;
import com.fastcampus.loan.exception.ResultType;
import com.fastcampus.loan.repository.CounselRepository;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentMatchers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;

import java.util.Optional;

@ExtendWith(MockitoExtension.class)
public class CounselServiceTest {
    // counsel service의 순수 비즈니스 로직만 테스트(다른 클래스 의존성은 신경쓰지 않음)

    @InjectMocks
    CounselServiceImpl counselService;

    @Mock
    private CounselRepository counselRepository;

    @Spy // 오브젝트를 맵핑해주는 역할이므로 모킹처리 없이 순수하게 사용하기 위함
    private ModelMapper modelMapper;

    @Test
    void Should_ReturnResponseOfNewCounselEntity_When_RequestCounsel(){
        Counsel entity = Counsel.builder()
                .name("kim member")
                .cellPhone("010-1111-2222")
                .email("abc@def.g")
                .memo("대출 받고 싶어요")
                .address("서울 어딘구 모른동")
                .addressDetail("101동")
                .zipCode("12345")
                .build();

        CounselDTO.Request request = CounselDTO.Request.builder()
                .name("kim member")
                .cellPhone("010-1111-2222")
                .email("abc@def.g")
                .memo("대출 받고 싶어요")
                .address("서울 어딘구 모른동")
                .addressDetail("101동")
                .zipCode("12345")
                .build();

        // repository의 save에 counsel class의 값이 매개변수로 주어지면 정의한 entity가 반환된다고 mocking
        when(counselRepository.save(ArgumentMatchers.any(Counsel.class))).thenReturn(entity);

        CounselDTO.Response actual = counselService.create(request);
        assertThat(actual.getName()).isSameAs(entity.getName());
    }


    @Test
    void Should_ReturnResponseOfExistCunselEntity_When_RequestExistCounselId(){
        Long findId=1L;

        Counsel entity = Counsel.builder()
                .counselId(1L)
                .build();

        when(counselRepository.findById(findId)).thenReturn(Optional.ofNullable(entity));

        CounselDTO.Response actual = counselService.get(findId);
        assertThat(actual.getCounselId()).isSameAs(findId);
    }

    @Test
    void 없는_아이디_조회시_예외_발생(){
        Long findId=2L;

        when(counselRepository.findById(findId)).thenThrow(new BaseException(ResultType.SYSTEM_ERROR));

        Assertions.assertThrows(BaseException.class, ()-> counselService.get(findId));

    }

    @Test
    void 업데이트_요청이_들어오면_존재하는_상담엔티티의_업데이트_된_응답리턴(){
        Long findId = 1L;
        Counsel entity = Counsel.builder()
                .counselId(1L)
                .name("Member Kim")
                .build();

        CounselDTO.Request request = CounselDTO.Request.builder()
                .name("Member Kang")
                .build();

        // 어떤 값이 있는 오브젝트가 들어오면 entity 리턴하게 처리
        when(counselRepository.save(ArgumentMatchers.any(Counsel.class))).thenReturn(entity);
        when(counselRepository.findById(findId)).thenReturn(Optional.ofNullable(entity));


        CounselDTO.Response actual = counselService.update(findId, request);
        assertThat(actual.getCounselId()).isSameAs(findId);
        assertThat(actual.getName()).isSameAs(request.getName());


    }
}
