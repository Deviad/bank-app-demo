package account.application.adapter;


import account.application.dto.UserCreateCommand;
import account.domain.model.Address;
import account.domain.model.User;
import jakarta.inject.Singleton;

@Singleton
public class UserAdapter {
    public User toEntity(UserCreateCommand user) {
        // I could use mapstruct for this mapping,
        // however for the sake of simplicity, because I don't have many mappings to do,
        // I will map this by hand.

        return new User()
                .withName(user.getName())
                .withSurname(user.getSurname())
                .withUsername(user.getPassword())
                .withPassword(user.getPassword())
                .withAddress(
                        new Address()
                                .withCity(user.getAddress().getCity())
                                .withCountry(user.getAddress().getCountry())
                                .withProvince(user.getAddress().getProvince())
                                .withStreet(user.getAddress().getStreet())
                );


    }
}