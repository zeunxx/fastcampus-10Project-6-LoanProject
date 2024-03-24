package com.fastcampus.loan.controller;

import com.fastcampus.loan.dto.ApplicationDTO;
import com.fastcampus.loan.dto.JudgementDTO;
import com.fastcampus.loan.dto.JudgementDTO.Response;
import com.fastcampus.loan.dto.ResponseDTO;
import com.fastcampus.loan.service.JudgementService;
import com.fastcampus.loan.service.JudgementServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/judgements")
public class JudgementController extends AbstractController {

    private final JudgementService judgementService;

    @PostMapping
    public ResponseDTO<Response> create(@RequestBody JudgementDTO.Request request){
        return ok(judgementService.create(request));
    }

    @GetMapping("/{judgementId}")
    public ResponseDTO<Response> get(@PathVariable Long judgementId){
        return ok(judgementService.get(judgementId));
    }

    @GetMapping("/applications/{applicationId}")
    public ResponseDTO<Response> getJudgementOfApplication(@PathVariable Long applicationId){
        return ok(judgementService.getJudgementOfApplicationId(applicationId));
    }

    @PutMapping("/{judgementId}")
    public ResponseDTO<Response> update(@PathVariable Long judgementId, @RequestBody JudgementDTO.Request request){
        return ok(judgementService.update(judgementId, request));
    }

    @DeleteMapping("/{judgementId}")
    public ResponseDTO<Void> delete(@PathVariable Long judgementId){
        judgementService.delete(judgementId);
        return ok();
    }

    @PatchMapping("/{judgementId}/grant")
    public ResponseDTO<ApplicationDTO.GrantAmount> grant(@PathVariable Long judgementId){
        return ok(judgementService.grant(judgementId));
    }

}
