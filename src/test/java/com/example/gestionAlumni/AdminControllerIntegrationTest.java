package com.example.gestionAlumni;

import com.example.gestionAlumni.Entities.Administrator;
import com.example.gestionAlumni.Entities.Alumni;
import com.example.gestionAlumni.Entities.Student;
import com.example.gestionAlumni.Repos.AdminRepository;
import com.example.gestionAlumni.Repos.AlumniRepository;
import com.example.gestionAlumni.Repos.StudentRepository;
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
public class AdminControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private AdminRepository adminRepository;

    @Autowired
    private AlumniRepository alumniRepository;

    @Autowired
    private StudentRepository studentRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private EmailService emailService;

    private Administrator admin;
    private Alumni pendingAlumni;

    @BeforeEach
    void setUp() {
        admin = new Administrator();
        admin.setFirstName("Super");
        admin.setLastName("Admin");
        admin.setEmail("admin.test@enicar.tn");
        admin.setPassword(passwordEncoder.encode("adminPass123"));
        admin.setActive(true);
        adminRepository.save(admin);

        pendingAlumni = new Alumni();
        pendingAlumni.setFirstName("Farouk");
        pendingAlumni.setLastName("Gharbi");
        pendingAlumni.setEmail("farouk@alumni.tn");
        pendingAlumni.setPassword(passwordEncoder.encode("secret"));
        pendingAlumni.setVerified(false);
        alumniRepository.save(pendingAlumni);
    }

    @Test
    @DisplayName("TI-ADMIN-01: POST /api/admin/login - Authentification réussie (Code 200)")
    void testAdminLogin_Success() throws Exception {
        Map<String, String> loginPayload = new HashMap<>();
        loginPayload.put("email", "admin.test@enicar.tn");
        loginPayload.put("password", "adminPass123");

        mockMvc.perform(post("/api/admin/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginPayload)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.email", is("admin.test@enicar.tn")))
                .andExpect(jsonPath("$.role", is("admin")))
                .andExpect(jsonPath("$.active", is(true)));
    }

    @Test
    @DisplayName("TI-ADMIN-02: POST /api/admin/login - Échec identifiants invalides (Code 400)")
    void testAdminLogin_BadCredentials() throws Exception {
        Map<String, String> loginPayload = new HashMap<>();
        loginPayload.put("email", "admin.test@enicar.tn");
        loginPayload.put("password", "wrong_password");

        mockMvc.perform(post("/api/admin/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginPayload)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("TI-ADMIN-03: GET /api/admin/alumni/pending - Récupération de la liste en attente (Code 200)")
    void testGetPendingAlumni() throws Exception {
        mockMvc.perform(get("/api/admin/alumni/pending"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(greaterThanOrEqualTo(1))))
                .andExpect(jsonPath("$[?(@.email == 'farouk@alumni.tn')].verified", contains(false)));
    }

    @Test
    @DisplayName("TI-ADMIN-04: GET /api/admin/students - Récupération des étudiants (Code 200)")
    void testGetAllStudents() throws Exception {
        Student s = new Student();
        s.setFirstName("Rania");
        s.setLastName("Mansour");
        s.setEmail("rania@etudiant.tn");
        s.setPassword(passwordEncoder.encode("pass"));
        s.setSpeciality("Informatique Industrielle");
        studentRepository.save(s);

        mockMvc.perform(get("/api/admin/students"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[?(@.email == 'rania@etudiant.tn')].speciality", contains("Informatique Industrielle")));
    }

    @Test
    @DisplayName("TI-ADMIN-05: PUT /api/admin/approve/{id} - Approbation d'un alumni (Code 200)")
    void testApproveAlumni() throws Exception {
        mockMvc.perform(put("/api/admin/approve/" + pendingAlumni.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.verified", is(true)));
    }

    @Test
    @DisplayName("TI-ADMIN-06: DELETE /api/admin/reject/{id} - Rejet d'un alumni (Code 200)")
    void testRejectAlumni() throws Exception {
        mockMvc.perform(delete("/api/admin/reject/" + pendingAlumni.getId()))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("rejected")));
    }
}
