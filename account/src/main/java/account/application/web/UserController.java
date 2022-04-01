package account.application.web;

import javax.validation.Valid;

import account.application.dto.UserCreateCommand;
import account.application.dto.UserDeleteCommand;
import account.application.service.UserFacade;
import account.application.service.UserQueryService;
import io.micronaut.http.annotation.Body;
import io.micronaut.http.annotation.Controller;
import io.micronaut.http.annotation.Delete;
import io.micronaut.http.annotation.Get;
import io.micronaut.http.annotation.PathVariable;
import io.micronaut.http.annotation.Post;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.inject.Inject;
import lombok.AllArgsConstructor;

@Controller("/user")
@AllArgsConstructor(onConstructor = @__({@Inject}))
public class UserController {

    UserFacade userFacade;
    UserQueryService queryService;

    @Get("/{userId}")
    @ApiResponse(responseCode = "404", description = "Customer not found")
    @Tag(name = "user")
    public void getUser(@PathVariable String userId) {
        queryService.getUser(userId);
    }

    @Post
    @ApiResponse(responseCode = "400", description = "Invalid data supplied")
    @Tag(name = "user")
    public void addUser(@RequestBody @Body @Valid UserCreateCommand command) {

        userFacade.addUser(command);
    }

    @Delete
    @Tag(name = "user")
    public void deleteUser(@RequestBody @Body @Valid UserDeleteCommand command) {
        userFacade.deleteUser(command.getUserId());
    }

}
