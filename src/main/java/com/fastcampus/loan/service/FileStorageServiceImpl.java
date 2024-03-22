package com.fastcampus.loan.service;

import com.fastcampus.loan.exception.BaseException;
import com.fastcampus.loan.exception.ResultType;
import com.fastcampus.loan.repository.ApplicationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;
import org.springframework.util.FileSystemUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.stream.Stream;

@Service
@RequiredArgsConstructor
public class FileStorageServiceImpl implements FileStorageService{

    @Value("${spring.servlet.multipart.location}")
    private String uploadPath;

    private final ApplicationRepository applicationRepository;

    @Override
    public void save(Long applicationId, MultipartFile file) {

        if(!isPresentApplicationId(applicationId)){
            throw new BaseException(ResultType.SYSTEM_ERROR);
        }

        try {
            // 파일 담을 폴더 하위에 사용자 전용 폴더 경로 생성 e.g., ../dir/1 , ../dir/2
            String applicationPath = uploadPath.concat("/"+applicationId);
            Path directoryPath = Path.of(applicationPath);

            // 사용자 폴더 존재하지 않으면 생성
            if (!Files.exists(directoryPath)){
                Files.createDirectory(directoryPath);
            }

            // 전달받은 파일 지정한 경로로 파일 COPY
            Files.copy(file.getInputStream(), Paths.get(applicationPath).resolve(file.getOriginalFilename()), StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException e) {
            throw new BaseException(ResultType.SYSTEM_ERROR);
        }
    }

    @Override
    public Resource load(Long applicationId, String fileName) {

        if(!isPresentApplicationId(applicationId)){
            throw new BaseException(ResultType.SYSTEM_ERROR);
        }

        try {
            // 파일 담을 폴더 하위에 사용자 전용 폴더 경로 생성 e.g., ../dir/1 , ../dir/2
            String applicationPath = uploadPath.concat("/"+applicationId);
            Path file = Paths.get(applicationPath).resolve(fileName);
            Resource resource = new UrlResource(file.toUri());
            if (resource.isReadable() || resource.exists()){
                return  resource;
            }else {
                throw new BaseException(ResultType.SYSTEM_ERROR);
            }
        } catch (MalformedURLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public Stream<Path> loadAll(Long applicationId) {

        if(!isPresentApplicationId(applicationId)){
            throw new BaseException(ResultType.SYSTEM_ERROR);
        }

        try{
            // 파일 담을 폴더 하위에 사용자 전용 폴더 경로 생성 e.g., ../dir/1 , ../dir/2
            String applicationPath = uploadPath.concat("/"+applicationId);
            // 매개변수로 들어온 Path에 해당하는 모든 경로를 탐색해서 반환함
            return Files.walk(Paths.get(applicationPath),1).filter(path -> !path.equals(Paths.get(applicationPath)));
        }catch (Exception e){
            throw new BaseException(ResultType.NOT_EXIST);
        }
    }

    @Override
    public void deleteAll(Long applicationId) {
        if(!isPresentApplicationId(applicationId)){
            throw new BaseException(ResultType.SYSTEM_ERROR);
        }
        // 파일 담을 폴더 하위에 사용자 전용 폴더 경로 생성 e.g., ../dir/1 , ../dir/2
        String applicationPath = uploadPath.concat("/"+applicationId);
        FileSystemUtils.deleteRecursively(Paths.get(applicationPath).toFile());
    }

    private boolean isPresentApplicationId(Long applicationId){
        return applicationRepository.findById(applicationId).isPresent();
    }
}
