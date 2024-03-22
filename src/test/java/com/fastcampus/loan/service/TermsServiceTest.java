package com.fastcampus.loan.service;

import com.fastcampus.loan.domain.Terms;
import com.fastcampus.loan.dto.TermsDTO;
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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class TermsServiceTest {

    @InjectMocks
    TermsServiceImpl termsService;

    @Mock
    TermsRepository termsRepository;

    @Spy
    ModelMapper modelMapper;

    @Test
    void 약관등록요청시_새로운_약관정보_리턴(){
        Terms entity = Terms.builder()
                .name("대출 이용 약관")
                .termsDetailUrl("https://abc-storage.com/test")
                .build();


        TermsDTO.Request request = TermsDTO.Request.builder()
                .name("대출 이용 약관")
                .termsDetailUrl("https://abc-storage.com/test")
                .build();

        when(termsRepository.save(ArgumentMatchers.any(Terms.class))).thenReturn(entity);

        TermsDTO.Response actual = termsService.create(request);

        Assertions.assertThat(actual.getName()).isSameAs(entity.getName());
        Assertions.assertThat(actual.getTermsDetailUrl()).isSameAs(entity.getTermsDetailUrl());
    }

    @Test
    void 약관리스트_요청시_존재하는_모든약관리턴(){
        Terms entityA = Terms.builder()
                .name("대출 이용약관 1")
                .termsDetailUrl("https://test.com/test1")
                .build();
        Terms entityB = Terms.builder()
                .name("대출 이용약관 2")
                .termsDetailUrl("https://test.com/test2")
                .build();

        List<Terms> list = new ArrayList<>(Arrays.asList(entityA, entityB));

        when(termsRepository.findAll()).thenReturn(list);

        List<TermsDTO.Response> actual = termsService.getAll();

        Assertions.assertThat(actual.size()).isSameAs(list.size());
    }
}
