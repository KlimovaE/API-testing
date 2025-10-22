package api.models.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import api.models.BaseModel;
import api.models.Transaction;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor

public class CreateAccountResponse extends BaseModel {
private long id;
private String accountNumber;
private double balance;
private List<Transaction> transactions;
}
