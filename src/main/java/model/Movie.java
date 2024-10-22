package model;


import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;


@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
public class Movie {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Size(min = 4, max = 50)
    private String title;

    @Past
    private LocalDate releaseDate;

    @ManyToOne(cascade = CascadeType.MERGE)
    private Studio studio;

    @Builder.Default
    @ManyToMany(cascade = CascadeType.MERGE)
    private Set<Actor> actors = new HashSet<>();

    public Movie playsRole(Actor actor) {
        actors.add(actor);
        return this;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Movie movie)) return false;
        return Objects.equals(id, movie.id);
    }

    @Override
    public int hashCode() {
        return 0;
    }
}
