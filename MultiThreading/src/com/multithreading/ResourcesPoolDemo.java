package com.multithreading;

public class ResourcesPoolDemo {
	private final static int NUMUSERS = 10;

	public static void main(String[] args) {
		// Create a resource coordinator
		ResourceCoordinator resourceCoordinator = new ResourceCoordinator();
		// Create and start user threads
		for (int userID = 0; userID < NUMUSERS; userID++) {
			new User(resourceCoordinator, userID).start();
		}
	}
}

class Resource {
	boolean allocated;
	int resourceID;

	Resource(int resourceID) {
		allocated = false;
		this.resourceID = resourceID;
	}

	void use(int userID) {
		try {
			System.out.println("User " + userID + " use resources " + resourceID);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}

class ResourceCoordinator {
	private final static int NUMBERRESOURCES = 3;
	private int totalAllcated;
	private Resource resources[];

	ResourceCoordinator() {
		// Initialize totalAllocated
		totalAllcated = 0;
		// Create resources
		resources = new Resource[NUMBERRESOURCES];
		for (int resourceID = 0; resourceID < NUMBERRESOURCES; resourceID++) {
			resources[resourceID] = new Resource(resourceID);
		}
	}

	synchronized Resource get() {
		// Wait for an available resources
		while (totalAllcated == NUMBERRESOURCES) {
			try {
				wait();
			} catch (InterruptedException e) {
				e.printStackTrace();
				System.exit(0);
			}
		}
		// Allocate an available resource
		Resource resource = null;
		for (int i = 0; i < NUMBERRESOURCES; i++) {
			if (resources[i].allocated == false) {
				resources[i].allocated = true;
				resource = resources[i];
				break;
			}
		}
		// Increment totalAllocated
		++totalAllcated;
		// Notify waiting threads
		notifyAll();
		// return resource
		return resource;
	}

	synchronized void put(Resource resource) {
		// Mark resource as available
		resource.allocated = false;
		// Decrement totalAllocated
		--totalAllcated;
		// Notify waiting threads
		notifyAll();
	}
}

class User extends Thread {
	ResourceCoordinator resourceCoordinator;
	int userID;

	User(ResourceCoordinator resourceCoordinator, int userID) {
		this.resourceCoordinator = resourceCoordinator;
		this.userID = userID;
	}

	public void run() {
		try {
			while (true) {
				Resource resource = resourceCoordinator.get();
				resource.use(userID);
				resourceCoordinator.put(resource);
				sleep(3000);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
