package Bing.Translation;

import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.events.StartElement;
import javax.xml.stream.events.XMLEvent;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

/**
 * Class to translate texts with the Bing API
 * Created by ngrande on 4/10/15.
 */
public class Translate {
    private TokenObject tokenObject;
    private final String baseUriTranslate = "http://api.microsofttranslator.com/v2/Http.svc/Translate";
    private final String baseUriTranslateArray = "http://api.microsofttranslator.com/V2/Http.svc/TranslateArray";

    // set the value in \"\"
    private final String textToTranslateParam = "text";
    private final String fromLangParam = "from";
    private final String toLangParam = "to";
    private final String encoding = "UTF-8";

    public Translate(TokenObject tokenObject) {
        this.tokenObject = tokenObject;
    }

    public TokenObject getTokenObject() {
        return tokenObject;
    }

    public void setTokenObject(TokenObject tokenObject) {
        this.tokenObject = tokenObject;
    }

    public synchronized String translateText(String textToTranslate, LangCode to) {
        try {
            URL url = new URL(String.format("%s?%s=%s&%s=%s",
                    baseUriTranslate, textToTranslateParam, URLEncoder.encode(textToTranslate, encoding),
                    toLangParam, to.getCode()));
            return translateText(url);
        } catch (MalformedURLException e) {
            e.printStackTrace();
            return null;
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
            return null;
        }
    }

    public synchronized String translateText(String textToTranslate, LangCode from, LangCode to) {
        URL url = null;
        try {
            url = new URL(String.format("%s?%s=%s&%s=%s&%s=%s",
                    baseUriTranslate, textToTranslateParam, URLEncoder.encode(textToTranslate, encoding),
                    fromLangParam, from.getCode(), toLangParam, to.getCode()));
            return translateText(url);
        } catch (MalformedURLException | UnsupportedEncodingException e) {
            e.printStackTrace();
            return null;
        }
    }

    private synchronized String translateText(URL url) {
        String result = null;
        HttpURLConnection connection = null;
        try {
            //Create connection
            connection = (HttpURLConnection) url.openConnection();

            connection.setRequestProperty("Authorization", tokenObject.getAuthorizationHeader());
            connection.setRequestMethod("GET");
            connection.setUseCaches(false);
            connection.setDoInput(true);

            //Get Response
            InputStream inputStream = connection.getInputStream();

            String[] stringArr = readXMLResponse(inputStream, "string");

            if (stringArr.length > 0) {
                result = stringArr[0];
            }

            connection.disconnect();
            return result;
        } catch (Exception e) {
            e.printStackTrace();
            return result;
        } finally {
            if (connection != null)
                connection.disconnect();
        }
    }

    public synchronized String[] translateTextArray(String[] textsToTranslate, LangCode from, LangCode to) throws ArrayToLongException, TooManyCharactersException {
        throwExceptionIfTooLong(textsToTranslate);
        return translateTextArray(buildXMlRequest(textsToTranslate, from, to, "text/plain"));
    }

    public synchronized String[] translateTextArray(String[] textsToTranslate, LangCode to) throws ArrayToLongException, TooManyCharactersException {
        throwExceptionIfTooLong(textsToTranslate);
        return translateTextArray(buildXMlRequest(textsToTranslate, null, to, "text/plain"));
    }

    private synchronized void throwExceptionIfTooLong(String[] textsToTranslate) throws ArrayToLongException, TooManyCharactersException {
        if (textsToTranslate.length > 2000) {
            throw new ArrayToLongException("Array of texts to translate should not exceed 2000 elements.");
        }
        int charCounter = 0;
        for (String s : textsToTranslate) {
            charCounter += s.toCharArray().length;
            if (charCounter > 10000) {
                throw new TooManyCharactersException("Sum of all characters of the text to translate should not exceed 10000.");
            }
        }
    }

    private synchronized String buildXMlRequest(String[] textsToTranslate, LangCode from, LangCode to, String contentType) {
        String textsAsXML = "";

        for (String text : textsToTranslate) {
            text = text.replace("Ä", "&#196;");
            text = text.replace("Ö", "&#214;");
            text = text.replace("Ü", "&#220;");
            text = text.replace("ä", "&#228;");
            text = text.replace("ö", "&#246;");
            text = text.replace("ü", "&#252;");
            text = text.replace("ß", "&#223;");

            text = text.replace("&", "&amp;");
            text = text.replace("'", "&apos;");
            text = text.replace("<", "&lt;");
            text = text.replace(">", "&gt;");
            text = text.replace("\"", "&quot;");

            textsAsXML += String.format("<string xmlns=\"http://schemas.microsoft.com/2003/10/Serialization/Arrays\">%s</string>", text);
        }
        String base = "<TranslateArrayRequest>" +
                "<AppId />" +
                "%s" +
                "<Options>" +
                " <Category xmlns=\"http://schemas.datacontract.org/2004/07/Microsoft.MT.Web.Service.V2\" />" +
                "<ContentType xmlns=\"http://schemas.datacontract.org/2004/07/Microsoft.MT.Web.Service.V2\">%s</ContentType>" +
                "<ReservedFlags xmlns=\"http://schemas.datacontract.org/2004/07/Microsoft.MT.Web.Service.V2\" />" +
                "<State xmlns=\"http://schemas.datacontract.org/2004/07/Microsoft.MT.Web.Service.V2\" />" +
                "<Uri xmlns=\"http://schemas.datacontract.org/2004/07/Microsoft.MT.Web.Service.V2\" />" +
                "<User xmlns=\"http://schemas.datacontract.org/2004/07/Microsoft.MT.Web.Service.V2\" />" +
                "</Options>" +
                "<Texts>" +
                "%s" +
                "</Texts>" +
                "<To>%s</To>" +
                "</TranslateArrayRequest>";

        return String.format(base, from != null ? "<From>" + from.getCode() + "</From>" : "<From/>", contentType, textsAsXML, to.getCode());
    }

    private synchronized String[] translateTextArray(String xmlRequestParam) {
        String[] result = null;
        URL url;
        HttpURLConnection connection = null;
        try {
            //Create connection
            url = new URL("http://api.microsofttranslator.com/v2/Http.svc/TranslateArray");
            connection = (HttpURLConnection) url.openConnection();
            connection.setRequestProperty("Content-Type", "text/xml");
            connection.setRequestMethod("POST");
            connection.setRequestProperty("Authorization", tokenObject.getAuthorizationHeader());
            connection.setUseCaches(false);
            connection.setDoInput(true);
            connection.setDoOutput(true);

            //Send request
            DataOutputStream dataOutputStream = new DataOutputStream(connection.getOutputStream());
            dataOutputStream.writeBytes(xmlRequestParam);
            dataOutputStream.flush();
            dataOutputStream.close();

            //Get Response
            InputStream inputStream = connection.getInputStream();

            result = readXMLResponse(inputStream, "TranslatedText");

            connection.disconnect();

        } catch (IOException e) {
            e.printStackTrace();
        }

        return result;
    }

    private synchronized String[] readXMLResponse(InputStream inputStream, String param) {
        List<String> stringList = new ArrayList<>();

        XMLInputFactory inputFactory = XMLInputFactory.newInstance();
        try {
            XMLEventReader eventReader = inputFactory.createXMLEventReader(inputStream);

            while (eventReader.hasNext()) {
                XMLEvent event = eventReader.nextEvent();

                if (event.isStartElement()) {
                    StartElement startElement = event.asStartElement();

                    if (startElement.getName().getLocalPart().toLowerCase().equals(param.toLowerCase())) {
                        event = eventReader.nextEvent();
                        String value = event.asCharacters().getData();
                        stringList.add(value);
                    }
                }
            }
        } catch (XMLStreamException e) {
            e.printStackTrace();
        }

        return stringList.toArray(new String[stringList.size()]);
    }
}
