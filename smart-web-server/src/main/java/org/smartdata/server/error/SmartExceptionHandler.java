/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.smartdata.server.error;

import org.apache.commons.lang.exception.ExceptionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.smartdata.exception.NotFoundException;
import org.smartdata.exception.SsmParseException;
import org.smartdata.metastore.MetaStoreException;
import org.springframework.beans.TypeMismatchException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import javax.validation.ConstraintViolationException;

import java.io.IOException;

@ControllerAdvice
public class SmartExceptionHandler extends ResponseEntityExceptionHandler {
  private static final Logger LOG = LoggerFactory.getLogger(SmartExceptionHandler.class);

  // TODO refactor error handling and status codes after api refactoring
  @ExceptionHandler(value = {IOException.class, MetaStoreException.class})
  protected ResponseEntity<Object> handleSsmExceptions(
      RuntimeException exception, WebRequest request) {
    return handleExceptionInternal(
        exception,
        request,
        "SSM_ERROR",
        HttpStatus.INTERNAL_SERVER_ERROR);
  }

  @ExceptionHandler(value = {
      ConstraintViolationException.class, SsmParseException.class})
  protected ResponseEntity<Object> handleValidationExceptions(
      Exception exception, WebRequest request) {
    return handleExceptionInternal(
        exception,
        request,
        "VALIDATION_ERROR",
        HttpStatus.BAD_REQUEST);
  }

  @ExceptionHandler(value = {NotFoundException.class})
  protected ResponseEntity<Object> handleNotFoundException(
      Exception exception, WebRequest request) {
    return handleExceptionInternal(
        exception,
        null,
        new HttpHeaders(),
        HttpStatus.NOT_FOUND,
        request);
  }

  @Override
  protected ResponseEntity<Object> handleMethodArgumentNotValid(
      MethodArgumentNotValidException exception,
      HttpHeaders headers,
      HttpStatus status,
      WebRequest request) {
    return handleValidationExceptions(exception, request);
  }

  protected ResponseEntity<Object> handleTypeMismatch(
      TypeMismatchException exception,
      HttpHeaders headers,
      HttpStatus status,
      WebRequest request) {
    return handleValidationExceptions(exception, request);
  }

  @Override
  protected ResponseEntity<Object> handleBindException(
      BindException exception,
      HttpHeaders headers,
      HttpStatus status,
      WebRequest request) {
    return handleValidationExceptions(exception, request);
  }

  private ResponseEntity<Object> handleExceptionInternal(
      Exception exception, WebRequest request, String errorCode, HttpStatus status) {
    LOG.error("Exception during handling request on {}",
        request.getDescription(false), exception);

    ErrorDto<String> errorBody = new ErrorDto<>(
        errorCode,
        exception.getMessage(),
        ExceptionUtils.getStackTrace(exception));

    return handleExceptionInternal(
        exception,
        errorBody,
        new HttpHeaders(),
        status,
        request);
  }
}
