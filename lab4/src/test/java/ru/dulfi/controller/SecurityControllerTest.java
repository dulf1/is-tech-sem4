package ru.dulfi.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import ru.dulfi.domain.Owner;
import ru.dulfi.domain.Pet;
import ru.dulfi.domain.PetColor;
import ru.dulfi.dto.PetDTO;
import ru.dulfi.service.PetService;
import ru.dulfi.service.OwnerService;
import ru.dulfi.service.PetAccessService;
import ru.dulfi.exception.GlobalExceptionHandler;
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors;
import org.springframework.security.access.AccessDeniedException;

import java.time.LocalDate;
import java.util.Collections;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest({PetController.class, OwnerController.class})
@Import({PetController.class, OwnerController.class, GlobalExceptionHandler.class})
public class SecurityControllerTest {

    @Configuration
    static class TestConfig {
    }

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private PetService petService;

    @MockBean
    private OwnerService ownerService;

    @MockBean
    private PetAccessService petAccessService;

    @Test
    public void testUnauthenticatedAccess() throws Exception {
        mockMvc.perform(get("/api/pets"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @WithMockUser(roles = "USER")
    public void testAuthenticatedUserAccess() throws Exception {
        Pet pet = createTestPet();
        when(petService.getAll(any())).thenReturn(new org.springframework.data.domain.PageImpl<>(Collections.singletonList(pet)));

        mockMvc.perform(get("/api/pets")
                .with(SecurityMockMvcRequestPostProcessors.csrf()))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    public void testAdminAccessToAllPets() throws Exception {
        Pet pet = createTestPet();
        when(petService.getAll(any())).thenReturn(new org.springframework.data.domain.PageImpl<>(Collections.singletonList(pet)));

        mockMvc.perform(get("/api/pets")
                .with(SecurityMockMvcRequestPostProcessors.csrf()))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(username = "user1", roles = "USER")
    public void testUserAccessToOwnPet() throws Exception {
        Pet pet = createTestPet();
        when(petService.getById(1L)).thenReturn(pet);
        doNothing().when(petAccessService).checkAccess(any(Pet.class));

        mockMvc.perform(get("/api/pets/1")
                .with(SecurityMockMvcRequestPostProcessors.csrf()))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(username = "user2", roles = "USER")
    public void testUserAccessToOtherPet() throws Exception {
        Pet pet = createTestPet();
        when(petService.getById(1L)).thenReturn(pet);
        doThrow(new AccessDeniedException("Доступ запрещен")).when(petAccessService).checkAccess(any(Pet.class));

        mockMvc.perform(get("/api/pets/1")
                .with(SecurityMockMvcRequestPostProcessors.csrf()))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    public void testAdminAccessToAnyPet() throws Exception {
        Pet pet = createTestPet();
        when(petService.getById(1L)).thenReturn(pet);
        doNothing().when(petAccessService).checkAccess(any(Pet.class));

        mockMvc.perform(get("/api/pets/1")
                .with(SecurityMockMvcRequestPostProcessors.csrf()))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(roles = "USER")
    public void testUserCreatePet() throws Exception {
        PetDTO petDTO = createTestPetDTO();
        Pet pet = createTestPet();
        when(ownerService.getById(1L)).thenReturn(pet.getOwner());
        when(petService.save(any())).thenReturn(pet);

        mockMvc.perform(post("/api/pets")
                .with(SecurityMockMvcRequestPostProcessors.csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(petDTO)))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    public void testAdminCreatePet() throws Exception {
        PetDTO petDTO = createTestPetDTO();
        Pet pet = createTestPet();
        when(ownerService.getById(1L)).thenReturn(pet.getOwner());
        when(petService.save(any())).thenReturn(pet);

        mockMvc.perform(post("/api/pets")
                .with(SecurityMockMvcRequestPostProcessors.csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(petDTO)))
                .andExpect(status().isOk());
    }

    private PetDTO createTestPetDTO() {
        PetDTO petDTO = new PetDTO();
        petDTO.setId(1L);
        petDTO.setName("Мурзик");
        petDTO.setBirthDate(LocalDate.now());
        petDTO.setBreed("Британский");
        petDTO.setColor(PetColor.BLACK);
        petDTO.setTailLength(25.0);
        petDTO.setOwnerId(1L);
        return petDTO;
    }

    private Pet createTestPet() {
        Pet pet = new Pet();
        pet.setId(1L);
        pet.setName("Мурзик");
        pet.setBirthDate(LocalDate.now());
        pet.setBreed("Британский");
        pet.setColor(PetColor.BLACK);
        pet.setTailLength(25.0);
        
        Owner owner = new Owner();
        owner.setId(1L);
        owner.setName("Oleg Kudrin");
        owner.setBirthDate(LocalDate.of(2005, 8, 27));
        pet.setOwner(owner);
        
        return pet;
    }
} 