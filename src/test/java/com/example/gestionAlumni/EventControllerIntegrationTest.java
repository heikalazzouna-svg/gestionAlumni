package com.example.gestionAlumni;

import com.example.gestionAlumni.Entities.Event;
import com.example.gestionAlumni.Entities.EventType;
import com.example.gestionAlumni.Entities.User;
import com.example.gestionAlumni.Repos.EventRepository;
import com.example.gestionAlumni.Repos.UserRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
public class EventControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private EventRepository eventRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ObjectMapper objectMapper;

    private Event testEvent;
    private User studentUser;

    @BeforeEach
    void setUp() {
        studentUser = new User();
        studentUser.setFirstName("Cyrine");
        studentUser.setLastName("Khemir");
        studentUser.setEmail("cyrine@enicar.tn");
        studentUser.setPassword("pass");
        userRepository.save(studentUser);

        testEvent = new Event();
        testEvent.setTitle("Hackathon AI ENICarthage");
        testEvent.setDescription("Compétition de 24h sur les modèles de langage");
        testEvent.setType(EventType.HACKATHON);
        testEvent.setStartDate(LocalDateTime.now().plusDays(10));
        testEvent.setEndDate(LocalDateTime.now().plusDays(11));
        testEvent.setSeats(50);
        testEvent.setRegisteredCount(0);
        testEvent.setParticipants(new ArrayList<>());
        testEvent.setStatus("APPROVED");
        testEvent.setCreatedByRole("ADMIN");
        eventRepository.save(testEvent);
    }

    @Test
    @DisplayName("TI-EVENT-01: POST /api/events - Création d'événement (Code 200)")
    void testCreateEvent() throws Exception {
        Event newEvent = new Event();
        newEvent.setTitle("Workshop Spring Boot & Docker");
        newEvent.setDescription("Atelier pratique");
        newEvent.setType(EventType.WORKSHOP);
        newEvent.setStartDate(LocalDateTime.now().plusDays(2));
        newEvent.setEndDate(LocalDateTime.now().plusDays(2).plusHours(3));
        newEvent.setSeats(30);
        newEvent.setCreatedByRole("ADMIN");

        mockMvc.perform(post("/api/events")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(newEvent)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title", is("Workshop Spring Boot & Docker")))
                .andExpect(jsonPath("$.status", is("APPROVED")));
    }

    @Test
    @DisplayName("TI-EVENT-02: GET /api/events/all - Récupération de tous les événements (Code 200)")
    void testGetAllEvents() throws Exception {
        mockMvc.perform(get("/api/events/all"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(greaterThanOrEqualTo(1))))
                .andExpect(jsonPath("$[?(@.title == 'Hackathon AI ENICarthage')]", hasSize(1)));
    }

    @Test
    @DisplayName("TI-EVENT-03: GET /api/events/{id} - Consultation d'un événement par ID (Code 200)")
    void testGetEventById() throws Exception {
        mockMvc.perform(get("/api/events/" + testEvent.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title", is("Hackathon AI ENICarthage")))
                .andExpect(jsonPath("$.type", is("HACKATHON")));
    }

    @Test
    @DisplayName("TI-EVENT-04: POST /api/events/{id}/register/{userId} - Inscription d'un participant (Code 200)")
    void testRegisterParticipant() throws Exception {
        mockMvc.perform(post("/api/events/" + testEvent.getId() + "/register/" + studentUser.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.registeredCount", is(1)));
    }

    @Test
    @DisplayName("TI-EVENT-05: PUT /api/events/{id}/approve - Approbation d'un événement (Code 200)")
    void testApproveEvent() throws Exception {
        testEvent.setStatus("PENDING");
        eventRepository.save(testEvent);

        mockMvc.perform(put("/api/events/" + testEvent.getId() + "/approve"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status", is("APPROVED")));
    }

    @Test
    @DisplayName("TI-EVENT-06: DELETE /api/events/{id} - Suppression d'un événement (Code 200)")
    void testDeleteEvent() throws Exception {
        mockMvc.perform(delete("/api/events/" + testEvent.getId()))
                .andExpect(status().isOk());
    }
}
