import java.awt.BorderLayout;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;

import com.amazonaws.services.ec2.AmazonEC2;
import com.amazonaws.services.ec2.model.Instance;
import com.amazonaws.services.ec2.model.Reservation;
import com.amazonaws.services.ec2.model.Tag;

public class Panel4 extends JPanel{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private AmazonEC2 ec2;
	private List<String[]> instances;
	private String[][] tableList;
	private DefaultTableModel model;
	private String[] colName = {"Name", "Instance Id", "State"};
	private JTable table1;
	
	public Panel4(AmazonEC2 ec2) {
		this.ec2 = ec2;
		setBorder(new EmptyBorder(5, 5, 5, 5));
		setLayout(new BorderLayout(0, 0));
		deploy();
	}
	
	public void deploy() {
		deployTable();
	}
	
	public void deployButton() {
		
	}
	
	public void load4Instance() {
		GetInstanceStatus gi = new GetInstanceStatus(ec2);
		List<Reservation> resList = gi.getInstanceList();
		instances = new ArrayList<String[]>();
		for(Reservation res : resList) {
			List<Instance> instanceList = res.getInstances();
			for (Instance instance:instanceList){
                List <Tag> t1 =instance.getTags();
                for (Tag teg:t1){
                    String[] list = {teg.getValue(), instance.getInstanceId(), instance.getState().getName()};
            		instances.add(list);
                }
			}
		}
		tableList = instances.toArray(new String[0][0]);
		model = new DefaultTableModel(tableList, colName);
		table1.setModel(model);
	}
	
	public void deployTable() {
		table1 = new JTable();
		load4Instance();
		this.add(new JScrollPane(table1));
	}
	
	

}
