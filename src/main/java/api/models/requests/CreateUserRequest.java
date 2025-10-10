package api.models.requests;


import api.configs.Config;
import api.generators.GeneratingRules;
import lombok.*;
import api.models.BaseModel;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Getter

public class CreateUserRequest extends BaseModel {
    @GeneratingRules(regex = "^[A-Za-z0-9]{3,15}$")
    private String username;
    @GeneratingRules(regex = "^[A-Z]{3}[a-z]{4}[0-9]{3}[$%&]{2}$")
    private String password;
    @GeneratingRules(regex = "^USER$")
    private String role;

    public static CreateUserRequest getAdmin() {
        return CreateUserRequest.builder().username(Config.getProperty("admin.username")).password("admin.password").build();
    }
}
