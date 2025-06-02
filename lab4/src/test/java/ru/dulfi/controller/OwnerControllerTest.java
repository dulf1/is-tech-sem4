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
import org.springframework.data.domain.PageRequest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors;
import org.springframework.test.web.servlet.MockMvc;
import ru.dulfi.domain.Owner;
import ru.dulfi.dto.OwnerDTO;
import ru.dulfi.service.OwnerService;
import ru.dulfi.exception.ResourceNotFoundException;
import ru.dulfi.exception.GlobalExceptionHandler;

import java.time.LocalDate;
import java.util.Collections;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(OwnerController.class)
@Import({OwnerController.class, GlobalExceptionHandler.class})
@WithMockUser
public class OwnerControllerTest {

    @Configuration
    static class TestConfig {
    }

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private OwnerService ownerService;

    @Test
    public void testGetAllOwners() throws Exception {
        Owner owner = createTestOwner();
        Page<Owner> page = new PageImpl<>(Collections.singletonList(owner));
        when(ownerService.getAll(any(PageRequest.class))).thenReturn(page);

        mockMvc.perform(get("/api/owners")
                .with(SecurityMockMvcRequestPostProcessors.csrf())
                .param("page", "0")
                .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].id").value(1))
                .andExpect(jsonPath("$.content[0].name").value("Oleg Kudrin"))
                .andExpect(jsonPath("$.content[0].birthDate").value("2005-08-27"));
    }

    @Test
    public void testGetOwnerById() throws Exception {
        Owner owner = createTestOwner();
        when(ownerService.getById(1L)).thenReturn(owner);

        mockMvc.perform(get("/api/owners/1")
                .with(SecurityMockMvcRequestPostProcessors.csrf()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("Oleg Kudrin"))
                .andExpect(jsonPath("$.birthDate").value("2005-08-27"));
    }

    @Test
    public void testGetOwnerByIdNotFound() throws Exception {
        when(ownerService.getById(1L)).thenThrow(new ResourceNotFoundException("Владелец не найден"));

        mockMvc.perform(get("/api/owners/1")
                .with(SecurityMockMvcRequestPostProcessors.csrf()))
                .andExpect(status().isNotFound());
    }

    @Test
    public void testCreateOwner() throws Exception {
        Owner owner = createTestOwner();
        OwnerDTO ownerDTO = createTestOwnerDTO();
        when(ownerService.save(any())).thenReturn(owner);

        mockMvc.perform(post("/api/owners")
                .with(SecurityMockMvcRequestPostProcessors.csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(ownerDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("Oleg Kudrin"))
                .andExpect(jsonPath("$.birthDate").value("2005-08-27"));
    }

    @Test
    public void testUpdateOwner() throws Exception {
        Owner owner = createTestOwner();
        OwnerDTO ownerDTO = createTestOwnerDTO();
        when(ownerService.update(any())).thenReturn(owner);

        mockMvc.perform(put("/api/owners/1")
                .with(SecurityMockMvcRequestPostProcessors.csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(ownerDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("Oleg Kudrin"))
                .andExpect(jsonPath("$.birthDate").value("2005-08-27"));
    }

    @Test
    public void testUpdateOwnerNotFound() throws Exception {
        OwnerDTO ownerDTO = createTestOwnerDTO();
        when(ownerService.update(any())).thenThrow(new ResourceNotFoundException("Владелец не найден"));

        mockMvc.perform(put("/api/owners/1")
                .with(SecurityMockMvcRequestPostProcessors.csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(ownerDTO)))
                .andExpect(status().isNotFound());
    }

    @Test
    public void testDeleteOwner() throws Exception {
        doNothing().when(ownerService).deleteByEntity(any());

        mockMvc.perform(delete("/api/owners/1")
                .with(SecurityMockMvcRequestPostProcessors.csrf()))
                .andExpect(status().isOk());
    }

    @Test
    public void testSearchOwners() throws Exception {
        Owner owner = createTestOwner();
        Page<Owner> page = new PageImpl<>(Collections.singletonList(owner));
        when(ownerService.searchByName(eq("Oleg Kudrin"), any(PageRequest.class))).thenReturn(page);

        mockMvc.perform(get("/api/owners/search")
                .with(SecurityMockMvcRequestPostProcessors.csrf())
                .param("name", "Oleg Kudrin")
                .param("page", "0")
                .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].id").value(1))
                .andExpect(jsonPath("$.content[0].name").value("Oleg Kudrin"))
                .andExpect(jsonPath("$.content[0].birthDate").value("2005-08-27"));
    }

    private Owner createTestOwner() {
        Owner owner = new Owner();
        owner.setId(1L);
        owner.setName("Oleg Kudrin");
        owner.setBirthDate(LocalDate.of(2005, 8, 27));
        return owner;
    }

    private OwnerDTO createTestOwnerDTO() {
        OwnerDTO dto = new OwnerDTO();
        dto.setId(1L);
        dto.setName("Oleg Kudrin");
        dto.setBirthDate(LocalDate.of(2005, 8, 27));
        return dto;
    }
} 