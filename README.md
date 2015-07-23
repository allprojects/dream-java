# dream-java
## IMPL project - Kim Berninger and Michael Raulf 

The objective of this project was to use Java's generics in order to provide a
type-safe version of the _DREAM_ framework. This implementation can be found in
the `Dream2` folder. The original implementation using _ANTRL_ in order to
parse reactive expressions is contained in the `Dream` folder.

Both projects are compatible with _Eclipse_ as well as _Maven_. However at the
time of writing the _AspectJ Maven Plugin_ was not yet compatible with
_Java 8_, so it is recommended to run the _ANTRL_ dependend _DREAM_ using
_Eclipse_.

The `DreamSim` folder contains a custom version of _DREAM_ which is used in
order to run a _ProtoPeer_ based simulation for measuring the overall
performance of _DREAM_ in an example network.

The _Latex_ code for a PDF containing the resulting graphs can be found in
`Graphs`. Similarly there is a roadmap of the overall project in the
`ChangeReport` folder.