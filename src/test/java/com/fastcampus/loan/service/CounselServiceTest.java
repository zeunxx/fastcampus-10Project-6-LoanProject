package com.fastcampus.loan.service;
import static org.mockito.Mockito.when;
import com.fastcampus.loan.domain.Counsel;
import com.fastcampus.loan.dto.CounselDTO;
import com.fastcampus.loan.repository.CounselRepository;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentMatchers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;

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

        // repository의 save에 counsel class의 값이 매개변수로 주어지면 정의한 entity가 반환도니다고 mocking
        when(counselRepository.save(ArgumentMatchers.any(Counsel.class))).thenReturn(entity);

        CounselDTO.Response actual = counselService.create(request);
        Assertions.assertThat(actual.getName()).isSameAs(entity.getName());
    }
}
