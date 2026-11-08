package com.example.gestionAlumni;

import com.example.gestionAlumni.Entities.Conversation;
import com.example.gestionAlumni.Entities.Message;
import com.example.gestionAlumni.Entities.User;
import com.example.gestionAlumni.Repos.ConversationRepository;
import com.example.gestionAlumni.Repos.MessageRepository;
import com.example.gestionAlumni.Repos.UserRepository;
import com.example.gestionAlumni.Services.MessageService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class MessageServiceTest {

    @Mock
    private MessageRepository messageRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private ConversationRepository conversationRepository;

    @InjectMocks
    private MessageService messageService;

    private User sender;
    private User receiver;

    @BeforeEach
    void setUp() {
        sender = new User();
        sender.setId(1L);
        sender.setFirstName("Heikal");
        sender.setLastName("Azzouna");

        receiver = new User();
        receiver.setId(2L);
        receiver.setFirstName("Issam");
        receiver.setLastName("Chatbouri");
    }

    @Test
    @DisplayName("TU-MSG-01: Envoi de message et création d'une nouvelle conversation")
    void testSendMessage_CreatesNewConversation_AndSavesMessage() {
        // Arrange
        when(userRepository.findById(1L)).thenReturn(Optional.of(sender));
        when(userRepository.findById(2L)).thenReturn(Optional.of(receiver));
        when(conversationRepository.findByUsers(sender, receiver)).thenReturn(Optional.empty());

        Conversation newConversation = new Conversation();
        newConversation.setUser1(sender);
        newConversation.setUser2(receiver);

        when(conversationRepository.save(any(Conversation.class))).thenReturn(newConversation);
        when(messageRepository.save(any(Message.class))).thenAnswer(i -> i.getArgument(0));

        // Act
        Message result = messageService.sendMessage(1L, 2L, "Bonjour, je cherche des informations sur le PFE");

        // Assert
        assertNotNull(result);
        assertEquals("Bonjour, je cherche des informations sur le PFE", result.getContent());
        assertEquals(sender, result.getSender());
        assertEquals(receiver, result.getReceiver());
        assertNotNull(result.getSentDate());
        assertEquals(newConversation, result.getConversation());
        verify(messageRepository, times(1)).save(any(Message.class));
    }

    @Test
    @DisplayName("TU-MSG-02: Envoi de message avec expéditeur inexistant lance une exception")
    void testSendMessage_SenderNotFound_ThrowsException() {
        // Arrange
        when(userRepository.findById(99L)).thenReturn(Optional.empty());

        // Act & Assert
        Exception ex = assertThrows(RuntimeException.class, () -> messageService.sendMessage(99L, 2L, "Hello"));
        assertEquals("Sender not found", ex.getMessage());
        verify(messageRepository, never()).save(any());
    }

    @Test
    @DisplayName("TU-MSG-03: Récupération de l'historique des messages entre deux utilisateurs")
    void testGetMessagesBetweenUsers_Success() {
        // Arrange
        Conversation conv = new Conversation();
        conv.setId(10L);
        conv.setUser1(sender);
        conv.setUser2(receiver);

        Message m1 = new Message();
        m1.setContent("Salut !");
        m1.setSentDate(LocalDateTime.now().minusMinutes(5));
        m1.setConversation(conv);

        when(userRepository.findById(1L)).thenReturn(Optional.of(sender));
        when(userRepository.findById(2L)).thenReturn(Optional.of(receiver));
        when(conversationRepository.findByUsers(sender, receiver)).thenReturn(Optional.of(conv));
        when(messageRepository.findByConversationOrderBySentDateAsc(conv)).thenReturn(List.of(m1));

        // Act
        List<Message> messages = messageService.getMessagesBetweenUsers(1L, 2L);

        // Assert
        assertEquals(1, messages.size());
        assertEquals("Salut !", messages.get(0).getContent());
    }

    @Test
    @DisplayName("TU-MSG-04: Récupération de messages sans conversation retourne liste vide")
    void testGetMessagesBetweenUsers_NoConversation_ReturnsEmptyList() {
        // Arrange
        when(userRepository.findById(1L)).thenReturn(Optional.of(sender));
        when(userRepository.findById(2L)).thenReturn(Optional.of(receiver));
        when(conversationRepository.findByUsers(sender, receiver)).thenReturn(Optional.empty());

        // Act
        List<Message> messages = messageService.getMessagesBetweenUsers(1L, 2L);

        // Assert
        assertTrue(messages.isEmpty());
        verify(messageRepository, never()).findByConversationOrderBySentDateAsc(any());
    }

    @Test
    @DisplayName("TU-MSG-05: Marquer les messages comme lus")
    void testMarkAsRead() {
        // Act
        messageService.markAsRead(10L, 1L);

        // Assert
        verify(messageRepository).markConversationAsRead(10L, 1L);
    }

    @Test
    @DisplayName("TU-MSG-06: Compteur des messages non lus")
    void testGetUnreadCount() {
        // Arrange
        when(messageRepository.countUnreadForUser(1L)).thenReturn(4L);

        // Act
        long count = messageService.getUnreadCount(1L);

        // Assert
        assertEquals(4L, count);
    }
}
