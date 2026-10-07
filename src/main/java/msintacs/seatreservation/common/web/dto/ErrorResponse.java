package msintacs.seatreservation.common.web.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

@Getter
@AllArgsConstructor
public class ErrorResponse {
    private ErrorCode code;
    private String message;
    private List<FieldErrorResponse> errors;
}
