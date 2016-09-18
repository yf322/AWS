import com.amazonaws.services.ec2.AmazonEC2;
import com.amazonaws.services.ec2.model.RebootInstancesRequest;
import com.amazonaws.services.ec2.model.StartInstancesRequest;
import com.amazonaws.services.ec2.model.StartInstancesResult;
import com.amazonaws.services.ec2.model.StopInstancesRequest;
import com.amazonaws.services.ec2.model.StopInstancesResult;
import com.amazonaws.services.ec2.model.TerminateInstancesRequest;
import com.amazonaws.services.ec2.model.TerminateInstancesResult;

public class InstanceBehavior {
	private AmazonEC2 ec2;

	public InstanceBehavior(AmazonEC2 ec2) {
		this.ec2 = ec2;
	}
	
	
	public void startInstanceWithId(String id) {
		StartInstancesRequest startRequest = new StartInstancesRequest().withInstanceIds(id);
        StartInstancesResult startResult = ec2.startInstances(startRequest);
        System.out.println(startResult.toString());
	}
	
	public void stopInstanceWithId(String id) {
		StopInstancesRequest stopRequest = new StopInstancesRequest().withInstanceIds(id).withForce(true);
        StopInstancesResult stopResult = ec2.stopInstances(stopRequest);
        System.out.println(stopResult.toString());
	}
	
	public void rebootInstanceWithId(String id) {
		RebootInstancesRequest rebootRequest = new RebootInstancesRequest().withInstanceIds(id);
        ec2.rebootInstances(rebootRequest);
	}
	
	public void terminateinstanceWithId(String id) {
		TerminateInstancesRequest terminateRequest = new TerminateInstancesRequest().withInstanceIds(id);
		TerminateInstancesResult terminateResult = ec2.terminateInstances(terminateRequest);
		System.out.println(terminateResult.toString());
	}
	
	public AmazonEC2 getClient() {
		return ec2;
	}
	
	public void setClient(AmazonEC2 ec2) {
		this.ec2 = ec2;
	}
}
