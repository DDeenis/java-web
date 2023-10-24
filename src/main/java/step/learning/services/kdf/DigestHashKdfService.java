package step.learning.services.kdf;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import step.learning.services.hash.HashService;

import javax.inject.Named;

@Singleton
public class DigestHashKdfService implements KdfService {
    private final HashService hashService;

    @Inject
    public DigestHashKdfService(@Named("Digest-Hash") HashService hashService) {
        this.hashService = hashService;
    }

    @Override
    public String getDerivedKey(String password, String salt) {
        return hashService.hash(salt + password + salt).substring(0, 32);
    }
}
