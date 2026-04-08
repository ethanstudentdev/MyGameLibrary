import javax.swing.*;
import javax.swing.border.LineBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * A JButton with a border with rounded corners and (once implemented)
 * a redirect which represents the view the user will be redirected to
 * when the button is pressed.
 * These buttons will be reused as UI elements (at least i did lol)
 *
 * @author Levi Snellgrove
 */
public class RoundedCornerButton extends JButton implements ActionListener {
    //will be useful when we have more uis to redirect to
    private JFrame redirect;

    //currently commented out, I was going to override paintComponent
    //that way the corners of the component itself are rounded and
    //it doesnt have to look like a sharpie marker border
    /*@Override
    protected void paintComponent(Graphics g){

    }
    */

    /**
     * The constructor for this type of button.
     * It sets the label, the border, and in the future will set the redirect
     * Every button of this type should have a label because the user needs
     * to know where it will redirect. Therefore, we don't need a constructor
     * that does not have a string label.
     *
     * @param label the string to label to button
     */
    public RoundedCornerButton(String label){
        super(label);
        super.setBorder(new LineBorder(Color.GRAY, 15, true));

        //this.redirect = redirect;
    }

    /**
     * The method that allows actionListeners to be attached to each button.
     * In the future it will redirect to that button's redirect location.
     *
     * @param e the event to be processed
     */
    @Override
    public void actionPerformed(ActionEvent e) {
        this.setText("performed");
    }
}
