package account.application.web;

import javax.validation.Valid;
import javax.ws.rs.HeaderParam;

import java.util.List;

import account.application.dto.AccountCreateCommand;
import account.application.dto.AccountDeleteCommand;
import account.application.dto.AccountResponse;
import account.application.service.AccountFacade;
import account.application.service.AccountQueryService;
import io.micronaut.http.annotation.Body;
import io.micronaut.http.annotation.Controller;
import io.micronaut.http.annotation.Delete;
import io.micronaut.http.annotation.Get;
import io.micronaut.http.annotation.PathVariable;
import io.micronaut.http.annotation.Post;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.inject.Inject;
import lombok.AllArgsConstructor;

@Controller("/account")
@AllArgsConstructor(onConstructor = @__({@Inject}))
public class AccountController {

    AccountFacade accountFacade;
    AccountQueryService queryService;

    @Get("/{accountId}")
    @Operation(summary = "Retrieve one account",
            description = "The account identified by accountId is returned"
    )
    @ApiResponse(
            content = @Content(mediaType = "text/plain",
                    schema = @Schema(type="string"))
    )
    @ApiResponse(responseCode = "404", description = "Account not found")
    @ApiResponse(responseCode = "400", description = "The userId is not the owner of the account")
    @Tag(name = "account")
    public void getAccount(@PathVariable String accountId, @HeaderParam("userId") String userId) {
        queryService.getAccount(accountId, userId);
    }


    @Post
    @ApiResponse(responseCode = "400", description = "Invalid data supplied")
    @Tag(name = "account")
    public void createAccount(@RequestBody @Body @Valid AccountCreateCommand command) {
        accountFacade.addAccount(command);
    }

    @Delete
    @Tag(name = "account")
    public void deleteAccount(@RequestBody @Body @Valid AccountDeleteCommand account) {
        accountFacade.deleteAccount(account);
    }

}
