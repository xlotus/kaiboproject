package cn.cibn.kaibo.model;

import java.io.Serializable;
import java.util.Objects;

public class MenuItem implements Serializable {
    private int id;
    private String menuName;

    public MenuItem(int id, String menuName) {
        this.id = id;
        this.menuName = menuName;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getMenuName() {
        return menuName;
    }

    public void setMenuName(String menuName) {
        this.menuName = menuName;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        MenuItem menuItem = (MenuItem) o;
        return id == menuItem.id && menuName.equals(menuItem.menuName);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, menuName);
    }
}
