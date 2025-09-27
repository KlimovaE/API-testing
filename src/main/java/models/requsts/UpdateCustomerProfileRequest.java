package models.requsts;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import models.BaseModel;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder

public class UpdateCustomerProfileRequest extends BaseModel {
    private String name;
}
