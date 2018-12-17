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

    public interface onBeforeDownloadListener {
        void onPreExecute();
    }

    private onDownloadedListener downloadedListener;
    private onBeforeDownloadListener beforeDownloadedListener;
    private String address;
    private Boolean includeCache;

    public RssReaderTask(String address, Boolean includeCache) {
        this.address = address;
        this.includeCache = includeCache;
    }

    private String getNewsFileName(String address) {
        return address.split("//")[1].split("/")[0] + ".xml";
    }

    @Override
    protected Document doInBackground(Void... voids) {
        String newsFileName;
        try {
            newsFileName  = FileManager.getDirectoryPath() + File.separator + getNewsFileName(address);
        }
        catch (Exception e) {
            return null;
        }
        if (includeCache && new File(newsFileName).exists()) {
            return fromCache(address);
        }
        return getData(address);
    }

    @Override
    protected void onPostExecute(Document document) {
        downloadedListener.onPostExecute(document);
        super.onPostExecute(document);
    }

    @Override
    protected void onPreExecute() {
        if (beforeDownloadedListener != null)
            beforeDownloadedListener.onPreExecute();
        super.onPreExecute();
    }

    public RssReaderTask addOnDownloadListener(onDownloadedListener listener) {
        downloadedListener = listener;
        return this;
    }

    public RssReaderTask addOnBeforeDownloadListener(onBeforeDownloadListener listener) {
        beforeDownloadedListener = listener;
        return this;
    }

    private Document fromCache(String address) {
        try {
            File file = new File(FileManager.getDirectoryPath() + File.separator + getNewsFileName(address));
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
            FileWriter writer = new FileWriter(new File(FileManager.getDirectoryPath() + File.separator + getNewsFileName(address)));
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
