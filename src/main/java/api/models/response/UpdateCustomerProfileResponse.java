package api.models.response;

import lombok.*;
import api.models.BaseModel;
import api.models.Customer;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Getter
public class UpdateCustomerProfileResponse extends BaseModel {
    private String message;
    private Customer customer;
}