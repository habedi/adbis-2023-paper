package base;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Properties;

public class SettingsLoader {

    private final String configFilePath;

    public SettingsLoader(String configFilePath) {
        this.configFilePath = configFilePath;
    }

    public Properties loadConfigs() {
        Properties prop = new Properties();
        String fileName = this.configFilePath;
        try (FileInputStream fis = new FileInputStream(fileName)) {
            prop.load(fis);
        } catch (FileNotFoundException e) {
            System.err.println(e);
        } catch (IOException e) {
            System.err.println(e);
        }
        return prop;
    }

}
