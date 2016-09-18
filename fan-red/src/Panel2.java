import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;

import com.amazonaws.services.ec2.AmazonEC2;
import com.amazonaws.services.ec2.model.Instance;
import com.amazonaws.services.ec2.model.Reservation;
import com.amazonaws.services.ec2.model.Tag;

public class Panel2 extends JPanel {
	private static final long serialVersionUID = 1L;
	private AmazonEC2 ec2;
	private JTable table;
	private DefaultTableModel model;
	private String[] colName = {"Name", "Instance Id", "State"};
	private ArrayList<String[]> instances;
	private String[][] tableList;

	/**
	 * Create the panel.
	 */
	public Panel2(AmazonEC2 ec2) {
		this.ec2 = ec2;
		setBorder(new EmptyBorder(5, 5, 5, 5));
		setLayout(new BorderLayout(0, 0));
		deploy();
	}
	
	public void deploy() {
		deployTable();
		deployButton();
	}
	
	public void load() {
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
		table.setModel(model);
	}
	
	public void refresh() {
		load();
		model.fireTableDataChanged();
	}
	
	public void deployTable() {
		table = new JTable();
		load();
		this.add(new JScrollPane(table));
	}
	
	public void deployButton() {
		JPanel panel = new JPanel();
		this.add(panel, BorderLayout.SOUTH);
		
		final InstanceBehavior ib = new InstanceBehavior(ec2);
		JButton btnStart = new JButton("Start");
		btnStart.setPreferredSize(new Dimension(100, 30));
		JButton btnStop = new JButton("Stop");
		btnStop.setPreferredSize(new Dimension(100, 30));
		JButton btnReboot = new JButton("Reboot");
		btnReboot.setPreferredSize(new Dimension(100, 30));
		JButton btnTerminate = new JButton("Terminate");
		btnTerminate.setPreferredSize(new Dimension(100, 30));
		JButton btnQuit = new JButton("Quit");
		btnQuit.setPreferredSize(new Dimension(100, 30));
		
		btnStart.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				int[] indices = table.getSelectedRows();
				for(int i = 0; i < indices.length; i++) {
					ib.startInstanceWithId(instances.get(indices[i])[1]);
					refresh();
				}
				
			}
		});
		
		btnStop.addActionListener(new ActionListener() {	
			@Override
			public void actionPerformed(ActionEvent e) {
				int[] indices = table.getSelectedRows();
				for(int i = 0; i < indices.length; i++) {
					ib.stopInstanceWithId(instances.get(indices[i])[1]);
					refresh();
				}
			}
		});
		
		btnReboot.addActionListener(new ActionListener() {			
			@Override
			public void actionPerformed(ActionEvent e) {
				int[] indices = table.getSelectedRows();
				for(int i = 0; i < indices.length; i++) {
					ib.stopInstanceWithId(instances.get(indices[i])[1]);
					refresh();
				}
			}
		});
		
		btnTerminate.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				int[] indices = table.getSelectedRows();
				for(int i = 0; i < indices.length; i++) {
					ib.terminateinstanceWithId(instances.get(indices[i])[1]);
					refresh();
				}
					
			}
		});
		
		btnQuit.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				System.exit(0);
			}
		});
		
		panel.add(btnStart);
		panel.add(btnStop);
		panel.add(btnReboot);
		panel.add(btnTerminate);
		panel.add(btnQuit);
	}
}
