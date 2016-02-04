Check complete glitch freedom optimization:
it can probably show some misbehaviors if multiple events from
multiple sources are waiting for a lock at the same time in the
same signal.

Local locking is not implemented in the simulation environment.
It would require to use local locking mechanisms, which might
impact on the performance of the local propagation algorithm,
but we assume the processing time to be always negligible in
our evaluation.