package com.example.demo;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.concurrent.*;

@RestController
public class ImportController {

    @Autowired private UploadService uploadService;
/*
    @GetMapping("/greeting")
    public String greeting(@RequestParam(name="name", required=false, defaultValue="World") String name, Model model) {
        model.addAttribute("name", name);
        return "greeting";
    }*/

    @PostMapping("/upload")
    public String uploadFile(@RequestParam("file") MultipartFile file, RedirectAttributes attributes) {
        System.out.println("how");
        return "the fuck";
    }

    @PostMapping("/upload-csv-file")
    public Long saveCsv(@RequestParam("file") MultipartFile file, Model model) { //Not async I don't think
        Callable<Long> task = () -> {
            try {
                return uploadService.runImport(file, model);
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
