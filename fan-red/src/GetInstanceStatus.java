import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import com.amazonaws.services.ec2.AmazonEC2;
import com.amazonaws.services.ec2.model.DescribeInstancesRequest;
import com.amazonaws.services.ec2.model.DescribeInstancesResult;
import com.amazonaws.services.ec2.model.Filter;
import com.amazonaws.services.ec2.model.Instance;
import com.amazonaws.services.ec2.model.Reservation;
import com.amazonaws.services.ec2.model.Tag;

public class GetInstanceStatus {
	private AmazonEC2 ec2;
	private List<String> names = new ArrayList<String>();
	
	
	public GetInstanceStatus(AmazonEC2 ec2) {
		this.ec2 = ec2;
	}
	
	public List<Reservation> getInstanceList() {
		DescribeInstancesRequest describeInstanceRequest = new DescribeInstancesRequest();
		DescribeInstancesResult describeInstanceResult = ec2.describeInstances(describeInstanceRequest);
		List<Reservation> list = describeInstanceResult.getReservations();
	
//		try {
//			for (Reservation res:list){
//                List <Instance> instancelist = res.getInstances();
//
//                for (Instance instance:instancelist){
//                    List <Tag> t1 =instance.getTags();
//                    for (Tag teg:t1){
//                        System.out.println("Instance Name   : "+teg.getValue());
//                    }
//                    System.out.println("Instance Status : "+instance.getState().getName());
//                    System.out.println();
//                }
//			}
//		} catch (Exception e) {
//			System.out.println("Error!");
//			e.printStackTrace();
//		}
		return list;
	}
	
	public void displayStatusWithId(String id) {
		DescribeInstancesRequest describeInstanceRequest = new DescribeInstancesRequest().withInstanceIds(id);
		DescribeInstancesResult describeInstanceResult = ec2.describeInstances(describeInstanceRequest);
		List<Reservation> list = describeInstanceResult.getReservations();
		try {
			for (Reservation res:list){
                List <Instance> instancelist = res.getInstances();

                for (Instance instance:instancelist){
                    System.out.println("Instance Status : "+instance.getState().getName());
                    List <Tag> t1 =instance.getTags();
                    for (Tag teg:t1){
                        System.out.println("Instance Name   : "+teg.getValue());
                    }
                }
			}
		} catch (Exception e) {
			System.out.println("Error!");
			e.printStackTrace();
		}
	}
	
	public void addInstanceName(){
		System.out.println("Enter name : ");
		BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
		try {
			addNames(br.readLine());
		} catch (IOException e) {
			System.out.println("Invalid Input, Try Again!");
			addInstanceName();
		}
	}
	
	public String[] getInstance() {
		DescribeInstancesRequest request = new DescribeInstancesRequest();

		addInstanceName();
		Filter filter = new Filter("tag:Name", names);

		DescribeInstancesResult result = ec2.describeInstances(request.withFilters(filter));
		List<Reservation> reservations = result.getReservations();
		
		ArrayList<String> idList = new ArrayList<String>();
		for (Reservation reservation : reservations) {
			List<Instance> instances = reservation.getInstances();
			for (Instance instance : instances) {
				idList.add(instance.getInstanceId());
			}
		}
		
		String[] ids = idList.toArray(new String[0]);
		return ids;
	}
	
	public List<String> getNames() {
		return names;
	}
	
	public void addNames(String name) {
		names.add(name);
	}
	
	public void removeNames(String name) {
		names.remove(name);
	}
	

}
