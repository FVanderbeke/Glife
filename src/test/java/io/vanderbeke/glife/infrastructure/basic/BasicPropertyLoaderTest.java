package io.vanderbeke.glife.infrastructure.basic;

import io.vanderbeke.glife.api.service.PropertyLoader;
import io.vanderbeke.glife.core.Constants;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Properties;

import static org.assertj.core.api.Assertions.assertThat;

public class BasicPropertyLoaderTest {

    @Rule
    public TemporaryFolder configRule = new TemporaryFolder();

    private String originalUserDir;

    @Before
    public void before() {
        originalUserDir = System.getProperty("user.dir");
    }

    @Test
    public void should_create_loader_even_if_no_config_file_available() throws Exception {
        // GIVEN
        File userDir = configRule.newFolder();

        // THEN
        PropertyLoader propertyLoader = new BasicPropertyLoader();
    }

    @Test
    public void should_take_system_properties() throws Exception {
        // GIVEN
        File userDir = configRule.newFolder();
        File emptyConfigFile = new File(userDir, BasicPropertyLoader.CONFIG_FILE_NAME);
        emptyConfigFile.createNewFile();
        PropertyLoader propertyLoader = new BasicPropertyLoader();

        System.setProperty("user.dir", userDir.getPath());
        System.setProperty(Constants.CONF_CLIENT_SERVER_PORT, "52");

        // WHEN
        Properties props = propertyLoader.load().get();

        // THEN
        assertThat(props.get(Constants.CONF_CLIENT_SERVER_PORT)).isEqualTo("52");
    }

    @Test
    public void should_override_properties_with_config_file() throws Exception {
        // GIVEN
        File userDir = configRule.newFolder();
        System.setProperty("user.dir", userDir.getPath());
        PropertyLoader propertyLoader = new BasicPropertyLoader();

        Path target = Paths.get(userDir.toURI()).resolve(BasicPropertyLoader.CONFIG_FILE_NAME);
        Path source = Paths.get(this.getClass().getResource(".").toURI()).resolve("full-config.properties");

        Files.copy(source, target);

        System.setProperty(Constants.CONF_CLIENT_SERVER_PORT, "52");

        // WHEN
        Properties props = propertyLoader.load().get();

        // THEN
        assertThat(props.get(Constants.CONF_CLIENT_SERVER_PORT)).isEqualTo("5000");
    }

    @Test
    public void should_override_system_properties_with_command() throws Exception {
        // GIVEN
        File userDir = configRule.newFolder();
        System.setProperty("user.dir", userDir.getPath());
        PropertyLoader propertyLoader = new BasicPropertyLoader();

        Path target = Paths.get(userDir.toURI()).resolve(BasicPropertyLoader.CONFIG_FILE_NAME);
        Path source = Paths.get(this.getClass().getResource(".").toURI()).resolve("full-config.properties");

        Files.copy(source, target);

        System.setProperty(Constants.CONF_CLIENT_SERVER_PORT, "52");

        // WHEN
        Properties props = propertyLoader.load("-JVMD=server.client.port:10", "-port=12").get();

        // THEN
        assertThat(props.get(Constants.CONF_CLIENT_SERVER_PORT)).isEqualTo("12");

    }

    @Test
    public void should_override_properties_with_command() throws Exception {
        // GIVEN
        File userDir = configRule.newFolder();
        System.setProperty("user.dir", userDir.getPath());
        PropertyLoader propertyLoader = new BasicPropertyLoader();

        Path target = Paths.get(userDir.toURI()).resolve(BasicPropertyLoader.CONFIG_FILE_NAME);
        Path source = Paths.get(this.getClass().getResource(".").toURI()).resolve("full-config.properties");

        Files.copy(source, target);

        System.setProperty(Constants.CONF_CLIENT_SERVER_PORT, "52");

        // WHEN
        Properties props = propertyLoader.load("-JVMD=server.client.port:10").get();

        // THEN
        assertThat(props.get(Constants.CONF_CLIENT_SERVER_PORT)).isEqualTo("10");

    }

    @Test
    public void should_clean_command_args() throws Exception {
        // GIVEN
        File userDir = configRule.newFolder();
        System.setProperty("user.dir", userDir.getPath());
        PropertyLoader propertyLoader = new BasicPropertyLoader();

        Path target = Paths.get(userDir.toURI()).resolve(BasicPropertyLoader.CONFIG_FILE_NAME);
        Path source = Paths.get(this.getClass().getResource(".").toURI()).resolve("full-config.properties");

        Files.copy(source, target);

        System.setProperty(Constants.CONF_CLIENT_SERVER_PORT, "52");

        // WHEN
        Properties props = propertyLoader.load("-JVMD=server.client.port:10", "'-port=12'").get();

        // THEN
        assertThat(props.get(Constants.CONF_CLIENT_SERVER_PORT)).isEqualTo("12");
    }

    @Test
    public void should_clean_sub_values_in_command_args() throws Exception {
        // GIVEN
        File userDir = configRule.newFolder();
        System.setProperty("user.dir", userDir.getPath());
        PropertyLoader propertyLoader = new BasicPropertyLoader();

        Path target = Paths.get(userDir.toURI()).resolve(BasicPropertyLoader.CONFIG_FILE_NAME);
        Path source = Paths.get(this.getClass().getResource(".").toURI()).resolve("full-config.properties");

        Files.copy(source, target);

        System.setProperty(Constants.CONF_CLIENT_SERVER_PORT, "52");

        // WHEN
        Properties props = propertyLoader.load("-port='12'", "-admin-port=\"5\"").get();

        // THEN
        assertThat(props.get(Constants.CONF_CLIENT_SERVER_PORT)).isEqualTo("12");
        assertThat(props.get(Constants.CONF_ADMIN_SERVER_PORT)).isEqualTo("5");
    }

    @After
    public void after() {
        System.setProperty("user.dir", originalUserDir);
        System.setProperty(Constants.CONF_CLIENT_SERVER_PORT, "");
    }


}
