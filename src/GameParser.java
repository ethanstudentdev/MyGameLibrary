import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.File;
import java.io.IOException;

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
            System.out.println(document.getDocumentElement().getNodeName());

            //Loop through all item elements
            NodeList list = document.getElementsByTagName("item");
            for(int i = 0 ; i < list.getLength() ; i++)
            {
                //Board Game for current item node (Represents a board game in XML doc)
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
}
