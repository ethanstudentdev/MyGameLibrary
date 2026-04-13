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
     * It sets the label and provides a clean, modern button appearance.
     * Every button of this type should have a label because the user needs
     * to know what action it performs.
     *
     * @param label the string to label the button
     */
    public RoundedCornerButton(String label){
        super(label);
        // Clean border with subtle rounding effect
        super.setBorder(BorderFactory.createEmptyBorder(8, 16, 8, 16));
        super.setFocusPainted(false);
        super.setContentAreaFilled(true);
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
