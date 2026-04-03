import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.*;
import org.xml.sax.SAXException;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

/**
 * AccountDatabase manages and holds all accounts,
 * and returns account data for validation.
 * Persists account data to an XML file.
 */
public class AccountDatabase {

    // Fields
    private static final String FILE_PATH = "assets/accounts.xml";

    private ArrayList<String> usernames = new ArrayList<>();
    private ArrayList<String> passwords = new ArrayList<>();
    private ArrayList<Boolean> adminStatus = new ArrayList<>();
    private ArrayList<ArrayList<String>> personalCollections = new ArrayList<>();

    // Constructors

    /**
     * Constructs an AccountDatabase and loads existing accounts from XML file.
     */
    public AccountDatabase() {
        loadFromFile();
    }

    // Methods

    /**
     * Creates and stores a new account with an empty personal collection, then saves to XML file.
     */
    public void setAccountInfo(String username, String password, boolean isAdmin) {
        usernames.add(username);
        passwords.add(password);
        adminStatus.add(isAdmin);
        personalCollections.add(new ArrayList<>());
        saveToFile();
        System.out.println("Account created for: " + username);
    }

    /**
     * Checks if a username already exists in the database.
     */
    public boolean userExists(String username) {
        for (int i = 0; i < usernames.size(); i++) {
            if (usernames.get(i).equals(username)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Validates login credentials against stored accounts.
     */
    public boolean validateLogin(String username, String password) {
        for (int i = 0; i < usernames.size(); i++) {
            if (usernames.get(i).equals(username) && passwords.get(i).equals(password)) {
                System.out.println("Login successful for: " + username);
                return true;
            }
        }
        System.out.println("Validation failed: no matching account found.");
        return false;
    }

    /**
     * Adds a game ID to a user's personal collection and saves to XML file.
     */
    public void addGameToCollection(String username, String gameId) {
        for (int i = 0; i < usernames.size(); i++) {
            if (usernames.get(i).equals(username)) {
                personalCollections.get(i).add(gameId);
                saveToFile();
                return;
            }
        }
    }

    /**
     * Removes a game ID from a user's personal collection and saves to XML file.
     */
    public void removeGameFromCollection(String username, String gameId) {
        for (int i = 0; i < usernames.size(); i++) {
            if (usernames.get(i).equals(username)) {
                personalCollections.get(i).remove(gameId);
                saveToFile();
                return;
            }
        }
    }

    /**
     * Gets the personal collection of game IDs for a given user.
     */
    public ArrayList<String> getPersonalCollection(String username) {
        for (int i = 0; i < usernames.size(); i++) {
            if (usernames.get(i).equals(username)) {
                return personalCollections.get(i);
            }
        }
        return new ArrayList<>();
    }

    /**
     * Saves all accounts to the XML file.
     */
    private void saveToFile() {
        try {
            //Building the doc to write
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder docBuilder = factory.newDocumentBuilder();
            Document document = docBuilder.newDocument();

            //Root element
            Element root = document.createElement("accounts");
            document.appendChild(root);

            //Loop through all accounts and append each as an element
            for (int i = 0; i < usernames.size(); i++) {
                Element accountElement = document.createElement("account");

                Element username = document.createElement("username");
                username.setTextContent(usernames.get(i));
                accountElement.appendChild(username);

                Element password = document.createElement("password");
                password.setTextContent(passwords.get(i));
                accountElement.appendChild(password);

                Element isAdmin = document.createElement("isAdmin");
                isAdmin.setTextContent(String.valueOf(adminStatus.get(i)));
                accountElement.appendChild(isAdmin);

                //Personal collection table of game IDs
                Element personalCollection = document.createElement("personalCollection");
                for (String gameId : personalCollections.get(i)) {
                    Element gameIdElement = document.createElement("gameId");
                    gameIdElement.setTextContent(gameId);
                    personalCollection.appendChild(gameIdElement);
                }
                accountElement.appendChild(personalCollection);

                root.appendChild(accountElement);
            }

            //Write document to file
            TransformerFactory transformerFactory = TransformerFactory.newInstance();
            Transformer transformer = transformerFactory.newTransformer();
            transformer.setOutputProperty(OutputKeys.INDENT, "yes");
            transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "4");
            transformer.transform(new DOMSource(document), new StreamResult(new File(FILE_PATH)));

        } catch (ParserConfigurationException | TransformerException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Loads accounts from the XML file if it exists.
     */
    private void loadFromFile() {
        //Setting up infile
        File accountFile = new File(FILE_PATH);
        if (!accountFile.exists()) {
            return;
        }

        try {
            //Building the doc to parse
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder docBuilder = factory.newDocumentBuilder();
            Document document = docBuilder.parse(accountFile);
            document.getDocumentElement().normalize();

            //Loop through all account elements
            NodeList list = document.getElementsByTagName("account");
            for (int i = 0; i < list.getLength(); i++) {
                Node node = list.item(i);
                if (node.getNodeType() == Node.ELEMENT_NODE) {
                    Element element = (Element) node;

                    usernames.add(element.getElementsByTagName("username").item(0).getTextContent());
                    passwords.add(element.getElementsByTagName("password").item(0).getTextContent());
                    adminStatus.add(Boolean.parseBoolean(element.getElementsByTagName("isAdmin").item(0).getTextContent()));

                    //Load personal collection table of game IDs
                    ArrayList<String> gameIds = new ArrayList<>();
                    NodeList gameIdList = element.getElementsByTagName("gameId");
                    for (int k = 0; k < gameIdList.getLength(); k++) {
                        gameIds.add(gameIdList.item(k).getTextContent());
                    }
                    personalCollections.add(gameIds);
                }
            }

        } catch (ParserConfigurationException | IOException | SAXException e) {
            throw new RuntimeException(e);
        }
    }
}
