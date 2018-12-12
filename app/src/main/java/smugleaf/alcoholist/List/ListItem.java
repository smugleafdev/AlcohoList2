package smugleaf.alcoholist.List;

public class ListItem {

    private String icon, title, description, price;

    public ListItem(String icon, String title, String description, String price) {
        this.icon = icon;
        this.title = title;
        this.description = description;
        this.price = price;
    }

    public String getIcon() {
        return icon;
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public String getPrice() {
        return price;
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setPrice(String price) {
        this.price = price;
    }
}