package api.models.requsts;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import api.models.BaseModel;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TransferAccountRequest extends BaseModel {
    private long senderAccountId;
    private long receiverAccountId;
    private double amount;
}
