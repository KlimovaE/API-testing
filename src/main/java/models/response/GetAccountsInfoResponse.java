package models.response;

import lombok.*;
import models.Account;
import models.BaseModel;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Getter

public class GetAccountsInfoResponse extends BaseModel {
    private List<Account> accounts;
}
