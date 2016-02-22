/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package bzh.terrevirtuelle.navisu.domain.navigation.sailingDirections.model.shom;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

/**
 *
 * @author serge
 * @date Feb 20, 2016
 *
 */
@XmlRootElement
@XmlType(name = "document", propOrder = {"metaData","book"})
@XmlAccessorType(XmlAccessType.FIELD)
public class Document {

    @XmlElement(name = "metaData")
    private Metadata metaData;
    @XmlElement(name = "ouvrage")
    private Book book;

    public Document() {
    }

    public Document(Metadata metaData, Book book) {
        this.metaData = metaData;
        this.book = book;
    }

    /**
     * Get the value of book
     *
     * @return the value of book
     */
    public Book getBook() {
        return book;
    }

    /**
     * Set the value of book
     *
     * @param book new value of book
     */
    public void setBook(Book book) {
        this.book = book;
    }

    /**
     * Get the value of metaData
     *
     * @return the value of metaData
     */
    public Metadata getMetaData() {
        return metaData;
    }

    /**
     * Set the value of metaData
     *
     * @param metaData new value of metaData
     */
    public void setMetaData(Metadata metaData) {
        this.metaData = metaData;
    }

    @Override
    public String toString() {
        return "Document{" + "metaData=" + metaData + ", book=" + book + '}';
    }

}
