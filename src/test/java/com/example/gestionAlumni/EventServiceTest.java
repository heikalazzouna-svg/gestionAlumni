package com.example.gestionAlumni;

import com.example.gestionAlumni.Entities.Event;
import com.example.gestionAlumni.Entities.EventType;
import com.example.gestionAlumni.Entities.User;
import com.example.gestionAlumni.Repos.EventRepository;
import com.example.gestionAlumni.Repos.UserRepository;
import com.example.gestionAlumni.Services.EventService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class EventServiceTest {

    @Mock
    private EventRepository eventRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private EventService eventService;

    private Event event;
    private User participant;

    @BeforeEach
    void setUp() {
        event = new Event();
        event.setId(100L);
        event.setTitle("Forum ENICarthage 2026");
        event.setDescription("Forum annuel de recrutement et de partenariats");
        event.setType(EventType.FORUM);
        event.setStartDate(LocalDateTime.now().plusDays(5));
        event.setEndDate(LocalDateTime.now().plusDays(6));
        event.setSeats(100);
        event.setRegisteredCount(0);
        event.setParticipants(new ArrayList<>());
        event.setStatus("APPROVED");

        participant = new User();
        participant.setId(15L);
        participant.setFirstName("Amine");
        participant.setLastName("Kallel");
        participant.setEmail("amine@enicar.tn");
    }

    @Test
    @DisplayName("TU-EVENT-01: Création d'un événement par un Admin -> statut APPROVED direct")
    void testCreateEvent_ByAdmin_Approved() {
        // Arrange
        event.setCreatedByRole("ADMIN");
        when(eventRepository.save(any(Event.class))).thenAnswer(i -> i.getArgument(0));

        // Act
        Event created = eventService.createEvent(event);

        // Assert
        assertEquals("APPROVED", created.getStatus());
        verify(eventRepository).save(event);
    }

    @Test
    @DisplayName("TU-EVENT-02: Création d'un événement par un Recruteur -> statut PENDING pour validation")
    void testCreateEvent_ByRecruiter_Pending() {
        // Arrange
        event.setCreatedByRole("RECRUITER");
        when(eventRepository.save(any(Event.class))).thenAnswer(i -> i.getArgument(0));

        // Act
        Event created = eventService.createEvent(event);

        // Assert
        assertEquals("PENDING", created.getStatus());
        verify(eventRepository).save(event);
    }

    @Test
    @DisplayName("TU-EVENT-03: Inscription d'un participant avec succès")
    void testRegisterParticipant_Success() {
        // Arrange
        when(eventRepository.findById(100L)).thenReturn(Optional.of(event));
        when(userRepository.findById(15L)).thenReturn(Optional.of(participant));
        when(eventRepository.save(any(Event.class))).thenAnswer(i -> i.getArgument(0));

        // Act
        Event updated = eventService.registerParticipant(100L, 15L);

        // Assert
        assertEquals(1, updated.getRegisteredCount());
        assertTrue(updated.getParticipants().contains(participant));
    }

    @Test
    @DisplayName("TU-EVENT-04: Inscription échoue quand l'événement est complet (Places = Max)")
    void testRegisterParticipant_EventFull_ThrowsException() {
        // Arrange
        event.setSeats(2);
        event.setRegisteredCount(2);
        when(eventRepository.findById(100L)).thenReturn(Optional.of(event));
        when(userRepository.findById(15L)).thenReturn(Optional.of(participant));

        // Act & Assert
        Exception ex = assertThrows(RuntimeException.class, () -> eventService.registerParticipant(100L, 15L));
        assertEquals("Event is full", ex.getMessage());
        verify(eventRepository, never()).save(any());
    }

    @Test
    @DisplayName("TU-EVENT-05: Inscription en double refusée")
    void testRegisterParticipant_AlreadyRegistered_ThrowsException() {
        // Arrange
        event.getParticipants().add(participant);
        event.setRegisteredCount(1);
        when(eventRepository.findById(100L)).thenReturn(Optional.of(event));
        when(userRepository.findById(15L)).thenReturn(Optional.of(participant));

        // Act & Assert
        Exception ex = assertThrows(RuntimeException.class, () -> eventService.registerParticipant(100L, 15L));
        assertEquals("Already registered", ex.getMessage());
    }

    @Test
    @DisplayName("TU-EVENT-06: Désinscription d'un participant")
    void testUnregisterParticipant_Success() {
        // Arrange
        event.getParticipants().add(participant);
        event.setRegisteredCount(1);
        when(eventRepository.findById(100L)).thenReturn(Optional.of(event));
        when(eventRepository.save(any(Event.class))).thenAnswer(i -> i.getArgument(0));

        // Act
        Event updated = eventService.unregisterParticipant(100L, 15L);

        // Assert
        assertEquals(0, updated.getRegisteredCount());
        assertFalse(updated.getParticipants().contains(participant));
    }

    @Test
    @DisplayName("TU-EVENT-07: Approbation et rejet d'un événement")
    void testApproveAndRejectEvent() {
        // Arrange
        when(eventRepository.findById(100L)).thenReturn(Optional.of(event));
        when(eventRepository.save(any(Event.class))).thenAnswer(i -> i.getArgument(0));

        // Act - Approve
        Event approved = eventService.approveEvent(100L);
        assertEquals("APPROVED", approved.getStatus());

        // Act - Reject
        Event rejected = eventService.rejectEvent(100L);
        assertEquals("REJECTED", rejected.getStatus());
    }
}
