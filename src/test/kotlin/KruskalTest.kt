package ru.etu.visualkruskal

import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test


internal class KruskalTest {

    @Nested
    inner class AddEdgeTest {
        private lateinit var solver: Kruskal

        @BeforeEach
        fun setUp() {
            solver = Kruskal()
        }

        @Test
        fun `Regular case`() {
            // Act
            val res: String = solver.addEdge('a', 'z', 23)
            val edges: List<Edge> = solver.getEdges()
            val nodes = solver.getNodes()

            // message check
            assertEquals("Edge added", res)

            // nodes-map check
            assertEquals(2, nodes.size)
            assertArrayEquals(arrayOf('z'), nodes['a']!!.toArray())
            assertArrayEquals(arrayOf('a'), nodes['z']!!.toArray())

            // Edges list check
            assertEquals(1, edges.size)
            assertEquals(23, edges.first().weight)
            assertEquals('a', edges.first().node1)
            assertEquals('z', edges.first().node2)
            assertEquals(EdgeState.NOT_SEEN, edges.first().state)
        }

        @Test
        fun `Reverse order of symbols`() {
            // Act
            val res: String = solver.addEdge('v', 'f', -3)
            val edges: List<Edge> = solver.getEdges()
            val nodes = solver.getNodes()

            // message check
            assertEquals("Edge added", res)

            // nodes-map check
            assertEquals(2, nodes.size)
            assertArrayEquals(arrayOf('v'), nodes['f']!!.toArray())
            assertArrayEquals(arrayOf('f'), nodes['v']!!.toArray())

            // Edges list check
            assertEquals(1, edges.size)
            assertEquals(-3, edges.first().weight)
            assertEquals('f', edges.first().node1)
            assertEquals('v', edges.first().node2)
            assertEquals(EdgeState.NOT_SEEN, edges.first().state)
        }

        @Test
        fun `Same symbols`() {
            // Act
            val res: String = solver.addEdge('d', 'd', 0)
            val edges: List<Edge> = solver.getEdges()
            val nodes = solver.getNodes()

            // message check
            assertEquals("Nodes must be different", res)

            // nodes-map check
            assertTrue(nodes.isEmpty())

            // Edges list check
            assertTrue(edges.isEmpty())
        }

        @Test
        fun `Incorrect symbols`() {
            // Act
            val res: String = solver.addEdge('A', 'd', 0)
            val edges: List<Edge> = solver.getEdges()
            val nodes = solver.getNodes()

            // message check
            assertEquals("Node name must be a latin letter", res)

            // nodes-map check
            assertTrue(nodes.isEmpty())

            // Edges list check
            assertTrue(edges.isEmpty())
        }

        @Test
        fun `Duplicate edge`() {
            // Arrange
            solver.addEdge('f', 'd', 3)

            // Act
            val res: String = solver.addEdge('d', 'f', 55)
            val edges: List<Edge> = solver.getEdges()
            val nodes = solver.getNodes()

            // message check
            assertEquals("Such edge already exists", res)

            // nodes-map check
            assertEquals(2, nodes.size)
            assertArrayEquals(arrayOf('f'), nodes['d']!!.toArray())
            assertArrayEquals(arrayOf('d'), nodes['f']!!.toArray())

            // Edges list check
            assertEquals(1, edges.size)
            assertEquals(3, edges.first().weight)
            assertEquals('d', edges.first().node1)
            assertEquals('f', edges.first().node2)
            assertEquals(EdgeState.NOT_SEEN, edges.first().state)

        }

        @Test
        fun `Two edges with the same vertex `() {
            // Arrange
            solver.addEdge('a', 'f', 3)

            // Act
            val res: String = solver.addEdge('f', 'b', 55)
            val edges: List<Edge> = solver.getEdges()
            val nodes = solver.getNodes()

            // message check
            assertEquals("Edge added", res)

            // nodes-map check
            assertEquals(3, nodes.size)
            assertArrayEquals(arrayOf('a', 'b'), nodes['f']!!.toArray())
            assertArrayEquals(arrayOf('f'), nodes['a']!!.toArray())
            assertArrayEquals(arrayOf('f'), nodes['b']!!.toArray())

            // Edges list check
            assertEquals(2, edges.size)

            assertEquals(3, edges.first().weight)
            assertEquals('a', edges.first().node1)
            assertEquals('f', edges.first().node2)
            assertEquals(EdgeState.NOT_SEEN, edges.first().state)

            assertEquals(55, edges.last().weight)
            assertEquals('b', edges.last().node1)
            assertEquals('f', edges.last().node2)
            assertEquals(EdgeState.NOT_SEEN, edges.last().state)

        }
    }

    @Nested
    inner class DeleteEdgeTest {
        private lateinit var solver: Kruskal

        @BeforeEach
        fun setUp() {
            solver = Kruskal()
        }

        @Test
        fun `One edge`() {
            // Arrange
            solver.addEdge('a', 'b', 43)

            // Act
            val res: String = solver.delEdge('a', 'b')
            val edges: List<Edge> = solver.getEdges()
            val nodes = solver.getNodes()

            // message check
            assertEquals("Edge removed", res)

            // nodes-map check
            assertEquals(2, nodes.size)
            assertTrue(nodes['a']!!.isEmpty())
            assertTrue(nodes['b']!!.isEmpty())

            // Edges list check
            assertTrue(edges.isEmpty())
        }

        @Test
        fun `Path of vertex`() {
            // Arrange
            solver.addEdge('a', 'b', -5)
            solver.addEdge('b', 'd', 1)
            solver.addEdge('a', 'c', 4)

            // Act
            val res: String = solver.delEdge('a', 'b')
            val edges: List<Edge> = solver.getEdges()
            val nodes = solver.getNodes()

            // message check
            assertEquals("Edge removed", res)

            // nodes-map check
            assertEquals(4, nodes.size)
            assertArrayEquals(arrayOf('c'), nodes['a']!!.toArray())
            assertArrayEquals(arrayOf('d'), nodes['b']!!.toArray())
            assertArrayEquals(arrayOf('b'), nodes['d']!!.toArray())
            assertArrayEquals(arrayOf('a'), nodes['c']!!.toArray())

            // Edges list check
            assertEquals(2, edges.size)

        }

        @Test
        fun `Reverse order of symbols`() {
            // Arrange
            solver.addEdge('a', 'b', -5)
            solver.addEdge('b', 'd', 1)
            solver.addEdge('a', 'c', 4)

            // Act
            val res: String = solver.delEdge('b', 'a')
            val edges: List<Edge> = solver.getEdges()
            val nodes = solver.getNodes()

            // message check
            assertEquals("Edge removed", res)

            // nodes-map check
            assertEquals(4, nodes.size)
            assertArrayEquals(arrayOf('c'), nodes['a']!!.toArray())
            assertArrayEquals(arrayOf('d'), nodes['b']!!.toArray())
            assertArrayEquals(arrayOf('d'), nodes['b']!!.toArray())
            assertArrayEquals(arrayOf('c'), nodes['a']!!.toArray())

            // Edges list check
            assertEquals(2, edges.size)
        }


        @Test
        fun `From the empty edges-list`() {
            // Act
            val res: String = solver.delEdge('b', 'a')
            val edges: List<Edge> = solver.getEdges()
            val nodes = solver.getNodes()

            // message check
            assertEquals("There is no such edge in the graph", res)

            // nodes-map check
            assertTrue(nodes.isEmpty())

            // Edges list check
            assertTrue(edges.isEmpty())
        }

        @Test
        fun `Non-existent edge`() {
            // Arrange
            solver.addEdge('a', 'b', -5)
            solver.addEdge('b', 'd', 1)
            solver.addEdge('a', 'c', 4)

            // Act
            val res: String = solver.delEdge('a', 'd')
            val edges: List<Edge> = solver.getEdges()
            val nodes = solver.getNodes()

            // message check
            assertEquals("There is no such edge in the graph", res)

            // nodes-map check
            assertEquals(4, nodes.size)
            assertArrayEquals(arrayOf('b', 'c'), nodes['a']!!.toArray())
            assertArrayEquals(arrayOf('a', 'd'), nodes['b']!!.toArray())
            assertArrayEquals(arrayOf('b'), nodes['d']!!.toArray())
            assertArrayEquals(arrayOf('a'), nodes['c']!!.toArray())

            // Edges list check
            assertEquals(3, edges.size)
        }
    }

    @Nested
    inner class AddNodeTest {
        private lateinit var solver: Kruskal

        @BeforeEach
        fun setUp() {
            solver = Kruskal()
        }

        @Test
        fun `Regular case`() {
            // Arrange
            solver.addNode('b')

            // Act
            val res: String = solver.addNode('a')
            val edges: List<Edge> = solver.getEdges()
            val nodes = solver.getNodes()

            // message check
            assertEquals("Node added", res)

            // nodes-map check
            assertEquals(2, nodes.size)
            assertTrue(nodes['a']!!.isEmpty())
            assertTrue(nodes['b']!!.isEmpty())

            // Edges list check
            assertTrue(edges.isEmpty())
        }

        @Test
        fun `First node`() {
            // Act
            val res: String = solver.addNode('a')
            val edges: List<Edge> = solver.getEdges()
            val nodes = solver.getNodes()

            // message check
            assertEquals("Node added", res)

            // nodes-map check
            assertEquals(1, nodes.size)
            assertTrue(nodes['a']!!.isEmpty())

            // Edges list check
            assertTrue(edges.isEmpty())
        }


        @Test
        fun `Duplicate node`() {
            // Arrange
            solver.addNode('a')
            solver.addNode('b')

            // Act
            val res: String = solver.addNode('a')
            val edges: List<Edge> = solver.getEdges()
            val nodes = solver.getNodes()

            // message check
            assertEquals("Such node already exists", res)

            // nodes-map check
            assertEquals(2, nodes.size)
            assertTrue(nodes['a']!!.isEmpty())
            assertTrue(nodes['b']!!.isEmpty())

            // Edges list check
            assertTrue(edges.isEmpty())
        }

        @Test
        fun `Incorrect symbol`() {
            // Arrange
            solver.addNode('a')
            solver.addNode('b')

            // Act
            val res: String = solver.addNode('F')
            val edges: List<Edge> = solver.getEdges()
            val nodes = solver.getNodes()

            // message check
            assertEquals("Node name must be a latin letter", res)

            // nodes-map check
            assertEquals(2, nodes.size)
            assertTrue(nodes['a']!!.isEmpty())
            assertTrue(nodes['b']!!.isEmpty())

            // Edges list check
            assertTrue(edges.isEmpty())
        }
    }

    @Nested
    inner class DeleteNodeTest {
        private lateinit var solver: Kruskal

        @BeforeEach
        fun setUp() {
            solver = Kruskal()
        }

        @Test
        fun `Isolated nodes`() {
            // Arrange
            solver.addNode('a')
            solver.addNode('b')
            solver.addNode('c')

            // Act
            val res: String = solver.delNode('b')
            val edges: List<Edge> = solver.getEdges()
            val nodes = solver.getNodes()

            // message check
            assertEquals("Node deleted", res)

            // nodes-map check
            assertEquals(2, nodes.size)
            assertTrue(nodes['a']!!.isEmpty())
            assertTrue(nodes['c']!!.isEmpty())

            // Edges list check
            assertTrue(edges.isEmpty())
        }


        @Test
        fun `One node`() {
            // Arrange
            solver.addNode('a')

            // Act
            val res: String = solver.delNode('a')
            val edges: List<Edge> = solver.getEdges()
            val nodes = solver.getNodes()

            // message check
            assertEquals("Node deleted", res)

            // nodes-map check
            assertTrue(nodes.isEmpty())

            // Edges list check
            assertTrue(edges.isEmpty())
        }

        @Test
        fun `Incorrect symbol`() {
            // Arrange
            solver.addNode('a')
            solver.addNode('b')
            solver.addNode('c')

            // Act
            val res: String = solver.delNode('Q')
            val edges: List<Edge> = solver.getEdges()
            val nodes = solver.getNodes()

            // message check
            assertEquals("There is no such node", res)

            // nodes-map check
            assertEquals(3, nodes.size)
            assertTrue(nodes['a']!!.isEmpty())
            assertTrue(nodes['b']!!.isEmpty())
            assertTrue(nodes['c']!!.isEmpty())

            // Edges list check
            assertTrue(edges.isEmpty())
        }

        @Test
        fun `Non-existent node`() {
            // Arrange
            solver.addNode('a')
            solver.addNode('b')
            solver.addNode('c')

            // Act
            val res: String = solver.delNode('g')
            val edges: List<Edge> = solver.getEdges()
            val nodes = solver.getNodes()

            // message check
            assertEquals("There is no such node", res)

            // nodes-map check
            assertEquals(3, nodes.size)
            assertTrue(nodes['a']!!.isEmpty())
            assertTrue(nodes['b']!!.isEmpty())
            assertTrue(nodes['c']!!.isEmpty())

            // Edges list check
            assertTrue(edges.isEmpty())
        }

        @Test
        fun `Terminal node`() {
            // Arrange
            solver.addEdge('a', 'b', 54)
            solver.addEdge('b', 'c', 12)

            // Act
            val res: String = solver.delNode('a')
            val edges: List<Edge> = solver.getEdges()
            val nodes = solver.getNodes()

            // message check
            assertEquals("Node deleted", res)

            // nodes-map check
            assertEquals(2, nodes.size)
            assertArrayEquals(arrayOf('c'), nodes['b']!!.toArray())
            assertArrayEquals(arrayOf('b'), nodes['c']!!.toArray())

            // Edges list check
            assertEquals(1, edges.size)
            assertEquals('b', edges.first().node1)
            assertEquals('c', edges.first().node2)
            assertEquals(12, edges.first().weight)
        }

        @Test
        fun `Two adjacent vertices`() {
            // Arrange
            solver.addEdge('a', 'b', 54)
            solver.addEdge('b', 'c', 12)

            // Act
            val res: String = solver.delNode('b')
            val edges: List<Edge> = solver.getEdges()
            val nodes = solver.getNodes()

            // message check
            assertEquals("Node deleted", res)

            // nodes-map check
            assertEquals(2, nodes.size)
            assertTrue(nodes['a']!!.isEmpty())
            assertTrue(nodes['c']!!.isEmpty())

            // Edges list check
            assertTrue(edges.isEmpty())
        }
    }

    @Nested
    inner class IsGraphConnectedTest {
        private lateinit var solver: Kruskal

        @BeforeEach
        fun setUp() {
            solver = Kruskal()
        }

        @Test
        fun `One node`() {
            // Arrange
            solver.addNode('a')

            // Act
            val res: Boolean = solver.isGraphConnected()

            // Assert
            assertTrue(res)
        }

        @Test
        fun `One edge`() {
            // Arrange
            solver.addEdge('a', 'b', 3)

            // Act
            val res: Boolean = solver.isGraphConnected()

            // Assert
            assertTrue(res)
        }

        @Test
        fun `Isolated nodes`() {
            // Arrange
            solver.addNode('a')
            solver.addNode('b')
            solver.addNode('c')

            // Act
            val res: Boolean = solver.isGraphConnected()

            // Assert
            assertFalse(res)
        }

        @Test
        fun `Ordinary connected graph `() {
            // Arrange
            solver.addEdge('a', 'b', 4)
            solver.addEdge('b', 'c', -54)
            solver.addEdge('c', 'd', 5)
            solver.addEdge('d', 'b', 43)
            solver.addEdge('a', 'f', 1)
            solver.addEdge('f', 'v', 4)

            // Act
            val res: Boolean = solver.isGraphConnected()

            // Assert
            assertTrue(res)
        }

        @Test
        fun `Ordinary disconnected graph `() {
            // Arrange
            solver.addEdge('a', 'b', 4)
            solver.addEdge('b', 'c', -54)
            solver.addEdge('c', 'd', 5)
            solver.addEdge('d', 'b', 43)
            solver.addEdge('a', 'f', 1)
            solver.addEdge('f', 'v', 4)
            solver.addEdge('y', 'z', 4)
            solver.addEdge('z', 'l', 4)
            solver.addEdge('l', 'm', 987)

            // Act
            val res: Boolean = solver.isGraphConnected()

            // Assert
            assertFalse(res)
        }
    }

    @Nested
    inner class DoAlgorithmTest {
        private lateinit var solver: Kruskal

        @BeforeEach
        fun setUp() {
            solver = Kruskal()
        }

        @Test
        fun `One edge`() {
            // Arrange
            solver.addEdge('a', 'b', 100)

            // Act
            val res: ArrayList<Edge> = solver.doAlgorithm()

            // Assert
            assertEquals(EdgeState.INCLUDED, res.first().state)
            assertEquals(100, solver.getWeight())
        }

        @Test
        fun `Tree with repeated weights`() {
            // Arrange
            val inputEdge: List<String> = arrayListOf(
                "a b 5", "a d 1", "d e 1", "d f 3", "d g 2", "a c 2", "c i 2", "c h 4"
            )
            solver.createGraph(inputEdge)

            // Act
            val res: ArrayList<Edge> = solver.doAlgorithm()

            // Assert
            res.forEach { assertEquals(EdgeState.INCLUDED, it.state) }
            assertEquals(20, solver.getWeight())
        }

        @Test
        fun `Joining two large connected components`() {
            // Arrange
            val inputEdge: List<String> = arrayListOf(
                "a b 1", "a f 3", "b c 2", "c d 5", "d e -4", "e f 1"
            )
            val expectedStates = arrayListOf(
                EdgeState.INCLUDED,
                EdgeState.INCLUDED,
                EdgeState.INCLUDED,
                EdgeState.INCLUDED,
                EdgeState.INCLUDED,
                EdgeState.NOT_SEEN
            )

            solver.createGraph(inputEdge)

            // Act
            val res: ArrayList<Edge> = solver.doAlgorithm()

            // Assert
            assertEquals(3, solver.getWeight())
            for (i in expectedStates.indices) assertEquals(expectedStates[i], res[i].state)

        }

        @Test
        fun `Check skipped edges`() {
            // Arrange
            val inputEdge: List<String> = arrayListOf(
                "d e 7", "b e 6", "b d 5", "c d 4", "b c 3", "a c 2", "a b 1"
            )
            val expectedStates = arrayListOf(
                EdgeState.INCLUDED,
                EdgeState.INCLUDED,
                EdgeState.DISCARDED,
                EdgeState.INCLUDED,
                EdgeState.DISCARDED,
                EdgeState.INCLUDED,
                EdgeState.NOT_SEEN
            )

            solver.createGraph(inputEdge)

            // Act
            val res: ArrayList<Edge> = solver.doAlgorithm()

            // Assert
            assertEquals(13, solver.getWeight())
            for (i in expectedStates.indices) assertEquals(expectedStates[i], res[i].state)

        }

        @Test
        fun `Disconnected graph`() {
            // Arrange
            val inputEdge: List<String> = arrayListOf(
                "a b 2", "b c 20", "b d 100", "e f -70"
            )

            solver.createGraph(inputEdge)

            // Act
            val res: ArrayList<Edge> = solver.doAlgorithm()

            // Assert
            assertEquals(0, solver.getWeight())
            res.forEach { assertEquals(EdgeState.NOT_SEEN, it.state) }

        }

    }


}