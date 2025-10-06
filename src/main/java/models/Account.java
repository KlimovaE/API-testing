package models;

import lombok.*;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Getter

public class Account extends BaseModel{
    private long id;
    private String accountNumber;
    private double balance;
    private String name;
    private String role;
    private List<Transaction> transactions;
}
