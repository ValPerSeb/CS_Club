
package app.model;

public class Person {
    private long id;
    private long document;
    private String name;
    private long cellPhone;

    public Person() {
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public long getDocument() {
        return document;
    }

    public void setDocument(long document) {
        this.document = document;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public long getCellPhone() {
        return cellPhone;
    }

    public void setCellPhone(long cellPhone) {
        this.cellPhone = cellPhone;
    }
}
