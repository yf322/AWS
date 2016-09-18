
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.services.ec2.AmazonEC2;
import com.amazonaws.services.ec2.AmazonEC2Client;

/**
 * 
 * @author fanyong
 *
 */
public class Main {
	final static AWSCredentials credentials = new BasicAWSCredentials("XXXXXXXXXXXXX", "XXXXXXXXXXXXXXXXXXXXX");
	final static String[] menu1_option = {"Start this instance", "Stop this instance", "Reboot this instance", "Back", "Quit"};
	final static String[] menu2_option = {"Change the configuration", "Create a Key Pair", "Create a Security Group", "Run an instance", "Quit"};
	final static String[] menu3_option = {"Change key pair's name", "Change security group name", "Change ImageId", "Change Vpc name", "Change instance type", "Back"};
	static AmazonEC2 ec2 = new AmazonEC2Client(credentials);
	static String id;
	static GetInstanceStatus getEC2Status = new GetInstanceStatus(ec2);
	
	public static void main(String[] args) {
		ec2.setEndpoint("ec2.us-east-1.amazonaws.com");
		DeployInstances di = new DeployInstances(ec2);
//		di.createLoadBalancer();
//		di.addInstanceToLb("i-b72fd6f2");
		di.addInstanceToLb("i-00804645");
	}
	
	public static void getInstanceName(){ 
		getEC2Status.getInstanceList();
		String[] ids = getEC2Status.getInstance();
		id = ids[0];
		System.out.println(id);
		getEC2Status.displayStatusWithId(id);
		System.out.println("You are now operating on id:" + id + ", what do you want to do?");
		menu1();
	}
	
	public static void menu1(){
		for(int i = 0; i < menu1_option.length; i++) {
			System.out.println("(" + (i+1) + ") " + menu1_option[i]);
		}
		BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
		try {
			int prompt = Integer.parseInt(br.readLine());
			switchOption1(prompt);
		} catch (NumberFormatException e) {
			System.out.println("Please enter an integer!");
			menu1();
		} catch (IOException e) {
			System.out.println("Invalid Input!");
			menu1();
		}
	}
	
	public static void switchOption1(int prompt){
		InstanceBehavior ib = new InstanceBehavior(ec2);
		switch (prompt){
		case 1:
			ib.startInstanceWithId(id);
			menu1();
			break;

		case 2:
			ib.stopInstanceWithId(id);
			menu1();
			break;

		case 3:
			ib.rebootInstanceWithId(id);
			menu1();
			break;
			
		case 4:
			getInstanceName();
			break;
			
		case 5:
			System.exit(0);
			break;
			
		default : 
			System.out.println("Not in range!");
			menu1();
			break;
		}
	}
	
	public static void menu2() {
		for(int i = 0; i < menu2_option.length; i++) {
			System.out.println("(" + (i+1) + ") " + menu2_option[i]);
		}
		BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
		try {
			int prompt = Integer.parseInt(br.readLine());
			switchOption2(prompt);
		} catch (NumberFormatException e) {
			System.out.println("Please enter an integer!");
			menu2();
		} catch (IOException e) {
			System.out.println("Invalid Input!");
			menu2();
		}
	}
	
	public static void switchOption2(int prompt) {
		DeployInstances di = new DeployInstances(ec2);
		switch(prompt) {
		case 1 :
			menu3();
			break;
		case 2 :
			di.createKeyPair();
			menu2();
			break;
		case 3 :
			di.createSecurityGroup();
			menu2();
			break;
		case 4 :
			di.runInstance();
			menu2();
			break;
		case 5 :
			System.exit(0);
			break;
		default :
			System.out.println("Not in range!");
			menu2();
			break;
		}
	}
	
	public static void menu3() {
		DeployInstances di = new DeployInstances(ec2);
		System.out.println("Key Pair : " + di.getKeyPairName());
		System.out.println("Security Group : " + di.getSecurityName());
		System.out.println("Image ID : " + di.getImageId());
		System.out.println("VPC Name : " + di.getVpcName());
		System.out.println("Instance Type : " + di.getInstanceType());
		for(int i = 0; i < menu3_option.length; i++) {
			System.out.println("(" + (i+1) + ") " + menu3_option[i]);
		}
		BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
		try {
			int prompt = Integer.parseInt(br.readLine());
			switchOption3(prompt);
		} catch (NumberFormatException e) {
			System.out.println("Please enter an integer!");
			menu3();
		} catch (IOException e) {
			System.out.println("Invalid Input!");
			menu3();
		}
	}
	
	public static void switchOption3(int prompt) {
		DeployInstances di = new DeployInstances(ec2);
		switch (prompt) {
		case 1 :
			di.setKeyPairName(getInput());
			menu3();
			break;
		case 2 :
			di.setSecurityName(getInput());
			menu3();
			break;
		case 3 :
			di.setImageId(getInput());
			menu3();
			break;
		case 4 :
			di.setVpcName(getInput());
			menu3();
			break;
		case 5 :
			di.setInstanceType(getInput());
			menu3();
			break;
		case 6 :
			menu2();
			break;
		default:
			System.out.println("Not in range!");
			menu3();
			break;
		}
		
	}
	
	public static String getInput() {
		BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
		try {
			String temp = br.readLine();
			if(!temp.equals(null)) {
				return temp;
			}
			else {
				System.out.println("Empty Input, Try Again!");
				getInput();
			}
		} catch (IOException e) {
			System.out.println("Invalid Input, Try Again!");
			getInput();
		}
		return null;
	}
	
}
