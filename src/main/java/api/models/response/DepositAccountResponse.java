package api.models.response;

import lombok.*;
import api.models.BaseModel;
import api.models.Transaction;

import java.util.List;


@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Getter
public class DepositAccountResponse extends BaseModel {
    private long id;
    private String accountNumber;
    private double balance;
    private List<Transaction> transactions;

}
