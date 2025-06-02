package ru.dulfi.ownerservice.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;
import ru.dulfi.ownerservice.domain.Owner;

@Repository
public interface OwnerRepository extends JpaRepository<Owner, Long>, PagingAndSortingRepository<Owner, Long> {
    Page<Owner> findByNameContainingIgnoreCase(String name, Pageable pageable);
} 