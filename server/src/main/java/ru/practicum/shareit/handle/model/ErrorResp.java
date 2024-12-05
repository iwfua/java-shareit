package ru.practicum.shareit.handle.model;

import lombok.Getter;

@Getter
public  class ErrorResp {
    private String error;

    public ErrorResp(String error) {
        this.error = error;
    }
}
