package com.app.onestepback.service.member.cmd;

public record LoginWithEmailPasswordCmd(
        String email,
        String password
) {
}
