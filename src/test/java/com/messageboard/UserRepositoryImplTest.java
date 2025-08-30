//package com.messageboard;
//
//import com.messageboard.entity.User;
//import org.junit.jupiter.api.Test;
//import org.junit.jupiter.api.extension.ExtendWith;
//import org.mockito.InjectMocks;
//import org.mockito.Mock;
//import org.mockito.junit.jupiter.MockitoExtension;
//import com.messageboard.repository.UserRepository;
//import com.messageboard.repository.UserService;
//import org.springframework.data.domain.Page;
//import org.springframework.data.domain.PageImpl;
//import org.springframework.data.domain.PageRequest;
//import org.springframework.data.domain.Pageable;
//
//import java.util.Collections;
//import java.util.List;
//
//import static org.junit.jupiter.api.Assertions.*;
//import static org.mockito.ArgumentMatchers.any;
//import static org.mockito.Mockito.*;
//
//@ExtendWith(MockitoExtension.class)
//class UserRepositoryImplTest {
//
//    @Mock
//    private UserRepository repository;
//
//    @InjectMocks
//    private UserService userRepoImpl;
//
//    private User createTestUser(String email) {
//        User user = new User();
//        user.setEmail(email);
//        user.setUserName("Test User");
//        user.setPassword("password");
//        return user;
//    }
//
//
//
//
//
//
//    @Test
//    void findAll_ShouldReturnEmptyList_WhenNoUsers() {
//
//        when(repository.findAll()).thenReturn(Collections.emptyList());
//
//        List<User> result = userRepoImpl.findAll();
//
//        // Assert
//        verify(repository).findAll();
//        assertTrue(result.isEmpty());
//    }
//
//
//    @Test
//    void findAllPageable_ShouldReturnEmptyPage_WhenNoUsers() {
//
//        Pageable pageable = PageRequest.of(0, 10);
//        Page<User> emptyPage = new PageImpl<>(Collections.emptyList(), pageable, 0);
//
//        when(repository.findAll(pageable)).thenReturn(emptyPage);
//
//        Page<User> result = userRepoImpl.findAllPageable(pageable);
//
//        // Assert
//        assertNotNull(result);
//        assertTrue(result.getContent().isEmpty());
//        assertEquals(0, result.getTotalElements());
//
//        verify(repository).findAll(pageable);
//    }
//
//
//
//    @Test
//    void delete_ShouldHandleNullUser() {
//
//        User user = null;
//        doThrow(new IllegalArgumentException("Entity must not be null")).when(repository).delete(user);
//        // Act & Assert
//        assertThrows(IllegalArgumentException.class, () -> {
//            userRepoImpl.delete(user);
//        });
//        verify(repository, times(1)).delete(user);
//    }
//}