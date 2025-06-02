package ru.dulfi.domain;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Setter
@Getter
@Data
@NoArgsConstructor
@Entity
@Table(name = "owners")
public class Owner {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(name = "birth_date", nullable = false)
    private LocalDate birthDate;

    @OneToMany(mappedBy = "owner", cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    @JsonManagedReference
    private List<Pet> pets = new ArrayList<>();

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("Owner{")
          .append("id=").append(id)
          .append(", name='").append(name).append('\'')
          .append(", birthDate=").append(birthDate)
          .append(", pets=[");
        
        if (pets != null && !pets.isEmpty()) {
            for (Pet pet : pets) {
                sb.append("\n  - ").append(pet.getName())
                  .append(" (ID: ").append(pet.getId())
                  .append(", порода: ").append(pet.getBreed())
                  .append(", цвет: ").append(pet.getColor())
                  .append(", длина хвоста: ").append(pet.getTailLength())
                  .append(")");
            }
        }
        sb.append("\n]}");
        return sb.toString();
    }
}
