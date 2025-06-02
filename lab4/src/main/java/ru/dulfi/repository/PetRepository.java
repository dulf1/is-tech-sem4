package ru.dulfi.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import ru.dulfi.domain.Pet;
import ru.dulfi.domain.PetColor;

import java.util.List;

@Repository
public interface PetRepository extends JpaRepository<Pet, Long>, PagingAndSortingRepository<Pet, Long> {
    Page<Pet> findByColor(PetColor color, Pageable pageable);
    
    @Query("SELECT p FROM Pet p WHERE p.color = :color AND p.tailLength > :minTailLength")
    Page<Pet> findByColorAndTailLengthGreaterThan(
        @Param("color") PetColor color,
        @Param("minTailLength") Double minTailLength,
        Pageable pageable
    );

    List<Pet> findByColor(PetColor color);
    List<Pet> findByColorAndTailLengthGreaterThan(PetColor color, Double minTailLength);
} 