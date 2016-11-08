# dream-java

The relevant source code is in the Dream2 folder. 


We initially need to assign a name to the current host `Host1`.
```java
Consts.hostName = "Host1";
```
Now on `Host1` we can define a Var `myVar` that is visible remotely. The var `myVar` constains a string, it is advertised with the name `exVar` and it is initialized to the value `AAA`. Later on the value is changed to `BBB`.
```java
Var<String> myVar = new Var<String>("exVar", "AAA");
myVar.set("BBB");
```

On a different host, `Host2` we can read the var defined on `Host1` whose name is `exVar`. We can also define a signal that builds a computation on top of it.

```java
RemoteVar<String> rv = new RemoteVar<String>("Host1", "exVar");

Signal<String> s = new Signal<String>>("s", () -> {
			return rv.get() + "XXX";	
		}, rv);
```
If rv is not available yet, ...
More examples:


```java
Var<Integer> a = new Var<>("a", Integer.valueOf(1));
	  
Var<Integer> b = new Var<>("b", Integer.valueOf(2));
	  
Signal<Integer> c =
    new Signal<>("c", () -> a.get() + b.get(), a, b);
```

Another example:

```
Var<List<Integer>> lst = new Var<>("lst", new ArrayList<Integer>());
	  
Signal<Integer> n =
    new Signal<>("n", () -> lst.get().size(), lst);
	  
    lst.modify(self -> {
        self.add(1);
	self.add(2);
	self.add(3);
    });
```
	  
## Locks
When using Atomic_Consistency it is required to lock a Var before reading its value.
```
	LockToken lock = DreamClient.instance.readLock(new HashSet<>("exVar@Host1"));
	String value = rv.get();
	DreamClient.instance.unlock(lock);
```

## Utility Class
In the dream.examples.util-package you can find the class Client which does all the needed setup for Dream.
It starts the DreamServer if it is not already running, sets the Client Name and connects to the Dependency Graph.
It also provides some additional utilities.

```
public class MyClass extends Client {

	public MyClass() {
		super("ClientName");
		
		// Here you can add you Vars/Signals
		Var<String> myVar = new Var<>("myVar", "value");
	}
}
```

It can also wait for defined Vars to be available:

```
public class OtherClass extends Client {

	public OtherClass() {
		super("OtherName");
		// The super class blocks until myVar@ClientName is available
		RemoteVar<String> theirVar = new RemoteVar<>("ClientName", "myVar");
	}
	
	@Override
	protected List<String> waitForVars() {
		return Arrays.asList("myVar@ClientName");
	}
	
	@Override
	protected void init() {
		// The code here is executed before the the super class blocks
	}
}
```

## Contributors

Alessandro Margara

Tobias Becker

Kim Berninger and Michael Raulf - IMPL Project



	  
