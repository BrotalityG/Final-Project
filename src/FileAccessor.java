import java.io.BufferedReader;
import java.io.FileReader;
import java.util.ArrayList;

public class FileAccessor {
    private ArrayList<Object> constants = new ArrayList<Object>();
    private ArrayList<Object> settings = new ArrayList<Object>();

    public ArrayList<Object> getConstants() {
        try (FileReader read = new FileReader("Constants.DAT")) {
            BufferedReader reader = new BufferedReader(read);
            String line;
            while ((line = reader.readLine()) != null) {
                constants.add(line);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        parseConstants();

        return constants;
    }

    private void parseConstants() {
        for (int i = 0; i < constants.size(); i++) {
            String var = (String) constants.get(i);

            if (var.matches("[0-9]+")) {
                constants.set(i, Integer.parseInt(var));
            } else if (var.matches("[0-9]+.[0-9]+")) {
                constants.set(i, Double.parseDouble(var));
            } else if (var.matches("true") || var.matches("false")) {
                constants.set(i, Boolean.parseBoolean(var));
            }
        }
    }

    public ArrayList<Object> getSettings() {
        try (FileReader read = new FileReader("Settings.DAT")) {
            BufferedReader reader = new BufferedReader(read);
            String line;
            while ((line = reader.readLine()) != null) {
                settings.add(line);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        parseSettings();

        return settings;
    }

    private void parseSettings() {
        for (int i = 0; i < settings.size(); i++) {
            String var = (String) settings.get(i);

            if (var.matches("[0-9]+")) {
                settings.set(i, Integer.parseInt(var));
            } else if (var.matches("[0-9]+.[0-9]+")) {
                settings.set(i, Double.parseDouble(var));
            } else if (var.matches("true") || var.matches("false")) {
                settings.set(i, Boolean.parseBoolean(var));
            }
        }
    }
}
