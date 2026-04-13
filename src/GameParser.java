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

public class GameParser {
    public static void main(){
        //XML file name
        String filePath = "C:\\Users\\Natha\\IdeaProjects\\MyGameLibrary\\assets\\bgg90Games.xml";

        //Master GameCollection object stores all games from path XML
        GameCollection master = new GameCollection("allGames") ;

        try{
            //Setting up infile
            File gameFile = new File(filePath);

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

                //Logic for name node
                NodeList nameList = document.getElementsByTagName("name");
                Node nameNode = nameList.item(i);
                if (nameNode.getNodeType() == Node.ELEMENT_NODE)
                {
                    Element items = (Element) nameNode;
                    NamedNodeMap attributes = items.getAttributes();
                    System.out.println("Current Node: " + (i + 1));
                    for (int k = 0; k < attributes.getLength(); k++)
                    {
                        Node attr = attributes.item(k);

                        System.out.println("Attribute name: " + attr.getNodeName());
                        System.out.println("Attribute value: " + attr.getNodeValue());
                        System.out.println();
                    }
                    //Only assigns BoardGame name if it is the primary name for the game
                    if(items.getAttribute("type").equals("primary"))
                    {
                        System.out.println("This was a primary!");
                        game.setTitle(items.getAttribute("value"));
                    }
                }
                //End name node logic

                master.addGame(game);
                System.out.println("---Next Node---");
            }

        } catch (ParserConfigurationException | IOException | SAXException e) {
            throw new RuntimeException(e);
        }
        //Debug method call
        master.printAllNames();
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
        List<BoardGame> games = new ArrayList<>();

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
                String title = "Unknown";
                String publisher = "Unknown";
                String description = "";
                String genre = "Unknown";

                NodeList nameNodes = itemElement.getElementsByTagName("name");
                for (int j = 0; j < nameNodes.getLength(); j++) {
                    Node nameNode = nameNodes.item(j);
                    if (nameNode.getNodeType() != Node.ELEMENT_NODE) {
                        continue;
                    }
                    Element nameElement = (Element) nameNode;
                    if ("primary".equals(nameElement.getAttribute("type"))) {
                        String value = nameElement.getAttribute("value");
                        if (value != null && !value.trim().isEmpty()) {
                            title = value.trim();
                        }
                        break;
                    }
                }

                NodeList descriptionNodes = itemElement.getElementsByTagName("description");
                if (descriptionNodes.getLength() > 0) {
                    description = descriptionNodes.item(0).getTextContent().trim();
                }

                NodeList linkNodes = itemElement.getElementsByTagName("link");
                for (int j = 0; j < linkNodes.getLength(); j++) {
                    Node linkNode = linkNodes.item(j);
                    if (linkNode.getNodeType() != Node.ELEMENT_NODE) {
                        continue;
                    }
                    Element linkElement = (Element) linkNode;
                    String type = linkElement.getAttribute("type");
                    String value = linkElement.getAttribute("value");
                    if (value == null || value.trim().isEmpty()) {
                        continue;
                    }
                    if ("boardgamepublisher".equals(type)) {
                        publisher = value.trim();
                    } else if ("boardgamecategory".equals(type) && "Unknown".equals(genre)) {
                        genre = value.trim();
                    }
                }

                games.add(new BoardGame(title, description, publisher, genre, new RatingList(), new ReviewList()));
            }
        } catch (ParserConfigurationException | IOException | SAXException e) {
            System.err.println("Unable to read games from XML: " + e.getMessage());
        }

        return games;
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
