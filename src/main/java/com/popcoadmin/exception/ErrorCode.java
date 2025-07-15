package com.popcoadmin.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public enum ErrorCode {

  INTERNAL_SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "INTERNAL_SERVER_ERROR", "서버 내부 오류가 발생했습니다"),

  USER_NOT_FOUND(HttpStatus.NOT_FOUND, "USER_NOT_FOUND", "사용자를 찾을 수 없습니다."),
  INVALID_PASSWORD(HttpStatus.UNAUTHORIZED, "INVALID_PASSWORD", "비밀번호가 올바르지 않습니다"),
  INVALID_TOKEN(HttpStatus.UNAUTHORIZED, "INVALID_TOKEN", "유효하지 않은 토큰입니다"),
  EXPIRED_TOKEN(HttpStatus.UNAUTHORIZED, "EXPIRED_TOKEN", "만료된 토큰입니다"),
  LOGIN_REQUIRED(HttpStatus.UNAUTHORIZED, "LOGIN_REQUIRED", "로그인이 필요합니다"),

  REVIEW_NOT_FOUND(HttpStatus.NOT_FOUND, "REVIEW_NOT_FOUND", "리뷰를 찾을 수 없습니다."),
  NOT_MY_REVIEW(HttpStatus.FORBIDDEN, "NOT_MY_REVIEW", "본인의 리뷰만 수정 또는 삭제할 수 있습니다."),
  INVALID_INPUT_VALUE(HttpStatus.BAD_REQUEST, "INVALID_INPUT_VALUE", "올바르지 않은 입력값입니다."),

  PERSONA_NOT_FOUND(HttpStatus.NOT_FOUND, "PERSONA_NOT_FOUND", "페르소나 정보를 찾을 수 없습니다."),

  QUIZ_NOT_FOUND(HttpStatus.NOT_FOUND, "QUIZ_NOT_FOUND", "퀴즈를 찾을 수 없습니다."),
  DUPLICATE_QUIZ_NAME(HttpStatus.CONFLICT, "DUPLICATE_QUIZ_NAME", "이미 존재하는 퀴즈 이름입니다."),
  QUIZ_ALREADY_STARTED(HttpStatus.BAD_REQUEST, "QUIZ_ALREADY_STARTED", "이미 시작된 퀴즈는 수정할 수 없습니다."),

  QUIZ_QUESTION_NOT_FOUND(HttpStatus.NOT_FOUND, "QUIZ_QUESTION_NOT_FOUND", "퀴즈 질문을 찾을 수 없습니다."),
  DUPLICATE_QUESTION_ORDER(HttpStatus.CONFLICT, "DUPLICATE_QUESTION_ORDER", "해당 퀴즈에 이미 동일한 순서의 질문이 존재합니다."),

  QUIZ_OPTION_NOT_FOUND(HttpStatus.NOT_FOUND, "QUIZ_OPTION_NOT_FOUND", "퀴즈 선택지를 찾을 수 없습니다."),
  INVALID_CORRECT_OPTION_COUNT(HttpStatus.BAD_REQUEST, "INVALID_CORRECT_OPTION_COUNT", "정답은 하나만 지정되어야 합니다."),
  NO_CORRECT_OPTION(HttpStatus.BAD_REQUEST, "NO_CORRECT_OPTION", "반드시 하나의 정답이 있어야 합니다.");

  private final HttpStatus status;
  private final String code;
  private final String message;


  ErrorCode(HttpStatus status, String code, String message) {
    this.status = status;
    this.code = code;
    this.message = message;
  }
}