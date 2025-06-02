package ru.dulfi.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
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
import ru.dulfi.exception.ResourceNotFoundException;
import ru.dulfi.exception.GlobalExceptionHandler;
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors;

import java.time.LocalDate;
import java.util.Collections;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(PetController.class)
@Import({PetController.class, GlobalExceptionHandler.class})
@WithMockUser
public class PetControllerTest {

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
    public void testGetAllPets() throws Exception {
        Pet pet = createTestPet();
        Page<Pet> page = new PageImpl<>(Collections.singletonList(pet));
        when(petService.getAll(any(Pageable.class))).thenReturn(page);

        mockMvc.perform(get("/api/pets")
                .with(SecurityMockMvcRequestPostProcessors.csrf())
                .param("page", "0")
                .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].id").value(1))
                .andExpect(jsonPath("$.content[0].name").value("Мурзик"))
                .andExpect(jsonPath("$.content[0].color").value("BLACK"));
    }

    @Test
    public void testGetPetById() throws Exception {
        Pet pet = createTestPet();
        when(petService.getById(1L)).thenReturn(pet);

        mockMvc.perform(get("/api/pets/1")
                .with(SecurityMockMvcRequestPostProcessors.csrf()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("Мурзик"));
    }

    @Test
    public void testGetPetByIdNotFound() throws Exception {
        when(petService.getById(1L)).thenThrow(new ResourceNotFoundException("Котик не найден"));

        mockMvc.perform(get("/api/pets/1")
                .with(SecurityMockMvcRequestPostProcessors.csrf()))
                .andExpect(status().isNotFound());
    }

    @Test
    public void testCreatePet() throws Exception {
        PetDTO petDTO = createTestPetDTO();
        Pet pet = createTestPet();
        when(ownerService.getById(1L)).thenReturn(pet.getOwner());
        when(petService.save(any())).thenReturn(pet);

        mockMvc.perform(post("/api/pets")
                .with(SecurityMockMvcRequestPostProcessors.csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(petDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("Мурзик"));
    }

    @Test
    public void testUpdatePet() throws Exception {
        PetDTO petDTO = createTestPetDTO();
        Pet pet = createTestPet();
        when(ownerService.getById(1L)).thenReturn(pet.getOwner());
        when(petService.update(any())).thenReturn(pet);
        doNothing().when(petAccessService).checkAccess(any(Pet.class));

        mockMvc.perform(put("/api/pets/1")
                .with(SecurityMockMvcRequestPostProcessors.csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(petDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("Мурзик"));
    }

    @Test
    public void testDeletePet() throws Exception {
        Pet pet = createTestPet();
        when(petService.getById(1L)).thenReturn(pet);
        doNothing().when(petAccessService).checkAccess(any(Pet.class));
        doNothing().when(petService).deleteByEntity(any());

        mockMvc.perform(delete("/api/pets/1")
                .with(SecurityMockMvcRequestPostProcessors.csrf()))
                .andExpect(status().isOk());
    }

    @Test
    public void testGetPetsByColor() throws Exception {
        Pet pet = createTestPet();
        Page<Pet> page = new PageImpl<>(Collections.singletonList(pet));
        when(petService.getPetsByColor(any(), any(Pageable.class))).thenReturn(page);

        mockMvc.perform(get("/api/pets/by-color/BLACK")
                .with(SecurityMockMvcRequestPostProcessors.csrf())
                .param("page", "0")
                .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].id").value(1))
                .andExpect(jsonPath("$.content[0].name").value("Мурзик"))
                .andExpect(jsonPath("$.content[0].color").value("BLACK"));
    }

    @Test
    public void testGetPetsByColorAndTailLength() throws Exception {
        Pet pet = createTestPet();
        Page<Pet> page = new PageImpl<>(Collections.singletonList(pet));
        when(petService.getPetsByColorAndTailLength(any(), any(), any(Pageable.class))).thenReturn(page);

        mockMvc.perform(get("/api/pets/by-color-and-tail")
                .with(SecurityMockMvcRequestPostProcessors.csrf())
                .param("color", "BLACK")
                .param("minTailLength", "25.0")
                .param("page", "0")
                .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].id").value(1))
                .andExpect(jsonPath("$.content[0].name").value("Мурзик"))
                .andExpect(jsonPath("$.content[0].color").value("BLACK"))
                .andExpect(jsonPath("$.content[0].tailLength").value(25.0));
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