package models.requsts;


import generators.GeneratingRules;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import models.BaseModel;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CreateUserRequest extends BaseModel {
    @GeneratingRules(regex = "^[A-Za-z0-9]{3,15}$")
    private String username;
    @GeneratingRules(regex = "^[A-Z]{3}[a-z]{4}[0-9]{3}[$%&]{2}$")
    private String password;
    @GeneratingRules(regex = "^USER$")
    private String role;
}
