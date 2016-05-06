# dream-java

The relevant source code is in the Dream2 folder. 


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



	  
