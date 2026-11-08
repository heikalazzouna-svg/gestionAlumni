package com.example.gestionAlumni.DTO;


import lombok.*;
import org.springframework.web.multipart.MultipartFile;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class StudentUpdateDTO {
    private String speciality;
    private String searchType; // "internship", "job", "collaboration"
    private int predictedGradYear;
    private MultipartFile document;
}