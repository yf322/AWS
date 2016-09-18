import java.awt.EventQueue;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.services.ec2.AmazonEC2;
import com.amazonaws.services.ec2.AmazonEC2Client;

public class RedGui extends JFrame {

	final static AWSCredentials credentials = new BasicAWSCredentials("XXXXXXXXXXXXXXXX", "XXXXXXXXXXXXXXXXXXXXXX");
	private static final long serialVersionUID = 1L;
	static AmazonEC2 ec2 = new AmazonEC2Client(credentials);
s
	
	public static void main(String[] args) {
		ec2.setEndpoint("ec2.us-east-1.amazonaws.com");
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					RedGui frame = new RedGui();
					frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the frame.
	 */
	public RedGui() {
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 600, 400);
		JPanel panel1 = new Panel1(ec2);
		JPanel panel2 = new Panel2(ec2);
		JPanel panel3 = new Panel3(ec2);
		JPanel panel4 = new Panel4(ec2);
		
		JTabbedPane tabbedPane = new JTabbedPane();
		tabbedPane.add("Operate an Instance", panel2);
		tabbedPane.add("Deploy an instance", panel1);
		tabbedPane.add("Configuration", panel3);
		tabbedPane.add("Load Balancer", panel4);
		add(tabbedPane);
	}
	
	
}
