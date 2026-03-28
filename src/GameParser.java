import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.*;
import org.xml.sax.SAXException;

import java.io.File;
import java.io.IOException;

public class GameParser {
    public static void main(){
        //XML file name
        String filePath = "C:\\Users\\Ethan\\IdeaProjects\\MyGameLibrary\\assets\\bgg90Games.xml";

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
                //End name children description nodes logic//

                //Adds BoardGame to master collection
                master.addGame(game);
            }

        } catch (ParserConfigurationException | IOException | SAXException e) {
            throw new RuntimeException(e);
        }
        //Debug method call
        master.printAllGames();
    }
}
