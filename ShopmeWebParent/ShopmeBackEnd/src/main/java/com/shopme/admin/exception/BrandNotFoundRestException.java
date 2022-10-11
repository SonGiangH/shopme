package com.shopme.admin.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(code = HttpStatus.NOT_FOUND, reason = "Brand was not found")
public class BrandNotFoundRestException extends Exception{

}
