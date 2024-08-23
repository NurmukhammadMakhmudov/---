package org.example;

import java.io.FileWriter;
import java.io.IOException;
import java.util.concurrent.ForkJoinPool;

public class Main {
    public static void main(String[] args) {
        String link = "https://sendel.ru/";
        ForkJoinPool forkJoinPool = new ForkJoinPool();
        LinkTreeBuilder linkTreeBuilder = new LinkTreeBuilder( link, 0);

       String siteMap = forkJoinPool.invoke(linkTreeBuilder);
       forkJoinPool.shutdown();
        try (FileWriter writer = new FileWriter("sitemap.txt")) {
            writer.write(siteMap);
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

}
