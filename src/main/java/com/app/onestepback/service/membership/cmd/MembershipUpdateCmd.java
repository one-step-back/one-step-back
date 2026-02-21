package com.app.onestepback.service.membership.cmd;

public record MembershipUpdateCmd(
        Long membershipId,
        Long artistId,
        String name,
        String description,
        String currentImageId,
        String newImageId,
        boolean deleteImage,
        Long price,
        Integer levelOrder
) {
}
