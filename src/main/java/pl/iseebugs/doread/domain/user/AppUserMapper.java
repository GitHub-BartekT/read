package pl.iseebugs.doread.domain.user;

import pl.iseebugs.doread.domain.user.dto.AppUserReadModel;
import pl.iseebugs.doread.domain.user.dto.AppUserWriteModel;

class AppUserMapper {
    static AppUserReadModel toAppUserReadModel(AppUser appUser){
        return AppUserReadModel.builder()
                .id(appUser.getId())
                .firstName(appUser.getFirstName())
                .lastName(appUser.getLastName())
                .email(appUser.getEmail())
                .password(appUser.getPassword())
                .role(appUser.getRole())
                .locked(appUser.getLocked())
                .enabled(appUser.getEnabled())
                .build();
    }

    static AppUser toAppUser(AppUserWriteModel appUser){
        return AppUser.builder()
                .id(appUser.getId())
                .firstName(appUser.getFirstName())
                .lastName(appUser.getLastName())
                .email(appUser.getEmail())
                .password(appUser.getPassword())
                .role(appUser.getRole())
                .locked(appUser.getLocked())
                .enabled(appUser.getEnabled())
                .build();
    }
}
