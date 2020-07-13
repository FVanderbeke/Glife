package io.vanderbeke.glife.infrastructure.basic;

import io.vanderbeke.glife.api.model.Automaton;
import io.vanderbeke.glife.api.model.ExpansionStrategy;
import io.vanderbeke.glife.api.model.RuleSet;
import io.vanderbeke.glife.business.basic.BasicAutomaton;
import io.vanderbeke.glife.business.basic.BasicAutomatonState;
import io.vanderbeke.glife.business.basic.BasicUniverse;
import io.vanderbeke.glife.core.Constants;;
import io.vavr.control.Try;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Properties;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

public class BasicAutomatonRepositoryTest {
    @Rule
    public TemporaryFolder folderRule = new TemporaryFolder();

    private BasicRenderer renderer = new BasicRenderer();
    private BasicAutomatonFactory factory = new BasicAutomatonFactory();
    private BasicAutomatonRepository repository;
    private Path workingDir;

    @Before
    public void before() throws Exception {
        workingDir = folderRule.newFolder("baseDir").toPath();
        System.setProperty(Constants.CONF_SERVER_BASE_DIR_PATH, workingDir.toString());
        repository = new BasicAutomatonRepository(renderer, factory, new Properties());
        System.setProperty(Constants.CONF_SERVER_BASE_DIR_PATH, "");
    }

    @Test
    public void should_save_automaton_from_empty_data() throws Exception {
        // GIVEN
        BasicUniverse universe = new BasicUniverse(3, 3, ExpansionStrategy.FIXED, new int[]{1,4,7}, RuleSet.Basic.CONWAY.name());
        BasicAutomatonState state = new BasicAutomatonState(0, universe);
        BasicAutomaton automaton = new BasicAutomaton(UUID.randomUUID(), state);
        String expectedMd5 = "AC232467BE403C552D16C854DFDD3602";
        File expectedSave = new File(getClass().getResource("correct.txt").toURI());

        // WHEN
        Try<Automaton> result = repository.save(automaton);

        // THEN
        assertThat(result.get()).isEqualTo(automaton);
        assertThat(workingDir.resolve(BasicAutomatonRepository.SAVE_SUB_DIR).toFile()).exists();
        assertThat(workingDir.resolve(BasicAutomatonRepository.SAVE_SUB_DIR).resolve(BasicAutomatonRepository.SAVE_FILE_NAME).toFile()).exists();
        assertThat(workingDir.resolve(BasicAutomatonRepository.SAVE_SUB_DIR).resolve(BasicAutomatonRepository.MD5_FILE_NAME).toFile()).exists();
        assertThat(workingDir.resolve(BasicAutomatonRepository.SAVE_SUB_DIR).resolve(BasicAutomatonRepository.MD5_FILE_NAME).toFile()).hasContent(expectedMd5);
        assertThat(workingDir.resolve(BasicAutomatonRepository.SAVE_SUB_DIR).resolve(BasicAutomatonRepository.SAVE_FILE_NAME).toFile()).hasContentEqualTo(expectedSave);
    }

    @Test
    public void should_check_corruption() throws Exception {
        // GIVEN
        BasicUniverse universe = new BasicUniverse(3, 3, ExpansionStrategy.FIXED, new int[]{1,4,7}, RuleSet.Basic.CONWAY.name());
        BasicAutomatonState state = new BasicAutomatonState(0, universe);
        BasicAutomaton automaton = new BasicAutomaton(UUID.randomUUID(), state);
        String expectedMd5 = "AC232467BE403C552D16C854DFDD3602";
        File corrupted = new File(getClass().getResource("corrupted.txt").toURI());

        // WHEN
        repository.save(automaton);

        // THEN
        assertThat(repository.isCorrupted()).isFalse();
        assertThat(workingDir.resolve(BasicAutomatonRepository.SAVE_SUB_DIR).resolve(BasicAutomatonRepository.MD5_FILE_NAME).toFile()).hasContent(expectedMd5);

        Path targetPath = workingDir.resolve(BasicAutomatonRepository.SAVE_SUB_DIR).resolve(BasicAutomatonRepository.SAVE_FILE_NAME);
        Path copyPath = workingDir.resolve(BasicAutomatonRepository.SAVE_SUB_DIR).resolve("old.txt");
        targetPath.toFile().renameTo(copyPath.toFile());
        Files.copy(corrupted.toPath(), targetPath);

        assertThat(repository.isCorrupted()).isTrue();
        assertThat(workingDir.resolve(BasicAutomatonRepository.SAVE_SUB_DIR).resolve(BasicAutomatonRepository.MD5_FILE_NAME).toFile()).hasContent(expectedMd5);

        targetPath.toFile().delete();
        Files.copy(copyPath, targetPath);
        assertThat(repository.isCorrupted()).isFalse();

        targetPath.toFile().delete();
        assertThat(repository.isCorrupted()).isTrue();

        Files.copy(copyPath, targetPath);
        assertThat(repository.isCorrupted()).isFalse();

        workingDir.resolve(BasicAutomatonRepository.SAVE_SUB_DIR).resolve(BasicAutomatonRepository.MD5_FILE_NAME).toFile().delete();
        assertThat(repository.isCorrupted()).isTrue();

        targetPath.toFile().delete();
        assertThat(repository.isCorrupted()).isFalse();
    }

    @Test
    public void should_delete_file() {
        // GIVEN
        BasicUniverse universe = new BasicUniverse(3, 3, ExpansionStrategy.FIXED, new int[]{1,4,7}, RuleSet.Basic.CONWAY.name());
        BasicAutomatonState state = new BasicAutomatonState(0, universe);
        BasicAutomaton automaton = new BasicAutomaton(UUID.randomUUID(), state);

        Path md5 = workingDir.resolve(BasicAutomatonRepository.SAVE_SUB_DIR).resolve(BasicAutomatonRepository.MD5_FILE_NAME);
        Path save = workingDir.resolve(BasicAutomatonRepository.SAVE_SUB_DIR).resolve(BasicAutomatonRepository.SAVE_FILE_NAME);
        // WHEN
        repository.save(automaton);

        assertThat(md5.toFile().exists()).isTrue();
        assertThat(save.toFile().exists()).isTrue();

        repository.delete();

        // THEN
        assertThat(md5.toFile().exists()).isFalse();
        assertThat(save.toFile().exists()).isFalse();
    }

    @Test
    public void should_check_exists() throws Exception {
        BasicUniverse universe = new BasicUniverse(3, 3, ExpansionStrategy.FIXED, new int[]{1,4,7}, RuleSet.Basic.CONWAY.name());
        BasicAutomatonState state = new BasicAutomatonState(0, universe);
        BasicAutomaton automaton = new BasicAutomaton(UUID.randomUUID(), state);

        assertThat(repository.exists()).isFalse();

        // WHEN
        repository.save(automaton);

        assertThat(repository.exists()).isTrue();

        repository.delete();

        assertThat(repository.exists()).isFalse();
    }


    @Test
    public void should_find_if_exists() throws Exception {
        // GIVEN
        BasicUniverse universe = new BasicUniverse(3, 3, ExpansionStrategy.FIXED, new int[]{1,4,7}, RuleSet.Basic.CONWAY.name());
        BasicAutomatonState state = new BasicAutomatonState(0, universe);
        BasicAutomaton automaton = new BasicAutomaton(UUID.randomUUID(), state);

        assertThat(repository.exists()).isFalse();

        // WHEN

        repository.save(automaton);

        assertThat(repository.exists()).isTrue();

        Automaton updatedAutomaton = repository.find().get().get();

        // THEN
        assertThat(updatedAutomaton.state().universe().width()).isEqualTo(universe.width());
        assertThat(updatedAutomaton.state().universe().height()).isEqualTo(universe.height());
        assertThat(updatedAutomaton.state().universe().aliveCells().toArray()).containsOnly(1,4,7);
    }

    @Test
    public void should_not_find_if_not_exists() {
        assertThat(repository.find().get()).isEmpty();
    }

    @Test
    public void should_delete_file_when_finding_corrupted() throws Exception {

        BasicUniverse universe = new BasicUniverse(3, 3, ExpansionStrategy.FIXED, new int[]{1,4,7}, RuleSet.Basic.CONWAY.name());
        BasicAutomatonState state = new BasicAutomatonState(0, universe);
        BasicAutomaton automaton = new BasicAutomaton(UUID.randomUUID(), state);
        String expectedMd5 = "AC232467BE403C552D16C854DFDD3602";
        File corrupted = new File(getClass().getResource("corrupted.txt").toURI());

        // WHEN
        repository.save(automaton);

        assertThat(repository.isCorrupted()).isFalse();
        assertThat(workingDir.resolve(BasicAutomatonRepository.SAVE_SUB_DIR).resolve(BasicAutomatonRepository.MD5_FILE_NAME).toFile()).hasContent(expectedMd5);

        Path targetPath = workingDir.resolve(BasicAutomatonRepository.SAVE_SUB_DIR).resolve(BasicAutomatonRepository.SAVE_FILE_NAME);
        Path copyPath = workingDir.resolve(BasicAutomatonRepository.SAVE_SUB_DIR).resolve("old.txt");
        targetPath.toFile().renameTo(copyPath.toFile());
        Files.copy(corrupted.toPath(), targetPath);

        assertThat(repository.isCorrupted()).isTrue();

        // THEN
        assertThat(repository.find().get()).isEmpty();
        assertThat(targetPath.toFile()).doesNotExist();
    }

    @Test
    public void should_update_save() throws Exception {
        // GIVEN
        BasicUniverse universe = new BasicUniverse(3, 3, ExpansionStrategy.FIXED, new int[]{1,4,7}, RuleSet.Basic.CONWAY.name());
        BasicAutomatonState state = new BasicAutomatonState(0, universe);
        BasicAutomaton automaton = new BasicAutomaton(UUID.randomUUID(), state);
        String notExpectedMd5 = "AC232467BE403C552D16C854DFDD3602";
        File updated = new File(getClass().getResource("updated.txt").toURI());

        // WHEN
        repository.save(automaton);

        universe = new BasicUniverse(3, 3, ExpansionStrategy.FIXED, new int[]{3,4,5}, RuleSet.Basic.CONWAY.name());
        state = new BasicAutomatonState(0, universe);
        automaton = new BasicAutomaton(UUID.randomUUID(), state);

        assertThat(repository.exists()).isTrue();

        repository.save(automaton);

        // THEN
        assertThat(repository.exists()).isTrue();

        Path md5 = workingDir.resolve(BasicAutomatonRepository.SAVE_SUB_DIR).resolve(BasicAutomatonRepository.MD5_FILE_NAME);
        Path save = workingDir.resolve(BasicAutomatonRepository.SAVE_SUB_DIR).resolve(BasicAutomatonRepository.SAVE_FILE_NAME);

        assertThat(save.toFile()).hasSameContentAs(updated);
        try {
            assertThat(md5.toFile()).hasContent(notExpectedMd5);
            Assert.fail();
        } catch (AssertionError ae) {
            // Good.
        }
    }

}
