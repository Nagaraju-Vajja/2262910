package service;

import Model.Book;
import com.example.demo.Repository.BookRepo;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;

@Service
public class BookService implements BookRepo {
    private HashMap<Integer, Book> hmap = new HashMap<>();

    public BookService(){
        Book b1= new Book(1,"BOOK1","Book1.jpg");
        Book b2= new Book(1,"BOOK2","Book2.jpg");
       hmap.put(b1.getId(),b1);
       hmap.put(b2.getId(),b2);
    }

    @Override
    public ArrayList<Book> getBooks() {
        Collection<Book> bookCollection =hmap.values();
        ArrayList<Book> al =new ArrayList<>(bookCollection);
        return al;
    }
}
