package com.example.demo;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.concurrent.*;

@CrossOrigin("*")
@RestController
public class ImportController {

    @Autowired private UploadService uploadService;

    @PostMapping("/upload-csv-file")
    public Long saveCsv(
            @RequestParam("file") MultipartFile file,
            @RequestParam("hasHeader") Boolean hasHeader,
            Model model) {
        Callable<Long> task = () -> {
            try {
                return uploadService.runImport(file, hasHeader, model);
            }
            catch (Exception e) {
                throw new IllegalStateException("task interrupted", e); //Bad error
            }
        };
        ExecutorService executor = Executors.newFixedThreadPool(1);
        Future<Long> future = executor.submit(task);

        Long result = null;
        try {
            result = future.get();
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }

        return result;
    }

    @GetMapping("/history")
    public UploadResult readHistory(@RequestParam("id") Long id) {
        return uploadService.readById(id);
    }

}
