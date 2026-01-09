package me.pindour.catppuccin.gui.widgets.input;

import me.pindour.catppuccin.gui.animation.Animation;
import me.pindour.catppuccin.gui.animation.Direction;
import me.pindour.catppuccin.gui.text.RichText;
import me.pindour.catppuccin.gui.text.TextSize;
import me.pindour.catppuccin.gui.themes.catppuccin.CatppuccinGuiTheme;
import me.pindour.catppuccin.gui.themes.catppuccin.widgets.WCatppuccinLabel;
import me.pindour.catppuccin.gui.themes.catppuccin.widgets.pressable.WCatppuccinCheckbox;
import meteordevelopment.meteorclient.gui.renderer.GuiRenderer;
import meteordevelopment.meteorclient.gui.widgets.WWidget;
import meteordevelopment.meteorclient.gui.widgets.containers.WHorizontalList;
import meteordevelopment.meteorclient.gui.widgets.containers.WVerticalList;
import meteordevelopment.meteorclient.gui.widgets.pressable.WTriangle;

import java.util.*;
import java.util.function.BiConsumer;
import java.util.function.Function;
import java.util.function.Predicate;

import static org.lwjgl.glfw.GLFW.GLFW_MOUSE_BUTTON_LEFT;

//? if >=1.21.9
import net.minecraft.client.gui.Click;

public abstract class WMultiSelect<T> extends WVerticalList {
    protected final String title;
    protected boolean expanded;

    protected final List<T> items;
    protected List<T> filteredItems;

    protected FilterMode filterMode = FilterMode.ALL;
    private int selectedCount = 0;

    protected Animation animation;

    private Function<T, String> labelMapper = Object::toString;
    private Predicate<T> selectionPredicate = item -> false;
    private BiConsumer<T, Boolean> selectionChangeHandler;

    protected WHeader header;
    private WCatppuccinCheckbox selectAllCheckbox;
    private WVerticalList itemContainer;

    public WMultiSelect(String title, List<T> items) {
        this.title = title;
        this.items = new ArrayList<>(items);
        filteredItems = new ArrayList<>(items);
    }

    @Override
    public void init() {
        CatppuccinGuiTheme theme = (CatppuccinGuiTheme) getTheme();

        animation = new Animation(
                theme.guiAnimationEasing(),
                theme.guiAnimationDuration(),
                expanded ? Direction.FORWARDS : Direction.BACKWARDS
        );

        // Header
        header = add(createHeader()).padBottom(pad()).expandX().widget();

        // Select all
        if (items.size() > 1) {
            WHorizontalList list = add(theme.horizontalList()).expandX().pad(4).widget();

            selectAllCheckbox = (WCatppuccinCheckbox) list.add(theme.checkbox(false)).padLeft(8).widget();
            list.add(theme.label("Select all")).padLeft(pad()).expandX();

            selectAllCheckbox.action = () -> {
                boolean shouldSelectAll = selectAllCheckbox.checked;
                filteredItems.forEach(item -> handleSelectionChange(item, shouldSelectAll));
                refreshItems();
            };

            add(theme.horizontalSeparator()).expandX();
        }

        // Items container
        itemContainer = add(theme.verticalList()).expandX().pad(4).widget();
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
        return super.propagateEvents(widget) && (expanded || widget instanceof WMultiSelect<?>.WHeader);
    }

    protected abstract WHeader createHeader();

    protected WItem createItem(T item) {
        return new WItem(item);
    }

    /**
     * Sets how to display each item as text.
     * @param labelMapper function that converts item to display string
     */
    public WMultiSelect<T> label(Function<T, String> labelMapper) {
        this.labelMapper = labelMapper;
        return this;
    }

    /**
     * Sets how to determine if an item is currently selected.
     * @param selectionPredicate function that returns true if item should be checked
     */
    public WMultiSelect<T> isSelected(Predicate<T> selectionPredicate) {
        this.selectionPredicate = selectionPredicate;
        return this;
    }

    /**
     * Sets callback for when user selects/deselects items.
     * @param handler function called with (item, isSelected) when selection changes
     */
    public WMultiSelect<T> onSelectionChange(BiConsumer<T, Boolean> handler) {
        this.selectionChangeHandler = handler;
        return this;
    }

    protected String getItemLabel(T item) {
        return labelMapper.apply(item);
    }

    protected boolean isItemSelected(T item) {
        return selectionPredicate.test(item);
    }

    protected void handleSelectionChange(T item, boolean selected) {
        if (selectionChangeHandler != null) selectionChangeHandler.accept(item, selected);
    }

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
        for (T item : items) {
            if (matchesFilter(item, query))
                filteredItems.add(item);
        }
    }

    private boolean matchesFilter(T item, String query) {
        boolean matchesQuery = query.isEmpty()
                || getItemLabel(item).toLowerCase().contains(query);

        boolean matchesFilterMode = switch (filterMode) {
            case ALL -> true;
            case SELECTED -> isItemSelected(item);
            case UNSELECTED -> !isItemSelected(item);
        };

        return matchesQuery && matchesFilterMode;
    }

    private void refreshItems() {
        itemContainer.clear();
        selectedCount = 0;

        for (T item : filteredItems) {
            if (isItemSelected(item)) selectedCount++;

            WItem itemWidget = createItem(item);
            itemContainer.add(itemWidget).expandX();
        }

        updateSelectAllState();
        updateHeaderLabel();
    }

    private void updateSelectAllState() {
        if (selectAllCheckbox != null) selectAllCheckbox.setChecked(selectedCount > 0);
    }

    private void updateHeaderLabel() {
        header.sizeLabel.set(header.getSizeLabel());
    }

    protected abstract class WHeader extends WHorizontalList {
        protected String title;
        protected WCatppuccinLabel sizeLabel;
        protected WTriangle triangle;

        public WHeader(String title) {
            this.title = title;
        }

        @Override
        public void init() {
            CatppuccinGuiTheme theme = (CatppuccinGuiTheme) getTheme();

            add(theme.label(RichText.bold(title))).expandX().padVertical(8).padLeft(12);
            sizeLabel = (WCatppuccinLabel) add(theme.label(getSizeLabel())).padHorizontal(pad()).widget();

            triangle = add(theme.triangle()).widget();
            triangle.action = () -> setExpanded(!expanded);
        }

        @Override
        public boolean render(GuiRenderer renderer, double mouseX, double mouseY, double delta) {
            if (triangle != null) triangle.rotation = 90 + 90 * animation.getProgress();
            return super.render(renderer, mouseX, mouseY, delta);
        }

        @Override
        public boolean onMouseClicked(Click click, boolean used) {
            if (mouseOver
                //? if >=1.21.9
                && click.button() == GLFW_MOUSE_BUTTON_LEFT
                //? if <1.21.9
                //&& button == GLFW_MOUSE_BUTTON_LEFT
                && !used
            ) {
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

    protected class WItem extends WHorizontalList {
        protected final T item;
        protected WCatppuccinCheckbox checkbox;

        public WItem(T item) {
            this.item = item;
        }

        @Override
        public void init() {
            boolean selected = isItemSelected(item);
            checkbox = (WCatppuccinCheckbox) add(theme.checkbox(selected)).padLeft(8).widget();
            checkbox.action = this::onSelection;

            add(theme.label(getItemLabel(item))).padLeft(pad()).expandX();
        }

        @Override
        protected void onCalculateSize() {
            super.onCalculateSize();
            height *= 1.25;
        }

        @Override
        public boolean onMouseClicked(Click click, boolean used) {
            if (mouseOver
                //? if >=1.21.9
                && click.button() == GLFW_MOUSE_BUTTON_LEFT
                //? if <=1.21.8
                //&& button == GLFW_MOUSE_BUTTON_LEFT
                && !used
                && !checkbox.mouseOver
            ) {
                checkbox.setChecked(!checkbox.checked);
                onSelection();
                return true;
            }

            return false;
        }

        private void onSelection() {
            boolean selected = checkbox.checked;

            selectedCount += selected ? 1 : -1;
            selectedCount = Math.clamp(selectedCount, 0, items.size());

            updateSelectAllState();
            updateHeaderLabel();

            handleSelectionChange(item, selected);
        }
    }

    public enum FilterMode {
        ALL,
        SELECTED,
        UNSELECTED
    }
}