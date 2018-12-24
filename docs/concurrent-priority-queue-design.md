# Concurrent Priority Queue Design

Here is a list of popular concurrent priority queue implementations. Complete set of papers can be found at `./pdf`.

## Skiplist-Based Concurrent Priority Queues, 2000 ([pdf](./pdf/2000.Skiplist-Based_Concurrent_Priority_Queues.pdf))

* Insert

```rust
let newNode = makeNode(value);
let newLevel = pickLevel();
for level in 0..newLevel {
	let node = findLastNodeLessThan(value);
	node.lock();
	node.pointRight(newNode);
	newNode.pointUp(makeNode(value));
	newNode = newNode.up();
}
```

* Delete

```rust
let node = findNode(value);
while let Node{up: upNode, right: rightNode, level: curLevel} = node{
	let pri = findLastNodeLessThan(value);
	pri.lock();
	node.lock();
	pri.pointRight(rightNode);
	node.pointRight(pri); // make sure current reader can go back to track
}
```
