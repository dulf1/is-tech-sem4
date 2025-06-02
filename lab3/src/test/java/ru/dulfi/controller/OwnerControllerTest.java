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
                .param("page", "0")
                .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].id").value(1))
                .andExpect(jsonPath("$.content[0].name").value("Олег"))
                .andExpect(jsonPath("$.content[0].birthDate").value("2005-08-27"));
    }

    @Test
    public void testGetOwnerById() throws Exception {
        Owner owner = createTestOwner();
        when(ownerService.getById(1L)).thenReturn(owner);

        mockMvc.perform(get("/api/owners/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("Олег"))
                .andExpect(jsonPath("$.birthDate").value("2005-08-27"));
    }

    @Test
    public void testGetOwnerByIdNotFound() throws Exception {
        when(ownerService.getById(1L)).thenThrow(new ResourceNotFoundException("Владелец не найден"));

        mockMvc.perform(get("/api/owners/1"))
                .andExpect(status().isNotFound());
    }

    @Test
    public void testCreateOwner() throws Exception {
        Owner owner = createTestOwner();
        OwnerDTO ownerDTO = createTestOwnerDTO();
        when(ownerService.save(any())).thenReturn(owner);

        mockMvc.perform(post("/api/owners")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(ownerDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("Олег"))
                .andExpect(jsonPath("$.birthDate").value("2005-08-27"));
    }

    @Test
    public void testUpdateOwner() throws Exception {
        Owner owner = createTestOwner();
        OwnerDTO ownerDTO = createTestOwnerDTO();
        when(ownerService.update(any())).thenReturn(owner);

        mockMvc.perform(put("/api/owners/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(ownerDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("Олег"))
                .andExpect(jsonPath("$.birthDate").value("2005-08-27"));
    }

    @Test
    public void testUpdateOwnerNotFound() throws Exception {
        OwnerDTO ownerDTO = createTestOwnerDTO();
        when(ownerService.update(any())).thenThrow(new ResourceNotFoundException("Владелец не найден"));

        mockMvc.perform(put("/api/owners/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(ownerDTO)))
                .andExpect(status().isNotFound());
    }

    @Test
    public void testDeleteOwner() throws Exception {
        doNothing().when(ownerService).deleteByEntity(any());

        mockMvc.perform(delete("/api/owners/1"))
                .andExpect(status().isOk());
    }

    @Test
    public void testSearchOwners() throws Exception {
        Owner owner = createTestOwner();
        Page<Owner> page = new PageImpl<>(Collections.singletonList(owner));
        when(ownerService.searchByName(eq("Олег"), any(PageRequest.class))).thenReturn(page);

        mockMvc.perform(get("/api/owners/search")
                .param("name", "Олег")
                .param("page", "0")
                .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].id").value(1))
                .andExpect(jsonPath("$.content[0].name").value("Олег"))
                .andExpect(jsonPath("$.content[0].birthDate").value("2005-08-27"));
    }

    private Owner createTestOwner() {
        Owner owner = new Owner();
        owner.setId(1L);
        owner.setName("Олег");
        owner.setBirthDate(LocalDate.of(2005, 8, 27));
        return owner;
    }

    private OwnerDTO createTestOwnerDTO() {
        OwnerDTO dto = new OwnerDTO();
        dto.setId(1L);
        dto.setName("Олег");
        dto.setBirthDate(LocalDate.of(2005, 8, 27));
        return dto;
    }
} 