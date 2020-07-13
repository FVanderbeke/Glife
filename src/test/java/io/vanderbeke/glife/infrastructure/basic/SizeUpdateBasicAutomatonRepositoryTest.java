package io.vanderbeke.glife.infrastructure.basic;

import io.vanderbeke.glife.api.model.Automaton;
import io.vanderbeke.glife.api.model.ExpansionStrategy;
import io.vanderbeke.glife.api.model.RuleSet;
import io.vanderbeke.glife.business.basic.BasicAutomaton;
import io.vanderbeke.glife.business.basic.BasicAutomatonState;
import io.vanderbeke.glife.business.basic.BasicUniverse;
import io.vanderbeke.glife.core.Constants;
import io.vavr.control.Try;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Optional;
import java.util.Properties;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

public class SizeUpdateBasicAutomatonRepositoryTest {
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
        Properties props = new Properties();
        props.setProperty(Constants.CONF_UNIVERSE_WIDTH, "5");
        props.setProperty(Constants.CONF_UNIVERSE_WIDTH, "5");
        repository = new BasicAutomatonRepository(renderer, factory, props);

        System.setProperty(Constants.CONF_SERVER_BASE_DIR_PATH, "");
    }

    @Test
    public void should_reset_if_saved_file_has_not_correct_size() throws Exception {
        // GIVEN
        BasicUniverse universe = new BasicUniverse(3, 3, ExpansionStrategy.FIXED, new int[]{1,4,7}, RuleSet.Basic.CONWAY.name());
        BasicAutomatonState state = new BasicAutomatonState(0, universe);
        File updated = new File(getClass().getResource("updated.txt").toURI());
        File md5 = new File(getClass().getResource("expected-md5.txt").toURI());
        Path sourceSave = updated.toPath();
        Path targetSave = workingDir.resolve(BasicAutomatonRepository.SAVE_SUB_DIR).resolve(BasicAutomatonRepository.SAVE_FILE_NAME);

        Path sourceMd5 = md5.toPath();
        Path targetMd5 = workingDir.resolve(BasicAutomatonRepository.SAVE_SUB_DIR).resolve(BasicAutomatonRepository.MD5_FILE_NAME);

        Files.createDirectory(workingDir.resolve(BasicAutomatonRepository.SAVE_SUB_DIR));

        Files.copy(sourceSave, targetSave);
        Files.copy(sourceMd5, targetMd5);

        assertThat(repository.exists()).isTrue();

        // WHEN
        Try<Optional<Automaton>> result = repository.find();

        // THEN
        assertThat(result.isFailure()).isFalse();
        assertThat(result.get()).isEmpty();
        assertThat(repository.exists()).isFalse();
        assertThat(new File(targetSave.toUri())).doesNotExist();
        assertThat(new File(targetMd5.toUri())).doesNotExist();
    }

}

