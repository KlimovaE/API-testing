package models.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import models.BaseModel;
import models.Transaction;

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
