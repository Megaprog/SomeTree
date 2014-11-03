package sometree;

import org.junit.Test;

import java.util.concurrent.ForkJoinPool;

import static org.junit.Assert.*;
import static org.hamcrest.Matchers.*;

/**
 * User: Tomas
 * Date: 11.03.14
 * Time: 22:17
 */
public class TreeTest {

    @Test
    public void testGetChildren() throws Exception {
        Tree tree = new Tree();
        assertThat(tree.getChildren(), empty());
    }

    @Test(expected = UnsupportedOperationException.class)
    public void testFailUpdateChildrenDirectly() throws Exception {
        Tree tree = new Tree();
        tree.getChildren().add(new Tree());
    }

    @Test
    public void testGetSetParent() throws Exception {
        Tree root = new Tree();
        assertNull(root.getParent());

        Tree child = new Tree();
        child.setParent(root);
        assertEquals(root, child.getParent());
        assertThat(root.getChildren(), contains(child));

        Tree otherRoot = new Tree();
        child.setParent(otherRoot);
        assertEquals(otherRoot, child.getParent());
        assertThat(root.getChildren(), empty());
        assertThat(otherRoot.getChildren(), contains(child));

        child.setParent(null);
        assertNull(child.getParent());
        assertThat(otherRoot.getChildren(), empty());
    }

    @Test
    public void testHeight() throws Exception {
        Tree root = new Tree();
        assertEquals(1, root.height());

        Tree child1 = new Tree();
        Tree child2 = new Tree();
        Tree child3 = new Tree();
        Tree child4 = new Tree();
        Tree child5 = new Tree();
        Tree child6 = new Tree();

        child1.setParent(root);
        child2.setParent(child1);
        child3.setParent(child2);
        child4.setParent(root);
        child5.setParent(child4);
        child6.setParent(root);
        assertThat(root.getChildren(), contains(child1, child4, child6));

        assertEquals(4, root.height());
        assertEquals(3, child1.height());
        assertEquals(2, child4.height());
        assertEquals(1, child6.height());
    }

    @Test
    public void testHeightForkJoin() throws Exception {
        Tree root = new Tree();
        assertEquals(1, root.height());

        Tree child1 = new Tree();
        Tree child2 = new Tree();
        Tree child3 = new Tree();
        Tree child4 = new Tree();
        Tree child5 = new Tree();
        Tree child6 = new Tree();

        child1.setParent(root);
        child2.setParent(child1);
        child3.setParent(child2);
        child4.setParent(root);
        child5.setParent(child4);
        child6.setParent(root);
        assertThat(root.getChildren(), contains(child1, child4, child6));

        final ForkJoinPool forkJoinPool = new ForkJoinPool();

        assertEquals(4, forkJoinPool.invoke(root.heightForkJoin()).intValue());
        assertEquals(3, forkJoinPool.invoke(child1.heightForkJoin()).intValue());
        assertEquals(2, forkJoinPool.invoke(child4.heightForkJoin()).intValue());
        assertEquals(1, forkJoinPool.invoke(child6.heightForkJoin()).intValue());
    }

    @Test
    public void testHeightParallel() throws Exception {
        Tree root = new Tree();
        assertEquals(1, root.height());

        Tree child1 = new Tree();
        Tree child2 = new Tree();
        Tree child3 = new Tree();
        Tree child4 = new Tree();
        Tree child5 = new Tree();
        Tree child6 = new Tree();

        child1.setParent(root);
        child2.setParent(child1);
        child3.setParent(child2);
        child4.setParent(root);
        child5.setParent(child4);
        child6.setParent(root);
        assertThat(root.getChildren(), contains(child1, child4, child6));

        assertEquals(4, root.heightParallel());
        assertEquals(3, child1.heightParallel());
        assertEquals(2, child4.heightParallel());
        assertEquals(1, child6.heightParallel());
    }
}
