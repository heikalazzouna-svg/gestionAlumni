package com.example.gestionAlumni.Services;

import com.example.gestionAlumni.Entities.Student;
import com.example.gestionAlumni.Repos.StudentRepository;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import java.util.UUID;

@FieldDefaults(level = AccessLevel.PRIVATE)
@Service
public class StudentService {

    @Autowired
    StudentRepository studentRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private EmailService emailService;

    // Méthode pour authentifier un étudiant
    public Student authenticate(String email, String rawPassword) {
        Student student = studentRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Invalid email"));

        if (!passwordEncoder.matches(rawPassword, student.getPassword())) {
            throw new RuntimeException("Invalid student credentials");
        }

        if (!student.isVerified()) {
            throw new RuntimeException("Account not verified. Check your email.");
        }

        return student;
    }

    // Méthode d'inscription d'un nouvel étudiant
    public Student signup(Student student) {
        if (studentRepository.findByEmail(student.getEmail()).isPresent()) {
            throw new RuntimeException("Email already in use");
        }
        student.setPassword(passwordEncoder.encode(student.getPassword()));
        
        String token = UUID.randomUUID().toString();
        student.setVerificationToken(token);
        student.setVerified(false);
        
        Student savedStudent = studentRepository.save(student);
        emailService.sendVerificationEmail(savedStudent.getEmail(), savedStudent.getFirstName(), token, "student");
        
        return savedStudent;
    }

    public Student verifyStudent(String token) {
        Student student = studentRepository.findByVerificationToken(token)
                .orElseThrow(() -> new RuntimeException("Invalid token"));

        student.setVerified(true);
        student.setVerificationToken(null);
        return studentRepository.save(student);
    }

    // Méthode pour compléter le profil de l'étudiant
    public Student completeProfile(Long id, String speciality, String searchType, int predictedGradYear, MultipartFile document) {
        // 1. Récupérer l'étudiant à partir de l'ID
        Student student = studentRepository.findById(id).orElseThrow(() -> new RuntimeException("Student not found"));

        // 2. Mettre à jour les informations du profil
        student.setSpeciality(speciality);
        student.setPredictedGradYear(predictedGradYear);
        student.setSearchType(searchType);

        // 3. Ajouter et gérer le document
        if (document != null && !document.isEmpty()) {
            // Nom du fichier à stocker dans la base de données
            String documentName = document.getOriginalFilename();

            try {
                student.setDocument(document.getBytes());
            } catch (java.io.IOException e) {
                throw new RuntimeException("Failed to read document", e);
            }
            student.setDocumentName(documentName);

        }

        // 4. Sauvegarder l'étudiant mis à jour dans la base de données
        return studentRepository.save(student);
    }

    public Student uploadResume(Long id, MultipartFile file) {
        Student student = studentRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Student not found"));

        if (file != null && !file.isEmpty()) {
            try {
                student.setDocument(file.getBytes());
                student.setDocumentName(file.getOriginalFilename());
            } catch (java.io.IOException e) {
                throw new RuntimeException("Failed to read file", e);
            }
        }

        return studentRepository.save(student);
    }
    public Student updateProfile(Long id, Student updatedData) {
        Student student = studentRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Student not found"));
        
        if (updatedData.getFirstName() != null) student.setFirstName(updatedData.getFirstName());
        if (updatedData.getLastName() != null) student.setLastName(updatedData.getLastName());
        if (updatedData.getSpeciality() != null) student.setSpeciality(updatedData.getSpeciality());
        if (updatedData.getPredictedGradYear() != 0) student.setPredictedGradYear(updatedData.getPredictedGradYear());
        if (updatedData.getSearchType() != null) student.setSearchType(updatedData.getSearchType());
        if (updatedData.getSkills() != null) student.setSkills(updatedData.getSkills());
        if (updatedData.getExperiences() != null) student.setExperiences(updatedData.getExperiences());
        if (updatedData.getProjects() != null) student.setProjects(updatedData.getProjects());
        if (updatedData.getInterests() != null) student.setInterests(updatedData.getInterests());
        if (updatedData.getAvailability() != null) student.setAvailability(updatedData.getAvailability());
        
        return studentRepository.save(student);
    }
}
