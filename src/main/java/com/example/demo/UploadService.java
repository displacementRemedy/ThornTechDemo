package com.example.demo;

import com.opencsv.CSVReader;
import com.opencsv.CSVReaderBuilder;
import com.opencsv.bean.ColumnPositionMappingStrategy;
import com.opencsv.bean.CsvToBean;
import com.opencsv.bean.CsvToBeanBuilder;
import org.hibernate.exception.ConstraintViolationException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.ui.Model;
import org.springframework.web.multipart.MultipartFile;
import org.thymeleaf.util.StringUtils;

import javax.naming.SizeLimitExceededException;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.StandardCharsets;
import java.text.NumberFormat;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Service
public class UploadService {

    @Autowired private ResultRepo resultRepo;
    @Autowired private UserRepo userRepo;

    public Long runImport(MultipartFile file, Model model) {

        // validate file
        if (file.isEmpty()) {
            model.addAttribute("message", "Please select a CSV file to upload.");
            model.addAttribute("status", false);
            return null;
        } else {

            //Define total row size
            Long total = 0L;
            //TODO ask if there's a header or not - I'm gonna assume it doesn't matter for the exercise and say yes
            try {
                InputStream inputStream = file.getInputStream();
                total = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8))
                        .lines()
                        .count() - 1;
                if (total > 1000000) {
                    throw new SizeLimitExceededException("File cannot have more than 1 million rows.");
                }
            } catch (Exception e) {
                e.printStackTrace();
                //TODO throw proper exception, this means there was an error getting the total number of rows
            }


            //initial result state
            UploadResult result = UploadResult.builder()
                    .fileName(file.getOriginalFilename())
                    .status(UploadStatus.IN_PROGRESS)
                    .total(total.intValue())
                    .success(0)
                    .failed(0)
                    .percentComplete("0.00%")
                    .build();
            result = resultRepo.save(result);

            ExecutorService executor = Executors.newFixedThreadPool(1);
            executor.submit(new UploadRunnable(result, total, file));

            return result.getId();
        }
    }

    public boolean isNameValid(String name) {
        return StringUtils.isEmpty(name) || name.length() < 50 || name.matches("[a-zA-Z]+");
    }

    public UploadResult readById(Long id) {
        return resultRepo.findById(id).get();
    }

    private class UploadRunnable implements Runnable {

        private UploadResult result;
        private Long total;
        private MultipartFile file;

        public UploadRunnable(UploadResult result, Long total, MultipartFile file) {
            this.result = result;
            this.total = total;
            this.file = file;
        }

        @Override
        public void run() {
            try (
                    Reader reader = new BufferedReader(new InputStreamReader(file.getInputStream()));
                    CSVReader csvReader = new CSVReaderBuilder(reader).withSkipLines(1).build();
            ) {

                //Define variables
                int success, failed;
                success = failed = 0;
                Set<User> users = new HashSet<>();
                NumberFormat format = NumberFormat.getPercentInstance();

                Iterator<User> csvUserIterator = buildCsvToBean(csvReader).iterator();
                while (csvUserIterator.hasNext()) {

                    try {
                        User user = csvUserIterator.next();

                        //pre-validation
                        if (!isNameValid(user.getFirstName()) || !isNameValid(user.getLastName()) ||
                                !user.getPhone().matches("[0-9]+") ||
                                user.getPhone().length() != 10) {
                            failed++;
                        } else {
                            if (users.add(user)) {
                                try {
                                    userRepo.save(user);
                                    success++;
                                } catch (ConstraintViolationException e) {
                                    e.printStackTrace();
                                    failed++;
                                }
                            } else {
                                failed++;
                            }
                        }

                    } catch (Exception e) {
                        failed++;
                    } finally {

                        double percent = (double) (success + failed) / total;

                        result.setSuccess(success);
                        result.setFailed(failed);
                        result.setPercentComplete(format.format(percent));
                        result = resultRepo.save(result);
                    }
                }
                result.setStatus(success == 0 ? UploadStatus.FAILED : UploadStatus.COMPLETE);

            } catch (Exception ex) {
                ex.printStackTrace();
            } finally {
                result = resultRepo.findById(result.getId()).get();
                result.setStatus(result.getSuccess() > 0 ? UploadStatus.COMPLETE : UploadStatus.FAILED);
                result = resultRepo.save(result);
            }
        }

        private CsvToBean<User> buildCsvToBean(CSVReader csvReader) {
            String[] columns = {"phone", "firstName", "lastName"};
            ColumnPositionMappingStrategy strategy = new ColumnPositionMappingStrategy();
            strategy.setType(User.class);
            strategy.setColumnMapping(columns);

            //Build csv reader
            CsvToBean<User> csvToBean = new CsvToBeanBuilder(csvReader)
                    .withIgnoreLeadingWhiteSpace(true)
                    .withMappingStrategy(strategy)
                    .build();
            return csvToBean;
        }
    }

}
