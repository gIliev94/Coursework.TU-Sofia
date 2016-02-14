package warehouse.client.gui;

import java.awt.CardLayout;
import java.awt.Color;
import javax.swing.ImageIcon;
import javax.swing.JFrame;

/**
 * Configuration class - sets up properties of FRAMES.
 * 
 * @author Georgi Iliev
 *
 */
public class Forms {

    private Forms() {
    }

    /**
     * Setup the common properties for unlimited number of frames.
     * 
     * @param forms
     */
    private static void configure(JFrame... forms) {
	for (JFrame form : forms) {
	    form.setResizable(false);
	    form.getContentPane().setBackground(Color.BLACK);
	    form.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	    form.getContentPane().setLayout(new CardLayout(0, 0));
	}
    }

    /**
     * Configure custom properties for the client frame.
     * 
     * @param clientForm
     */
    static void configureClientForm(JFrame clientForm) {
	configure(clientForm);

	clientForm.setTitle("CLIENT REQUEST");
	clientForm.setBounds(100, 100, 493, 411);
	clientForm.setIconImage(new ImageIcon(clientForm.getClass().getResource("/storage.png")).getImage());
    }

}
