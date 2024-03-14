package com.fastcampus.loan.controller;

import com.fastcampus.loan.dto.CounselDTO;
import com.fastcampus.loan.dto.ResponseDTO;
import com.fastcampus.loan.service.CounselService;
import com.fastcampus.loan.service.CounselServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import static com.fastcampus.loan.dto.CounselDTO.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/counsels")
public class CounselController extends AbstractController {


    private final CounselService counselService;

    @PostMapping
    public ResponseDTO<Response> create(@RequestBody Request request){
        return ok(counselService.create(request));
    }

}
