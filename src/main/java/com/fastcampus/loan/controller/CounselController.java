package com.fastcampus.loan.controller;

import com.fastcampus.loan.dto.CounselDTO;
import com.fastcampus.loan.dto.ResponseDTO;
import com.fastcampus.loan.service.CounselService;
import com.fastcampus.loan.service.CounselServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

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

    @GetMapping("/{counselId}")
    public ResponseDTO<Response> get(@PathVariable Long counselId){
        return ok(counselService.get(counselId));
    }

    @PutMapping("/{counselId}")
    public ResponseDTO<Response> update(@PathVariable Long counselId, @RequestBody Request request){
        return ok(counselService.update(counselId, request));
    }

    @DeleteMapping("/{counselId}")
    public ResponseDTO<Response> delete(@PathVariable Long counselId){
        counselService.delete(counselId);
        return ok();
    }

}
