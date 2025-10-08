package api.models.response;

import lombok.*;
import api.models.Account;
import api.models.BaseModel;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Getter

public class GetAccountsInfoResponse extends BaseModel {
    private List<Account> accounts;
}
