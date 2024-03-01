package model;

public class SelectedItem {
    private String selectedItem;
    private String origin;

    public SelectedItem(String selectedItem, String origin) {
        this.selectedItem = selectedItem;
        this.origin = origin;
    }

    public String getSelectedItem() {
        return selectedItem;
    }

    public String getOrigin() {
        return origin;
    }
}
