package com.example.gestionAlumni;

import com.example.gestionAlumni.Entities.Alumni;
import com.example.gestionAlumni.Repos.AlumniRepository;
import com.example.gestionAlumni.Services.EmailService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.Map;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
public class AlumniControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private AlumniRepository alumniRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private EmailService emailService;

    private Alumni verifiedAlumni;

    @BeforeEach
    void setUp() {
        verifiedAlumni = new Alumni();
        verifiedAlumni.setFirstName("Mehdi");
        verifiedAlumni.setLastName("Driss");
        verifiedAlumni.setEmail("mehdi.driss@alumni.tn");
        verifiedAlumni.setPassword(passwordEncoder.encode("alumniPass123"));
        verifiedAlumni.setCurrentCompany("Expensya");
        verifiedAlumni.setCurrentJob("Lead Tech");
        verifiedAlumni.setSpeciality("Génie Logiciel");
        verifiedAlumni.setVerified(true);
        verifiedAlumni.setActive(true);
        alumniRepository.save(verifiedAlumni);
    }

    @Test
    @DisplayName("TI-ALUMNI-01: POST /api/alumni/signup - Inscription nouvel alumni (Code 200)")
    void testSignup_Success() throws Exception {
        Map<String, Object> newAlumni = new HashMap<>();
        newAlumni.put("firstName", "Ines");
        newAlumni.put("lastName", "Jemli");
        newAlumni.put("email", "ines.jemli@alumni.tn");
        newAlumni.put("password", "inesPass123");
        newAlumni.put("speciality", "Data Science");

        mockMvc.perform(post("/api/alumni/signup")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(newAlumni)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.email", is("ines.jemli@alumni.tn")))
                .andExpect(jsonPath("$.verified", is(false)));
    }

    @Test
    @DisplayName("TI-ALUMNI-02: POST /api/alumni/signup - Email en double rejeté (Code 400)")
    void testSignup_DuplicateEmail() throws Exception {
        Map<String, Object> duplicate = new HashMap<>();
        duplicate.put("firstName", "Mehdi");
        duplicate.put("lastName", "Driss");
        duplicate.put("email", "mehdi.driss@alumni.tn");
        duplicate.put("password", "pass");

        mockMvc.perform(post("/api/alumni/signup")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(duplicate)))
                .andExpect(status().isBadRequest())
                .andExpect(content().string(containsString("Email already in use")));
    }

    @Test
    @DisplayName("TI-ALUMNI-03: POST /api/alumni/login - Connexion alumni vérifié réussie (Code 200)")
    void testLogin_Success() throws Exception {
        Map<String, String> loginPayload = new HashMap<>();
        loginPayload.put("email", "mehdi.driss@alumni.tn");
        loginPayload.put("password", "alumniPass123");

        mockMvc.perform(post("/api/alumni/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginPayload)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.email", is("mehdi.driss@alumni.tn")))
                .andExpect(jsonPath("$.currentCompany", is("Expensya")));
    }

    @Test
    @DisplayName("TI-ALUMNI-04: GET /api/alumni/directory - Consultation de l'annuaire (Code 200)")
    void testGetDirectory() throws Exception {
        mockMvc.perform(get("/api/alumni/directory"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(greaterThanOrEqualTo(1))))
                .andExpect(jsonPath("$[?(@.email == 'mehdi.driss@alumni.tn')].currentJob", contains("Lead Tech")));
    }

    @Test
    @DisplayName("TI-ALUMNI-05: GET /api/alumni/directory/{id} - Consultation fiche alumni (Code 200)")
    void testGetAlumniById() throws Exception {
        mockMvc.perform(get("/api/alumni/directory/" + verifiedAlumni.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.firstName", is("Mehdi")))
                .andExpect(jsonPath("$.currentCompany", is("Expensya")));
    }
}
