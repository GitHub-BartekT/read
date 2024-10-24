package pl.iseebugs.doread.infrastructure;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import pl.iseebugs.doread.domain.ApiResponse;
import pl.iseebugs.doread.domain.account.ApiResponseFactory;
import pl.iseebugs.doread.domain.account.EmailNotFoundException;
import pl.iseebugs.doread.domain.account.create.InvalidEmailTypException;
import pl.iseebugs.doread.domain.email.EmailSender;
import pl.iseebugs.doread.domain.email.InvalidEmailTypeException;
import pl.iseebugs.doread.domain.module.ModuleNotFoundException;
import pl.iseebugs.doread.domain.session.SessionNotFoundException;
import pl.iseebugs.doread.domain.user.AppUserNotFoundException;

@ControllerAdvice
class GlobalExceptionalHandler {

    @ExceptionHandler(EmailSender.EmailConflictException.class)
    ResponseEntity<ApiResponse<Void>> handlerEmailConflictException(EmailSender.EmailConflictException e) {
        return ResponseEntity.ok().body(
                ApiResponseFactory.createResponseWithoutData(
                        HttpStatus.CONFLICT.value(),
                        e.getMessage()));
    }

    @ExceptionHandler(InvalidEmailTypeException.class)
    ResponseEntity<ApiResponse<Void>> handlerInvalidEmailTypeException(InvalidEmailTypeException e) {
        return ResponseEntity.ok().body(
                ApiResponseFactory.createResponseWithoutData(
                        HttpStatus.INTERNAL_SERVER_ERROR.value(),
                        e.getMessage()));
    }

    @ExceptionHandler(AppUserNotFoundException.class)
    ResponseEntity<ApiResponse<Void>> handlerAppUserNotFoundException(AppUserNotFoundException e) {
        return ResponseEntity.ok().body(
                ApiResponseFactory.createResponseWithoutData(
                        HttpStatus.NOT_FOUND.value(),
                        e.getMessage()));
    }

    @ExceptionHandler(EmailNotFoundException.class)
    ResponseEntity<ApiResponse<Void>> handlerEmailNotFoundException(EmailNotFoundException e) {
        return ResponseEntity.ok().body(
                ApiResponseFactory.createResponseWithoutData(
                        HttpStatus.NOT_FOUND.value(),
                        e.getMessage()));
    }

    @ExceptionHandler(InvalidEmailTypException
            .class)
    ResponseEntity<ApiResponse<Void>> handlerInvalidEmailTypException (InvalidEmailTypException e) {
        return ResponseEntity.ok().body(
                ApiResponseFactory.createResponseWithoutData(
                        HttpStatus.BAD_REQUEST.value(),
                        e.getMessage()));
    }

    @ExceptionHandler(SessionNotFoundException.class)
    ResponseEntity<ApiResponse<Void>> handlerSessionNotFoundException(SessionNotFoundException e) {
        return ResponseEntity.ok().body(
                ApiResponseFactory.createResponseWithoutData(
                        HttpStatus.NOT_FOUND.value(),
                        e.getMessage()));
    }

    @ExceptionHandler(ModuleNotFoundException.class)
    ResponseEntity<ApiResponse<Void>> handlerModuleNotFoundException(ModuleNotFoundException e) {
        return ResponseEntity.ok().body(
                ApiResponseFactory.createResponseWithoutData(
                        HttpStatus.NOT_FOUND.value(),
                        e.getMessage()));
    }

    @ExceptionHandler(IllegalArgumentException.class)
    ResponseEntity<ApiResponse<Void>> handlerIllegalArgumentException(IllegalArgumentException e) {
        return ResponseEntity.ok().body(
                ApiResponseFactory.createResponseWithoutData(
                        HttpStatus.BAD_REQUEST.value(),
                        e.getMessage()));
    }

}
