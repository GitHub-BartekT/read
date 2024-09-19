package pl.iseebugs.doread.domain.security;

import pl.iseebugs.doread.domain.user.dto.AppUserReadModel;

class AppUserMapperLogin {
    static AppUserInfoDetails fromAppUserReadModelToUserDetails(AppUserReadModel userDetails) {
        return new AppUserInfoDetails(
                userDetails.firstName(),
                userDetails.lastName(),
                userDetails.email(),
                userDetails.password(),
                userDetails.role());
    }
}
