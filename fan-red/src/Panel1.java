import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.GroupLayout;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.border.EmptyBorder;

import com.amazonaws.services.ec2.AmazonEC2;

public class Panel1 extends JPanel {
	private static final long serialVersionUID = 1L;
	private AmazonEC2 ec2;
	private JComboBox<String> keypairCombo;
	private JComboBox<String> securityCombo;
	private JComboBox<String> imageCombo;
	private JComboBox<String> vpcCombo;
	private JTextField instanceTypeTxt;
	private JTextField instanceNameTxt;
	private String[] keyPairs;
	private String[] securityGroups;
	private String[] vpcs;
	private String[] images;
	/**
	 * Create the panel.
	 */
	public Panel1(AmazonEC2 ec2) {
		this.ec2 = ec2;
		setBorder(new EmptyBorder(5, 5, 5, 5));
		setLayout(new BorderLayout(0, 0));
		deploy();
	}

	public void deploy() {
		deployText();
		deployButton();
	}
	
	public void deployButton() {
		JPanel panel = new JPanel();
		this.add(panel, BorderLayout.SOUTH);
		
		JButton btnLaunch = new JButton("Launch");
		btnLaunch.setPreferredSize(new Dimension(150, 30));
		JButton btnQuit = new JButton("Quit");
		btnQuit.setPreferredSize(new Dimension(150, 30));
		panel.add(btnLaunch);
		panel.add(btnQuit);
		
		final DeployInstances di = new DeployInstances(ec2);
		
		btnLaunch.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				di.setImageId(images[imageCombo.getSelectedIndex()]);
				di.setInstanceType(instanceTypeTxt.getText());
				di.setKeyPairName(keyPairs[keypairCombo.getSelectedIndex()]);
				di.setSecurityName(securityGroups[securityCombo.getSelectedIndex()]);
				di.setVpcName(vpcs[vpcCombo.getSelectedIndex()]);
				di.setInstanceName(instanceNameTxt.getText());
				di.runInstance();
				JOptionPane.showMessageDialog(null, "Instance Launched!");
			}
		});
		
		btnQuit.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				System.exit(0);
			}
		});
	}
	
	public void deployText() {
		DeployInstances di = new DeployInstances(ec2);
		JPanel panel = new JPanel();
		this.add(panel, BorderLayout.CENTER);
		GroupLayout layout = new GroupLayout(panel);
		panel.setLayout(layout);
		layout.setAutoCreateGaps(true);
		layout.setAutoCreateContainerGaps(true);
		
        JLabel keypairLabel = new JLabel("<html><br>Key Pair : </html>");
        JLabel securityLabel = new JLabel("<html><br>Security Group : </html>");
        JLabel imageLabel = new JLabel("<html><br>Image ID : </html>");
        JLabel vpclabel = new JLabel("<html><br>VPC Name : </html>");
        JLabel instanceTypeLabel = new JLabel("<html><br>Instance Type : </html>");
        JLabel instanceNameLabel = new JLabel("<html><br>Instance Name : </html>");

        keyPairs = di.getKeyPairs();
        securityGroups = di.getSecurityGroups();
        vpcs = di.getVpcs();
        images = di.getImages();
        keypairCombo = new JComboBox<String>(keyPairs);
        keypairCombo.setPrototypeDisplayValue("            ");
        securityCombo = new JComboBox<String>(securityGroups);
        securityCombo.setPrototypeDisplayValue("            ");
        imageCombo = new JComboBox<String>(images);
        vpcCombo = new JComboBox<String>(vpcs);
        vpcCombo.setPrototypeDisplayValue("            ");
        instanceTypeTxt = new JTextField(di.getInstanceType());
        instanceNameTxt = new JTextField(di.getInstanceName());
        
        
        layout.setVerticalGroup(
        		layout.createSequentialGroup()
        		.addGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING)
        				.addComponent(keypairLabel)
        				.addComponent(keypairCombo))
        		.addGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING)
        				.addComponent(securityLabel)
        				.addComponent(securityCombo))
        		.addGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING)
        				.addComponent(imageLabel)
        				.addComponent(imageCombo))
        		.addGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING)
        				.addComponent(vpclabel)
        				.addComponent(vpcCombo))
        		.addGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING)
        				.addComponent(instanceTypeLabel)
        				.addComponent(instanceTypeTxt))
        		.addGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING)
        				.addComponent(instanceNameLabel)
        				.addComponent(instanceNameTxt))
        );
        layout.setHorizontalGroup(layout.createSequentialGroup()
        		.addGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING)
        				.addComponent(keypairLabel)
        				.addComponent(securityLabel)
        				.addComponent(imageLabel)
        				.addComponent(vpclabel)
        				.addComponent(instanceTypeLabel)
        				.addComponent(instanceNameLabel))
        		.addGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING)
        				.addComponent(keypairCombo)
        				.addComponent(securityCombo)
        				.addComponent(imageCombo)
        				.addComponent(vpcCombo)
        				.addComponent(instanceTypeTxt)
        				.addComponent(instanceNameTxt))
        );
	}
}
