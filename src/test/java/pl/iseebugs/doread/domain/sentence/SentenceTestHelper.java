package pl.iseebugs.doread.domain.sentence;

import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import pl.iseebugs.doread.domain.module.ModuleFacade;
import pl.iseebugs.doread.domain.module.ModuleNotFoundException;
import pl.iseebugs.doread.domain.module.dto.ModuleReadModel;
import pl.iseebugs.doread.domain.sentence.dto.SentenceWriteModel;
import pl.iseebugs.doread.domain.user.AppUserFacade;
import pl.iseebugs.doread.domain.user.AppUserNotFoundException;
import pl.iseebugs.doread.domain.user.dto.AppUserReadModel;
import pl.iseebugs.doread.domain.user.dto.AppUserWriteModel;

import java.util.ArrayList;
import java.util.List;

@Log4j2
@Component
@AllArgsConstructor
class SentenceTestHelper {

    AppUserFacade appUserFacade;
    SentenceFacade sentenceFacade;
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

    void createTestSentences(Long userId, Long moduleId, int modulesQuantity) throws AppUserNotFoundException {
        for (int i = 0; i < modulesQuantity; i++) {
            String sentenceText = "testSentence_" + (i + 1);
            sentenceFacade.create(userId, moduleId, sentenceText);
            log.info("Created sentence: {}, for user: {}", sentenceText, userId);
        }
    }

    private void deleteAllSentencesInModule(Long userId, Long moduleId){
        List<SentenceWriteModel> sentenceList = new ArrayList<>();
        sentenceFacade.rearrangeSetByModuleId(userId, moduleId, sentenceList);
    }

    void deleteAllUserModulesAndSentences(Long userId) throws ModuleNotFoundException {
        List<ModuleReadModel> userModules = moduleFacade.getModulesByUserId(userId);
        for (ModuleReadModel module : userModules) {
            deleteAllSentencesInModule(userId, module.getId());
            moduleFacade.deleteModule(module.getId(), userId);
        }
    }

    void clearUserData(final Long userId) throws Exception {
        deleteAllUserModulesAndSentences(userId);
        deleteTestUser(userId);
    }
}
