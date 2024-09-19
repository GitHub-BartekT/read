package pl.iseebugs.doread.domain.user.dto;

import lombok.Builder;

@Builder
public record AppUserReadModel(
        Long id,
        String firstName,
        String lastName,
        String email,
        String password,
        String role,
        Boolean locked,
        Boolean enabled) {
}
