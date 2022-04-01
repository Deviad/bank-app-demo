package account.domain.model;


import javax.persistence.Column;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToMany;
import javax.persistence.UniqueConstraint;
import javax.persistence.Version;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;
import lombok.With;
import lombok.extern.slf4j.Slf4j;

@Entity
@Getter
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode
@ToString
@With
@Slf4j
public class User {
    @Id
    private String id;
    @NotBlank
    private String name;
    @NotBlank
    private String surname;
    @OneToMany(mappedBy = "user")
    @EqualsAndHashCode.Exclude
    @ToString.Exclude
    private List<Account> accounts = new ArrayList<>();

    @Embedded
    private Address address;
    @Column(unique=true)
    @NotBlank
    private String username;
    @NotBlank
    private String password;

    public User(String id,
                String name,
                String surname,
                Address address,
                String username,
                String password) {


        this.id = id;
        this.name = name;
        this.surname = surname;
        this.address = address;
        this.username = username;
        this.password = password;
    }

    public static User create(User user) {
        String id = String.format("user-%s", UUID.randomUUID());
        return new User(id, user.getName(), user.getSurname(), user.getAddress(), user.getUsername(), user.getPassword());
    }

}
