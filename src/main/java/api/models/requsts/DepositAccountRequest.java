package api.models.requsts;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import api.models.BaseModel;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class DepositAccountRequest extends BaseModel {
    private long id;
    private double balance;
}
