package pl.assolution.rocks;

import android.graphics.Bitmap;
import android.widget.ImageView;

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

    public static class ItemsList extends ArrayList<SimplyItem> {

    }
}
