# Concurrent Priority Queue Design

Here is a list of popular concurrent priority queue implementations. Mentioned proposals are selectively detailed to introduce the data structure used in Crowd Discrete Event Simulation based on reviews from [Practical Concurrent Priority Queues](./pdf/2015.Practical_Concurrent_Priority_Queues.pdf).

Complete set of papers can be found at `./pdf`.

## Skiplist-Based Concurrent Priority Queues, 2000 ([pdf](./pdf/2000.Skiplist-Based_Concurrent_Priority_Queues.pdf))

### Operation Design

* Insert

Bottom-to-top approach.

Determine the new node height by random generator. Then for each level find the insert node, lock and modify forward pointer.

* Delete

Top-to-bottom approach.

Find the supposedly deleted node. For each level, lock both current node and preceding node. Preceding point to next node THEN current node point to PREDECIND node, which ensures the current reader of this node will continue on the right track.

* Delete-min

Add delete flag and insert timestamp to each logical node.

For requesting thread, use CAS on delete flag to find and set the minimum node that is not deleted AND is inserted before this request. Then stick to common routine for deleting.

### Comment

* Fined-grained locking

* No rebalance overhead

* Large amont of CAS operations on flag cacheline

* No ordering mechanism for request, may cause starvation

## A Skiplist-Based Concurrent Priority Queue with Minimal Memory Contention, 2015

### Operation Design

* Delete

Logically delete by CAS set deletion flag. If the requester finds out logically deleted prefix exceeds boundary, use CAS on list head to physically delete.

Boundary of buffered stale node is determined through benchmarks. Suggested value 128 for 32 threads workload.

* Insert

Insert after logically deleted node, maintaining the prefix of deleted nodes.

### Comment

* Reduce CAS operation count by buffering deleted data

