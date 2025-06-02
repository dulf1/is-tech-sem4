package ru.dulfi.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

@Getter
@Entity
@Table(name = "pets")
public class Pet {
    @Setter
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Setter
    @Column(nullable = false)
    private String name;

    @Setter
    @Column(name = "birth_date", nullable = false)
    private LocalDate birthDate;

    @Setter
    @Column(nullable = false)
    private String breed;

    @Setter
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private PetColor color;

    @Setter
    @Column(name = "tail_length")
    private Double tailLength;

    @Setter
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "owner_id")
    private Owner owner;

    @ManyToMany
    @JoinTable(
        name = "pet_friends",
        joinColumns = @JoinColumn(name = "pet_id"),
        inverseJoinColumns = @JoinColumn(name = "friend_id")
    )
    private Set<Pet> friends = new HashSet<>();

    @Override
    public String toString() {
        return "Pet{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", birthDate=" + birthDate +
                ", breed='" + breed + '\'' +
                ", color=" + color +
                ", tailLength=" + tailLength +
                ", ownerId=" + (owner != null ? owner.getId() : "null") +
                '}';
    }
}
