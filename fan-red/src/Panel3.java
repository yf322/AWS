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
import com.amazonaws.services.ec2.AmazonEC2;

public class Panel3 extends JPanel {
	private static final long serialVersionUID = 1L;
	private AmazonEC2 ec2;
	private JTextField keypairTxt;
	private JTextField securityTxt;
	private JComboBox<String> vpcCombo;
	private String[] vpcs;
	
	public Panel3(AmazonEC2 ec2) {
		this.ec2 = ec2;
//		setBorder(new EmptyBorder(5, 5, 5, 5));
//		setLayout(new BorderLayout(0, 0));
		deploy();
	}
	
	public void deploy() {
		deployText();
		deployButton();
	}
	
	public void deployButton() {
		JPanel panel = new JPanel();
		this.add(panel, BorderLayout.SOUTH);
		
		JButton btnKeyPair = new JButton("Create Key Pair");
		btnKeyPair.setPreferredSize(new Dimension(200, 30));
		JButton btnSecurity = new JButton("Create Security Group");
		btnSecurity.setPreferredSize(new Dimension(200, 30));
		JButton btnQuit = new JButton("Quit");
		btnQuit.setPreferredSize(new Dimension(100, 30));
		
		panel.add(btnKeyPair);
		panel.add(btnSecurity);
		panel.add(btnQuit);
		final DeployInstances di = new DeployInstances(ec2);
		
		btnKeyPair.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				di.setKeyPairName(keypairTxt.getText());
				di.createKeyPair();
				JOptionPane.showMessageDialog(null, "Key Pair Created!");
			}
		});
		
		btnSecurity.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				di.setSecurityName(securityTxt.getText());
				di.setVpcName(vpcs[vpcCombo.getSelectedIndex()]);
				di.createSecurityGroup();
				JOptionPane.showMessageDialog(null, "Security Group Created!");
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
		
		JLabel keypairLabel = new JLabel("Key Pair : ");
        JLabel securityLabel = new JLabel("Security Group : ");
        JLabel vpclabel = new JLabel("VPC Name : ");
        
        vpcs = di.getVpcs();
        keypairTxt = new JTextField(di.getKeyPairName());
        securityTxt = new JTextField(di.getSecurityName());
        vpcCombo = new JComboBox<String>(vpcs);
        vpcCombo.setPrototypeDisplayValue("            ");
        
        layout.setVerticalGroup(layout.createSequentialGroup()
        		.addGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING)
        				.addComponent(keypairLabel)
        				.addComponent(keypairTxt))
        		.addGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING)
        				.addComponent(securityLabel)
        				.addComponent(securityTxt))
        		.addGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING)
        				.addComponent(vpclabel)
        				.addComponent(vpcCombo))
        		);
        layout.setHorizontalGroup(layout.createSequentialGroup()
        		.addGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING)
        				.addComponent(keypairLabel)
        				.addComponent(securityLabel)
        				.addComponent(vpclabel))
        		.addGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING)
        				.addComponent(keypairTxt)
        				.addComponent(securityTxt)
        				.addComponent(vpcCombo))
        		);
	}

}
