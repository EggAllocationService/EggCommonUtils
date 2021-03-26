package io.egg.common.fields;

public interface FieldDelegate<T> {
    public byte[] serialize(Object o);
    public T deserialize(byte[] data);
}
