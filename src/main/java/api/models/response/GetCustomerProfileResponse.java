package api.models.response;

import lombok.*;
import api.models.BaseModel;
import api.models.Account;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Getter

public class GetCustomerProfileResponse extends BaseModel {

    private long id;
    private String username;
    private String password;
    private String name;
    private String role;
    private List<Account> accounts;;
}




