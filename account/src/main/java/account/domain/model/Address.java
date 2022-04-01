package account.domain.model;

import javax.persistence.Embeddable;
import javax.validation.constraints.NotBlank;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.With;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Embeddable
@With
public class Address {
    @NotBlank
    String city;
    @NotBlank
    String province;
    @NotBlank
    String street;
    @NotBlank
    String country;
}
