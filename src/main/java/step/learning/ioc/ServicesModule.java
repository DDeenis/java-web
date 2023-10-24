package step.learning.ioc;

import com.google.inject.AbstractModule;
import com.google.inject.Provides;
import com.google.inject.name.Names;
import step.learning.services.db.DbProvider;
import step.learning.services.db.PlanetScaleDbProvider;
import step.learning.services.formparse.FormParseService;
import step.learning.services.formparse.MixedFormParseService;
import step.learning.services.hash.*;
import step.learning.services.kdf.DigestHashKdfService;
import step.learning.services.kdf.KdfService;
import step.learning.services.random.*;

public class ServicesModule extends AbstractModule {
    @Override
    protected void configure() {
        bind(HashService.class).annotatedWith(Names.named("Digest-Hash")).to(SHA256HashService.class);
        bind(HashService.class).annotatedWith(Names.named("DSA-Hash")).to(MD5HashService.class);

        bind(FormParseService.class).to(MixedFormParseService.class);
        bind(DbProvider.class).to(PlanetScaleDbProvider.class);
        bind(String.class).annotatedWith(Names.named("db-prefix")).toInstance("kn201__");
        bind(KdfService.class).to(DigestHashKdfService.class);
    }

    private RandomService randomService;
    @Provides
    private RandomService injectRandomService() {
        if(randomService == null) {
            randomService = new RandomServiceV1();
        }
        return randomService;
    }
}