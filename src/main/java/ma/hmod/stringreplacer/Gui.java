package ma.hmod.stringreplacer;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.io.IOException;

public class Gui extends JFrame {


    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;
    public JTextField inputfld, outputfld, oldStrFld, newStrFld;
    public JTextArea console;
    public JCheckBox casesensitiveBox, replaceWholeWordBox, equalsBox;

	public Gui() {
		
    }

    public void visible() {
    	getContentPane().setLayout(null);
		
		JPanel panel = new JPanel();
		panel.setBounds(0, 0, 284, 461);
		getContentPane().add(panel);
		panel.setLayout(null);
		
		inputfld = new JTextField();
		inputfld.setToolTipText("Path");
		inputfld.setBounds(10, 27, 264, 20);
		panel.add(inputfld);
		inputfld.setColumns(10);
		
		JLabel inputlbl = new JLabel("Input");
		inputlbl.setHorizontalAlignment(SwingConstants.CENTER);
		inputlbl.setBounds(0, 11, 46, 14);
		panel.add(inputlbl);
		
		JLabel outputlbl = new JLabel("Output");
		outputlbl.setHorizontalAlignment(SwingConstants.CENTER);
		outputlbl.setBounds(5, 58, 46, 14);
		panel.add(outputlbl);
		
		outputfld = new JTextField();
		outputfld.setToolTipText("Output");
		outputfld.setBounds(10, 76, 264, 20);
		panel.add(outputfld);
		outputfld.setColumns(10);
		
		
		JLabel targetstrLbl = new JLabel("Target String");
		targetstrLbl.setBounds(10, 131, 82, 14);
		panel.add(targetstrLbl);
		
		oldStrFld = new JTextField();
		oldStrFld.setToolTipText("Old String");
		oldStrFld.setBounds(102, 128, 172, 20);
		panel.add(oldStrFld);
		oldStrFld.setColumns(10);
		
		JLabel newStrLbl = new JLabel("Replace With");
		newStrLbl.setBounds(10, 163, 82, 14);
		panel.add(newStrLbl);
		
		newStrFld = new JTextField();
		newStrFld.setToolTipText("New String");
		newStrFld.setBounds(102, 160, 172, 20);
		panel.add(newStrFld);
		newStrFld.setColumns(10);
		
		casesensitiveBox = new JCheckBox("Casesensitive");
		casesensitiveBox.setBounds(6, 224, 105, 23);
		panel.add(casesensitiveBox);
		
		replaceWholeWordBox = new JCheckBox("Replace whole word");
		replaceWholeWordBox.setBounds(6, 250, 146, 23);
		panel.add(replaceWholeWordBox);
		
		JCheckBox equalsBox = new JCheckBox("Equals");
		equalsBox.setBounds(6, 276, 86, 23);
		panel.add(equalsBox);
		
		console = new JTextArea();
		console.setBounds(10, 306, 264, 115);
		panel.add(console);
		
//		TextAreaOutputStream 
		
		JButton replaceBtn = new JButton("Replace");

		replaceBtn.addMouseListener(new MouseAdapter() {
			@Override
			public void mousePressed(MouseEvent e) {
				try {
					File target = new File(inputfld.getText()), out = new File(outputfld.getText()), src = new File(System.getProperty("user.dir") + "/" + target.getName().substring(0, target.getName().indexOf(".")));
					String oldStr = oldStrFld.getText(), newStr = newStrFld.getText();
					boolean equals = equalsBox.isSelected(), cases = casesensitiveBox.isSelected(), wholeWord = replaceWholeWordBox.isSelected();
					src.mkdir();
					new Utils().replace(src.getAbsolutePath()+"/", target, out, newStr, oldStr, equals, cases, wholeWord);
				} catch (IOException e1) {
					System.out.println("Err: " + e1.getMessage());
				}
			}
		});
		replaceBtn.setBounds(102, 427, 89, 23);
		panel.add(replaceBtn);
		
        super.setTitle("String Replacer");
        super.setVisible(true);
        super.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        super.setResizable(false);
        super.setSize(new Dimension(300, 500));
    }
    
    
}
