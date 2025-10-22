package api.models.requests;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import api.models.BaseModel;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder

public class UpdateCustomerProfileRequest extends BaseModel {
    private String name;
}
