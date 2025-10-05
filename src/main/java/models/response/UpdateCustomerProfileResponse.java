package models.response;

import lombok.*;
import models.BaseModel;
import models.Customer;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Getter
public class UpdateCustomerProfileResponse extends BaseModel {
    private String message;
    private Customer customer;
}