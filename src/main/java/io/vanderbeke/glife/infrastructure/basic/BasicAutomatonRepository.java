package io.vanderbeke.glife.infrastructure.basic;

import io.vanderbeke.glife.api.factory.AutomatonFactory;
import io.vanderbeke.glife.api.model.Automaton;
import io.vanderbeke.glife.api.model.Configuration;
import io.vanderbeke.glife.api.repository.AutomatonRepository;
import io.vanderbeke.glife.api.service.Renderer;
import io.vanderbeke.glife.core.Constants;
import io.vanderbeke.glife.core.PropertiesUtil;
import io.vavr.control.Try;

import javax.xml.bind.DatatypeConverter;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.MessageDigest;
import java.util.Optional;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

public class BasicAutomatonRepository implements AutomatonRepository {
    private static final Logger LOGGER = Logger.getLogger(BasicAutomatonRepository.class.getName());

    public static final String SAVE_FILE_NAME = "save.txt";
    public static final String MD5_FILE_NAME = "save.md5";
    public static final String SAVE_SUB_DIR = "data";

    private final Renderer renderer;
    private final AutomatonFactory automatonFactory;
    private final Path saveDirectory;
    private final Properties originalProperties;
    /**
     * Internal provider. Used for reloading/find. Not injected.
     */
    private final BasicProvider internalProdiver = new BasicProvider();

    public BasicAutomatonRepository(Renderer renderer, AutomatonFactory automatonFactory, Properties originalProperties) {
        this.renderer = renderer;
        this.automatonFactory = automatonFactory;
        this.originalProperties = originalProperties;

        String definedDir = Optional.of(Constants.CONF_SERVER_BASE_DIR_PATH)
                .map(System::getProperty)
                .filter(val -> !"".equals(val.trim()))
                .orElse(System.getProperty("user.dir"));

        this.saveDirectory = Paths.get(definedDir).resolve("data");
    }

    private Path getSaveFilePath() {
        return this.saveDirectory.resolve(SAVE_FILE_NAME);
    }

    private File getSaveFile() {
        return getSaveFilePath().toFile();
    }

    private File getChecksumFile() {
        return this.saveDirectory.resolve(MD5_FILE_NAME).toFile();
    }

    private Automaton unsafeSave(Automaton automaton, String renderedValue) throws Exception {
        if (!this.saveDirectory.toFile().exists()) {
            Files.createDirectory(this.saveDirectory);
        }

        MessageDigest messaDigest = MessageDigest.getInstance("MD5");
        messaDigest.update(renderedValue.getBytes());
        String checksum = DatatypeConverter.printHexBinary(messaDigest.digest());

        try (BufferedWriter saveFile = new BufferedWriter(new FileWriter(getSaveFile()));
             BufferedWriter checksumFile = new BufferedWriter(new FileWriter(getChecksumFile()))) {
            saveFile.write(renderedValue);
            checksumFile.write(checksum);
        }

        return automaton;
    }

    private Try<String> renderInMemory(Automaton automaton) {
        try (ByteArrayOutputStream out = new ByteArrayOutputStream()) {
            renderer.render(automaton, out);
            return Try.success(out.toString());
        } catch (IOException e) {
            return Try.failure(e);
        }
    }

    @Override
    public synchronized Try<Automaton> save(Automaton automaton) {
        return renderInMemory(automaton)
                .flatMap(content -> Try.of(() -> unsafeSave(automaton, content)));
    }

    @Override
    public synchronized boolean exists() {
        return getSaveFile().exists();
    }

    @Override
    public synchronized boolean isCorrupted() {
        if (!this.getSaveFile().exists() && this.getChecksumFile().exists()) {
            return true;
        } else if (this.getSaveFile().exists() && !this.getChecksumFile().exists()) {
            return true;
        } else if (!this.getSaveFile().exists() && !this.getChecksumFile().exists()) {
            return false;
        }

        try (BufferedReader checksumFile = new BufferedReader(new FileReader(this.getChecksumFile()))) {

            final StringBuilder checksumBuilder = new StringBuilder("");
            checksumFile.lines().forEach(checksumBuilder::append);

            String savedChecksum = checksumBuilder.toString().trim();

            MessageDigest messageDigest = MessageDigest.getInstance("MD5");
            messageDigest.update(Files.readAllBytes(getSaveFilePath()));
            String fileChecksum = DatatypeConverter.printHexBinary(messageDigest.digest());

            return !fileChecksum.equals(savedChecksum);
        } catch (Exception e) {
            return true;
        }
    }

    @Override
    public synchronized void delete() {
        if (this.getSaveFile().exists()) {
            this.getSaveFile().delete();
        }
        if (this.getChecksumFile().exists()) {
            this.getChecksumFile().delete();
        }
    }

    @Override
    public synchronized Try<Optional<Automaton>> find() {
        if (!exists()) {
            return Try.success(Optional.empty());
        } else if (exists() && isCorrupted()) {
            LOGGER.log(Level.WARNING, "Save file is corrupted. Deleting it.");
            delete();
            return Try.success(Optional.empty());
        }

        // HACK
        Properties props = new Properties(this.originalProperties);
        props.setProperty(Constants.CONF_PATTERN_FILE_PATH, getSaveFilePath().toString());

        Try<Automaton> result = internalProdiver.provide(props)
                .flatMap(this::checkConfiguration)
                .flatMap(automatonFactory::create);

        if (result.isFailure()) {
            LOGGER.log(Level.WARNING, "Save file is corrupted. Deleting it.");
            delete();
            return Try.success(Optional.empty());
        }

        return result.map(Optional::of);
        // HACK
    }

    private Try<Configuration> checkConfiguration(Configuration configuration) {
        int currentWidth = PropertiesUtil.getIntProperty(originalProperties,Constants.CONF_UNIVERSE_WIDTH, configuration.pattern().width());
        int currentHeight = PropertiesUtil.getIntProperty(originalProperties,Constants.CONF_UNIVERSE_HEIGHT, configuration.pattern().height());

        if (currentWidth != configuration.pattern().width() || currentHeight != configuration.pattern().height()) {
            return Try.failure(new IllegalStateException("Saved file has not the correct size."));
        }

        return Try.success(configuration);
    }
}
