package com.example.gestionAlumni;

import com.example.gestionAlumni.DTO.AlumniAdminSummaryDTO;
import com.example.gestionAlumni.DTO.StudentAdminSummaryDTO;
import com.example.gestionAlumni.Entities.Administrator;
import com.example.gestionAlumni.Entities.Alumni;
import com.example.gestionAlumni.Entities.Student;
import com.example.gestionAlumni.Repos.AdminRepository;
import com.example.gestionAlumni.Repos.AlumniRepository;
import com.example.gestionAlumni.Repos.StudentRepository;
import com.example.gestionAlumni.Services.AdminService;
import com.example.gestionAlumni.Services.EmailService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class AdminServiceTest {

    @Mock
    private AdminRepository adminrepo;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private AlumniRepository alumniRepository;

    @Mock
    private StudentRepository studentRepository;

    @Mock
    private EmailService emailService;

    @InjectMocks
    private AdminService adminService;

    private Administrator testAdmin;
    private Alumni testAlumni;
    private Student testStudent;

    @BeforeEach
    void setUp() {
        testAdmin = new Administrator();
        testAdmin.setId(1L);
        testAdmin.setEmail("admin@test.com");
        testAdmin.setPassword("encoded_secret");
        testAdmin.setActive(true);

        testAlumni = new Alumni();
        testAlumni.setId(10L);
        testAlumni.setFirstName("Ahmed");
        testAlumni.setLastName("Ben Salah");
        testAlumni.setEmail("ahmed@alumni.com");
        testAlumni.setVerified(false);

        testStudent = new Student();
        testStudent.setId(20L);
        testStudent.setFirstName("Sami");
        testStudent.setLastName("Trabelsi");
        testStudent.setEmail("sami@student.com");
        testStudent.setSpeciality("Génie Logiciel");
    }

    @Test
    @DisplayName("TU-ADMIN-01: Approbation d'un compte alumni avec envoi d'email")
    void testApproveAlumni_Success() {
        // Arrange
        when(alumniRepository.findById(10L)).thenReturn(Optional.of(testAlumni));
        when(alumniRepository.save(any(Alumni.class))).thenAnswer(i -> i.getArgument(0));

        // Act
        Alumni result = adminService.approveAlumni(10L);

        // Assert
        assertNotNull(result);
        assertTrue(result.isVerified());
        verify(alumniRepository).save(testAlumni);
        verify(emailService).sendEmail(eq("ahmed@alumni.com"), contains("Approved"), anyString());
    }

    @Test
    @DisplayName("TU-ADMIN-02: Approbation d'un alumni inexistant lance une exception")
    void testApproveAlumni_NotFound_ThrowsException() {
        // Arrange
        when(alumniRepository.findById(99L)).thenReturn(Optional.empty());

        // Act & Assert
        Exception ex = assertThrows(RuntimeException.class, () -> adminService.approveAlumni(99L));
        assertEquals("Alumni not found", ex.getMessage());
        verify(emailService, never()).sendEmail(anyString(), anyString(), anyString());
    }

    @Test
    @DisplayName("TU-ADMIN-03: Rejet et suppression d'un compte alumni avec envoi d'email")
    void testRejectAlumni_Success() {
        // Arrange
        when(alumniRepository.findById(10L)).thenReturn(Optional.of(testAlumni));

        // Act
        adminService.rejectAlumni(10L);

        // Assert
        verify(alumniRepository).delete(testAlumni);
        verify(emailService).sendEmail(eq("ahmed@alumni.com"), contains("Rejected"), anyString());
    }

    @Test
    @DisplayName("TU-ADMIN-04: Authentification Admin réussie")
    void testAuthenticate_Success() {
        // Arrange
        when(adminrepo.findByEmail("admin@test.com")).thenReturn(Optional.of(testAdmin));
        when(passwordEncoder.matches("password123", "encoded_secret")).thenReturn(true);

        // Act
        Administrator authAdmin = adminService.authenticate("admin@test.com", "password123");

        // Assert
        assertNotNull(authAdmin);
        assertEquals("admin@test.com", authAdmin.getEmail());
    }

    @Test
    @DisplayName("TU-ADMIN-05: Authentification Admin - Mot de passe incorrect")
    void testAuthenticate_BadCredentials_ThrowsException() {
        // Arrange
        when(adminrepo.findByEmail("admin@test.com")).thenReturn(Optional.of(testAdmin));
        when(passwordEncoder.matches("wrong_pwd", "encoded_secret")).thenReturn(false);

        // Act & Assert
        Exception ex = assertThrows(RuntimeException.class, () -> adminService.authenticate("admin@test.com", "wrong_pwd"));
        assertEquals("Invalid credentials", ex.getMessage());
    }

    @Test
    @DisplayName("TU-ADMIN-06: Authentification Admin - Compte inactif")
    void testAuthenticate_InactiveAccount_ThrowsException() {
        // Arrange
        testAdmin.setActive(false);
        when(adminrepo.findByEmail("admin@test.com")).thenReturn(Optional.of(testAdmin));
        when(passwordEncoder.matches("password123", "encoded_secret")).thenReturn(true);

        // Act & Assert
        Exception ex = assertThrows(RuntimeException.class, () -> adminService.authenticate("admin@test.com", "password123"));
        assertEquals("Account is inactive.", ex.getMessage());
    }

    @Test
    @DisplayName("TU-ADMIN-07: Liste des alumni en attente de vérification")
    void testListPendingAlumni() {
        // Arrange
        when(alumniRepository.findByVerifiedFalse()).thenReturn(List.of(testAlumni));

        // Act
        List<AlumniAdminSummaryDTO> pending = adminService.listPendingAlumni();

        // Assert
        assertEquals(1, pending.size());
        assertEquals("ahmed@alumni.com", pending.get(0).getEmail());
        assertFalse(pending.get(0).isVerified());
    }

    @Test
    @DisplayName("TU-ADMIN-08: Liste de tous les étudiants")
    void testListAllStudents() {
        // Arrange
        when(studentRepository.findAll()).thenReturn(List.of(testStudent));

        // Act
        List<StudentAdminSummaryDTO> students = adminService.listAllStudents();

        // Assert
        assertEquals(1, students.size());
        assertEquals("sami@student.com", students.get(0).getEmail());
    }
}
