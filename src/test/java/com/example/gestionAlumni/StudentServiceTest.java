package com.example.gestionAlumni;

import com.example.gestionAlumni.Entities.Student;
import com.example.gestionAlumni.Repos.StudentRepository;
import com.example.gestionAlumni.Services.EmailService;
import com.example.gestionAlumni.Services.StudentService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class StudentServiceTest {

    @Mock
    private StudentRepository studentRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private EmailService emailService;

    @InjectMocks
    private StudentService studentService;

    private Student student;

    @BeforeEach
    void setUp() {
        student = new Student();
        student.setId(5L);
        student.setFirstName("Nour");
        student.setLastName("Jaziri");
        student.setEmail("nour.jaziri@email.tn");
        student.setPassword("student_pwd");
        student.setSpeciality("Génie Logiciel");
        student.setPredictedGradYear(2025);
        student.setVerified(false);
    }

    @Test
    @DisplayName("TU-STUDENT-01: Inscription d'un étudiant avec génération de jeton")
    void testSignup_Success() {
        // Arrange
        when(studentRepository.findByEmail(student.getEmail())).thenReturn(Optional.empty());
        when(passwordEncoder.encode("student_pwd")).thenReturn("encoded_student_pwd");
        when(studentRepository.save(any(Student.class))).thenAnswer(i -> i.getArgument(0));

        // Act
        Student saved = studentService.signup(student);

        // Assert
        assertNotNull(saved);
        assertEquals("encoded_student_pwd", saved.getPassword());
        assertNotNull(saved.getVerificationToken());
        assertFalse(saved.isVerified());
        verify(emailService).sendVerificationEmail(eq("nour.jaziri@email.tn"), eq("Nour"), anyString(), eq("student"));
    }

    @Test
    @DisplayName("TU-STUDENT-02: Inscription étudiant avec email déjà pris")
    void testSignup_EmailExists_ThrowsException() {
        // Arrange
        when(studentRepository.findByEmail(student.getEmail())).thenReturn(Optional.of(student));

        // Act & Assert
        Exception ex = assertThrows(RuntimeException.class, () -> studentService.signup(student));
        assertEquals("Email already in use", ex.getMessage());
        verify(studentRepository, never()).save(any());
    }

    @Test
    @DisplayName("TU-STUDENT-03: Authentification étudiant vérifié")
    void testAuthenticate_Verified_Success() {
        // Arrange
        student.setPassword("hashed_pwd");
        student.setVerified(true);
        when(studentRepository.findByEmail("nour.jaziri@email.tn")).thenReturn(Optional.of(student));
        when(passwordEncoder.matches("mypass", "hashed_pwd")).thenReturn(true);

        // Act
        Student authenticated = studentService.authenticate("nour.jaziri@email.tn", "mypass");

        // Assert
        assertNotNull(authenticated);
        assertEquals("Nour", authenticated.getFirstName());
    }

    @Test
    @DisplayName("TU-STUDENT-04: Authentification avec mot de passe incorrect")
    void testAuthenticate_BadPassword_ThrowsException() {
        // Arrange
        student.setPassword("hashed_pwd");
        when(studentRepository.findByEmail("nour.jaziri@email.tn")).thenReturn(Optional.of(student));
        when(passwordEncoder.matches("wrongpass", "hashed_pwd")).thenReturn(false);

        // Act & Assert
        Exception ex = assertThrows(RuntimeException.class, () -> studentService.authenticate("nour.jaziri@email.tn", "wrongpass"));
        assertEquals("Invalid student credentials", ex.getMessage());
    }

    @Test
    @DisplayName("TU-STUDENT-05: Validation du compte étudiant par jeton")
    void testVerifyStudent_Success() {
        // Arrange
        student.setVerificationToken("token-xyz");
        when(studentRepository.findByVerificationToken("token-xyz")).thenReturn(Optional.of(student));
        when(studentRepository.save(any(Student.class))).thenAnswer(i -> i.getArgument(0));

        // Act
        Student verified = studentService.verifyStudent("token-xyz");

        // Assert
        assertTrue(verified.isVerified());
        assertNull(verified.getVerificationToken());
    }

    @Test
    @DisplayName("TU-STUDENT-06: Complétion du profil avec téléversement de CV")
    void testCompleteProfile_WithDocument() {
        // Arrange
        when(studentRepository.findById(5L)).thenReturn(Optional.of(student));
        when(studentRepository.save(any(Student.class))).thenAnswer(i -> i.getArgument(0));

        MockMultipartFile file = new MockMultipartFile(
                "document",
                "cv_nour.pdf",
                "application/pdf",
                "Contenu dummy du CV".getBytes()
        );

        // Act
        Student updated = studentService.completeProfile(5L, "Systèmes Embarqués", "Stage PFE", 2026, file);

        // Assert
        assertEquals("Systèmes Embarqués", updated.getSpeciality());
        assertEquals("Stage PFE", updated.getSearchType());
        assertEquals(2026, updated.getPredictedGradYear());
        assertEquals("cv_nour.pdf", updated.getDocumentName());
        assertNotNull(updated.getDocument());
    }
}
