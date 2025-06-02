package ru.dulfi.petservice.messaging;

import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Component;
import ru.dulfi.petservice.domain.Pet;
import ru.dulfi.petservice.dto.PetDTO;
import ru.dulfi.petservice.exception.ResourceNotFoundException;
import ru.dulfi.petservice.service.PetService;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class PetMessageListener {

    private final PetService petService;
    private final RabbitTemplate rabbitTemplate;

    @Value("${rabbitmq.exchange.pet}")
    private String petExchange;

    @Value("${rabbitmq.routing-key.pet.response}")
    private String petResponseRoutingKey;

    @Autowired
    public PetMessageListener(PetService petService, RabbitTemplate rabbitTemplate) {
        this.petService = petService;
        this.rabbitTemplate = rabbitTemplate;
    }

    @RabbitListener(queues = "${rabbitmq.queue.pet.request}")
    public void processMessage(PetMessage message) {
        System.out.println("Получено сообщение: " + message.getCorrelationId() + ", действие: " + message.getAction());
        
        PetMessageResponse response = new PetMessageResponse();
        response.setCorrelationId(message.getCorrelationId());
        response.setAction(message.getAction());

        try {
            switch (message.getAction()) {
                case GET_ALL:
                    List<Pet> pets = petService.getAll();
                    response.setPets(pets.stream()
                            .map(this::convertToDTO)
                            .collect(Collectors.toList()));
                    break;
                case GET_BY_ID:
                    Pet pet = petService.getById(message.getPetId());
                    response.setPet(convertToDTO(pet));
                    break;
                case GET_BY_OWNER_ID:
                    List<Pet> ownerPets = petService.getPetsByOwnerId(message.getOwnerId());
                    response.setPets(ownerPets.stream()
                            .map(this::convertToDTO)
                            .collect(Collectors.toList()));
                    break;
                case SEARCH_BY_NAME:
                    Pageable pageable = PageRequest.of(
                        message.getPage() != null ? message.getPage() : 0,
                        message.getSize() != null ? message.getSize() : 10,
                        message.getSortDirection() != null && message.getSortDirection().equalsIgnoreCase("desc") 
                            ? Sort.Direction.DESC : Sort.Direction.ASC,
                        message.getSortBy() != null ? message.getSortBy() : "id"
                    );
                    
                    Page<Pet> petsPage = petService.findPetsByName(message.getSearchName(), pageable);
                    
                    response.setPets(petsPage.getContent().stream()
                            .map(this::convertToDTO)
                            .collect(Collectors.toList()));
                    response.setTotalPages(petsPage.getTotalPages());
                    response.setTotalElements(petsPage.getTotalElements());
                    break;
                case CREATE:
                    System.out.println("Создаем нового котика: " + message.getPet().getName());
                    System.out.println("Полученные данные:");
                    System.out.println("  Имя: " + message.getPet().getName());
                    System.out.println("  Цвет: " + message.getPet().getColor() + " (тип: " + (message.getPet().getColor() != null ? message.getPet().getColor().getClass().getName() : "null") + ")");
                    System.out.println("  Порода: " + message.getPet().getBreed());
                    System.out.println("  Дата рождения: " + message.getPet().getBirthDate());
                    System.out.println("  Длина хвоста: " + message.getPet().getTailLength());
                    System.out.println("  ID владельца: " + message.getPet().getOwnerId());
                    
                    Pet newPet = convertToEntity(message.getPet());
                    Pet savedPet = petService.save(newPet);
                    response.setPet(convertToDTO(savedPet));
                    System.out.println("Котик успешно создан с ID: " + savedPet.getId());
                    break;
                case UPDATE:
                    Pet petToUpdate = convertToEntity(message.getPet());
                    Pet updatedPet = petService.update(petToUpdate);
                    response.setPet(convertToDTO(updatedPet));
                    break;
                case DELETE:
                    Pet petToDelete = petService.getById(message.getPetId());
                    petService.deleteByEntity(petToDelete);
                    response.setSuccess(true);
                    break;
                default:
                    response.setSuccess(false);
                    response.setErrorMessage("Unknown action: " + message.getAction());
            }
            response.setSuccess(true);
        } catch (ResourceNotFoundException e) {
            System.out.println("Ошибка: ресурс не найден - " + e.getMessage());
            response.setSuccess(false);
            response.setErrorMessage(e.getMessage());
        } catch (Exception e) {
            System.out.println("Ошибка при обработке сообщения: " + e.getMessage());
            e.printStackTrace();
            response.setSuccess(false);
            response.setErrorMessage("Error processing message: " + e.getMessage());
        }

        System.out.println("Отправляем ответ: " + response.getCorrelationId() + ", успех: " + response.isSuccess());
        rabbitTemplate.convertAndSend(petExchange, petResponseRoutingKey, response);
    }

    private PetDTO convertToDTO(Pet pet) {
        if (pet == null) return null;
        
        System.out.println("Преобразую Pet в PetDTO: " + pet);
        
        PetDTO dto = new PetDTO();
        dto.setId(pet.getId());
        dto.setName(pet.getName());
        dto.setBirthDate(pet.getBirthDate());
        dto.setBreed(pet.getBreed());
        dto.setColor(pet.getColor());
        System.out.println("Установлен цвет: " + pet.getColor() + " -> " + dto.getColor());
        dto.setTailLength(pet.getTailLength());
        dto.setOwnerId(pet.getOwnerId());
        return dto;
    }

    private Pet convertToEntity(PetDTO dto) {
        if (dto == null) return null;
        
        System.out.println("Преобразую PetDTO в Pet: " + dto + ", цвет: " + dto.getColor());
        
        Pet pet = new Pet();
        pet.setId(dto.getId());
        pet.setName(dto.getName());
        pet.setBirthDate(dto.getBirthDate());
        pet.setBreed(dto.getBreed());
        
        try {
            pet.setColor(dto.getColor());
            System.out.println("Установлен цвет: " + dto.getColor() + " -> " + pet.getColor());
        } catch (Exception e) {
            System.out.println("Ошибка при установке цвета: " + e.getMessage());
            e.printStackTrace();
        }
        
        pet.setTailLength(dto.getTailLength());
        pet.setOwnerId(dto.getOwnerId());
        return pet;
    }
} 