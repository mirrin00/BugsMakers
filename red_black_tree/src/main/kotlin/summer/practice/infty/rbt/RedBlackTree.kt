package summer.practice.infty.rbt

import kotlin.math.pow

class RedBlackTree<T: Comparable<T>, V> {
    private var root: Node<T, V>? = null // root of the tree
    var height: Int = 0 // Height of the tree, not black!!!
        private set

    // True - if node is red, otherwise - false
    private fun isRedNode(node: Node<T, V>?): Boolean{
        return node?.is_red ?: false
    }

    // True - if node is black, otherwise - false
    private fun isBlackNode(node: Node<T, V>?): Boolean{
        return !isRedNode(node)
    }

    // Find and return the node with key
    private fun findNode(key: T): Node<T, V>?{
        var cur = root
        while(cur != null){
            if(key == cur.key) break
            cur = if(key < cur.key) cur.left
            else cur.right
        }
        return cur
    }

    // Find and return the value with key
    fun find(key: T): V?{
        return findNode(key)?.data
    }

    // Insert the new value with key
    fun insert(key: T, value: V?){
        if(root == null){
            height = 1
            root = Node(key, value)
            insertBalance(root)
            return
        }
        var cur = root
        while(cur != null){
            if(key == cur.key){
                cur.data = value
                return
            }
            if(key < cur.key){
                if(cur.left == null){
                    cur.left = Node(key, value, cur, true)
                    insertBalance(cur.left)
                    break
                }
                else cur = cur.left
            }else{
                if(cur.right == null){
                    cur.right = Node(key, value, cur, false)
                    insertBalance(cur.right)
                    break
                }
                else cur = cur.right
            }
        }
        updateHeight()
    }

    private fun updateHeight(){
        height = 0
        fun heightUpdate(node: Node<T, V>?, height: Int) {
            if (node == null) return
            if(height > this.height) this.height = height
            heightUpdate(node.left, height + 1)
            heightUpdate(node.right, height + 1)
        }
        heightUpdate(root, 1)
    }

    // Return ArrayList of values in-order
    fun getData(): ArrayList<V?>{
        val values = ArrayList<V?>()
        fun inOrder(node: Node<T, V>?) {
            if (node == null) return
            inOrder(node.left)
            values.add(node.data)
            inOrder(node.right)
        }
        inOrder(root)
        return values
    }

    fun getNodes(): Array<Node<T, V>?>{
        val nodes = Array<Node<T, V>?>(2.0.pow(height).toInt() - 1){null}
        println("GETNODES: Size is ${nodes.size}")
        fun inOrder(node: Node<T, V>?, index: Int) {
            if (node == null) return
            inOrder(node.left, index * 2 + 1)
            nodes[index] = node
            inOrder(node.right, index * 2 + 2)
        }
        inOrder(root, 0)
        return nodes
    }

    // Balance the tree after insert
    private fun insertBalance(node: Node<T, V>?){
        if(node == null) return
        rotate(node)
        recolor(node)
    }

    // Recolor nodes
    private fun recolor(node: Node<T, V>){
        if(node.parent == null) node.is_red = false
        val parent = node.parent ?: return
        if(parent.parent == null) parent.is_red = false
        val grandparent = parent.parent ?: return
        val uncle = if(parent.is_left) grandparent.right
                    else grandparent.left
        if(isRedNode(parent) && isRedNode(uncle)){
            parent.is_red = false
            uncle?.is_red = false
            grandparent.is_red = true
            insertBalance(grandparent)
        }else if(isRedNode(parent)) parent.is_red = false
    }

    // Checks, if rotation is possible, and performs it
    private fun rotate(node: Node<T, V>){
        val parent = node.parent ?: return
        val grandparent = parent.parent ?: return
        val uncle = if(parent.is_left) grandparent.right
                    else grandparent.left
        if(isRedNode(parent) && isBlackNode(uncle)){
            if(node.is_left != parent.is_left){
                smallRotate(node)
                bigRotate(node, grandparent)
            }else bigRotate(parent, grandparent)
        }
    }

    // Performs small rotation for node
    private fun smallRotate(node: Node<T, V>){
        println("SmallRotate")
        val parent = node.parent ?: return
        val grandparent = parent.parent ?: return
        node.parent = grandparent
        parent.parent = node
        if(parent.is_left){
            grandparent.left = node
            node.is_left = true
            parent.right = node.left
            node.left?.is_left = false
            node.left?.parent = parent
            parent.parent = node
            node.left = parent
        }else{
            grandparent.right = node
            node.is_left = false
            parent.left = node.right
            node.right?.is_left = true
            node.right?.parent = parent
            parent.parent = node
            node.right = parent
        }
    }

    // Performs big rotation for node
    private fun bigRotate(node: Node<T, V>, parent: Node<T, V>){
        node.swapKeyAndData(parent)
        val son_same_dir = node.getSon(node.is_left)
        val son_other_dir = node.getSon(!node.is_left)
        val son = parent.getSon(!node.is_left)
        parent.changeSon(son_same_dir, node.is_left)
        son_same_dir?.parent = parent
        node.changeSon(son_other_dir, node.is_left)
        son_other_dir?.invertLeft()
        node.changeSon(son, !node.is_left)
        son?.parent = node
        parent.changeSon(node, !node.is_left)
        node.invertLeft()
    }

    // Deletes value by key and returns it, or null, if the key does not exist
    fun delete(key: T): V?{
        val node = findNode(key)
        deleteNode(node)
        updateHeight()
        return node?.data
    }

    // Deletes node from tree and call balance
    private fun deleteNode(node: Node<T, V>?){
        if(node == null) return
        val left = node.left
        val right = node.right
        when{
            left == null && right == null -> {
                val parent = node.parent
                if(parent == null){
                    root = null
                }else{
                    parent.changeSon(null, node.is_left)
                    if(isBlackNode(node)) deleteBalance(null, parent, node.is_left)
                }
            }
            left != null && right != null -> {
                var cur: Node<T, V> = right
                while(cur.left != null) cur = cur.left!!
                node.swapKeyAndData(cur)
                val parent = cur.parent ?: return // FIXME: throw exception
                cur.right?.is_left = cur.is_left
                cur.right?.parent = parent
                parent.changeSon(cur.right, cur.is_left)
                deleteBalance(cur, parent)
            }
            else -> { /* in that case only is null, because case with two nulls
                         described above */
                // FIXME: throw exception
                val son = node.left ?: node.right ?: return
                val parent = node.parent
                son.is_left = node.is_left
                son.parent = parent
                if(parent == null){
                    root = son
                }else{
                    parent.changeSon(son, node.is_left)
                    if(isBlackNode(node)) deleteBalance(son, parent)
                }
            }
        }
    }

    // Balances tree after deletion
    private fun deleteBalance(node: Node<T, V>?,
                              parent: Node<T, V>,
                              is_left: Boolean = true){/* true, if node is left
                                                          son of parent */
        if(isRedNode(node)){
            node?.is_red = false
            return
        }
        var sibling = parent.getSon(!(node?.is_left ?: is_left))
        // sibling not null, because red node can not be null
        if(isRedNode(sibling)){
            bigRotate(sibling!!, parent)
            sibling = sibling.getSon(!sibling.is_left)
        }
        /*
         * Also sibling is not null because deleted node is black.
         * If sibling is null and deleted node on the other side
         * is black, then it is not RBT, because the black height
         * is different for the sides
         */

        val son_same_dir = sibling?.getSon(sibling.is_left)
        val son_other_dir = sibling?.getSon(!sibling.is_left)
        if(isBlackNode(son_same_dir) && isBlackNode(son_other_dir)){
            sibling?.is_red = false
            // new_node = parent
            if(isRedNode(parent)){
                parent.is_red = false
            }else{
                val grandparent = parent.parent
                if(grandparent != null) deleteBalance(parent, grandparent)
            }
        }else{
            /*
             * son_other_dir is red, because condition
             * of two black sons is above
             */
            if(isBlackNode(son_same_dir))
                bigRotate(son_other_dir!!, sibling!!)
            bigRotate(sibling!!, parent)
            parent.getSon(!sibling.is_left)!!.is_red = false
        }
    }
}