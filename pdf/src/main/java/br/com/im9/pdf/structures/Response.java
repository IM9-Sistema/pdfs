package br.com.im9.pdf.structures;


public class Response<T> {
    public T data;
    public boolean success;

    public Response(T data, boolean success) {
        this.data = data;
        this.success = success;
    }

    public Response(T data) {
        this.data = data;
        this.success = true;
    }
}
