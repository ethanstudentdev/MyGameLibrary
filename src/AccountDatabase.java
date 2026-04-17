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
import java.util.List;
import java.util.Map;

/**
 * AccountDatabase manages and holds all accounts,
 * and returns account data for validation.
 * Persists account data to an XML file.
 */
public class AccountDatabase {

    private static final String FILE_PATH = "assets/accounts.xml";

    private final ArrayList<String> usernames = new ArrayList<>();
    private final ArrayList<String> passwords = new ArrayList<>();
    private final ArrayList<Boolean> adminStatus = new ArrayList<>();
    private final ArrayList<ArrayList<SavedCollection>> personalCollections = new ArrayList<>();
    private final java.util.Map<String, java.util.Map<String, Integer>> userRatings = new java.util.HashMap<>();
    private final java.util.Map<String, java.util.Map<String, String>> userReviews = new java.util.HashMap<>();

    // Helper object used to store a user's named collection and the game IDs assigned to it.
    private static class SavedCollection {
        private final String title;
        private final ArrayList<String> gameIds;

        private SavedCollection(String title) {
            this.title = title;
            this.gameIds = new ArrayList<>();
        }

        private String getTitle() {
            return title;
        }

        private List<String> getGameIds() {
            return gameIds;
        }
    }

    public AccountDatabase() {
        loadFromFile();
    }

    public void setAccountInfo(String username, String password, boolean isAdmin) {
        usernames.add(username);
        passwords.add(password);
        adminStatus.add(isAdmin);

        ArrayList<SavedCollection> savedCollections = new ArrayList<>();
        savedCollections.add(new SavedCollection("Favorites"));
        personalCollections.add(savedCollections);

        saveToFile();
        System.out.println("Account created for: " + username);
    }

    /**
     * Checks if a username already exists in the database.
     */
    public boolean userExists(String username) {
        for (String existing : usernames) {
            if (existing.equals(username)) {
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

    public void createCollection(String username, String collectionName) {
        int userIndex = findUserIndex(username);
        if (userIndex == -1 || collectionName == null || collectionName.isBlank()) {
            return;
        }

        ArrayList<SavedCollection> collections = personalCollections.get(userIndex);
        for (SavedCollection collection : collections) {
            if (collection.getTitle().equalsIgnoreCase(collectionName.trim())) {
                return;
            }
        }

        collections.add(new SavedCollection(collectionName.trim()));
        saveToFile();
    }

    public void removeCollection(String username, String collectionName) {
        int userIndex = findUserIndex(username);
        if (userIndex == -1 || collectionName == null) {
            return;
        }

        ArrayList<SavedCollection> collections = personalCollections.get(userIndex);
        collections.removeIf(collection -> collection.getTitle().equals(collectionName));
        saveToFile();
    }

    public List<String> getCollectionNames(String username) {
        int userIndex = findUserIndex(username);
        if (userIndex == -1) {
            return new ArrayList<>();
        }

        List<String> names = new ArrayList<>();
        for (SavedCollection collection : personalCollections.get(userIndex)) {
            names.add(collection.getTitle());
        }
        return names;
    }

    public List<String> getCollectionGameIds(String username, String collectionName) {
        int userIndex = findUserIndex(username);
        if (userIndex == -1 || collectionName == null) {
            return new ArrayList<>();
        }

        for (SavedCollection collection : personalCollections.get(userIndex)) {
            if (collection.getTitle().equals(collectionName)) {
                return new ArrayList<>(collection.getGameIds());
            }
        }
        return new ArrayList<>();
    }

    public void addGameToCollection(String username, String collectionName, String gameId) {
        int userIndex = findUserIndex(username);
        if (userIndex == -1 || collectionName == null || gameId == null || gameId.isBlank()) {
            return;
        }

        for (SavedCollection collection : personalCollections.get(userIndex)) {
            if (collection.getTitle().equals(collectionName)) {
                if (!collection.getGameIds().contains(gameId)) {
                    collection.getGameIds().add(gameId);
                    saveToFile();
                }
                return;
            }
        }
    }

    public void removeGameFromCollection(String username, String collectionName, String gameId) {
        int userIndex = findUserIndex(username);
        if (userIndex == -1 || collectionName == null || gameId == null) {
            return;
        }

        for (SavedCollection collection : personalCollections.get(userIndex)) {
            if (collection.getTitle().equals(collectionName)) {
                collection.getGameIds().remove(gameId);
                saveToFile();
                return;
            }
        }
    }

    public List<String> getPersonalCollection(String username) {
        return getCollectionGameIds(username, "Favorites");
    }

    public void addGameToCollection(String username, String gameId) {
        addGameToCollection(username, "Favorites", gameId);
    }

    public void removeGameFromCollection(String username, String gameId) {
        removeGameFromCollection(username, "Favorites", gameId);
    }

    public int getUserRating(String username, String gameId) {
        Map<String, Integer> ratings = userRatings.get(username);
        if (ratings != null) {
            return ratings.getOrDefault(gameId, 0);
        }
        return 0;
    }

    public void setUserRating(String username, String gameId, int rating) {
        userRatings.computeIfAbsent(username, k -> new java.util.HashMap<>()).put(gameId, rating);
        saveToFile();
    }

    public String getUserReview(String username, String gameId) {
        Map<String, String> reviews = userReviews.get(username);
        if (reviews != null) {
            return reviews.get(gameId);
        }
        return null;
    }

    public void setUserReview(String username, String gameId, String review) {
        userReviews.computeIfAbsent(username, k -> new java.util.HashMap<>()).put(gameId, review);
        saveToFile();
    }

    public List<String> getUserReviews(String username, String gameId) {
        // Assuming multiple reviews? But for now, single review per game per user.
        String review = getUserReview(username, gameId);
        return review != null ? java.util.Arrays.asList(review) : new ArrayList<>();
    }

    private int findUserIndex(String username) {
        for (int i = 0; i < usernames.size(); i++) {
            if (usernames.get(i).equals(username)) {
                return i;
            }
        }
        return -1;
    }

    // Persist all accounts and their saved collections to XML disk storage.
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

                Element collectionsElement = document.createElement("collections");
                for (SavedCollection savedCollection : personalCollections.get(i)) {
                    Element collectionElement = document.createElement("collection");

                    Element titleElement = document.createElement("title");
                    titleElement.setTextContent(savedCollection.getTitle());
                    collectionElement.appendChild(titleElement);

                    for (String gameId : savedCollection.getGameIds()) {
                        Element gameIdElement = document.createElement("gameId");
                        gameIdElement.setTextContent(gameId);
                        collectionElement.appendChild(gameIdElement);
                    }
                    collectionsElement.appendChild(collectionElement);
                }
                accountElement.appendChild(collectionsElement);

                // Add ratings
                Element ratingsElement = document.createElement("ratings");
                Map<String, Integer> ratings = userRatings.get(usernames.get(i));
                if (ratings != null) {
                    for (Map.Entry<String, Integer> entry : ratings.entrySet()) {
                        Element ratingElement = document.createElement("rating");
                        ratingElement.setAttribute("gameId", entry.getKey());
                        ratingElement.setAttribute("value", String.valueOf(entry.getValue()));
                        ratingsElement.appendChild(ratingElement);
                    }
                }
                accountElement.appendChild(ratingsElement);

                // Add reviews
                Element reviewsElement = document.createElement("reviews");
                Map<String, String> reviews = userReviews.get(usernames.get(i));
                if (reviews != null) {
                    for (Map.Entry<String, String> entry : reviews.entrySet()) {
                        Element reviewElement = document.createElement("review");
                        reviewElement.setAttribute("gameId", entry.getKey());
                        reviewElement.setTextContent(entry.getValue());
                        reviewsElement.appendChild(reviewElement);
                    }
                }
                accountElement.appendChild(reviewsElement);

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

    // Load accounts and collections from the XML file into memory.
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
                if (node.getNodeType() != Node.ELEMENT_NODE) {
                    continue;
                }

                Element element = (Element) node;
                usernames.add(element.getElementsByTagName("username").item(0).getTextContent());
                passwords.add(element.getElementsByTagName("password").item(0).getTextContent());
                adminStatus.add(Boolean.parseBoolean(element.getElementsByTagName("isAdmin").item(0).getTextContent()));

                ArrayList<SavedCollection> savedCollections = new ArrayList<>();
                NodeList collectionNodes = element.getElementsByTagName("collection");
                for (int k = 0; k < collectionNodes.getLength(); k++) {
                    Node collectionNode = collectionNodes.item(k);
                    if (collectionNode.getNodeType() != Node.ELEMENT_NODE) {
                        continue;
                    }

                    Element collectionElement = (Element) collectionNode;
                    String title = "Unnamed";
                    if (collectionElement.getElementsByTagName("title").getLength() > 0) {
                        title = collectionElement.getElementsByTagName("title").item(0).getTextContent();
                    }

                    SavedCollection savedCollection = new SavedCollection(title);
                    NodeList gameIdList = collectionElement.getElementsByTagName("gameId");
                    for (int j = 0; j < gameIdList.getLength(); j++) {
                        savedCollection.getGameIds().add(gameIdList.item(j).getTextContent());
                    }
                    savedCollections.add(savedCollection);
                }

                // Load ratings
                NodeList ratingsNodes = element.getElementsByTagName("rating");
                Map<String, Integer> ratingsMap = new java.util.HashMap<>();
                for (int k = 0; k < ratingsNodes.getLength(); k++) {
                    Node ratingNode = ratingsNodes.item(k);
                    if (ratingNode.getNodeType() != Node.ELEMENT_NODE) continue;
                    Element ratingElement = (Element) ratingNode;
                    String gameId = ratingElement.getAttribute("gameId");
                    int value = Integer.parseInt(ratingElement.getAttribute("value"));
                    ratingsMap.put(gameId, value);
                }
                userRatings.put(usernames.get(i), ratingsMap);

                // Load reviews
                NodeList reviewsNodes = element.getElementsByTagName("review");
                Map<String, String> reviewsMap = new java.util.HashMap<>();
                for (int k = 0; k < reviewsNodes.getLength(); k++) {
                    Node reviewNode = reviewsNodes.item(k);
                    if (reviewNode.getNodeType() != Node.ELEMENT_NODE) continue;
                    Element reviewElement = (Element) reviewNode;
                    String gameId = reviewElement.getAttribute("gameId");
                    String review = reviewElement.getTextContent();
                    reviewsMap.put(gameId, review);
                }
                userReviews.put(usernames.get(i), reviewsMap);

                personalCollections.add(savedCollections);
            }
        } catch (ParserConfigurationException | IOException | SAXException e) {
            throw new RuntimeException(e);
        }
    }
}
