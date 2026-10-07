package msintacs.seatreservation.common.web.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class FieldErrorResponse {

    private String field;
    private ValidationErrorCode errorCode;
    private String message;
}
