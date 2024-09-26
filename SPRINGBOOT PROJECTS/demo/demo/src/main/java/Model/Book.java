package Model;

public class Book {

    private int id;
    private String name;
    private String imageurl;

    public Book(int id, String name, String imageurl){
        this.id=id;
        this.name=name;
        this.imageurl=imageurl;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getImageurl() {
        return imageurl;
    }

    public void setImageurl(String imageurl) {
        this.imageurl = imageurl;
    }

    public void put(int id, Book b1) {
    }
}
