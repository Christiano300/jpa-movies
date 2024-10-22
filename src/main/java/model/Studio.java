package model;


import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;


@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
public class Studio {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String name;

    @Builder.Default
    @OneToMany(mappedBy = "studio", cascade = CascadeType.MERGE)
    private List<Movie> movieList = new ArrayList<>();

    public Studio produced(Movie m) {
      movieList.add(m);
      m.setStudio(this);
      return this;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Studio studio)) return false;
        return Objects.equals(id, studio.id);
    }

    @Override
    public int hashCode() {
        return 0;
    }
}
