import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.io.IOException;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Map;

public class TelBot extends TelegramLongPollingBot {

    private static ListStorage listStorage = new ListStorage();

    @Override
    public String getBotUsername() {
        return "skrejp_bot";
    }

    @Override
    public String getBotToken() {
        return "5914952957:AAHMHDBQCReDVrxHLPrrcMNXrZoUknpKlOg";
    }

    @Override
    public void onUpdateReceived(Update update) {

        SendMessage sndMessage = new SendMessage();

        if (update.hasMessage()) {

            String command = update.getMessage().getText();
            Message rcvMessage = update.getMessage();

            if (rcvMessage.isCommand()) {

                if (command.startsWith(Command.EPIC)) {

                    listStorage.addUrl("https://novi.kupujemprodajem.com/knjige/epska-fantastika/grupa/8/1095/");

                    sndMessage.setText("Tražim u Epskoj Fantastici...\n Unesi naziv knjige: ");

                    sndMessage.setChatId(update.getMessage().getChatId());

                    try {
                        execute(sndMessage);
                    } catch (TelegramApiException e) {
                        e.printStackTrace();
                    }

                } else if (command.startsWith(Command.SCIENCE)) {

                    listStorage.addUrl("https://novi.kupujemprodajem.com/knjige/naucna-fantastika/grupa/8/355/");

                    sndMessage.setText("Tražim u Naučnoj Fantastici...\n Unesi naziv knjige: ");

                    sndMessage.setChatId(update.getMessage().getChatId());

                    try {
                        execute(sndMessage);
                    } catch (TelegramApiException e) {
                        e.printStackTrace();
                    }
                }
            } else {
                sndMessage.setText("Tražim Knjigu: " + command);

                sndMessage.setChatId(update.getMessage().getChatId());

                try {
                    execute(sndMessage);
                } catch (TelegramApiException e) {
                    e.printStackTrace();
                }

                String url = listStorage.getUrlList().get(0);
                listStorage.removeUrl();

                try {
                    scrape(url, command);
                } catch (IOException e) {
                    e.printStackTrace();
                }

                if (listStorage.bookListSize()==0) {

                    sndMessage.setText("Nema tražene knjige.");

                    sndMessage.setChatId(update.getMessage().getChatId());

                    try {
                        execute(sndMessage);
                    } catch (TelegramApiException e) {
                        e.printStackTrace();
                    }

                } else {

                    for (int i = 0; i < listStorage.bookListSize(); i++) {
                        String bookNamePricePage = listStorage.printBookList(i);

                        sndMessage.setText(bookNamePricePage);

                        sndMessage.setChatId(update.getMessage().getChatId());

                        try {
                            execute(sndMessage);
                        } catch (TelegramApiException e) {
                            e.printStackTrace();
                        }
                    }
                }

                listStorage.removeBook();

            }

        }

    }

    public static void scrape(String url, String book) throws IOException {

        int pageNum = 1;
        int maxPage = largestPage(url);

        while (true) {

            String nUrl = url + pageNum + "?page=" + pageNum;
            Map<String, String> cookies = Jsoup.connect(nUrl).execute().cookies();
            Document document = Jsoup.connect(nUrl).userAgent("Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/110.0.0.0 Safari/537.36").cookies(cookies).get();
            //System.out.println(document.body().html());
            Elements bookListing = document.getElementsByClass("AdItem_adHolder__NoNLJ");

            for (Element listing : bookListing) {

                String title = listing.getElementsByClass("AdItem_name__80tI5").text();

                if (title.toLowerCase().contains(book.toLowerCase())) {
                    //System.out.println("Knjiga --> " + title + " --> " + price + " --> str " + pageNum);
                    System.out.println(book + " " + pageNum);
                    String price = listing.getElementsByClass("AdItem_price__jUgxi").text();
                    String link = listing.getElementsByClass("Link_link__J4Qd8").attr("href");
                    listStorage.addBook(title, price, pageNum, link);

                }
            }


//            if (arrows.size() == 28) {
            if (pageNum <= maxPage) {
                pageNum++;
            } else {
                //int bookCount = listStorage.bookListSize(); // PROVERAVAM KOLIKO IMA KNJIGA U LISTI
                //System.out.println(listStorage.getBookList()); //ISPISUJE LISTU KNJIGA
                break;

            }
        }
    }

    public static int largestPage(String url) throws IOException{
        listStorage.removeNumbList();
        String nUrl = url + "1" + "?page=" + "1";
        Map<String, String> cookies = Jsoup.connect(nUrl).execute().cookies();
        Document document = Jsoup.connect(nUrl).userAgent("Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/110.0.0.0 Safari/537.36").cookies(cookies).get();
        Elements arrows = document.getElementsByClass("Button_children__3mYJw");
        for (Element listArrows : arrows) {
            String text = listArrows.text();
            String value = text.replaceAll("\\D+", "");
            if (value.isEmpty()) {
                continue;
            } else {
                int number = Integer.parseInt(value);
                listStorage.addPageNumb(number);
            }
        }
        int max = listStorage.getNumb(0);
        for (int i = 1; i < listStorage.pageNumbSize(); i++) {
            if (max < listStorage.getNumb(i))
                max = listStorage.getNumb(i);
        }
        System.out.println(max);
        return max;
    }

}
