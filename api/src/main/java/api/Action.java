package api;

@ParameterExtension
public interface Action<T> {
    void execute(T t);
}
