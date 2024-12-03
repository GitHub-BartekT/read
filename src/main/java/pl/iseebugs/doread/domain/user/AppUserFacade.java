package pl.iseebugs.doread.domain.user;

import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import pl.iseebugs.doread.domain.account.EmailNotFoundException;
import pl.iseebugs.doread.domain.user.dto.AppUserReadModel;
import pl.iseebugs.doread.domain.user.dto.AppUserWriteModel;

import java.util.UUID;

@Service
@AllArgsConstructor
@Log4j2
public class AppUserFacade {

    private final AppUserRepository appUserRepository;

    public boolean existsByEmail(String email) {
        return appUserRepository.existsByEmail(email);
    }

    public AppUserReadModel findUserById(Long id) throws AppUserNotFoundException {
        AppUser user = appUserRepository.findById(id).orElseThrow(AppUserNotFoundException::new);
        return AppUserMapper.toAppUserReadModel(user);
    }

    public AppUserReadModel findByEmail(String email) throws UsernameNotFoundException, EmailNotFoundException {
        AppUser user = appUserRepository.findByEmail(email).orElseThrow(EmailNotFoundException::new);
        return AppUserMapper.toAppUserReadModel(user);
    }

    public AppUserReadModel updatePersonalData(AppUserWriteModel appUser) throws AppUserNotFoundException, EmailNotFoundException {
        if (!existsByEmail(appUser.getEmail())) {
            throw new AppUserNotFoundException();
        }

        AppUser toUpdate = appUserRepository
                .findByEmail(appUser.getEmail())
                .orElseThrow(AppUserNotFoundException::new);

        if (appUser.getFirstName() != null && !appUser.getFirstName().isBlank()) {
            toUpdate.setFirstName(appUser.getFirstName());
        }

        if (appUser.getLastName() != null && !appUser.getLastName().isBlank()) {
            toUpdate.setLastName(appUser.getLastName());
        }
        AppUser updated = appUserRepository.save(toUpdate);
        return AppUserMapper.toAppUserReadModel(updated);
    }

    public AppUserReadModel update(AppUserWriteModel appUser) throws AppUserNotFoundException, EmailNotFoundException {
        AppUser toUpdate = appUserRepository
                .findById(appUser.getId())
                .orElseThrow(AppUserNotFoundException::new);

        if (validateStringArgument(appUser.getFirstName())) {
            toUpdate.setFirstName(appUser.getFirstName());
        }
        if (validateStringArgument(appUser.getLastName())) {
            toUpdate.setLastName(appUser.getLastName());
        }
        if (validateStringArgument(appUser.getEmail())) {
            toUpdate.setEmail(appUser.getEmail());
        }
        if (validateStringArgument(appUser.getPassword())) {
            toUpdate.setPassword(appUser.getPassword());
            log.info("set new password: {}", appUser.getPassword());
        }
        if (validateStringArgument(appUser.getRole())) {
            toUpdate.setRole(appUser.getRole());
        }
        if(appUser.getLocked() != null){
            toUpdate.setLocked(appUser.getLocked());
        }
        if(appUser.getEnabled() != null){
            toUpdate.setEnabled(appUser.getEnabled());
        }

        AppUser updated = appUserRepository.save(toUpdate);
        return AppUserMapper.toAppUserReadModel(updated);
    }

    private boolean validateStringArgument(String argument) {
        return argument != null && !argument.isBlank();
    }

    public void confirmAccount(Long id) throws AppUserNotFoundException {
        appUserRepository.findById(id).orElseThrow(AppUserNotFoundException::new);

        appUserRepository.confirmAccount(id);
    }

    public AppUserReadModel create(AppUserWriteModel appUser) throws AppUserNotFoundException {
        if (appUser.getId() != null) {
            throw new IllegalArgumentException("Id could be present.");
        }
        if (appUserRepository.existsByEmail(appUser.getEmail())) {
            throw new IllegalArgumentException("User already exists.");
        }
        AppUser toCreate = AppUserMapper.toAppUser(appUser);
        toCreate.setConfirmed(false);
        AppUser created = appUserRepository.save(toCreate);

        return AppUserMapper.toAppUserReadModel(created);
    }

    public void anonymization(final Long id) throws AppUserNotFoundException, EmailNotFoundException {
        AppUser user = appUserRepository.findById(id).orElseThrow(AppUserNotFoundException::new);
        AppUserWriteModel toAnonymization = AppUserWriteModel.builder()
                .id(user.getId())
                .role("DELETED")
                .firstName(UUID.randomUUID().toString())
                .lastName(UUID.randomUUID().toString())
                .password(UUID.randomUUID().toString())
                .email(UUID.randomUUID().toString())
                .locked(true)
                .build();
        update(toAnonymization);
    }
}
