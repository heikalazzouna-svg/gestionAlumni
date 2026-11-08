package com.example.gestionAlumni;

import com.example.gestionAlumni.Entities.Recruiter;
import com.example.gestionAlumni.Repos.RecruiterRepository;
import com.example.gestionAlumni.Services.EmailService;
import com.example.gestionAlumni.Services.RecruiterService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class RecruiterServiceTest {

    @Mock
    private RecruiterRepository recruiterRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private EmailService emailService;

    @InjectMocks
    private RecruiterService recruiterService;

    private Recruiter recruiter;

    @BeforeEach
    void setUp() {
        recruiter = new Recruiter();
        recruiter.setId(80L);
        recruiter.setFirstName("Salma");
        recruiter.setLastName("Gharbi");
        recruiter.setEmail("salma@instadeep.com");
        recruiter.setPassword("raw_recruiter_pwd");
        recruiter.setCompanyName("InstaDeep");
        recruiter.setSector("Artificial Intelligence");
        recruiter.setActive(true);
        recruiter.setVerified(false);
    }

    @Test
    @DisplayName("TU-RECRUITER-01: Inscription d'un recruteur avec génération de token")
    void testSignup_Success() {
        // Arrange
        when(recruiterRepository.findByEmail(recruiter.getEmail())).thenReturn(Optional.empty());
        when(passwordEncoder.encode("raw_recruiter_pwd")).thenReturn("hashed_recruiter_pwd");
        when(recruiterRepository.save(any(Recruiter.class))).thenAnswer(i -> i.getArgument(0));

        // Act
        Recruiter saved = recruiterService.signup(recruiter);

        // Assert
        assertNotNull(saved);
        assertEquals("hashed_recruiter_pwd", saved.getPassword());
        assertNotNull(saved.getVerificationToken());
        assertTrue(saved.isActive());
        verify(emailService).sendVerificationEmail(eq("salma@instadeep.com"), eq("Salma"), anyString(), eq("recruiter"));
    }

    @Test
    @DisplayName("TU-RECRUITER-02: Authentification réussie d'un recruteur vérifié et actif")
    void testAuthenticate_Success() {
        // Arrange
        recruiter.setPassword("hashed_pwd");
        recruiter.setVerified(true);
        recruiter.setActive(true);

        when(recruiterRepository.findByEmail("salma@instadeep.com")).thenReturn(Optional.of(recruiter));
        when(passwordEncoder.matches("mypassword", "hashed_pwd")).thenReturn(true);

        // Act
        Recruiter auth = recruiterService.authenticate("salma@instadeep.com", "mypassword");

        // Assert
        assertNotNull(auth);
        assertEquals("InstaDeep", auth.getCompanyName());
    }

    @Test
    @DisplayName("TU-RECRUITER-03: Authentification échoue si le compte n'est pas actif")
    void testAuthenticate_Inactive_ThrowsException() {
        // Arrange
        recruiter.setPassword("hashed_pwd");
        recruiter.setVerified(true);
        recruiter.setActive(false);

        when(recruiterRepository.findByEmail("salma@instadeep.com")).thenReturn(Optional.of(recruiter));
        when(passwordEncoder.matches("mypassword", "hashed_pwd")).thenReturn(true);

        // Act & Assert
        Exception ex = assertThrows(RuntimeException.class, () -> recruiterService.authenticate("salma@instadeep.com", "mypassword"));
        assertEquals("Account is not active.", ex.getMessage());
    }

    @Test
    @DisplayName("TU-RECRUITER-04: Mise à jour du profil de l'entreprise")
    void testUpdateProfile() {
        // Arrange
        when(recruiterRepository.findById(80L)).thenReturn(Optional.of(recruiter));
        when(recruiterRepository.save(any(Recruiter.class))).thenAnswer(i -> i.getArgument(0));

        Recruiter updateData = new Recruiter();
        updateData.setPresentation("Leader mondial de l'IA décisionnelle");
        updateData.setLocation("Tunis, Les Berges du Lac");

        // Act
        Recruiter updated = recruiterService.updateProfile(80L, updateData);

        // Assert
        assertEquals("Leader mondial de l'IA décisionnelle", updated.getPresentation());
        assertEquals("Tunis, Les Berges du Lac", updated.getLocation());
    }
}
