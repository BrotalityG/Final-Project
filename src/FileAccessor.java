import java.awt.Color;
import java.io.BufferedReader;
import java.io.File;
// import java.nio.file.Files;
// import java.nio.file.Path;
import java.io.FileReader;
import java.io.FileWriter;
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

    private void tryWriteSettings(ArrayList<String> newSettings) {
        try (FileWriter write = new FileWriter("Settings.DAT")) {
            for (String var : newSettings) {
                write.write(var + "\n");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private boolean checkIfSaveExists(String filename) {
        File f = new File("Saves", filename + ".DAT");

        return f.exists();
    }

    private String[] packBodies(ArrayList<GenericBody> bodies) {
        String[] packedBodies = new String[bodies.size()];

        for (int i = 0; i < bodies.size(); i++) {
            GenericBody body = bodies.get(i);
            String packedBody = "";

            if (body instanceof RectBody) {
                packedBody += "RectBody";
            } else if (body instanceof SphereBody) {
                packedBody += "SphereBody";
            }

            packedBody += " " + body.getPosition()[0] + " " + body.getPosition()[1] + " " + body.getVelocity()[0] + " " + body.getVelocity()[1] + " " + body.getSize() + " " + body.getElasticity() + " " + body.getMass() + " " + body.getColor().getRed() + " " + body.getColor().getGreen() + " " + body.getColor().getBlue() + " " + body.canCollide() + " " + body.isStatic() + " " + body.getID();

            packedBodies[i] = packedBody;
        }

        return packedBodies;
    }

    private GenericBody unpackBody(String packedBody) {
        String[] bodyData = packedBody.split("[\s]+");
        GenericBody body = null;

        if (bodyData[0].equals("RectBody")) {
            body = new RectBody(Double.parseDouble(bodyData[7]), Integer.parseInt(bodyData[5]), Double.parseDouble(bodyData[6]), new int[] {Integer.parseInt(bodyData[1]), Integer.parseInt(bodyData[2])}, Boolean.parseBoolean(bodyData[11]), Boolean.parseBoolean(bodyData[12]), new Color(Integer.parseInt(bodyData[8]), Integer.parseInt(bodyData[9]), Integer.parseInt(bodyData[10])), Integer.parseInt(bodyData[13]));
        } else if (bodyData[0].equals("SphereBody")) {
            body = new SphereBody(Double.parseDouble(bodyData[7]), Integer.parseInt(bodyData[5]), Double.parseDouble(bodyData[6]), new int[] {Integer.parseInt(bodyData[1]), Integer.parseInt(bodyData[2])}, Boolean.parseBoolean(bodyData[11]), Boolean.parseBoolean(bodyData[12]), new Color(Integer.parseInt(bodyData[8]), Integer.parseInt(bodyData[9]), Integer.parseInt(bodyData[10])), Integer.parseInt(bodyData[13]));
        }

        if (body != null) {
            body.setVelocity(new double[]{Double.parseDouble(bodyData[3]), Double.parseDouble(bodyData[4])});
        }

        return body;
    }

    public void saveToFile(String filename, ArrayList<GenericBody> bodies, GuiClass gui, boolean overwrite) {
        if (!overwrite && checkIfSaveExists(filename)) {
            gui.overwriteMenu(filename);
            return;
        }

        String[] packedBodies = packBodies(bodies);
        try (FileWriter write = new FileWriter(new File("Saves", filename + ".DAT"))) {
            for (String body : packedBodies) {
                write.write(body + "\n");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void writeSettings(ArrayList<Object> newSettings) {
        ArrayList<String> newList = new ArrayList<String>();

        for (int i = 0; i < newSettings.size(); i++) {
            Object var = newSettings.get(i);
            
            if (var instanceof Integer) {
                newList.add(Integer.toString((int) var));
            } else if (var instanceof Double) {
                newList.add(Double.toString((double) var));
            } else if (var instanceof Boolean) {
                newList.add(Boolean.toString((boolean) var));
            }
        }

        tryWriteSettings(newList);
    }

    public File[] getAllSaves() {
        File saves = new File("Saves");
        File[] saveFiles = saves.listFiles();

        return saveFiles;
    }

    public ArrayList<GenericBody> loadFromFile(String filename, GuiClass gui) {
        ArrayList<GenericBody> bodies = new ArrayList<GenericBody>();

        try (FileReader read = new FileReader(new File("Saves", filename))) {
            BufferedReader reader = new BufferedReader(read);
            String line;
            while ((line = reader.readLine()) != null) {
                bodies.add(unpackBody(line));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return bodies;
    }
}
