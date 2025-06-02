package ru.dulfi.gatewayservice.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.dulfi.gatewayservice.dto.OwnerDTO;
import ru.dulfi.gatewayservice.dto.PetDTO;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;

@Service
public class GatewayService {

    private final PetMessagingService petMessagingService;
    private final OwnerMessagingService ownerMessagingService;

    @Autowired
    public GatewayService(PetMessagingService petMessagingService, OwnerMessagingService ownerMessagingService) {
        this.petMessagingService = petMessagingService;
        this.ownerMessagingService = ownerMessagingService;
    }

    public OwnerDTO getOwnerWithPets(Long ownerId) throws ExecutionException, InterruptedException {
        CompletableFuture<OwnerDTO> ownerFuture = ownerMessagingService.getOwnerById(ownerId);
        OwnerDTO owner = ownerFuture.get();
        
        if (owner == null) {
            throw new RuntimeException("Владелец с ID " + ownerId + " не найден");
        }
        
        CompletableFuture<List<PetDTO>> petsFuture = petMessagingService.getAllPets(0, 1000, "id", "asc");
        List<PetDTO> allPets = petsFuture.get();
        
        List<PetDTO> ownerPets = allPets.stream()
                .filter(pet -> pet.getOwnerId() != null && pet.getOwnerId().equals(ownerId))
                .collect(Collectors.toList());
        
        ownerPets.forEach(pet -> pet.setOwner(owner));
        
        owner.setPets(ownerPets);
        
        return owner;
    }

    public PetDTO getPetWithOwner(Long petId) throws ExecutionException, InterruptedException {
        CompletableFuture<PetDTO> petFuture = petMessagingService.getPetById(petId);
        PetDTO pet = petFuture.get();
        
        if (pet == null) {
            throw new RuntimeException("Питомец с ID " + petId + " не найден");
        }
        
        if (pet.getOwnerId() != null) {
            CompletableFuture<OwnerDTO> ownerFuture = ownerMessagingService.getOwnerById(pet.getOwnerId());
            OwnerDTO owner = ownerFuture.get();
            pet.setOwner(owner);
        }
        
        return pet;
    }

    public List<OwnerDTO> getAllOwnersWithPets(int page, int size, String sortBy, String sortDirection) 
            throws ExecutionException, InterruptedException {
        CompletableFuture<List<OwnerDTO>> ownersFuture = ownerMessagingService.getAllOwners(page, size, sortBy, sortDirection);
        List<OwnerDTO> owners = ownersFuture.get();
        
        CompletableFuture<List<PetDTO>> petsFuture = petMessagingService.getAllPets(0, 1000, "id", "asc");
        List<PetDTO> allPets = petsFuture.get();
        
        for (OwnerDTO owner : owners) {
            List<PetDTO> ownerPets = allPets.stream()
                    .filter(pet -> pet.getOwnerId() != null && pet.getOwnerId().equals(owner.getId()))
                    .collect(Collectors.toList());
            
            ownerPets.forEach(pet -> pet.setOwner(owner));
            
            owner.setPets(ownerPets);
        }
        
        return owners;
    }

    public List<PetDTO> getAllPetsWithOwners(int page, int size, String sortBy, String sortDirection) 
            throws ExecutionException, InterruptedException {
        CompletableFuture<List<PetDTO>> petsFuture = petMessagingService.getAllPets(page, size, sortBy, sortDirection);
        List<PetDTO> pets = petsFuture.get();
        
        CompletableFuture<List<OwnerDTO>> ownersFuture = ownerMessagingService.getAllOwners(0, 1000, "id", "asc");
        List<OwnerDTO> allOwners = ownersFuture.get();
        
        for (PetDTO pet : pets) {
            if (pet.getOwnerId() != null) {
                OwnerDTO owner = allOwners.stream()
                        .filter(o -> o.getId().equals(pet.getOwnerId()))
                        .findFirst()
                        .orElse(null);
                
                pet.setOwner(owner);
            }
        }
        
        return pets;
    }

    public OwnerDTO createOwnerWithPets(OwnerDTO ownerDTO) throws ExecutionException, InterruptedException {
        List<PetDTO> petsToSave = ownerDTO.getPets() != null ? new ArrayList<>(ownerDTO.getPets()) : new ArrayList<>();
        
        ownerDTO.setPets(null);
        
        CompletableFuture<OwnerDTO> ownerFuture = ownerMessagingService.createOwner(ownerDTO);
        OwnerDTO savedOwner = ownerFuture.get();
        
        List<PetDTO> savedPets = new ArrayList<>();
        
        if (!petsToSave.isEmpty()) {
            for (PetDTO pet : petsToSave) {
                pet.setOwnerId(savedOwner.getId());
                CompletableFuture<PetDTO> petFuture = petMessagingService.createPet(pet);
                PetDTO savedPet = petFuture.get();
                savedPet.setOwner(savedOwner);
                savedPets.add(savedPet);
            }
        }
        
        savedOwner.setPets(savedPets);
        
        return savedOwner;
    }

    public PetDTO createPet(PetDTO petDTO) throws ExecutionException, InterruptedException {
        if (petDTO.getOwnerId() != null) {
            CompletableFuture<OwnerDTO> ownerFuture = ownerMessagingService.getOwnerById(petDTO.getOwnerId());
            OwnerDTO owner = ownerFuture.get();
            if (owner != null) {
                petDTO.setOwner(owner);
            }
        }
        
        CompletableFuture<PetDTO> petFuture = petMessagingService.createPet(petDTO);
        return petFuture.get();
    }

    public OwnerDTO updateOwner(Long id, OwnerDTO ownerDTO) throws ExecutionException, InterruptedException {
        ownerDTO.setId(id);
        
        CompletableFuture<OwnerDTO> ownerFuture = ownerMessagingService.updateOwner(ownerDTO);
        return ownerFuture.get();
    }

    public PetDTO updatePet(Long id, PetDTO petDTO) throws ExecutionException, InterruptedException {
        petDTO.setId(id);
        
        CompletableFuture<PetDTO> petFuture = petMessagingService.updatePet(petDTO);
        return petFuture.get();
    }

    public void deleteOwner(Long id) throws ExecutionException, InterruptedException {
        CompletableFuture<List<PetDTO>> petsFuture = petMessagingService.getAllPets(0, 1000, "id", "asc");
        List<PetDTO> allPets = petsFuture.get();
        
        List<PetDTO> ownerPets = allPets.stream()
                .filter(pet -> pet.getOwnerId() != null && pet.getOwnerId().equals(id))
                .collect(Collectors.toList());
        
        for (PetDTO pet : ownerPets) {
            CompletableFuture<Boolean> deletePetFuture = petMessagingService.deletePet(pet.getId());
            deletePetFuture.get();
        }
        
        CompletableFuture<Boolean> deleteOwnerFuture = ownerMessagingService.deleteOwner(id);
        deleteOwnerFuture.get();
    }

    public void deletePet(Long id) throws ExecutionException, InterruptedException {
        CompletableFuture<Boolean> deletePetFuture = petMessagingService.deletePet(id);
        deletePetFuture.get();
    }
    
    public List<PetDTO> searchPetsByName(String name, int page, int size, String sortBy, String sortDirection) 
            throws ExecutionException, InterruptedException {
        CompletableFuture<List<PetDTO>> petsFuture = petMessagingService.searchPetsByName(name, page, size, sortBy, sortDirection);
        List<PetDTO> pets = petsFuture.get();
        
        if (pets != null && !pets.isEmpty()) {
            CompletableFuture<List<OwnerDTO>> ownersFuture = ownerMessagingService.getAllOwners(0, 1000, "id", "asc");
            List<OwnerDTO> allOwners = ownersFuture.get();
            
            for (PetDTO pet : pets) {
                if (pet.getOwnerId() != null) {
                    OwnerDTO owner = allOwners.stream()
                            .filter(o -> o.getId().equals(pet.getOwnerId()))
                            .findFirst()
                            .orElse(null);
                    
                    pet.setOwner(owner);
                }
            }
        }
        
        return pets;
    }
} 