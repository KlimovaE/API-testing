package models.response;

import lombok.*;
import models.BaseModel;
import models.Account;

import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Getter
@Data
public class TransferAccountResponse extends BaseModel {
    private List<Account> userAccountsData;
}

