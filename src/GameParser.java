import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.*;
import org.xml.sax.SAXException;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * A class that implements parsing an XML database file
 *
 * @author Ethan Johnson
 */
public class GameParser {
    /**
     * this method parses a complete XML file and populates a GameCollection object with all of the BoardGame
     * entries contained within the XML file
     *
     * @param gameFile The XML file to be parsed in the form of a Java File type
     * @return A game collection representing all games to be used for searching, sorting, and all other functionality
     *         of the application
     */
    public static void main(String[] args){
        String filePath = "assets/bgg90Games.xml";
        GameParser parser = new GameParser();
        GameCollection master = parser.parse(new File(filePath));
        master.printAllNames();
    }

    public GameCollection parse(File gameFile)
    {
        GameCollection collection = new GameCollection("allGames");
        try{
            //Building the doc to parse
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder docBuilder = factory.newDocumentBuilder();
            Document document = docBuilder.parse(gameFile);
            document.getDocumentElement().normalize();

            //Fetch top node
            System.out.print("Root node: ");
            System.out.println(document.getDocumentElement().getNodeName());

            //Loop through all item elements
            NodeList list = document.getElementsByTagName("item");
            for(int i = 0 ; i < list.getLength() ; i++)
            {
                //Board Game for current item
                BoardGame game = new BoardGame();

                //Represents item node
                Node node = list.item(i);

                //Represents a generic element of node item
                Element itemElement = (Element) node;

                //Logic for children name nodes//
                NodeList nameList = itemElement.getElementsByTagName("name");

                for(int j = 0 ; j < nameList.getLength() ; j++)
                {
                    Node nameNode = nameList.item(j);
                    if(nameNode.getNodeType() == Node.ELEMENT_NODE)
                    {
                        Element nameElement = (Element) nameNode;
                        if ("primary".equals(nameElement.getAttribute("type")))
                        {
                            game.setTitle(nameElement.getAttribute("value"));
                        }
                    }
                }
                //End children name nodes logic//

                //Logic for children description nodes//
                NodeList descriptionList = itemElement.getElementsByTagName("description");
                Node descriptionNode = descriptionList.item(0);
                if(descriptionNode.getNodeType() == Node.ELEMENT_NODE)
                {
                    Element descriptionElement = (Element) descriptionNode;
                    //Sets a description if there is one
                    if(!(descriptionElement.getTextContent()).isBlank())
                    {
                        game.setDescription(descriptionElement.getTextContent());
                    }
                }
                //End children description nodes logic//

                //Logic for children image nodes//
                NodeList imageList = itemElement.getElementsByTagName("image");
                Node imageNode = imageList.item(0);
                if(imageNode.getNodeType() == Node.ELEMENT_NODE)
                {
                    Element imageElement = (Element) imageNode;
                    //Sets an image if there is one
                    if(!(imageElement.getTextContent()).isBlank())
                    {
                        game.setImage(imageElement.getTextContent());
                    }
                }
                //End children image nodes logic//

                //Logic for children link nodes//
                NodeList linkList = itemElement.getElementsByTagName("link");

                //Loops through link nodes
                for(int j = 0 ; j < linkList.getLength() ; j++)
                {
                    Node linkNode = linkList.item(j);
                    //Logic for boardgamecategory nodes
                    if(linkNode.getNodeType() == Node.ELEMENT_NODE)
                    {
                        Element genreElement = (Element) linkNode;
                        if ("boardgamecategory".equals(genreElement.getAttribute("type")))
                        {
                            game.addGenre(genreElement.getAttribute("value"));
                        }
                        if ("boardgamepublisher".equals(genreElement.getAttribute("type")))
                        {
                            game.setPublisher(genreElement.getAttribute("value"));
                        }
                    }
                }
                //End children link nodes logic//

                //Adds BoardGame to collection
                collection.addGame(game);
            }

        } catch (ParserConfigurationException | IOException | SAXException e) {
            throw new RuntimeException(e);
        }
        //Returns the collection
        return collection;
    }

    public static List<CategoryCount> getCategoryCountsFromFile(String xmlFilePath) {
        Map<String, Integer> counts = new HashMap<>();

        try {
            File gameFile = new File(xmlFilePath);
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder docBuilder = factory.newDocumentBuilder();
            Document document = docBuilder.parse(gameFile);
            document.getDocumentElement().normalize();

            NodeList items = document.getElementsByTagName("item");
            for (int i = 0; i < items.getLength(); i++) {
                Node itemNode = items.item(i);
                if (itemNode.getNodeType() != Node.ELEMENT_NODE) {
                    continue;
                }

                Element itemElement = (Element) itemNode;
                NodeList linkNodes = itemElement.getElementsByTagName("link");
                for (int j = 0; j < linkNodes.getLength(); j++) {
                    Node linkNode = linkNodes.item(j);
                    if (linkNode.getNodeType() != Node.ELEMENT_NODE) {
                        continue;
                    }

                    Element linkElement = (Element) linkNode;
                    if ("boardgamecategory".equals(linkElement.getAttribute("type"))) {
                        String value = linkElement.getAttribute("value");
                        if (value != null && !value.trim().isEmpty()) {
                            value = value.trim();
                            counts.put(value, counts.getOrDefault(value, 0) + 1);
                        }
                    }
                }
            }
        } catch (ParserConfigurationException | IOException | SAXException e) {
            System.err.println("Unable to read categories from XML: " + e.getMessage());
        }

        List<CategoryCount> categoryCounts = new ArrayList<>();
        for (Map.Entry<String, Integer> entry : counts.entrySet()) {
            categoryCounts.add(new CategoryCount(entry.getKey(), entry.getValue()));
        }
        categoryCounts.sort((a, b) -> Integer.compare(b.getCount(), a.getCount()));
        return categoryCounts;
    }

    public static List<BoardGame> parseAllGames(String xmlFilePath) {
        GameParser parser = new GameParser();
        return parser.parse(new File(xmlFilePath)).getGames();
    }

    public static class CategoryCount {
        private final String name;
        private final int count;

        public CategoryCount(String name, int count) {
            this.name = name;
            this.count = count;
        }

        public String getName() {
            return name;
        }

        public int getCount() {
            return count;
        }

        @Override
        public String toString() {
            return name + " (" + count + ")";
        }
    }
}
