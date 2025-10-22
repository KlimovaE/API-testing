package api.models.response;

import lombok.*;
import api.models.BaseModel;
import api.models.Account;

import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Getter
@Data
public class TransferAccountResponse extends BaseModel {
    private List<Account> userAccountsData;
}

