package com.shopme.common.entity;

import javax.persistence.*;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "categories")
public class Category {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(length = 128, nullable = false,unique = true)
    private String name;

    @Column(length = 64, nullable = false,unique = true)
    private String alias;

    @Column(length = 128, nullable = false)
    private String image;

    private boolean enabled;

    // Refer to parent
    @OneToOne
    @JoinColumn(name = "parent_id")
    private Category parent;

    // Sub-categories, 1 main category has many sub-categories
    @OneToMany(mappedBy = "parent")
    private Set<Category> children = new HashSet<>();

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAlias() {
        return alias;
    }

    public void setAlias(String alias) {
        this.alias = alias;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public Category getParent() {
        return parent;
    }

    public void setParent(Category parent) {
        this.parent = parent;
    }

    public Set<Category> getChildren() {
        return children;
    }

    public void setChildren(Set<Category> children) {
        this.children = children;
    }

    // Constructor
    public Category() {
    }
    public Category(Integer id, String name, String alias) {
        this.id = id;
        this.name = name;
        this.alias = alias;
    }
    public Category(String name, String alias) {
        this.name = name;
        this.alias = alias;
        this.image = "default.png";
    }

    public Category(String name, String alias, Category parent) {
        this.name = name;
        this.alias = alias;
        this.image = "default.png";
        this.parent = parent;
    }

    // Image properties not record to DB
    @Transient
    public String getImagePath() {
        if(id == null || image.isEmpty()) return "/images/default-category.png";
        return "/category-photos/" + id + "/"+ image;
    }

    // Function copy Category by object
    public static Category copyIdAndNameCategory(Category category) {
        Category copyCategory = new Category();

        copyCategory.setId(category.getId());
        copyCategory.setName(category.getName());
        return copyCategory;
    }

    // Function copy category by id and name
    public static Category copyIdAndNameCategory(Integer id, String name) {
        Category copyCategory = new Category();

        copyCategory.setId(id);
        copyCategory.setName(name);
        return copyCategory;
    }

    // To String method to print only name of category NOT object

    @Override
    public String toString() {
        return this.name;
    }
}
