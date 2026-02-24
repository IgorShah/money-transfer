package ru.netology.moneytransfer.dto;

import jakarta.validation.constraints.NotBlank;

public class ConfirmRequest {
    @NotBlank
    private String operationId;

    @NotBlank
    private String code;

    public ConfirmRequest() {
    }

    public ConfirmRequest(String operationId, String code) {
        this.operationId = operationId;
        this.code = code;
    }

    public String getOperationId() {
        return operationId;
    }

    public void setOperationId(String operationId) {
        this.operationId = operationId;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }
}
