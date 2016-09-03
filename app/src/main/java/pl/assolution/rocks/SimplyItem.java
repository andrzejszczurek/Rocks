package pl.assolution.rocks;

import android.graphics.Bitmap;
import java.util.ArrayList;

/**
 * Created by Andrzej on 2016-08-30.
 */
public class SimplyItem {

    private int id;
    private String id_rocks;
    private String designation;
    private String description;
    private String author;
    private String img_url;
    private Bitmap image;
    private String type;
    private String color;
    private String texture;
    private String structure;
    private String composition;
    private String other;
    private String binder;
//    private String add_date;
//    private String mod_date;

    public SimplyItem(int id, String id_rocks, String designation, String author, String type, String color, String texture, String structure, String composition, String binder, String other, Bitmap image, String img_url) {
        this.id = id;
        this.id_rocks = id_rocks;
        this.designation = designation;
        this.author = author;
        this.type = type;
        this.color = color;
        this.texture = texture;
        this.structure = structure;
        this.composition = composition;
        this.other = other;
        this.image = image;
        this.img_url = img_url;
        this.binder = binder;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getId() {
        return id;
    }

    public SimplyItem(int id ,String id_rocks, String designation, String description, String author, String img_url, Bitmap image) {
        this.id = id;
        this.id_rocks = id_rocks;
        this.designation = designation;
        this.description = description;
        this.author = author;
        this.img_url = img_url;
        this.image = image;
    }

    public Bitmap getImage() {
        return image;
    }

    public String getId_rocks() {
        return id_rocks;
    }

    public String getDesignation() {
        return designation;
    }

    public String getDescription() {
        return description;
    }

    public String getAuthor() {
        return author;
    }

    public String getImg_url() {
        return img_url;
    }

    public String getType() {
        return type;
    }

    public String getColor() {
        return color;
    }

    public String getTexture() {
        return texture;
    }

    public String getStructure() {
        return structure;
    }

    public String getComposition() {
        return composition;
    }

    public String getOther() {
        return other;
    }

    public String getBinder() {
        return binder;
    }

    public static class ItemsList extends ArrayList<SimplyItem> {

    }
}

