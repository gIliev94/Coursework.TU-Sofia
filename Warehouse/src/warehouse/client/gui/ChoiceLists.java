package warehouse.client.gui;

import java.awt.Font;
import javax.swing.JComboBox;
import javax.swing.JPanel;

/**
 * Configuration class - sets up properties of COMBO BOXES.
 * 
 * @author Georgi Iliev
 *
 */
public class ChoiceLists {

    public ChoiceLists() {
    }

    /**
     * Setup the common properties for unlimited number of combo box and add
     * them to the specified panel.
     * 
     * @param panel
     * @param choiceLists
     */
    @SafeVarargs
    private static void configure(JPanel panel, JComboBox<Object>... choiceLists) {
	for (JComboBox<Object> list : choiceLists) {
	    list.setFont(new Font("Tahoma", Font.BOLD, 12));
	    panel.add(list);
	}
    }

    /**
     * Configures the combo box for panel: MAIN.
     * 
     * @param panelMain
     * @param productChoiceList
     */
    static void configureMainProductsList(JPanel panelMain, JComboBox<Object> productChoiceList) {
	configure(panelMain, productChoiceList);

	productChoiceList.setBounds(25, 37, 184, 27);
    }

}
