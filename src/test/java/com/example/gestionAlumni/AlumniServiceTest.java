package com.example.gestionAlumni;

import com.example.gestionAlumni.DTO.AlumniPreferencesDTO;
import com.example.gestionAlumni.Entities.Alumni;
import com.example.gestionAlumni.Entities.MentorshipEvaluation;
import com.example.gestionAlumni.Entities.MentorshipSession;
import com.example.gestionAlumni.Repos.AlumniRepository;
import com.example.gestionAlumni.Repos.MentorshipEvaluationRepository;
import com.example.gestionAlumni.Repos.MentorshipSessionRepository;
import com.example.gestionAlumni.Services.AlumniService;
import com.example.gestionAlumni.Services.EmailService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class AlumniServiceTest {

    @Mock
    private AlumniRepository alumniRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private EmailService emailService;

    @Mock
    private MentorshipEvaluationRepository mentorshipEvaluationRepository;

    @Mock
    private MentorshipSessionRepository mentorshipSessionRepository;

    @InjectMocks
    private AlumniService alumniService;

    private Alumni alumni;

    @BeforeEach
    void setUp() {
        alumni = new Alumni();
        alumni.setId(1L);
        alumni.setFirstName("Mohamed");
        alumni.setLastName("Trabelsi");
        alumni.setEmail("mohamed.trabelsi@email.tn");
        alumni.setPassword("plain_password");
        alumni.setVerified(false);
        alumni.setCurrentCompany("Sofrecom");
        alumni.setCurrentJob("DevOps Engineer");
    }

    @Test
    @DisplayName("TU-ALUMNI-01: Inscription d'un Alumni avec génération de token et email")
    void testSignup_Success() {
        // Arrange
        when(alumniRepository.findByEmail(alumni.getEmail())).thenReturn(Optional.empty());
        when(passwordEncoder.encode("plain_password")).thenReturn("encoded_hash");
        when(alumniRepository.save(any(Alumni.class))).thenAnswer(i -> i.getArgument(0));

        // Act
        Alumni saved = alumniService.signup(alumni);

        // Assert
        assertNotNull(saved);
        assertEquals("encoded_hash", saved.getPassword());
        assertNotNull(saved.getVerificationToken());
        assertFalse(saved.isVerified());
        verify(emailService).sendVerificationEmail(eq("mohamed.trabelsi@email.tn"), eq("Mohamed"), anyString(), eq("alumni"));
    }

    @Test
    @DisplayName("TU-ALUMNI-02: Inscription avec un email déjà existant échoue")
    void testSignup_DuplicateEmail_ThrowsException() {
        // Arrange
        when(alumniRepository.findByEmail(alumni.getEmail())).thenReturn(Optional.of(alumni));

        // Act & Assert
        Exception ex = assertThrows(RuntimeException.class, () -> alumniService.signup(alumni));
        assertEquals("Email already in use", ex.getMessage());
        verify(alumniRepository, never()).save(any());
    }

    @Test
    @DisplayName("TU-ALUMNI-03: Authentification d'un Alumni vérifié")
    void testAuthenticate_Verified_Success() {
        // Arrange
        alumni.setPassword("hashed_secret");
        alumni.setVerified(true);
        when(alumniRepository.findByEmail("mohamed.trabelsi@email.tn")).thenReturn(Optional.of(alumni));
        when(passwordEncoder.matches("secret123", "hashed_secret")).thenReturn(true);

        // Act
        Alumni auth = alumniService.authenticate("mohamed.trabelsi@email.tn", "secret123");

        // Assert
        assertNotNull(auth);
        assertEquals("Mohamed", auth.getFirstName());
    }

    @Test
    @DisplayName("TU-ALUMNI-04: Authentification d'un Alumni non vérifié échoue")
    void testAuthenticate_NotVerified_ThrowsException() {
        // Arrange
        alumni.setPassword("hashed_secret");
        alumni.setVerified(false);
        when(alumniRepository.findByEmail("mohamed.trabelsi@email.tn")).thenReturn(Optional.of(alumni));
        when(passwordEncoder.matches("secret123", "hashed_secret")).thenReturn(true);

        // Act & Assert
        Exception ex = assertThrows(RuntimeException.class, () -> alumniService.authenticate("mohamed.trabelsi@email.tn", "secret123"));
        assertEquals("Account not verified. Check your email.", ex.getMessage());
    }

    @Test
    @DisplayName("TU-ALUMNI-05: Validation du compte via Token de vérification")
    void testVerifyAlumni_ValidToken() {
        // Arrange
        alumni.setVerificationToken("valid-token-123");
        when(alumniRepository.findByVerificationToken("valid-token-123")).thenReturn(Optional.of(alumni));
        when(alumniRepository.save(any(Alumni.class))).thenAnswer(i -> i.getArgument(0));

        // Act
        Alumni verified = alumniService.verifyAlumni("valid-token-123");

        // Assert
        assertTrue(verified.isVerified());
        assertNull(verified.getVerificationToken());
    }

    @Test
    @DisplayName("TU-ALUMNI-06: Mise à jour des préférences de mentorat")
    void testUpdatePreferences() {
        // Arrange
        when(alumniRepository.findById(1L)).thenReturn(Optional.of(alumni));
        when(alumniRepository.save(any(Alumni.class))).thenAnswer(i -> i.getArgument(0));

        AlumniPreferencesDTO prefs = new AlumniPreferencesDTO();
        prefs.setOpenToMentoring(true);
        prefs.setWillingToRefer(true);
        prefs.setOpenToCareerAdvice(false);

        // Act
        Alumni updated = alumniService.updatePreferences(1L, prefs);

        // Assert
        assertTrue(updated.isOpenToMentoring());
        assertTrue(updated.isWillingToRefer());
        assertFalse(updated.isOpenToCareerAdvice());
    }

    @Test
    @DisplayName("TU-ALUMNI-07: Calcul de la note moyenne de mentorat")
    void testGetAverageRating() {
        // Arrange
        MentorshipEvaluation eval1 = new MentorshipEvaluation();
        eval1.setRating(5);
        MentorshipEvaluation eval2 = new MentorshipEvaluation();
        eval2.setRating(3);

        when(mentorshipEvaluationRepository.findByMentorshipRequestProposerId(1L))
                .thenReturn(List.of(eval1, eval2));

        // Act
        Double avg = alumniService.getAverageRating(1L);

        // Assert
        assertEquals(4.0, avg);
    }

    @Test
    @DisplayName("TU-ALUMNI-08: Statistiques de mentorat pour un alumni")
    void testGetMentorStats() {
        // Arrange
        alumni.setExpertise("Architectures Cloud & Microservices");
        alumni.setOpenToMentoring(true);
        when(alumniRepository.findById(1L)).thenReturn(Optional.of(alumni));

        MentorshipSession s1 = new MentorshipSession();
        when(mentorshipSessionRepository.findByMentorshipRequestProposerId(1L)).thenReturn(List.of(s1));

        MentorshipEvaluation eval = new MentorshipEvaluation();
        eval.setRating(4);
        when(mentorshipEvaluationRepository.findByMentorshipRequestProposerId(1L)).thenReturn(List.of(eval));

        // Act
        Map<String, Object> stats = alumniService.getMentorStats(1L);

        // Assert
        assertNotNull(stats);
        assertEquals(1L, stats.get("mentorId"));
        assertEquals(1, stats.get("totalSessions"));
        assertEquals(4.0, stats.get("averageRating"));
        assertEquals(1, stats.get("reviewCount"));
        assertEquals(true, stats.get("isAvailable"));
    }
}
