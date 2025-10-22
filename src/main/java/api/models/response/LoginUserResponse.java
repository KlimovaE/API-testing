package api.models.response;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import api.models.BaseModel;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LoginUserResponse extends BaseModel {
    private String username;
    private String role;
}
