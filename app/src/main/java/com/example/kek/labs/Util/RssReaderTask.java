package com.example.kek.labs.Util;

import android.os.AsyncTask;

import com.example.kek.labs.Managers.FileManager;

import org.w3c.dom.Document;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

public class RssReaderTask extends AsyncTask<Void, Void, Document> {
    public interface onDownloadedListener {
        void onPostExecute(Document rss);
    }

    private onDownloadedListener listener;
    private String address;

    public RssReaderTask(String address) {
        this.address = address;
    }

    @Override
    protected Document doInBackground(Void... voids) {
        if (new File(FileManager.getDirectoryPath() + File.separator + "news.xml").exists()) {
            return fromCache(address);
        }
        return getData(address);
    }

    @Override
    protected void onPostExecute(Document document) {
        listener.onPostExecute(document);
        super.onPostExecute(document);
    }

    public RssReaderTask addOnDownloadListener(onDownloadedListener listener) {
        this.listener = listener;
        return this;
    }

    private Document fromCache(String address) {
        try {
            File file = new File(FileManager.getDirectoryPath() + File.separator + "news.xml");
            FileInputStream inputStream = new FileInputStream(file);

            DocumentBuilderFactory builderFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = builderFactory.newDocumentBuilder();
            Document xmlDoc = builder.parse(inputStream);

            return xmlDoc;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    private Document getData(String address) {
        try {
            URL url = new URL(address);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            InputStream inputStream = connection.getInputStream();

            DocumentBuilderFactory builderFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = builderFactory.newDocumentBuilder();
            Document xmlDoc = builder.parse(inputStream);

            DOMSource source = new DOMSource(xmlDoc);
            FileWriter writer = new FileWriter(new File(FileManager.getDirectoryPath() + File.separator + "news.xml"));
            StreamResult result = new StreamResult(writer);

            TransformerFactory transformerFactory = TransformerFactory.newInstance();
            Transformer transformer = transformerFactory.newTransformer();
            transformer.transform(source, result);
            return xmlDoc;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}
