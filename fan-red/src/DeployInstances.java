import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.services.ec2.AmazonEC2;
import com.amazonaws.services.ec2.model.AuthorizeSecurityGroupIngressRequest;
import com.amazonaws.services.ec2.model.CreateKeyPairRequest;
import com.amazonaws.services.ec2.model.CreateKeyPairResult;
import com.amazonaws.services.ec2.model.CreateSecurityGroupRequest;
import com.amazonaws.services.ec2.model.CreateSecurityGroupResult;
import com.amazonaws.services.ec2.model.CreateTagsRequest;
import com.amazonaws.services.ec2.model.DescribeImagesRequest;
import com.amazonaws.services.ec2.model.DescribeImagesResult;
import com.amazonaws.services.ec2.model.DescribeKeyPairsRequest;
import com.amazonaws.services.ec2.model.DescribeKeyPairsResult;
import com.amazonaws.services.ec2.model.DescribeSecurityGroupsRequest;
import com.amazonaws.services.ec2.model.DescribeSecurityGroupsResult;
import com.amazonaws.services.ec2.model.DescribeSubnetsRequest;
import com.amazonaws.services.ec2.model.DescribeSubnetsResult;
import com.amazonaws.services.ec2.model.DescribeVpcsRequest;
import com.amazonaws.services.ec2.model.DescribeVpcsResult;
import com.amazonaws.services.ec2.model.Filter;
import com.amazonaws.services.ec2.model.Image;
import com.amazonaws.services.ec2.model.IpPermission;
import com.amazonaws.services.ec2.model.KeyPair;
import com.amazonaws.services.ec2.model.KeyPairInfo;
import com.amazonaws.services.ec2.model.RunInstancesRequest;
import com.amazonaws.services.ec2.model.RunInstancesResult;
import com.amazonaws.services.ec2.model.SecurityGroup;
import com.amazonaws.services.ec2.model.Subnet;
import com.amazonaws.services.ec2.model.Tag;
import com.amazonaws.services.ec2.model.Vpc;
import com.amazonaws.services.elasticloadbalancing.AmazonElasticLoadBalancingClient;
import com.amazonaws.services.elasticloadbalancing.model.CreateLoadBalancerRequest;
import com.amazonaws.services.elasticloadbalancing.model.CreateLoadBalancerResult;
import com.amazonaws.services.elasticloadbalancing.model.DeregisterInstancesFromLoadBalancerRequest;
import com.amazonaws.services.elasticloadbalancing.model.Listener;
import com.amazonaws.services.elasticloadbalancing.model.RegisterInstancesWithLoadBalancerRequest;

public class DeployInstances {
	final AWSCredentials credentials = new BasicAWSCredentials("XXXXXXXXXXXXXXXXXX", "XXXXXXXXXXXXXXXXXXXXXXXXXX");
	private AmazonElasticLoadBalancingClient elb = new AmazonElasticLoadBalancingClient(credentials);
	private AmazonEC2 ec2;
	private String Vpc_name = "TS Sandbox VPC";
	private String subnet_name = "Public Subnet 1";
	private String subnetId = "subnet-ccd47eba";
	private String keypair_name = "Fan-keypair";
	private String security_name = "Red";
	private String instance_type = "t2.micro";
	private String ImageId = "ami-f5f41398";
	private String securityId;
	private String instanceName = "Red";
	private String loadBalancerName = "Red-lb";
	
	public DeployInstances(AmazonEC2 ec2) {
		this.ec2 = ec2;
	}
	
	public String[] getSecurityGroups() {
		DescribeSecurityGroupsRequest describeSecurityGroupsRequest = new DescribeSecurityGroupsRequest();
		DescribeSecurityGroupsResult describeSecurityGroupsResult = ec2.describeSecurityGroups(describeSecurityGroupsRequest);
		List<SecurityGroup> securityGroups = describeSecurityGroupsResult.getSecurityGroups();
		ArrayList<String> securityGroupsNames = new ArrayList<String>();
		for(SecurityGroup securityGroup : securityGroups) {
			securityGroupsNames.add(securityGroup.getGroupName());
		}
		return securityGroupsNames.toArray(new String[0]);
	}
	
	public void createSecurityGroup() {
		CreateSecurityGroupRequest request = new CreateSecurityGroupRequest();
		request.withDescription("fan test security group")
		       .withGroupName(security_name)
		       .withVpcId(getVpcId());
		
		CreateSecurityGroupResult result = ec2.createSecurityGroup(request);
		System.out.println(result.getGroupId());
		securityId = result.getGroupId();
		
		IpPermission ipSSH = new IpPermission();
		ipSSH.withIpRanges("0.0.0.0/0")
			 .withIpProtocol("tcp")
			 .withFromPort(22)
			 .withToPort(22);
		
		IpPermission ipHttp = new IpPermission();
		ipHttp.withIpRanges("0.0.0.0/0")
			  .withIpProtocol("tcp")
			  .withFromPort(80)
			  .withToPort(80);
		
		AuthorizeSecurityGroupIngressRequest asgir = new AuthorizeSecurityGroupIngressRequest();
		asgir.withGroupId(securityId)
			 .withIpPermissions(ipHttp, ipSSH);
		
		ec2.authorizeSecurityGroupIngress(asgir);
	}
	
	public String getSecurityName() {
		return security_name;
	}
	
	public void setSecurityName(String newName) {
		security_name = newName;
	}
	
	public String getSecurityId() {
		DescribeSecurityGroupsRequest describeSecurityGroupsRequest = new DescribeSecurityGroupsRequest();
		DescribeSecurityGroupsResult describeSecurityGroupsResult = ec2.describeSecurityGroups(describeSecurityGroupsRequest);
		List<SecurityGroup> securityGroups = describeSecurityGroupsResult.getSecurityGroups();
		for(SecurityGroup securityGroup : securityGroups) {
			if(securityGroup.getGroupName().equals(security_name)){
				return securityGroup.getGroupId();
			}
		}
		return null;
	}
	
	public String getInstanceType() {
		return instance_type;
	}
	
	public void setInstanceType(String newType) {
		instance_type = newType;
	}
	
	public String[] getKeyPairs() {
		DescribeKeyPairsRequest describeKeyPairsRequest = new DescribeKeyPairsRequest();
		DescribeKeyPairsResult describeKeyPairsResult = ec2.describeKeyPairs(describeKeyPairsRequest);
		List<KeyPairInfo> keyPairInfos = describeKeyPairsResult.getKeyPairs();
		ArrayList<String> keyPairNames = new ArrayList<String>();
		for(KeyPairInfo keyPair:keyPairInfos) {
			keyPairNames.add(keyPair.getKeyName());
		}
		return keyPairNames.toArray(new String[0]);
	}
	
	public void createKeyPair() {
		CreateKeyPairRequest request = new CreateKeyPairRequest();
		request.withKeyName(keypair_name);
		CreateKeyPairResult result = ec2.createKeyPair(request);
		KeyPair keyPair = new KeyPair();
		keyPair = result.getKeyPair();
		String privateKey = keyPair.getKeyMaterial();
		byte[] data = privateKey.getBytes();
		try {
			FileOutputStream outputStream = new FileOutputStream("/Users/fanyong/Desktop/" + keypair_name + ".pem");
			outputStream.write(data);
			outputStream.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		System.out.println(privateKey);
	}
	
	public String getKeyPairName() {
		return keypair_name;
	}
	
	public void setKeyPairName(String newName) {
		keypair_name = newName;
	}
	
	public void runInstance() {
		System.out.println(getSecurityId());
		RunInstancesRequest request = new RunInstancesRequest();
		request.withImageId(ImageId)
        	   .withInstanceType(instance_type)
        	   .withMinCount(1)
        	   .withMaxCount(1)
        	   .withKeyName(keypair_name)
        	   .withSecurityGroupIds(getSecurityId())
        	   .withSubnetId(subnetId);
		RunInstancesResult result = ec2.runInstances(request);
		String instanceId = result.getReservation().getInstances().get(0).getInstanceId();
		System.out.println(instanceId);
		
		List<Tag> tags = new ArrayList<Tag>();

		Tag teg = new Tag();
		teg.setKey("Name");
		teg.setValue(instanceName);
		tags.add(teg);
		
		CreateTagsRequest createTagsRequest = new CreateTagsRequest();
		createTagsRequest.setTags(tags);
		createTagsRequest.withResources(instanceId);
		ec2.createTags(createTagsRequest);
	}
	
	public String getInstanceName() {
		return instanceName;
	}
	
	public void setInstanceName(String newName) {
		instanceName = newName;
	}
	
	public String getVpcName() {
		return Vpc_name;
	}
	
	public void setVpcName(String newName) {
		Vpc_name = newName;
	}
	
	public String[] getVpcs() {
		DescribeVpcsRequest describeVpcsRequest = new DescribeVpcsRequest();
		DescribeVpcsResult describeVpcsResult = ec2.describeVpcs(describeVpcsRequest);
		List<Vpc> vpcs = describeVpcsResult.getVpcs();
		ArrayList<String> vpcNames = new ArrayList<String>();
		for(Vpc vpc : vpcs) {
			List<Tag> tags = vpc.getTags();
			for(Tag tag : tags) {
				vpcNames.add(tag.getValue());
			}
		}
		return vpcNames.toArray(new String[0]);
	}
	
	public String getVpcId() {
		DescribeVpcsRequest request = new DescribeVpcsRequest();
		List<String> vpcNames = new ArrayList<String>();
		vpcNames.add(Vpc_name);
		Filter filter = new Filter("tag:Name", vpcNames);
		DescribeVpcsResult result = ec2.describeVpcs(request.withFilters(filter));
		List<Vpc> vpcs = result.getVpcs();
		return vpcs.get(0).getVpcId();
	}
	
	public String getSubnetName() {
		return subnet_name;
	}
	
	public void setSubnetName(String newName) {
		subnet_name = newName;
	}
		
	public String getSubnetId() {
		DescribeSubnetsRequest request = new DescribeSubnetsRequest();
		List<String> subnetNames = new ArrayList<String>();
		subnetNames.add(subnet_name);
		Filter filter = new Filter("tag:Name", subnetNames);
		DescribeSubnetsResult result = ec2.describeSubnets(request.withFilters(filter));
		List<Subnet> subnets = result.getSubnets();
		return subnets.get(0).getSubnetId();
	}
	
	public String[] getImages() {
		DescribeImagesRequest describeImagesRequest = new DescribeImagesRequest().withImageIds(ImageId);
		DescribeImagesResult describeImagesResult = ec2.describeImages(describeImagesRequest);
		List<Image> images = describeImagesResult.getImages();
		ArrayList<String> imageIds = new ArrayList<String>();
		for(Image image : images) {
			imageIds.add(image.getImageId());
		}
		return imageIds.toArray(new String[0]);
	}
	
	public String getImageId() {
		return ImageId;
	}
	
	public void setImageId(String newId) {
		ImageId = newId;
	}
	
	public void createLoadBalancer() {
		CreateLoadBalancerRequest createLoadBalancerRequest = new CreateLoadBalancerRequest();
		
		createLoadBalancerRequest.withSubnets(subnetId,"subnet-8f61acd7")
								 .withSecurityGroups(getSecurityId())
								 .withLoadBalancerName(loadBalancerName)
								 .withListeners(new Listener("HTTP", 80, 80));
		
		CreateLoadBalancerResult createLoadBalancerResult = elb.createLoadBalancer(createLoadBalancerRequest);
		System.out.println(createLoadBalancerResult.getDNSName());
	}
	
	public void addInstanceToLb(String instanceId) {
		RegisterInstancesWithLoadBalancerRequest registerInstancesWithLoadBalancerRequest = new RegisterInstancesWithLoadBalancerRequest();
		registerInstancesWithLoadBalancerRequest.withLoadBalancerName(loadBalancerName)
												.withInstances(new com.amazonaws.services.elasticloadbalancing.model.Instance(instanceId));
		elb.registerInstancesWithLoadBalancer(registerInstancesWithLoadBalancerRequest);
	}
	
	public void removeInstanceToLb(String instanceId) {
		DeregisterInstancesFromLoadBalancerRequest deregisterInstancesFromLoadBalancerRequest = new DeregisterInstancesFromLoadBalancerRequest();
		deregisterInstancesFromLoadBalancerRequest.withLoadBalancerName(loadBalancerName)
												  .withInstances(new com.amazonaws.services.elasticloadbalancing.model.Instance(instanceId));
		elb.deregisterInstancesFromLoadBalancer(deregisterInstancesFromLoadBalancerRequest);
	}
	
	public String getLoadBalancerName() {
		return loadBalancerName;
	}
	
	public void setLoadBalancerName(String newName) {
		loadBalancerName = newName;
	}

}
