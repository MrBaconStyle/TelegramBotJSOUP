import java.security.PublicKey;
import java.util.ArrayList;

public class ListStorage {

    private ArrayList<String> urlList = new ArrayList<String>();
    private ArrayList<String> bookList = new ArrayList<String>();
    private ArrayList<Integer> pageNumberList = new ArrayList<Integer>();

    public void addUrl(String url) {
        urlList.add(url);
    }

    public ArrayList<String> getUrlList() {
        return urlList;
    }

    public void removeUrl() {
        urlList.remove(0);
    }

    public void addBook(String book, String price, int page, String link) {
        bookList.add("Knjiga --> " + book + " --> " + price + " --> str " + page + "\n" + "https://novi.kupujemprodajem.com" + link);
    }

    public ArrayList<String> getBookList() {
        return bookList;
    }

    public void removeBook() {
        bookList.removeAll(bookList);
    }

    public int bookListSize() {
        return bookList.size();
    }

    public String printBookList(int index) {
        return bookList.get(index);
    }

    public void addPageNumb(int number) {
        pageNumberList.add(number);
    }

    public int pageNumbSize() {
        return pageNumberList.size();
    }

    public int getNumb(int number) {
        return pageNumberList.get(number);
    }

    public void removeNumbList() {
        pageNumberList.removeAll(pageNumberList);
    }

}

