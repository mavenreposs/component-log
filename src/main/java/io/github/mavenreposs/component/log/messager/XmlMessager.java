package io.github.mavenreposs.component.log.messager;

import io.github.mavenreposs.component.log.LogUtil;
import io.github.mavenreposs.component.log.contracts.MessagerInterface;

import javax.xml.transform.*;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;
import java.io.StringReader;
import java.io.StringWriter;

public class XmlMessager implements MessagerInterface {

    private final String xml;

    public XmlMessager(String xml) {
        this.xml = xml;
    }

    @Override
    public String getParseMessage() {
        if (LogUtil.isEmpty(xml)) {
            return "Empty/Null xml content";
        }

        try {
            Source xmlInput = new StreamSource(new StringReader(xml));
            StreamResult xmlOutput = new StreamResult(new StringWriter());
            Transformer transformer = TransformerFactory.newInstance().newTransformer();
            transformer.setOutputProperty(OutputKeys.INDENT, "yes");
            transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "2");
            transformer.transform(xmlInput, xmlOutput);
            return xmlOutput.getWriter().toString().replaceFirst(">", ">\n");
        } catch (TransformerException e) {
            return "Invalid xml content";
        }
    }
}
