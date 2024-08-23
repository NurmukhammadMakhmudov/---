package org.example;

import org.jsoup.HttpStatusException;
import org.jsoup.Jsoup;
import org.jsoup.UnsupportedMimeTypeException;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ConcurrentSkipListSet;
import java.util.concurrent.RecursiveTask;

public class LinkTreeBuilder extends RecursiveTask<String> {
    private final String rootLink;
    private final int depth;
    private static final Set<String> visitedLinks = new ConcurrentSkipListSet<>();
    private static final String DOMAIN = "sendel.ru";



    public LinkTreeBuilder(String rootLink, int depth) {
        this.rootLink = rootLink;
        this.depth = depth;
    }

    @Override
    protected String compute() {
        try {
            Thread.sleep(150);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        StringBuilder result = new StringBuilder();
        if (visitedLinks.add(rootLink)) {
            try {
                Document document = Jsoup.connect(rootLink).get();
                Elements links = document.select("a[href]");

                result.append("    ".repeat(depth)).append(rootLink).append("\n");

                List<LinkTreeBuilder> subTasks = new ArrayList<>();
                for (Element link : links) {
                    String absHref = link.attr("abs:href");
                    if (absHref.contains(DOMAIN) && !absHref.contains("#")) {
                        LinkTreeBuilder task = new LinkTreeBuilder(absHref, depth + 1);
                        subTasks.add(task);
                        task.fork();
                    }
                }

                for (LinkTreeBuilder task : subTasks) {
                    result.append(task.join());
                }

            }catch (HttpStatusException e) {
                System.err.println("HTTP error fetching URL. Status=" + e.getStatusCode() + ", URL=" + e.getUrl());

            } catch (UnsupportedMimeTypeException e) {
                System.err.println("Unsupported MIME type. Mimetype=" + e.getMimeType() + ", URL=" + e.getUrl());

            } catch (IOException e) {
                System.err.println("IOException while fetching URL: " + rootLink);
            }
        }

        return result.toString();
    }
}