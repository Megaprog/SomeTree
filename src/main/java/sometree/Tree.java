package sometree;


import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ForkJoinTask;
import java.util.concurrent.RecursiveTask;
import java.util.stream.Collectors;

/**
 * User: Tomas
 * Date: 11.03.14
 * Time: 22:07
 */
public class Tree {
    private Tree parent;
    private final List<Tree> children = new ArrayList<Tree>();

    public List<Tree> getChildren() {
        return Collections.unmodifiableList(children);
    }

    public Tree getParent() {
        return parent;
    }

    public void setParent(Tree parent) {
        if (this.parent != null) {
            this.parent.children.remove(this);
        }
        this.parent = parent;
        if (parent != null) {
            parent.children.add(this);
        }
    }

    public int height() {
        int maxHeight = 0;
        for (Tree child : children) {
            maxHeight = Math.max(maxHeight, child.height());
        }
        return maxHeight + 1;
    }

    public RecursiveTask<Integer> heightForkJoin() {
        return new RecursiveTask<Integer>() {
            @Override
            protected Integer compute() {
                int maxHeight = 0;

                for (ForkJoinTask<Integer> childHeight : children.stream().map(child -> child.heightForkJoin().fork()).collect(Collectors.toList())) {
                    maxHeight = Math.max(maxHeight, childHeight.join());
                }

                return maxHeight + 1;
            }
        };
    }

    public int heightParallel() {
        return children.parallelStream().mapToInt(Tree::heightParallel).max().orElse(0) + 1;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Tree tree = (Tree) o;

        if (!children.equals(tree.children)) return false;
        if (parent != null ? !parent.equals(tree.parent) : tree.parent != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = parent != null ? parent.hashCode() : 0;
        result = 31 * result + children.hashCode();
        return result;
    }

    @Override
    public String toString() {
        return "Tree{" +
                "parent=" + parent +
                ", children=" + children +
                '}';
    }
}
