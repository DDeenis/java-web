package step.learning.services.random;

public interface RandomService {
    String randomHex(int charLength);
    void seed(String iv);
}
