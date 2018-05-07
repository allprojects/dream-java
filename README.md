# DREAM Reactive Programming Framework

DREAM - distributed reactive programming
middleware with flexible consistency guarantees.

## Why DREAM?

Different applications require different levels of consistency
and that manually implementing the required level on a middleware that
provides a lower one annuls the abstraction improvements of reactive
programming. DREAM enables the developers to select
the best trade-off between consistency and overhead for the problem at
hand.

The reactive programming paradigm aims to simplify the development of reactive
systems. It provides abstractions to define time-changing values that are
automatically updated by the runtime according to their dependencies.

The benefits of reactive programming in distributed settings have been
recognized for long. Most solutions for distributed reactive
programming enforce the same semantics as in single processes, introducing
communication and synchronization costs that hamper scalability.

DREAM defines precise propagation
semantics in terms of consistency guarantees that constrain the order and
isolation of value updates. 

## Consistency Levels

Consistency levels supported by DREAM explained in a nutshell. For a precise definition see the academic publications.

**FIFO Consistency** Ensures that a signal reflects the changes to a single variable in the order in which they occur.

**Causal Consistency** Analogous to causal consistency in replicated data stores, which guarantees that operations that are causally related with each other take place in the same order in all the replicas, and this order reflects the causal dependency.

**Single-source Glitch Freedom** All the effects caused by an update on a node become visible at the same time. This means that a reader cannot observe the effects of an update on a node in an order that violates causality.

**Complete Glitch Freedom** Ensures that the results of two propagations are the same as if the propagations took place in some sequential order, without interleaving at any node.

**Atomic Consistency** Complete Glitch Freedom plus a read operation cannot observe only *some* of the effects of the change in a source: either it observes all of them, or none.



## Academic Publications

A. Margara, G. Salvaneschi, On the Semantics of Distributed Reactive Programming: the Cost of Consistency, IEEE Transactions on Software Engineering, 2018.

A. Margara and G. Salvaneschi, We have a DREAM: Distributed Reactive Programming with Consistency Guarantees, In Proceedings of the 8th International Conference on Distributed Event-Based Systems (DEBS ’14). Mumbay, India, May 26–29, 2014.

## Examples

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
	  
### Locks
When using Atomic_Consistency it is required to lock a Var before reading its value.
```
	LockToken lock = DreamClient.instance.readLock(new HashSet<>("exVar@Host1"));
	String value = rv.get();
	DreamClient.instance.unlock(lock);
```

### Getting Started
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

Alessandro Margara, Guido Salvaneschi, Tobias Becker, Kim Berninger, Michael Raulf, Ram Kamath.



	  
