package com.example.kek.labs.Util;

import com.example.kek.labs.Models.FeedItem;

import org.jsoup.Jsoup;
import org.jsoup.select.Elements;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import java.util.ArrayList;
import java.util.List;

public final class Parser {
    public static List<FeedItem> parseRss(Document data) {
        List<FeedItem> feedItems = new ArrayList<>();

        if (data == null) return feedItems;

        Element root = data.getDocumentElement();
        Node channel = root.getChildNodes().item(1);
        NodeList items = channel.getChildNodes();
        for (int i = 0; i < items.getLength(); i++) {
            Node curChild = items.item(i);
            if (curChild.getNodeName().equalsIgnoreCase("item")) {
                FeedItem item = new FeedItem();
                NodeList childNodes = curChild.getChildNodes();
                String descriptionUri = null;
                String enclosureUri = null;
                String mediaUri = null;
                for (int j = 0; j < childNodes.getLength(); j++) {
                    Node curNode = childNodes.item(j);

                    if (curNode.getNodeName().equalsIgnoreCase("title")) {
                        item.setTitle(curNode.getTextContent());
                    } else if (curNode.getNodeName().equalsIgnoreCase("description")) {
                        org.jsoup.nodes.Document description = Jsoup.parse(curNode.getTextContent());
                        Elements images = description.getElementsByTag("img");
                        if (images != null && images.size() != 0)
                            descriptionUri = images.get(0).attr("src");
                        item.setDescription(description.text());
                    } else if (curNode.getNodeName().equalsIgnoreCase("pubDate")) {
                        item.setPubDate(curNode.getTextContent());
                    } else if (curNode.getNodeName().equalsIgnoreCase("link")) {
                        item.setLink(curNode.getTextContent());
                    } else if (curNode.getNodeName().equalsIgnoreCase("enclosure")) {
                        enclosureUri = curNode.getAttributes().getNamedItem("url").getTextContent();
                    } else if (curNode.getNodeName().equalsIgnoreCase("media:thumbnail")) {
                        mediaUri = curNode.getAttributes().item(0).getTextContent();
                    }
                }
                String url = mediaUri;
                if (mediaUri == null || mediaUri.length() == 0) {
                    if (enclosureUri != null && enclosureUri.length() != 0)
                        url = enclosureUri;
                    else
                        url = descriptionUri;
                }
                item.setThumbnailUrl(url);
                feedItems.add(item);
            }
        }
        return feedItems;
    }
}
