package pl.iseebugs.doread.domain.module;

import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Component;
import pl.iseebugs.doread.domain.module.dto.ModuleReadModel;
import pl.iseebugs.doread.domain.sentence.SentenceFacade;
import pl.iseebugs.doread.domain.user.AppUserFacade;
import pl.iseebugs.doread.domain.user.AppUserNotFoundException;
import pl.iseebugs.doread.domain.user.dto.AppUserReadModel;
import pl.iseebugs.doread.domain.user.dto.AppUserWriteModel;

import java.util.List;

@Log4j2
@Component
@AllArgsConstructor
class ModuleTestHelper {

    AppUserFacade appUserFacade;
    ModuleFacade moduleFacade;

    AppUserReadModel createTestUser(String email) throws AppUserNotFoundException {
        AppUserWriteModel newUser = AppUserWriteModel.builder()
                .email(email)
                .password("fooPassword")
                .build();

        return appUserFacade.create(newUser);
    }

    AppUserReadModel createTestUser() throws AppUserNotFoundException {
        return createTestUser("some.foo.bar@email.com");
    }

    private void deleteTestUser(Long userId) throws Exception {
        AppUserReadModel user = appUserFacade.findUserById(userId);
        appUserFacade.anonymization(user.id());
    }

    void createTestModules(Long userId, int modulesQuantity) throws AppUserNotFoundException {
        for (int i = 0; i < modulesQuantity; i++) {
            String moduleName = "testModule_" + (i + 1);
            moduleFacade.createModule(userId, moduleName);
            log.info("Created module: {}, for user: {}", moduleName, userId);
        }
    }

    void deleteAllUserModules(Long userId) {
        List<ModuleReadModel> userModules = moduleFacade.getModulesByUserId(userId);
        for (ModuleReadModel module : userModules) {
            moduleFacade.deleteModule(module.getId(), userId);
        }
    }

    void clearUserData(final Long userId) throws Exception {
        deleteAllUserModules(userId);
        deleteTestUser(userId);
    }
}
