package models;

import lombok.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Getter

public class Transaction {
    private long id;
    private double amount;
    private String type;
    private String timestamp;
    private long relatedAccountId;

}
