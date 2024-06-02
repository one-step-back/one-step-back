package com.app.onestepback.domain.type.post;

import lombok.Getter;

@Getter
public enum PostSortType {
    ID_DESC("ID_DESC"),
    LIKE_DESC("LIKE_DESC"),
    VIEW_DESC("VIEW_DESC");

    private final String value;

    PostSortType(String value) {
        this.value = value;
    }

    @Override
    public String toString() {
        return value;
    }
}
