package com.example.demo;

import com.example.demo.UploadResult;
import org.springframework.data.repository.CrudRepository;

public interface ResultRepo extends CrudRepository<UploadResult, Long> {

}