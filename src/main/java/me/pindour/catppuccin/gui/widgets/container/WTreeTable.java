package me.pindour.catppuccin.gui.widgets.container;

import meteordevelopment.meteorclient.gui.widgets.containers.WHorizontalList;
import meteordevelopment.meteorclient.gui.widgets.containers.WTable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.BiConsumer;
import java.util.function.Function;
import java.util.function.Predicate;

public class WTreeTable<T> extends WTable {
    private final List<T> items;
    private final Function<T, Set<T>> dependencyResolver;
    private final Predicate<T> visibility;
    private final BiConsumer<WTable, T> factoryCreator;

    private final List<Runnable> removalTasks = new ArrayList<>();
    private int totalVisible;

    public WTreeTable(List<T> items, Function<T, Set<T>> dependencyResolver, Predicate<T> visibility, BiConsumer<WTable, T> factoryCreator) {
        this.items = items;
        this.dependencyResolver = dependencyResolver;
        this.visibility = visibility;
        this.factoryCreator = factoryCreator;
    }

    @Override
    public void init() {
        super.init();
        buildTree();
    }

    private void buildTree() {
        removalTasks.clear();
        totalVisible = 0;

        Map<T, Node<T>> nodes = new HashMap<>();
        for (T item : items) nodes.put(item, new Node<>(item));

        List<Node<T>> roots = new ArrayList<>();
        for (int i = 0; i < items.size(); i++) {
            T item = items.get(i);
            Node<T> node = nodes.get(item);

            T parent = findParent(item, i);
            if (parent != null) nodes.get(parent).children.add(node);
            else roots.add(node);
        }

        for (Node<T> root : roots) renderNode(this, root);
    }

    private T findParent(T item, int index) {
        Set<T> dependencies = dependencyResolver.apply(item);
        if (dependencies == null || dependencies.isEmpty()) return null;

        for (int i = index - 1; i >= 0; i--) {
            T candidate = items.get(i);
            if (dependencies.contains(candidate)) return candidate;
        }
        return null;
    }

    private boolean renderNode(WTable table, Node<T> node) {
        T item = node.item;
        boolean visible = visibility.test(item);
        if (visible) totalVisible++;

        int itemRow = table.rowI();
        factoryCreator.accept(table, item);
        table.row();

        if (!visible) removalTasks.add(() -> table.removeRow(itemRow));

        if (!node.children.isEmpty()) {
            int childrenRow = table.rowI();

            WHorizontalList indentList = theme.horizontalList();
            indentList.add(theme.verticalSeparator()).padHorizontal(theme.textHeight() / 2).expandWidgetY();

            WTable childTable = theme.table();
            childTable.verticalSpacing = this.verticalSpacing;
            indentList.add(childTable).expandX();

            boolean hasVisibleChildren = false;
            for (Node<T> child : node.children) {
                hasVisibleChildren |= renderNode(childTable, child);
            }

            table.add(indentList).expandX();
            table.row();

            if (!visible || !hasVisibleChildren) removalTasks.add(() -> table.removeRow(childrenRow));
        }

        return visible;
    }

    public void performRemovals() {
        for (int i = removalTasks.size() - 1; i >= 0; i--) {
            removalTasks.get(i).run();
        }
    }

    public boolean isEmpty() {
        return totalVisible == 0;
    }

    private static class Node<T> {
        final T item;
        final List<Node<T>> children = new ArrayList<>();

        Node(T item) {
            this.item = item;
        }
    }
}
