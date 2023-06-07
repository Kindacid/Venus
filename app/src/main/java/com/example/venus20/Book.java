package com.example.venus20;

public class Book {
    private String titulo;
    private String editor;
    private String author;
    private String genero;
    private String ondeEnc;
    private String bookID;

    public Book(){}

    public Book(String bookID, String titulo, String editor, String author, String genero, String ondeEnc) {
        this.bookID = bookID;
        this.titulo = titulo;
        this.editor = editor;
        this.author = author;
        this.genero = genero;
        this.ondeEnc = ondeEnc;
    }

    public Book(String titulo, String autor, String editora, String numPag, String ondeEnc) {
    }

    public String getBookID() {
        return bookID;
    }

    public void setBookID(String bookID) {
        this.bookID = bookID;
    }

    public String getTitulo() {
        return titulo;
    }

    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

    public String getEditor() {
        return editor;
    }

    public void setEditor(String editor) {
        this.editor = editor;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getGenero() {
        return genero;
    }

    public void setGenero(String genero) {
        this.genero = genero;
    }

    public String getOndeEnc() {
        return ondeEnc;
    }

    public void setOndeEnc(String ondeEnc) {
        this.ondeEnc = ondeEnc;
    }
}

