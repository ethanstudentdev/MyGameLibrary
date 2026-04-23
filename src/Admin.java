import org.w3c.dom.*;
import javax.xml.parsers.*;
import javax.xml.transform.*;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.OutputKeys;
import java.io.File;

/**
 * Admin manages the application config file (config.xml).
 * It stores the paths for accounts and games files.
 * Any user with isAdmin = true can change these paths.
 */
public class Admin{

    static final String CONFIG_PATH = "assets/config.xml";

    private String accountsFile;
    private String gamesFile;

    /**
     * Loads config from assets/config.xml on creation.
     */
    public Admin() {
        // Default values in case the file is missing
        accountsFile = "assets/accounts.xml";
        gamesFile = "bgg90Games.xml";
        loadConfig();
    }

    public String getAccountsFile() {
        return accountsFile;
    }

    // TODO: Use method with the ui to allow admin users to change the accounts file path
    public void setAccountsFile(String path) {
        this.accountsFile = path;
        saveConfig();
    }
    // TODO: Use method with the ui to allow admin users to change the games file path
    public String getGamesFile() {
        return gamesFile;
    }

    // TODO: Use method with the ui to allow admin users to change the games file path
    public void setGamesFile(String path) {
        this.gamesFile = path;
        saveConfig();
    }

    /**
     * Loads paths from config.xml.
     */
    private void loadConfig() {
        File file = new File(CONFIG_PATH);
        if (!file.exists()) {
            return;
        }
        try {
            DocumentBuilder builder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
            Document doc = builder.parse(file);
            doc.getDocumentElement().normalize();

            NodeList accountsNode = doc.getElementsByTagName("accountsFile");
            if (accountsNode.getLength() > 0) {
                accountsFile = accountsNode.item(0).getTextContent();
            }

            NodeList gamesNode = doc.getElementsByTagName("gamesFile");
            if (gamesNode.getLength() > 0) {
                gamesFile = gamesNode.item(0).getTextContent();
            }

        } catch (Exception e) {
            System.out.println("Could not load config: " + e.getMessage());
        }
    }

    /**
     * Saves current paths to config.xml.
     */
    private void saveConfig() {
        try {
            DocumentBuilder builder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
            Document doc = builder.newDocument();

            Element root = doc.createElement("config");
            doc.appendChild(root);

            Element accounts = doc.createElement("accountsFile");
            accounts.setTextContent(accountsFile);
            root.appendChild(accounts);

            Element games = doc.createElement("gamesFile");
            games.setTextContent(gamesFile);
            root.appendChild(games);

            Transformer transformer = TransformerFactory.newInstance().newTransformer();
            transformer.setOutputProperty(OutputKeys.INDENT, "yes");
            transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "4");
            transformer.transform(new DOMSource(doc), new StreamResult(new File(CONFIG_PATH)));

        } catch (Exception e) {
            System.out.println("Could not save config: " + e.getMessage());
        }
    }
}
