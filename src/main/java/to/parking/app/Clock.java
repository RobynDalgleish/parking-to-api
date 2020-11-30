package to.parking.app;

import javax.inject.Singleton;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;

@Singleton
public class Clock {
    public OffsetDateTime time(){
        return OffsetDateTime.now(ZoneOffset.UTC);
    }
}
