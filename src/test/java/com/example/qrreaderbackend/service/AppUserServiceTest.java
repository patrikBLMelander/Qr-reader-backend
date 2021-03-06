package com.example.qrreaderbackend.service;

/**
 * Created by Patrik Melander
 * Date: 2021-06-16
 * Time: 13:34
 * Project: Qr-reader-backend
 * Copyright: MIT
 */


import com.example.qrreaderbackend.DTO.AppUserResponse;
import com.example.qrreaderbackend.model.AppUser;
import com.example.qrreaderbackend.repository.AppUserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.server.ResponseStatusException;


import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class AppUserServiceTest {

    @Mock
    AppUserRepository mockAppUserRepository;



    AppUserService appUserService;

    List<AppUser> mockList;


    AppUser mockUser1;
    AppUser mockUser2;
    AppUser mockUser3;


    @BeforeEach
    void init() {


        mockUser1 = new AppUser()
                .setFirstName("Mickey")
                .setLastName("Mouse")
                .setPassword("mickey123")
                .setEmail("mickey@email.com");
        mockUser2 = new AppUser()
                .setFirstName("Donald")
                .setLastName("Duck")
                .setPassword("donald123")
                .setEmail("donald@email.com");
        mockUser3 = new AppUser()
                .setFirstName("Goofy")
                .setLastName("Goof")
                .setPassword("goofy123")
                .setEmail("goofy@email.com");


        mockList = Arrays.asList(mockUser1,mockUser2,mockUser3);


        appUserService = new AppUserService(mockAppUserRepository);
    }


    @Test
    void getAllUsersTest() {

        int notCorrectListSize = 1000;

        when(mockAppUserRepository.findAll())
                .thenReturn(mockList);


        List<AppUserResponse> expected = mockList.stream()
                .map(AppUser::toResponse)
                .collect(Collectors.toList());

        List<AppUserResponse> actual = appUserService.getAll();

        assertEquals(actual.size(),expected.size());

        assertNotEquals(actual.size(), notCorrectListSize);

        assertEquals(actual.get(0).getEmail(),expected.get(0).getEmail());

        assertEquals(actual.get(1).getEmail(),expected.get(1).getEmail());

        assertEquals(actual.get(2).getEmail(),expected.get(2).getEmail());

        assertNotEquals(actual.get(0).getEmail(), expected.get(1).getEmail());

        assertThrows(IndexOutOfBoundsException.class, () -> actual.get(10));

        verify(mockAppUserRepository, times(1))
                .findAll();
    }

    @Test
    void updatePasswordTest() {

        String newPassword = "newPassword";
        String wrongPassword = "thisPasswordIsForTestingWrongPassword";

        when(mockAppUserRepository.findByEmail(mockUser1.getEmail()))
                .thenReturn(java.util.Optional.ofNullable(mockUser1));

        when(mockAppUserRepository.save(mockUser1))
                .thenReturn(mockUser1.setPassword(newPassword));

        AppUserResponse expected = mockUser1.setPassword(newPassword)
                .toResponse();

        AppUserResponse actual = appUserService.updatePassword(newPassword,mockUser1.getEmail());

        assertEquals(expected.getPassword(), actual.getPassword());

        assertNotEquals(wrongPassword, actual.getPassword());

        verify(mockAppUserRepository, times(1))
                .save(any());

        verify(mockAppUserRepository, times(1))
                .findByEmail(anyString());
    }




    @Test
    void deleteUserTest() {

        String email = "mickey@email.com";
        String wrongmail = "wrong@email.com";
        String password = "mickey123";
        String wrongPassword = "wrong123";

        when(mockAppUserRepository.findByEmail(mockUser1.getEmail()))
                .thenReturn(java.util.Optional.ofNullable(mockUser1));

        assertEquals("User with email: " + email + " has been deleted", appUserService.deleteAppUser(email, password));

        assertThrows(ResponseStatusException.class, () -> appUserService.deleteAppUser(email, wrongPassword));

        assertThrows(ResponseStatusException.class, () -> appUserService.deleteAppUser(wrongmail, password));

        verify(mockAppUserRepository, times(1))
                .deleteById(any());
    }



    @Test
    void addingNewDuplicateUserThrowsTest() {
        when(mockAppUserRepository.findByEmail(mockUser1.getEmail()))
                .thenReturn(Optional.of(mockUser1));

        assertThrows(ResponseStatusException.class, () -> appUserService.addAppUser(mockUser1));

        verify(mockAppUserRepository, times(1)).findByEmail(anyString());
    }

}

