import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.*;
import org.xml.sax.SAXException;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

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
}
