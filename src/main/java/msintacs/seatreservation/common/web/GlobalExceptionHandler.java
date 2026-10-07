package msintacs.seatreservation.common.web;

import msintacs.seatreservation.common.exception.BusinessException;
import msintacs.seatreservation.common.web.dto.ErrorCode;
import msintacs.seatreservation.common.web.dto.ErrorResponse;
import msintacs.seatreservation.common.web.dto.FieldErrorResponse;
import msintacs.seatreservation.common.web.dto.ValidationErrorCode;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.ArrayList;
import java.util.List;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(BusinessException.class)
    public ResponseEntity<ErrorResponse> handleBusinessException(BusinessException e) {

        ErrorCode errorCode = e.getErrorCode();

        ErrorResponse response = new ErrorResponse(
                errorCode,
                errorCode.getMessage(),
                List.of()
        );

        return ResponseEntity
                .status(errorCode.getStatus())
                .body(response);
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ErrorResponse> handleNotReadableException(HttpMessageNotReadableException e) {

        ErrorResponse response = new ErrorResponse(
                ErrorCode.INVALID_REQUEST_BODY,
                ErrorCode.INVALID_REQUEST_BODY.getMessage(),
                List.of()
        );

        return ResponseEntity
                .status(ErrorCode.INVALID_REQUEST_BODY.getStatus())
                .body(response);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleValidationException(MethodArgumentNotValidException e) {

        List<FieldErrorResponse> errors = new ArrayList<>();

        for (FieldError fieldError : e.getBindingResult().getFieldErrors()) {

            ValidationErrorCode errorCode = toValidationErrorCode(fieldError.getCode());

            errors.add(new FieldErrorResponse(
                    fieldError.getField(),
                    errorCode,
                    fieldError.getDefaultMessage()
            ));
        }

        ErrorResponse response = new ErrorResponse(
                ErrorCode.INVALID_INPUT,
                ErrorCode.INVALID_INPUT.getMessage(),
                errors
        );

        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(response);
    }

    private ValidationErrorCode toValidationErrorCode(String fieldErrorCode) {
        return switch (fieldErrorCode) {
            case "Email", "Pattern" -> ValidationErrorCode.INVALID_FORMAT;
            case "Size" -> ValidationErrorCode.INVALID_LENGTH;
            case "NotBlank" -> ValidationErrorCode.REQUIRED;
            case null, default -> ValidationErrorCode.NOT_DEFINED;
        };
    }
}
