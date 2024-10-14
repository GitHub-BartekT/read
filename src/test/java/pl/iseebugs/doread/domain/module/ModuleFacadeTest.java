package pl.iseebugs.doread.domain.module;

import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import pl.iseebugs.doread.BaseIT;
import pl.iseebugs.doread.domain.account.delete.AccountDeleteFacade;
import pl.iseebugs.doread.domain.module.dto.ModuleReadModel;
import pl.iseebugs.doread.domain.security.projection.LoginTokenDto;
import pl.iseebugs.doread.domain.user.AppUser;
import pl.iseebugs.doread.domain.user.AppUserFacade;
import pl.iseebugs.doread.domain.user.AppUserNotFoundException;
import pl.iseebugs.doread.domain.user.dto.AppUserReadModel;
import pl.iseebugs.doread.domain.user.dto.AppUserWriteModel;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowable;
import static org.junit.jupiter.api.Assertions.*;

@Transactional
class ModuleFacadeTest extends BaseIT {

    @Autowired
    ModuleFacade moduleFacade;

    @Autowired
    AppUserFacade appUserFacade;

    @Autowired
    AccountDeleteFacade accountDeleteFacade;

    @Test
    void getModuleByUserIdAndModuleId_throws_ModuleNotFoundException() {
        // Given + When
        Throwable e = catchThrowable(() -> moduleFacade.getModuleByUserIdAndModuleId(1L, 1L));

        // Then
        assertAll(
                () -> assertThat(e).isInstanceOf(ModuleNotFoundException.class)
        );
    }

    @Test
    void getModuleByUserIdAndModuleId() throws Exception {
        // Given
        AppUserReadModel savedUser = createTestUser();
        Long userId = savedUser.id();

        String moduleName = "fooModule";
        ModuleReadModel savedModule = moduleFacade.createModule(userId, moduleName);

        // + When
        ModuleReadModel result = moduleFacade.getModuleByUserIdAndModuleId(userId, 1L);

        // Then
        assertAll(
                () -> assertThat(result.getModuleName()).isEqualTo(moduleName)
        );

        // Clear Test Environment
        moduleFacade.deleteModule(savedModule.getId(), savedUser.id());
        deleteTestUser(savedUser.id());
    }

    @NotNull
    private AppUserReadModel createTestUser() throws AppUserNotFoundException {
        AppUserWriteModel newUser = AppUserWriteModel.builder()
                .email("foo@email.com")
                .password("fooPassword")
        .build();

        return appUserFacade.create(newUser);
    }

    private void deleteTestUser(Long userId) throws Exception {
        AppUserReadModel user = appUserFacade.findUserById(userId);
        LoginTokenDto deleteToken = accountDeleteFacade.deleteUser(user);
        accountDeleteFacade.confirmDeleteToken(deleteToken.token());
    }

}