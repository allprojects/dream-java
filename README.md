# dream-java

The relevant source code is in the Dream2 folder. 


We initially need to assign a name to the current host `Host1`.
```java
Consts.hostName = "Host1";
```
Now on `Host1` we can define a Var `myVar` that is visible remotely. The var `myVar` constains a string, it is advertised with the name `exVar` and it is initialized to the value `AAA`.
```
Var<String> myVar = new Var<String>("exVar", "AAA");
```

On a different host, `Host2` we can read the var defined on `Host1` whose name is `exVar`. We can also define a signal that builds a computation on top of it.

```
RemoteVar<String> rv = new RemoteVar<String>("Host1", "exVar");

Signal<String> s = new Signal<String>>("s", () -> {
			return rv.get() + "XXX";	
		}, rv);
```
If rv is not available yet, ...
More examples:


```
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
	  
## Contributors

Alessandro Margara

Tobias Becker

Kim Berninger and Michael Raulf - IMPL Project



	  
