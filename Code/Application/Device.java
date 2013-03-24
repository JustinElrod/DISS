package com.example.distributediss;

public class Device {
	
	String hostName;
	String param2;
	boolean isAvailable = true;
	
	public boolean isAvailable() {
		return isAvailable;
	}

	public void setAvailable(boolean isAvailable) {
		this.isAvailable = isAvailable;
	}

	public Device(String hostName, String param2) {
		super();
		this.hostName = hostName;
		this.param2 = param2;
	}

	public String getHostName() {
		return hostName;
	}

	public void setHostName(String hostName) {
		this.hostName = hostName;
	}

	public String getParam2() {
		return param2;
	}

	public void setParam2(String param2) {
		this.param2 = param2;
	}

	@Override
	public String toString() {
		return "Device [hostName=" + hostName + ", param2=" + param2 + "]" + " was disc:" + 
				Core.getDeviceList().get(Core.getDeviceList().indexOf(this)).isAvailable();
	}
	
	

}
