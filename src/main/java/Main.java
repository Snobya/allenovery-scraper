
import org.openqa.selenium.*;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;


import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;


public class Main {


    public static void main(String[] args) {
        System.setProperty("webdriver.chrome.driver", "C:\\Users\\Влад\\Desktop\\chromedriver.exe"); //Change your path to webdriver

        WebDriver driver = new ChromeDriver();
        try {

            driver.navigate().to("http://www.allenovery.com/search/Pages/peopleresults.aspx?k=%2A&amp%3Bstart1=26&start1=1");

            driver.manage().timeouts().implicitlyWait(3, TimeUnit.SECONDS);

            String xPhoto = "//*[@id=\"DOC_DETAIL_SRB_g_44c85f85_4cf6_4442_91ab_1e71ac80258b_1\"]/div/div[1]/a/img";
            String xName = "//*[@id=\"DOC_DETAIL_SRB_g_44c85f85_4cf6_4442_91ab_1e71ac80258b_1\"]/div/div[2]";
            String xDescription = "//*[@id=\"DOC_DETAIL_SRB_g_44c85f85_4cf6_4442_91ab_1e71ac80258b_1\"]/div/div[3]";

            int indexPages = 1;

            parseData:
            for (; ; ) {

                for (int i = 1; i < 25 + 1; i++) {
                    Ultimate.scrapPost(driver, i, xPhoto, xName, xDescription);
                    if (i == 25) {
                        indexPages = indexPages + 25;

                        if (indexPages == 2726) break parseData;


                        Ultimate.goNextPage(driver, i, indexPages);
                    }


                }
            }

            List<String> jsonsFinal = Ultimate.parseToJson();
            for (int i = 0; i < jsonsFinal.size(); i++) {
                System.out.println(jsonsFinal.get(i));
            }

        } finally {
            driver.close();
        }


    }

}


class Ultimate {
    static List<String> posts = new ArrayList<>();
    static List<String> photos = new ArrayList<>();
    static List<String> names = new ArrayList<>();
    static List<String> locations = new ArrayList<>();
    static List<String> numbers = new ArrayList<>();
    static List<String> description = new ArrayList<>();

    static List<String> jsons = new ArrayList<>();

    static void scrapPost(WebDriver driver, int i, String xPhoto, String xName, String xDescription) {

        WebElement post = driver.findElement(By.xpath(xPhoto.substring(0, 63) + i + xPhoto.substring(64)));
        photos.add(post.getAttribute("src"));


        post = driver.findElement(By.xpath(xName.substring(0, 63) + i + xName.substring(64)));

        String[] data = post.getText().split("\n");

        if (!(data.length < 4)) {
            names.add(data[0]);
            posts.add(data[1]);
            locations.add(data[2]);
            numbers.add(data[3]);

            post = driver.findElement(By.xpath(xDescription.substring(0, 63) + i + xDescription.substring(64)));
            description.add(post.getText());


        }


    }

    static void goNextPage(WebDriver driver, int i, int index) {

        WebElement next = driver.findElement(By.xpath("//*[@id=\"SRP_NextImg\"]"));


        driver.navigate().to(" http://www.allenovery.com/search/Pages/peopleresults.aspx?k=%2A&amp%3Bstart1=26&start1=" + index);
        WebDriverWait wait1 = new WebDriverWait(driver, 30000000);
        wait1.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//*[@id=\"DOC_DETAIL_SRB_g_44c85f85_4cf6_4442_91ab_1e71ac80258b_" + i + "\"]/div/div[1]/a/img")));

    }

    static List<String> parseToJson() {

        for (int i = 0; i < names.size(); i++) {
            String json = "{ photo: '" + photos.get(i) + "', name:  '" + names.get(i) + "', post:  '" + posts.get(i) + "',location:  '" + locations.get(i) + "',number:  '" + numbers.get(i) + "',description:  '" + description.get(i) + "',}";
            jsons.add(json);

        }
        return jsons;
    }


}
