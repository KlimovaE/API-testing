package api.models.response;

import lombok.*;
import api.models.BaseModel;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Getter
public class CreateUserResponse extends BaseModel {
        private long id;
        private String username;
        private String password;
        private String  name;
        private String role;
        private List<String> accounts;
}
