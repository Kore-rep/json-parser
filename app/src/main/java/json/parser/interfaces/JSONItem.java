package json.parser.interfaces;

public interface JSONItem {
    public String toString();

    public void addItem(Object item);

    public void addItem(String key, Object value);

}
