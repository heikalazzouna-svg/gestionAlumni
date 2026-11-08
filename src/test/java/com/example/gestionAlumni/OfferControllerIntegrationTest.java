package com.example.gestionAlumni;

import com.example.gestionAlumni.DTO.OffreDTO;
import com.example.gestionAlumni.Entities.Alumni;
import com.example.gestionAlumni.Entities.Offer;
import com.example.gestionAlumni.Repos.AlumniRepository;
import com.example.gestionAlumni.Repos.OfferRepository;
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

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
public class OfferControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private OfferRepository offerRepository;

    @Autowired
    private AlumniRepository alumniRepository;

    @Autowired
    private ObjectMapper objectMapper;

    private Alumni publisher;
    private Offer offer;

    @BeforeEach
    void setUp() {
        publisher = new Alumni();
        publisher.setFirstName("Kais");
        publisher.setLastName("Saidi");
        publisher.setEmail("kais@alumni.tn");
        publisher.setPassword("pass");
        alumniRepository.save(publisher);

        offer = new Offer();
        offer.setTitle("Ingénieur DevOps Junior");
        offer.setDescription("Offre de CDI chez Telnet");
        offer.setType("job");
        offer.setCompany("Telnet");
        offer.setProposedSalary(2000L);
        offer.setCreator(publisher);
        offerRepository.save(offer);
    }

    @Test
    @DisplayName("TI-OFFER-01: GET /api/offers - Récupération des offres triées par date (Code 200)")
    void testGetAllOffers() throws Exception {
        mockMvc.perform(get("/api/offers"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(greaterThanOrEqualTo(1))))
                .andExpect(jsonPath("$[?(@.title == 'Ingénieur DevOps Junior')]", hasSize(1)));
    }

    @Test
    @DisplayName("TI-OFFER-02: GET /api/offers/search?title=... - Recherche par titre (Code 200)")
    void testSearchOffers() throws Exception {
        mockMvc.perform(get("/api/offers/search").param("title", "DevOps"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(greaterThanOrEqualTo(1))))
                .andExpect(jsonPath("$[0].company", is("Telnet")));
    }

    @Test
    @DisplayName("TI-OFFER-03: GET /api/offers/{id} - Consultation d'une offre existante (Code 200)")
    void testGetOfferById_Found() throws Exception {
        mockMvc.perform(get("/api/offers/" + offer.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title", is("Ingénieur DevOps Junior")))
                .andExpect(jsonPath("$.proposedSalary", is(2000)));
    }

    @Test
    @DisplayName("TI-OFFER-04: GET /api/offers/{id} - Offre inexistante retourne 404")
    void testGetOfferById_NotFound() throws Exception {
        mockMvc.perform(get("/api/offers/99999"))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("TI-OFFER-05: POST /api/offers/{id}/offers - Publication d'une nouvelle offre (Code 200)")
    void testCreateOffer() throws Exception {
        OffreDTO dto = new OffreDTO();
        dto.setTitle("Stage Data Analyst");
        dto.setDescription("Mission de 6 mois");
        dto.setType("internship");
        dto.setCompany("BIAT");
        dto.setPosition("Data Intern");
        dto.setProposedSalary(800L);

        mockMvc.perform(post("/api/offers/" + publisher.getId() + "/offers")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title", is("Stage Data Analyst")))
                .andExpect(jsonPath("$.company", is("BIAT")));
    }
}
