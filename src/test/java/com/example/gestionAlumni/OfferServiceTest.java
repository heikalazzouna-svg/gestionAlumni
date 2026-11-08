package com.example.gestionAlumni;

import com.example.gestionAlumni.DTO.OffreDTO;
import com.example.gestionAlumni.Entities.Alumni;
import com.example.gestionAlumni.Entities.Offer;
import com.example.gestionAlumni.Repos.AlumniRepository;
import com.example.gestionAlumni.Repos.OfferRepository;
import com.example.gestionAlumni.Services.OfferService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class OfferServiceTest {

    @Mock
    private OfferRepository offerRepository;

    @Mock
    private AlumniRepository alumniRepository;

    @InjectMocks
    private OfferService offerService;

    private Alumni alumni;
    private Offer offer;

    @BeforeEach
    void setUp() {
        alumni = new Alumni();
        alumni.setId(2L);
        alumni.setFirstName("Sarra");
        alumni.setLastName("Ben Ali");

        offer = new Offer();
        offer.setId(50L);
        offer.setTitle("Stage Ingénieur Cloud & DevOps");
        offer.setDescription("Recherche d'un stagiaire PFE pour mise en place CI/CD");
        offer.setType("internship");
        offer.setCompany("Orange Tunisie");
        offer.setProposedSalary(1200L);
        offer.setCreator(alumni);
    }

    @Test
    @DisplayName("TU-OFFER-01: Création d'une offre par un Alumni")
    void testCreateOffer_Success() {
        // Arrange
        OffreDTO dto = new OffreDTO();
        dto.setTitle("Stage Ingénieur Cloud & DevOps");
        dto.setDescription("Recherche d'un stagiaire PFE");
        dto.setType("internship");
        dto.setCompany("Orange Tunisie");
        dto.setDuration("6 mois");
        dto.setPosition("DevOps Intern");
        dto.setProposedSalary(1200L);

        when(alumniRepository.findById(2L)).thenReturn(Optional.of(alumni));
        when(offerRepository.save(any(Offer.class))).thenAnswer(i -> i.getArgument(0));

        // Act
        Offer created = offerService.createOffer(2L, dto);

        // Assert
        assertNotNull(created);
        assertEquals("Stage Ingénieur Cloud & DevOps", created.getTitle());
        assertEquals(alumni, created.getCreator());
        verify(offerRepository).save(any(Offer.class));
    }

    @Test
    @DisplayName("TU-OFFER-02: Création d'offre avec un créateur inexistant lance une exception")
    void testCreateOffer_AlumniNotFound_ThrowsException() {
        // Arrange
        OffreDTO dto = new OffreDTO();
        when(alumniRepository.findById(999L)).thenReturn(Optional.empty());

        // Act & Assert
        Exception ex = assertThrows(RuntimeException.class, () -> offerService.createOffer(999L, dto));
        assertEquals("Alumni not found", ex.getMessage());
        verify(offerRepository, never()).save(any());
    }

    @Test
    @DisplayName("TU-OFFER-03: Recherche d'offres par titre")
    void testSearchOffersByTitle() {
        // Arrange
        when(offerRepository.findByTitleContainingIgnoreCaseOrderByCreatedAtDesc("DevOps"))
                .thenReturn(List.of(offer));

        // Act
        List<Offer> results = offerService.searchOffersByTitle("DevOps");

        // Assert
        assertEquals(1, results.size());
        assertEquals("Stage Ingénieur Cloud & DevOps", results.get(0).getTitle());
    }

    @Test
    @DisplayName("TU-OFFER-04: Récupération d'une offre par son ID")
    void testGetOfferById_Success() {
        // Arrange
        when(offerRepository.findById(50L)).thenReturn(Optional.of(offer));

        // Act
        Offer found = offerService.getOfferById(50L);

        // Assert
        assertNotNull(found);
        assertEquals(50L, found.getId());
    }
}
