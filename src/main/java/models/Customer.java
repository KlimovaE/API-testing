package models;

import lombok.*;

import java.util.List;
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Getter
public class Customer extends BaseModel{
    private long id;
    private String username;
    private String password;
    private String role;
    private String name;
    private List<String> accounts;
}
