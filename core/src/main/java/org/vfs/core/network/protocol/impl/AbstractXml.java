package org.vfs.core.network.protocol.impl;

import org.jdom.Document;
import org.jdom.output.Format;
import org.jdom.output.XMLOutputter;

/**
 * @author Lipatov Nikita
 */
public class AbstractXml
{

    protected Document document = null;

    /**
     * Method returns xml in string view.
     * @return Result string.
     **/
    public String toXml()
    {
        return getXMLText(document).trim();
    }

    /**
     * Document to string
     * @param document Jdom document of xml-data.
     * @return xml-content.
     **/
    protected String getXMLText(Document document)
    {
        XMLOutputter out = new XMLOutputter();
        Format format = Format.getPrettyFormat();
        format.setLineSeparator("\n");
        format.setEncoding("UTF-8");
        out.setFormat(format);
        return out.outputString(document);
    }
}
