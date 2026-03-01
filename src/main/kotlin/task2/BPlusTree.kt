package org.example.task2

class BPlusTree(private val logger: EventLogger) {

    private val maxKeys = 6
    var root: Node = Node(isLeaf = true)

    inner class Node(
        var isLeaf: Boolean,
        var keys: MutableList<Int> = mutableListOf(),
        var children: MutableList<Node> = mutableListOf(),
        var next: Node? = null // Ссылка на следующий лист
    )

    fun insert(key: Int) {
        val path = mutableListOf<Node>()
        var current = root

        while (!current.isLeaf) {
            logger.log(TraceEvent.SEARCH_VISIT, "Internal Node keys: ${current.keys}")
            path.add(current)
            var i = 0
            while (i < current.keys.size && key >= current.keys[i]) {
                i++
            }
            current = current.children[i]
        }

        logger.log(TraceEvent.SEARCH_VISIT, "Leaf Node keys: ${current.keys}")
        insertIntoLeaf(current, key, path)
    }

    private fun insertIntoLeaf(leaf: Node, key: Int, path: MutableList<Node>) {
        var pos = 0
        while (pos < leaf.keys.size && leaf.keys[pos] < key) pos++
        leaf.keys.add(pos, key)
        logger.log(TraceEvent.INSERT_LEAF, "Key $key added")

        if (leaf.keys.size > maxKeys) {
            splitLeaf(leaf, path)
        }
    }

    private fun splitLeaf(leaf: Node, path: MutableList<Node>) {
        logger.log(TraceEvent.SPLIT_LEAF, "Leaf size ${leaf.keys.size} > $maxKeys")

        // Середина (при 7 ключах: splitIndex = 3 (4-й элемент))
        // Left: 0..2 (3 keys), Right: 3..6 (4 keys)
        val splitIndex = (leaf.keys.size) / 2

        val newLeaf = Node(isLeaf = true)

        // В B+ дереве в правом узле остаются ключи, включая разделитель, если он поднимается
        // Но здесь мы просто делим список
        val rightKeys = leaf.keys.subList(splitIndex, leaf.keys.size)
        newLeaf.keys.addAll(rightKeys)

        // Оставляем левую часть
        val leftKeys = leaf.keys.subList(0, splitIndex)
        val tempKeys = ArrayList(leftKeys) // копия
        leaf.keys.clear()
        leaf.keys.addAll(tempKeys)

        newLeaf.next = leaf.next
        leaf.next = newLeaf

        // Ключ для поднятия (первый ключ правого узла)
        val promoteKey = newLeaf.keys[0]
        logger.log(TraceEvent.PROMOTE_KEY, "Promoting $promoteKey")

        if (path.isEmpty()) {
            createNewRoot(leaf, newLeaf, promoteKey)
        } else {
            val parent = path.removeAt(path.lastIndex)
            insertIntoInternal(parent, promoteKey, newLeaf, path)
        }
    }

    private fun insertIntoInternal(parent: Node, key: Int, rightChild: Node, path: MutableList<Node>) {
        var pos = 0
        while (pos < parent.keys.size && parent.keys[pos] < key) pos++

        parent.keys.add(pos, key)
        parent.children.add(pos + 1, rightChild)

        if (parent.keys.size > maxKeys) {
            splitInternal(parent, path)
        }
    }

    private fun splitInternal(node: Node, path: MutableList<Node>) {
        logger.log(TraceEvent.SPLIT_INTERNAL, "Internal node full")

        val splitIndex = node.keys.size / 2
        val promoteKey = node.keys[splitIndex]

        val newInternal = Node(isLeaf = false)

        val rightKeys = node.keys.subList(splitIndex + 1, node.keys.size)
        newInternal.keys.addAll(rightKeys)

        val rightChildren = node.children.subList(splitIndex + 1, node.children.size)
        newInternal.children.addAll(rightChildren)

        val leftKeys = ArrayList(node.keys.subList(0, splitIndex))
        val leftChildren = ArrayList(node.children.subList(0, splitIndex + 1))

        node.keys.clear()
        node.keys.addAll(leftKeys)
        node.children.clear()
        node.children.addAll(leftChildren)

        logger.log(TraceEvent.PROMOTE_KEY, "Promoting $promoteKey from internal")

        if (path.isEmpty()) {
            createNewRoot(node, newInternal, promoteKey)
        } else {
            val parent = path.removeAt(path.lastIndex)
            insertIntoInternal(parent, promoteKey, newInternal, path)
        }
    }

    private fun createNewRoot(left: Node, right: Node, key: Int) {
        logger.log(TraceEvent.NEW_ROOT, "Tree height increased")
        val newRoot = Node(isLeaf = false)
        newRoot.keys.add(key)
        newRoot.children.add(left)
        newRoot.children.add(right)
        root = newRoot
    }
}