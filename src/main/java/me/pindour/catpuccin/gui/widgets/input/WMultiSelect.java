package me.pindour.catpuccin.gui.widgets.input;

import me.pindour.catpuccin.gui.animation.Animation;
import me.pindour.catpuccin.gui.animation.Direction;
import me.pindour.catpuccin.gui.text.RichText;
import me.pindour.catpuccin.gui.text.TextSize;
import me.pindour.catpuccin.gui.themes.catpuccin.CatpuccinGuiTheme;
import me.pindour.catpuccin.gui.themes.catpuccin.widgets.WCatpuccinLabel;
import me.pindour.catpuccin.gui.themes.catpuccin.widgets.pressable.WCatpuccinCheckbox;
import meteordevelopment.meteorclient.gui.renderer.GuiRenderer;
import meteordevelopment.meteorclient.gui.widgets.WWidget;
import meteordevelopment.meteorclient.gui.widgets.containers.WHorizontalList;
import meteordevelopment.meteorclient.gui.widgets.containers.WVerticalList;
import meteordevelopment.meteorclient.gui.widgets.input.WTextBox;
import meteordevelopment.meteorclient.gui.widgets.pressable.WTriangle;

import java.util.*;
import java.util.function.BiConsumer;

import static org.lwjgl.glfw.GLFW.GLFW_MOUSE_BUTTON_LEFT;

public abstract class WMultiSelect<T> extends WVerticalList {
    protected final String title;
    protected boolean expanded;
    protected final List<ItemInfo<T>> items;

    protected List<ItemInfo<T>> filteredItems;
    protected FilterMode filterMode = FilterMode.ALL;
    private int selectedCount = 0;

    protected Animation animation;
    public BiConsumer<T, Boolean> onSelection;

    private WHeader header;
    private WItem selectAllItem;
    private WVerticalList itemContainer;

    public WMultiSelect(String title, List<ItemInfo<T>> items, WTextBox searchBox) {
        this.title = title;
        this.items = new ArrayList<>(items);

        if (searchBox != null) searchBox.action = () -> updateFilter(searchBox.get());

        filteredItems = new ArrayList<>(items);
    }

    @Override
    public void init() {
        CatpuccinGuiTheme theme = (CatpuccinGuiTheme) getTheme();

        animation = new Animation(theme.uiAnimationType(), theme.uiAnimationSpeed());
        animation.finishedAt(expanded ? Direction.FORWARDS : Direction.BACKWARDS);

        // Header
        header = add(createHeader()).padBottom(pad()).expandX().widget();

        // Select all
        if (items.size() > 1) {
            selectAllItem = createItem(new ItemInfo<>(null, "Select all", false));
            selectAllItem.action = () -> {
                boolean shouldSelectAll = selectAllItem.checkbox.checked;

                for (ItemInfo<T> item : filteredItems)
                    item.selected = shouldSelectAll;

                refreshItems();
            };

            add(selectAllItem).expandX();
            add(theme.horizontalSeparator()).padBottom(4).expandX();
        }

        // Items container
        itemContainer = add(theme.verticalList()).expandX().widget();
        itemContainer.spacing = 2;

        // Items
        refreshItems();
    }

    @Override
    protected void onCalculateSize() {
        super.onCalculateSize();

        height = (expanded || animation.isRunning()
                ? (height - header.height) * animation.getProgress() + header.height
                : header.height);
    }

    @Override
    public boolean render(GuiRenderer renderer, double mouseX, double mouseY, double delta) {
        if (!visible) return true;

        boolean isAnimationRunning = animation.isRunning();

        if (isAnimationRunning) {
            invalidate();
            renderer.scissorStart(x, y, width, height);
        }

        boolean toReturn = super.render(renderer, mouseX, mouseY, delta);

        if (isAnimationRunning) renderer.scissorEnd();

        return toReturn;
    }

    @Override
    protected void renderWidget(WWidget widget, GuiRenderer renderer, double mouseX, double mouseY, double delta) {
        if (expanded || animation.getProgress() > 0 || widget instanceof WMultiSelect<?>.WHeader) {
            widget.render(renderer, mouseX, mouseY, delta);
        }
    }

    @Override
    protected boolean propagateEvents(WWidget widget) {
        return expanded || widget instanceof WMultiSelect<?>.WHeader;
    }

    protected abstract WHeader createHeader();

    protected abstract WItem createItem(ItemInfo<T> itemInfo);

    public void setExpanded(boolean expanded) {
        if (this.expanded == expanded) return;
        this.expanded = expanded;

        animation.start(expanded ? Direction.FORWARDS : Direction.BACKWARDS);
    }

    public void setFilterMode(FilterMode filterMode) {
        this.filterMode = filterMode;
    }

    public void updateFilter(String query) {
        String normalizedQuery = query.trim().toLowerCase();
        filteredItems.clear();

        if (normalizedQuery.isEmpty() && filterMode == FilterMode.ALL)
            filteredItems = new ArrayList<>(items);

        else filterItems(normalizedQuery);

        refreshItems();

        // Close if no items match the filter
        if (filteredItems.isEmpty()) setExpanded(false);
    }

    private void filterItems(String query) {
        for (ItemInfo<T> itemInfo : items) {
            if (matchesFilter(itemInfo, query))
                filteredItems.add(itemInfo);
        }
    }

    private boolean matchesFilter(ItemInfo<T> itemInfo, String query) {
        boolean matchesQuery = query.isEmpty()
                || itemInfo.label.toLowerCase().contains(query);

        boolean matchesFilterMode = switch (filterMode) {
            case ALL -> true;
            case SELECTED -> itemInfo.selected;
            case UNSELECTED -> !itemInfo.selected;
        };

        return matchesQuery && matchesFilterMode;
    }

    private void refreshItems() {
        itemContainer.clear();
        selectedCount = 0;

        for (ItemInfo<T> itemInfo : filteredItems) {
            if (itemInfo.selected) selectedCount++;

            WItem itemWidget = createItem(itemInfo);
            itemContainer.add(itemWidget).expandX().widget();
        }

        updateSelectAllState();
        updateHeaderLabel();
    }

    private void updateSelectAllState() {
        if (selectAllItem != null) selectAllItem.setSelected(selectedCount > 0);
    }

    private void updateHeaderLabel() {
        header.sizeLabel.set(header.getSizeLabel());
    }

    protected abstract class WHeader extends WHorizontalList {
        protected String title;
        protected WCatpuccinLabel sizeLabel;
        protected WTriangle triangle;

        public WHeader(String title) {
            this.title = title;
        }

        @Override
        public void init() {
            CatpuccinGuiTheme theme = (CatpuccinGuiTheme) getTheme();

            add(theme.label(RichText.bold(title))).expandX().padVertical(8).padLeft(12);
            sizeLabel = (WCatpuccinLabel) add(theme.label(getSizeLabel())).padHorizontal(pad()).widget();

            triangle = add(theme.triangle()).widget();
            triangle.action = () -> setExpanded(!expanded);
        }

        @Override
        public boolean render(GuiRenderer renderer, double mouseX, double mouseY, double delta) {
            if (triangle != null) triangle.rotation = 90 + 90 * animation.getProgress();
            return super.render(renderer, mouseX, mouseY, delta);
        }

        @Override
        public boolean onMouseClicked(double mouseX, double mouseY, int button, boolean used) {
            if (mouseOver && button == GLFW_MOUSE_BUTTON_LEFT && !used) {
                onClick();
                return true;
            }

            return false;
        }

        protected void onClick() {
            setExpanded(!expanded);
        }

        protected RichText getSizeLabel() {
            return RichText.of("(" + selectedCount + "/" + filteredItems.size() + ")")
                    .scale(TextSize.SMALL.get());
        }
    }

    protected abstract class WItem extends WHorizontalList {
        protected final ItemInfo<T> itemInfo;
        protected WCatpuccinCheckbox checkbox;
        public Runnable action;

        public WItem(ItemInfo<T> itemInfo) {
            this.itemInfo = itemInfo;
        }

        @Override
        public void init() {
            checkbox = (WCatpuccinCheckbox) add(theme.checkbox(itemInfo.selected)).padLeft(8).widget();
            checkbox.action = this::toggleSelection;

            if (itemInfo.itemWidget != null) add(itemInfo.itemWidget);

            add(theme.label(itemInfo.label)).padLeft(pad()).expandX();
        }

        @Override
        protected void onCalculateSize() {
            super.onCalculateSize();
            height *= 1.25;
        }

        @Override
        public boolean onMouseClicked(double mouseX, double mouseY, int button, boolean used) {
            if (mouseOver && button == GLFW_MOUSE_BUTTON_LEFT && !used) {
                toggleSelection();
                return true;
            }

            return false;
        }

        public void setSelected(boolean selected) {
            itemInfo.selected = selected;
            checkbox.setChecked(selected);
        }

        private void toggleSelection() {
            setSelected(!itemInfo.selected);

            // Only update counter for real items (not for "Select all")
            if (selectAllItem != null && itemInfo.item != null) {
                selectedCount += itemInfo.selected ? 1 : -1;
                updateSelectAllState();
            }

            updateHeaderLabel();

            if (action != null) action.run();
            if (onSelection != null) onSelection.accept(itemInfo.item, itemInfo.selected);
        }
    }

    public static class ItemInfo<T> {
        public T item;
        public String label;
        public boolean selected;
        public WWidget itemWidget;

        public ItemInfo(T item, String label, boolean selected) {
            this.item = item;
            this.label = label;
            this.selected = selected;
        }

        public void setItemWidget(WWidget itemWidget) {
            this.itemWidget = itemWidget;
        }
    }

    public enum FilterMode {
        ALL,
        SELECTED,
        UNSELECTED
    }
}
